package com.pagoda.account.migrate.producerconsumer.old.member;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.service.account.UserService;
import com.pagoda.account.migrate.service.member.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
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
public class Member4ProdConsManager {

    private IMemberService memberService;

    private UserService userService;

    public Member4ProdConsManager(IMemberService memberService, UserService userService) {
        this.memberService = memberService;
        this.userService = userService;

    }


    public void  run() {
        long startTime = System.currentTimeMillis();

        //由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序
        final RejectedExecutionHandler handler1 = (r, executor) -> {
            log.info("太忙了,ProductThread "+ executor.toString());
            if (!executor.isShutdown()) {
               // log.info("ProductThread Handler 尝试 重试 TaskNum " + ((ProductThread) r).getTaskNum());
                r.run();
            }
        };

        final RejectedExecutionHandler handler2 = (r, executor) -> {
            log.info("太忙了,UserConsumer "+ executor.toString());
            // msgQueue.offer(((DBThread) r).getMsg());
            if (!executor.isShutdown()) {
                //log.info("UserConsumer Handler 尝试 重试 TaskNum " + ((UserConsumer) r).getTaskNum());
                r.run();
            }
        };

        BlockingQueue publicBoxQueue= new LinkedBlockingQueue(5);   //定义了一个大小为5的盒子
        //为多生产者和多消费者分别开创的线程池
        ThreadPoolExecutor productPool =
                new ThreadPoolExecutor(2, 4, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2), handler1);//new ThreadPoolExecutor.CallerRunsPolicy()
        ThreadPoolExecutor consumerPool =
                new ThreadPoolExecutor(4, 6, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(3), handler2);

        log.info("start");


        Map<String, Object> minAndMaxAndCountModifyTimeMap = memberService.findMinAndMaxAndCountModifyTime();

        String minUpdateTimeStr = minAndMaxAndCountModifyTimeMap.get("minUpdateTime").toString();
        String maxUpdateTimeStr = minAndMaxAndCountModifyTimeMap.get("maxUpdateTime").toString();
        Long memberCount = new BigInteger(minAndMaxAndCountModifyTimeMap.get("memberCount").toString()).longValue();
        log.info("minAndMaxAndCountMemberIdMap={}", minAndMaxAndCountModifyTimeMap.toString());


        Date minUpdateTimeD = DateUtil.parse(minUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        Date maxUpdateTimeD = DateUtil.parse(maxUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        //毫秒值
        long minUpdateTime = minUpdateTimeD.getTime();
        long maxUpdateTime = maxUpdateTimeD.getTime();

        long betweenMs = DateUtil.betweenMs(minUpdateTimeD, maxUpdateTimeD);
        log.info("betweenMs={}", betweenMs);
        float width = betweenMs / memberCount;
        log.info("width={}", width);

        //TODO
        long batchCount = 5000 * 10;// * 60 * 60 * 24;
        long batchWidth = (long) Math.ceil(batchCount * width);
        log.info("batchWidth={}", batchWidth);

        long betweenDay = DateUtil.betweenDay(minUpdateTimeD, maxUpdateTimeD, true);
        log.info("betweenDay={}", betweenDay);

        Map<String, Future> futureMap = new TreeMap<>();

        //TODO
        String beginUpdateTimeStr = "20161101000000";
        String terminalUpdateTimeStr = "20170101000000";//"20160201000000" 20161101000000-20170101000000
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

        if(beginUpdateTimeMs > minUpdateTime){
            minUpdateTime = beginUpdateTimeMs;
        }
        if(terminalUpdateTimeMs < maxUpdateTime){
            maxUpdateTime = terminalUpdateTimeMs;
        }


        long thisIndex = 0L;
        for(long curTime = minUpdateTime, curIndex= 0L; curTime < maxUpdateTime; curTime += batchWidth, curIndex++){
            long startUpdateTime = curTime;
            long endUpdateTime = curTime + batchWidth;
            thisIndex = curIndex;

            String startUpdateTimeStr = DateUtil.format(DateUtil.date(startUpdateTime), DatePattern.PURE_DATETIME_PATTERN);
            String endUpdateTimeStr = DateUtil.format(DateUtil.date(endUpdateTime), DatePattern.PURE_DATETIME_PATTERN);

            log.info("【startUpdateTime={}, endUpdateTime={}】", startUpdateTimeStr, endUpdateTimeStr);
/*            if(endUpdateTime > terminalUpdateTimeMs || endUpdateTime >= maxUpdateTime){
                log.info("达到 终止条件值=terminalUpdateTimeMs={}, 【startUpdateTime={}, endUpdateTime={}】 " , terminalUpdateTimeMs, startUpdateTime, endUpdateTime);
                break;
            }*/

            try {

                Future prodFuture = productPool.submit(new ProductThread(curIndex, publicBoxQueue, memberService, startUpdateTimeStr, endUpdateTimeStr));
                Future consFuture = consumerPool.submit(new ConsumerThread(curIndex, publicBoxQueue, userService, startUpdateTimeStr, endUpdateTimeStr));
                futureMap.put(curIndex + "_prodFuture", prodFuture);
                futureMap.put(curIndex + "_consFuture", consFuture);
            } catch (Exception e) {
                log.error("productPool execute 异常 " , e);
            }

        }


        // TODO 异常 的话怎么处理，怎么记录日志信息

        log.info("生产者 任务添加完毕");

        productPool.shutdown();
        consumerPool.shutdown();
        try {
            productPool.awaitTermination(200, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("awaitTermination 异常 " , e);
        }
        try {
            consumerPool.awaitTermination(200, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("awaitTermination 异常 " , e);
        }

        log.info("end");


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

                    log.info("i={}_prodFuture 未完成等待1秒", i);

                    try {
                        Thread.sleep(1000);//每次轮询休息1毫秒（CPU纳秒级），避免CPU高速轮循耗空CPU-
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        }


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
