package com.pagoda.account.migrate.producerconsumer.base.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NumberUtil;
import com.pagoda.account.migrate.producerconsumer.base.IProdConsManager;
import com.pagoda.account.migrate.producerconsumer.base.Storage;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.producerconsumer.base.normal.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author wfg
 * @since 山竹
 */
@Slf4j
public abstract class BaseProdConsManager implements IProdConsManager {

    private ExecutorService producerPool;
    private ExecutorService consumerPool;

    private CountDownLatch producerGate;
    private CountDownLatch consumerGate;

    private Storage<Map<String, Object>> storage;

    private RedisTemplate redisTemplate;


    private Long pageSize;
    private Map<String, Future<Result>> futureMap;

    private TimeInterval managerInterval;

    {
        this.managerInterval = new TimeInterval();
    }

    @Override
    public ExecutorService createProducerPool() {
        return producerPool;
    }

    @Override
    public ExecutorService createConsumerPool() {
        return consumerPool;
    }

    @Override
    public Storage<Map<String, Object>> createStorage() {
        Storage<Map<String, Object>> storage = new Storage().setRedisTemplate(redisTemplate);
        return storage;
    }


    @Override
    public void init() {
        log.info("======init pool start ===========");
        this.producerPool = createProducerPool();
        this.consumerPool = createConsumerPool();
        log.info("=======init pool finish =============");
        log.info("======init storage start ===========");
        this.storage = createStorage();
        log.info("=======init storage finish =============");


        log.info("=======getTaskPageSize start ============");
        this.pageSize = getTaskPageSize();
        log.info("===============getTaskPageSize finish ============");
        log.info("pageSize={}", this.pageSize);

        log.info("=======gate start ============");
        this.producerGate = new CountDownLatch(pageSize.intValue());
        this.consumerGate = new CountDownLatch(pageSize.intValue());
        log.info("===============gate finish ============");


    }


    @Override
    public void run() {

        this.futureMap = new TreeMap<>();

        for(int curIndex = 0; curIndex < pageSize; curIndex++){

            log.info("【第{}页, 共={}页，正在载入中。。。】", curIndex+1, pageSize);
            forecastRemianTimes();
            try {
                BaseProducer producer = buildProducer();
                producer.setTaskNum(curIndex).registerProdConsManager(this);

                BaseConsumer consumer = buildConsumer();
                consumer.setTaskNum(curIndex).registerProdConsManager(this);

                Future prodFuture = producerPool.submit(producer);
                futureMap.put(producer.getIdentity(), prodFuture);

                Future consFuture = consumerPool.submit(consumer);
                futureMap.put(consumer.getIdentity(), consFuture);
            } catch (Exception e) {
                log.error("producerPool execute 异常 " , e);
            }

        }

        log.info("=============所有生产者 任务添加完毕 =============");

        try {
            log.info("=============等待 所有生产者全部执行结束。。=============");
            producerGate.await();
            log.info("=============所有生产者 任务全部执行完成！。。=============");
        } catch (InterruptedException e) {
            log.error(" {}  发生中断异常, errorMsg={}", "productGate ");
            Thread.currentThread().interrupt();
        }

        if(storage.isEmpty()){
            log.info("仓库缓冲池货物 已全部为空 ！");
        }

        forecastRemianTimes();
        try {
            log.info("=============等待 所有消费者全部执行结束。。=============");
            consumerGate.await();
            log.info("=============所有消费者 任务全部执行完成！。。=============");
        } catch (InterruptedException e) {
            log.error(" {}  发生中断异常, errorMsg={}", "consumerGate ");
            Thread.currentThread().interrupt();
        }

    }

    private void checkAllWorkerIsDone() {
        //等待全部执行完毕
        for(int i = 0;  i < pageSize; i++){
            Future prodFuture = futureMap.get(BaseProducer.getIdentity(i));
            Future consFuture = futureMap.get(BaseConsumer.getIdentity(i));
            while(true){
                if(prodFuture.isDone() && !prodFuture.isCancelled()
                        && consFuture.isDone()&& !consFuture.isCancelled()
                        ){
                    break;
                }

                if(!prodFuture.isDone()){
                    log.info("{} 未完成等待5秒", BaseProducer.getIdentity(i));
                    log.info("producerPool,线程池状态={}", producerPool.toString());
                }
                if(!consFuture.isDone()){
                    log.info("{} 未完成等待5秒", BaseConsumer.getIdentity(i));
                    log.info("consumerPool,线程池状态={}", consumerPool.toString());
                }

                try {
                    //每次轮询休息1毫秒（CPU纳秒级），避免CPU高速轮循耗空CPU-
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    log.error(" {}  发生中断异常, errorMsg={}", "轮询各个线程是否完成方法。", e.toString());
                    Thread.currentThread().interrupt();
                }

            }

        }

    }


