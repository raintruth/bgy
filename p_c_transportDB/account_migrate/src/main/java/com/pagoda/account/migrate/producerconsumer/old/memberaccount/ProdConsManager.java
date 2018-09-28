package com.pagoda.account.migrate.producerconsumer.old.memberaccount;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.pagoda.account.migrate.service.account.UserAccountService;
import com.pagoda.account.migrate.service.account.UserService;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
import com.pagoda.account.migrate.service.member.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j
public class ProdConsManager {

    private IMemberAccountService memberAccountService;
    private IMemberService memberService;

    private UserAccountService userAccountService;

    private UserService userService;


    public ProdConsManager(IMemberAccountService memberAccountService, UserAccountService userAccountService) {
        this.memberAccountService = memberAccountService;
        this.userAccountService = userAccountService;

    }

    public ProdConsManager(IMemberAccountService memberAccountService, UserAccountService userAccountService, UserService userService) {
        this.memberAccountService = memberAccountService;
        this.userAccountService = userAccountService;
        this.userService = userService;
    }


    public void  run() {
        long startTime = System.currentTimeMillis();

        //由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序
        final RejectedExecutionHandler handler1 = (r, executor) -> {
            log.info("太忙了,ProductThread "+ executor.toString());
            if (!executor.isShutdown()) {
                //log.info("ProductThread Handler 尝试 重试 TaskNum " + ((ProductThread) r).getTaskNum());
                r.run();
            }
        };

        final RejectedExecutionHandler handler2 = (r, executor) -> {
            log.info("太忙了,UserConsumer "+ executor.toString());
            // msgQueue.offer(((DBThread) r).getMsg());
            if (!executor.isShutdown()) {
               // log.info("UserConsumer Handler 尝试 重试 TaskNum " + ((UserConsumer) r).getTaskNum());
                r.run();
            }
        };

        BlockingQueue publicBoxQueue= new ArrayBlockingQueue(10);   //定义了一个大小为5的盒子
        //为多生产者和多消费者分别开创的线程池
        ThreadPoolExecutor productPool =
                new ThreadPoolExecutor(2, 20, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2),handler1);//new ThreadPoolExecutor.CallerRunsPolicy()
        ThreadPoolExecutor consumerPool =
                new ThreadPoolExecutor(4, 20, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(3), handler2);

        log.info("start");

        //1 首先 userAccount 依赖于user  所以 按照user 全量 分页 memberIds+ userAccount的modifyTime时间段
        Map<String, Object> minAndMaxAndCountModifyTimeMap = memberAccountService.findMinAndMaxAndCountModifyTime(0);


        String minUpdateTimeStr = minAndMaxAndCountModifyTimeMap.get("minUpdateTime").toString();
        String maxUpdateTimeStr = minAndMaxAndCountModifyTimeMap.get("maxUpdateTime").toString();
        Long memberCount = new BigInteger(minAndMaxAndCountModifyTimeMap.get("memberCount").toString()).longValue();
        log.info("minAndMaxAndCountMemberIdMap={}", minAndMaxAndCountModifyTimeMap.toString());



