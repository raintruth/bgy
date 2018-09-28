package com.pagoda.account.migrate.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsPoolConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author zfr
 * springboot管理线程池类
 */
@Slf4j
@Configuration
public class TreadPoolConfig {
    /**
     * 消费队列线程-消费记录线程
     * @return
     */
    @Bean(value = "consumerUserAccountRecordPool")
    public ExecutorService buildConsumerQueueThreadPool(){
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("consumer-UserAccountRecord-queue-thread-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(200, 200, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(200),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
        return pool ;
    }

    /**
     * 请求用户和账户信息线程
     * @return
     */
    @Bean(value = "requestUserAndAccountPool")
    public ExecutorService buildRequestQueueThreadPool(){
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("consumer-UserAndAccount-queue-thread-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(50,  Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
        return pool ;
    }

    /**
     * 请求会员系统的记录条数
     * @return
     */
    @Bean(value = "requestMemberRecordPool")
    public ExecutorService buildRequestMemberRecordPool(){
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("consumer-requestMemberRecordPool-queue-thread-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(30),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
        return pool ;
    }

    /**
     * 请求账户系统的用户记录
     * @return
     */
    @Bean(value = "requestUserAccountRecordPool")
    public ExecutorService buildRequestUserAccountRecordPool(){
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("consumer-requestUserAccountRecordPool-queue-thread-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(50, 50, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(50),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
        return pool ;
    }

    /**
       =======================================user  useraccout===============================================
     */

    /**
     * user 生产者线程池
     * @return
     */
    @Bean(value = "userProducerPool")
    public ExecutorService buildUserProducerPool(){

        //TODO 起个好看的名字
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("user-producer-pool-queue-thread-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(ProdConsPoolConstants.Producer.CORE_POOL_SIZE, ProdConsPoolConstants.Producer.MAXIMUM_POOL_SIZE, ProdConsPoolConstants.Producer.KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue(ProdConsPoolConstants.Producer.BLOCKING_QUEUE_SIZE), namedThreadFactory, producerHandler);//, namedThreadFactory
        return pool ;
    }
    //由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序
    final RejectedExecutionHandler producerHandler = (r, executor) -> {
        if (!executor.isShutdown()) {
            //FIXME java.util.concurrent.FutureTask cannot be cast to com.pagoda.account.migrate.producerconsumer.base.impl.Worker
            //log.info("太忙了,线程队列池满 {}  尝试 重试加入任务ing+++, pool={} ", ((Worker) r).getIdentity(), executor.toString());
            log.info("太忙了,{}线程队列池满  ", "producerHandler");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            r.run();
        }
    };


    /**
     * user 消费者线程池
     * @return
     */
    @Bean(value = "userConsumerPool")
    public ExecutorService buildUserConsumerPool(){
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("user-consumer-pool-queue-thread-%d").build();

        ExecutorService pool =
                new ThreadPoolExecutor(ProdConsPoolConstants.Consumer.CORE_POOL_SIZE, ProdConsPoolConstants.Consumer.MAXIMUM_POOL_SIZE, ProdConsPoolConstants.Consumer.KEEP_ALIVE_TIME,
                        TimeUnit.MILLISECONDS, new ArrayBlockingQueue(ProdConsPoolConstants.Consumer.BLOCKING_QUEUE_SIZE), namedThreadFactory, consumerHandler);//, namedThreadFactory

        return pool;
    }

    final RejectedExecutionHandler consumerHandler = (r, executor) -> {
        if (!executor.isShutdown()) {
            //log.info("太忙了,线程队列池满 {}  尝试 重试加入任务ing+++, pool={} ", ((Worker) r).getIdentity(), executor.toString());
            log.info("太忙了,{}线程队列池满  ", "consumerHandler");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            r.run();
        }
    };



}
