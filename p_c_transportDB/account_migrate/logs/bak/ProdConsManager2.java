package com.pagoda.account.migrate.producerconsumer.old.memberaccount;

import com.pagoda.account.migrate.service.account.UserAccountService;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j
public class ProdConsManager2 {

    private IMemberAccountService memberAccountService;

    private UserAccountService userAccountService;

    public ProdConsManager2(IMemberAccountService memberAccountService, UserAccountService userAccountService) {
        this.memberAccountService = memberAccountService;
        this.userAccountService = userAccountService;

    }


    public void  run() {
        long startTime = System.currentTimeMillis();
/*
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

        BlockingQueue publicBoxQueue= new ArrayBlockingQueue(5);   //定义了一个大小为5的盒子
        //为多生产者和多消费者分别开创的线程池
        ThreadPoolExecutor productPool =
                new ThreadPoolExecutor(2, 20, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2),handler1);//new ThreadPoolExecutor.CallerRunsPolicy()
        ThreadPoolExecutor consumerPool =
                new ThreadPoolExecutor(4, 20, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(3), handler2);

        log.info("start");

        Map<String, Object> minAndMaxAndCountMemberIdMap = memberAccountService.findMinAndMaxAndCountMemberId(0);

       // Integer minMemberId = new BigInteger(minAndMaxAndCountMemberIdMap.get("minMemberId").toString()).intValue();
        Integer maxMemberId = new BigInteger(minAndMaxAndCountMemberIdMap.get("maxMemberId").toString()).intValue();

        Integer minMemberId =  10000;

        Integer validMemberCount = new BigInteger(minAndMaxAndCountMemberIdMap.get("validMemberCount").toString()).intValue();
        Map<String, Future> futureMap = new LinkedHashMap<>();

        int batchCount = 5000;
        int terminalMemberId = 61688044;

        int countNum = validMemberCount/batchCount + 1;
        int startMemberId = minMemberId;
        int curIndex = 0;
        for(int i = 0;  i < countNum; i++){
            curIndex = i;
            int endMemberId = startMemberId + batchCount;

            log.info("【startMemberId={}, endMemberId={}】",startMemberId, endMemberId);
            if(endMemberId > terminalMemberId || endMemberId >= maxMemberId){
                log.info("达到 终止条件值=  " + endMemberId);
                break;
            }

            try {

                Future prodFuture = productPool.submit(new ProductThread(i, publicBoxQueue, memberAccountService, startMemberId, endMemberId));
                Future consFuture = consumerPool.submit(new UserConsumer(i, publicBoxQueue, userAccountService, startMemberId, endMemberId));
                futureMap.put(i + "_prodFuture", prodFuture);
                futureMap.put(i + "_consFuture", consFuture);
            } catch (Exception e) {
                log.error("productPool execute 异常 " , e);
            }

            startMemberId = endMemberId;
        }*/

        // TODO 异常 的话怎么处理，怎么记录日志信息

        log.info("生产者 任务添加完毕");
/*

        productPool.shutdown();
        consumerPool.shutdown();
        try {
            productPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            log.error("awaitTermination 异常 " , e);
        }
        try {
            consumerPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            log.error("awaitTermination 异常 " , e);
        }


        log.info("end");

        //等待全部执行完毕
        for(int i = 0;  i < curIndex; i++){
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
*/
    }


}