        Date minUpdateTimeD = DateUtil.parse(minUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        Date maxUpdateTimeD = DateUtil.parse(maxUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        //毫秒值
        long minUpdateTime = minUpdateTimeD.getTime();
        long maxUpdateTime = maxUpdateTimeD.getTime();


        long betweenDay = DateUtil.betweenDay(minUpdateTimeD, maxUpdateTimeD, true);
        log.info("betweenDay={}", betweenDay);

        Map<String, Future> futureMap = new TreeMap<>();

        //TODO 这里是 user表的最大最小区间
        String beginUpdateTimeStr = "20161101000000"; //20161102050540 20161101000000
        String terminalUpdateTimeStr = "20161102000000";//"20160201000000" 20161101000000-20170101000000

        //TODO
        int memberIdCount = 5000 ;
        int batchCount = 5000 ;
        Long totalCountSize = 1150000L;//115002L
//int terminalMemberId = 50000;
        if(StrUtil.isEmpty(beginUpdateTimeStr) ){
            beginUpdateTimeStr = minUpdateTimeStr;
        }
        if(StrUtil.isEmpty(terminalUpdateTimeStr) ){
            terminalUpdateTimeStr = maxUpdateTimeStr;
        }

        Date beginUpdateTimeD = DateUtil.parse(beginUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        long beginUpdateTimeMs = beginUpdateTimeD.getTime();

        Date terminalUpdateTimeD = DateUtil.parse(terminalUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        long terminalUpdateTimeMs = terminalUpdateTimeD.getTime();


        if(beginUpdateTimeMs > terminalUpdateTimeMs){
            log.error("起始时间不能大于结束时间-beginUpdateTimeMs={}>terminalUpdateTimeMs={}", beginUpdateTimeMs, terminalUpdateTimeMs);
            return;
        }
/*
        if(beginUpdateTimeMs > minUpdateTime){
            minUpdateTime = beginUpdateTimeMs;
        }
        if(terminalUpdateTimeMs < maxUpdateTime){
            maxUpdateTime = terminalUpdateTimeMs;
        }*/


        String startUpdateTimeStr = DateUtil.format(DateUtil.date(minUpdateTime), DatePattern.PURE_DATETIME_PATTERN);
        String endUpdateTimeStr = DateUtil.format(DateUtil.date(maxUpdateTime), DatePattern.PURE_DATETIME_PATTERN);




        Long pageCount = userService.countUserPartList(beginUpdateTimeStr, terminalUpdateTimeStr);
        Long pageCount2 = memberAccountService.countMemberAccountValidPartList(startUpdateTimeStr, endUpdateTimeStr, 0);
        if(pageCount == 0){
            log.error("通过时间段-【beginUpdateTimeStr={}, terminalUpdateTimeStr={}】查询数据为空！", beginUpdateTimeStr, terminalUpdateTimeStr);
            return;
        }
        Long pageSize = NumberUtil.round(pageCount * 1D/ batchCount, 0,RoundingMode.UP).longValue();
        Long totalSize = NumberUtil.round(totalCountSize * 1D/ batchCount, 0,RoundingMode.UP).longValue();
/*        if(totalCount < pageCount2){
            pageCount2 = totalCount;
        }*/

        log.info("【startUpdateTime={}, endUpdateTime={}, pageCount={}】", startUpdateTimeStr, endUpdateTimeStr, pageCount);

        final CountDownLatch productGate = new CountDownLatch(pageSize.intValue());
        int thisIndex = 0;
        for(int curIndex = 0; curIndex < pageSize; curIndex++){
            thisIndex = curIndex;

            Pageable pageable = PageRequest.of(curIndex, memberIdCount, Sort.by("member_id"));
            log.info("【第{}页, 共={}页，startUpdateTime={}, endUpdateTime={}】", curIndex+1, pageSize, startUpdateTimeStr, endUpdateTimeStr);
            Map<String, Object> storageInfoMap = userService.findMemberIdInfoMapByPage(pageable);

            try {

                Future prodFuture = productPool.submit(new ProductThread(curIndex, 0,publicBoxQueue, productGate, memberAccountService, startUpdateTimeStr, endUpdateTimeStr, storageInfoMap, null));
                futureMap.put(curIndex + "_prodFuture", prodFuture);

                Future consFuture = consumerPool.submit(new ConsumerThread(curIndex, 0, publicBoxQueue, productGate, userAccountService, startUpdateTimeStr, endUpdateTimeStr, null));
                futureMap.put(curIndex + "_consFuture", consFuture);


            } catch (Exception e) {
                log.error("productPool execute 异常 " , e);
            }



        }


        // TODO 异常 的话怎么处理，怎么记录日志信息

        log.info("生产者 任务添加完毕");

        try {
            productGate.await();
            log.info("生产者 任务全部执行完成！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if(publicBoxQueue.size() == 0){
            log.info("缓冲池货物已被全部取走！");
        }
        //consumerPool.shutdown();

/*

        productPool.shutdown();
        consumerPool.shutdown();
        try {
            productPool.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("productPool awaitTermination 异常 " , e);
        }
        try {
            consumerPool.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("consumerPool awaitTermination 异常 " , e);
        }
*/

        log.info("end");

/*

        //等待全部执行完毕
        for(int i = 0;  i < thisIndex; i++){
            Future prodFuture = futureMap.get(i + "_prodFuture");
            Future consFuture = futureMap.get(i + "_consFuture");
            while(true){
                if(prodFuture.isDone() && !prodFuture.isCancelled()
                        && consFuture.isDone()&& !consFuture.isCancelled()
                        ){
                    break;
                }else {
                    if(!prodFuture.isDone()){
                        log.info("i={}_prodFuture 未完成等待1秒", i);
                    }
                    if(!consFuture.isDone()){
                        log.info("i={}_consFuture 未完成等待1秒", i);
                    }
                    log.info("productPool,线程池状态={}", productPool.toString());
                    log.info("consumerPool,线程池状态={}", consumerPool.toString());
                    try {
                        Thread.sleep(1000);//每次轮询休息1毫秒（CPU纳秒级），避免CPU高速轮循耗空CPU-
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
*/



        //执行结果总览
        futureMap.forEach((k, v) -> {

            try {
                log.info("key={}, v={}", k, v.get());
            } catch (InterruptedException e) {
                log.error("futureMap get() 异常 " , e);
            } catch (ExecutionException e) {
                log.error("futureMap .get() 异常 " , e);
            }

        } );


        long endTime = System.currentTimeMillis();
        log.info("总共 耗时："+(endTime-startTime)/1000.0+"s");

    }


}