    @Override
    public Map<String, Future<Result>> getExecResult() {

        ArrayList<String> failWorkerNameList = CollUtil.newArrayList();

        //执行结果总览
        futureMap.forEach((k, v) -> {

            try {
                //JSONUtil.toJsonStr(v.get().getBeautyResult())
                Result result = v.get();
                if(!result.isSuccess()){
                    failWorkerNameList.add(k);
                }
                log.info("key={}, {}", k, result.getBeautyResult());

            } catch (InterruptedException e) {
                log.error("futureMap get() 异常 " , e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                log.error("futureMap .get() 异常 " , e);
            }

        } );

        log.info("总页数={}页, 分页生产每批次数量={}个", pageSize, ProdConsWorkerConstants.UserAccount.BATCH_COUNT);
        log.info("生产者个数={}个, 消费者个数={}个，失败总个数={}个,失败人员清单={}", pageSize, pageSize, failWorkerNameList.size(), failWorkerNameList);
        log.info("最后主仓库包裹剩余数量={}个, 失败后转入的备用仓库包裹剩余数量={}个", storage.getQueues().size(), storage.getExeFailQueues().size());

        return futureMap;
    }

    @Override
    public void start() {

        this.managerInterval.start();
        log.info("================================ ProdConsManager start =======================================");

        log.info("========================== init start ===================================");
        init();

        double initTimes = managerInterval.intervalRestart()/1000.0;
        log.info("ProdConsManager init 总共 耗时：{} s", initTimes);
        log.info("========================== init finish ===================================");

        log.info("========================== run start ===================================");
        run();

        double runTimes = managerInterval.intervalRestart()/1000.0;
        log.info("ProdConsManager run 总共 耗时：{} s", runTimes);
        log.info("========================== run finish ===================================");

        log.info("========================== checkAllWorkerIsDone start ===================================");
        checkAllWorkerIsDone();
        log.info("========================== checkAllWorkerIsDone finish ===================================");

        log.info("========================== getExecResult start ===================================");
        getExecResult();

        double resultTimes = managerInterval.intervalRestart()/1000.0;
        log.info("ProdConsManager getExecResult 总共 耗时：{} s", resultTimes);
        log.info("========================== getExecResult finish ===================================");

        BigDecimal alltimeMs = NumberUtil.round((initTimes + runTimes + resultTimes), 3);
        BigDecimal alltimeMin = NumberUtil.round(alltimeMs.doubleValue() * 1000/DateUnit.MINUTE.getMillis() , 3);
        log.info("ProdConsManager all 总共 耗时：{} s = {} min", alltimeMs, alltimeMin);
        log.info("================================ ProdConsManager all finish =======================================");

    }


    /**
     * 预测剩余时间
     */
    public void forecastRemianTimes() {
        double lostTimes = this.getManagerInterval().intervalMs()/1000.0;
        double prodRemianForecastTimes = this.getProducerGate().getCount() * lostTimes / (0.001 + this.getPageSize() - this.getProducerGate().getCount());
        double consRemianForecastTimes = this.getConsumerGate().getCount() * lostTimes / (0.001 + this.getPageSize() - this.getConsumerGate().getCount());

        log.info("生产者还有{}个排队执行中---消费者还有{}个排队执行中--预计剩余完成时间：生产者=({})秒, 消费者=({})秒", this.getProducerGate().getCount(), this.getConsumerGate().getCount(), NumberUtil.round(prodRemianForecastTimes, 3), NumberUtil.round(consRemianForecastTimes, 3));
    }


    /**
     * 子类 获得分页所需的总页数
     * @return 总页数
     */
    protected abstract Long getTaskPageSize();
    /**
     * 子类 构造生产者
     * @return BaseProducer
     */
    protected abstract BaseProducer buildProducer();

    /**
     * 子类 构造消费者
     * @return BaseConsumer
     */
    protected abstract BaseConsumer buildConsumer();




    public ExecutorService getProducerPool() {
        return producerPool;
    }

    public BaseProdConsManager setProducerPool(ExecutorService producerPool) {
        this.producerPool = producerPool;
        return this;
    }

    public ExecutorService getConsumerPool() {
        return consumerPool;
    }

    public BaseProdConsManager setConsumerPool(ExecutorService consumerPool) {
        this.consumerPool = consumerPool;
        return this;
    }


    public CountDownLatch getProducerGate() {
        return producerGate;
    }
    public BaseProdConsManager setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        return this;
    }


    public BaseProdConsManager setProducerGate(CountDownLatch producerGate) {
        this.producerGate = producerGate;
        return this;
    }

    public CountDownLatch getConsumerGate() {
        return consumerGate;
    }

    public BaseProdConsManager setConsumerGate(CountDownLatch consumerGate) {
        this.consumerGate = consumerGate;
        return this;
    }

    public Storage<Map<String, Object>> getStorage() {
        return storage;
    }

    public BaseProdConsManager setStorage(Storage<Map<String, Object>> storage) {
        this.storage = storage;
        return this;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public BaseProdConsManager setPageSize(Long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Map<String, Future<Result>> getFutureMap() {
        return futureMap;
    }

    public BaseProdConsManager setFutureMap(Map<String, Future<Result>> futureMap) {
        this.futureMap = futureMap;
        return this;
    }

    public TimeInterval getManagerInterval() {
        return managerInterval;
    }

    public BaseProdConsManager setManagerInterval(TimeInterval managerInterval) {
        this.managerInterval = managerInterval;
        return this;
    }



}
