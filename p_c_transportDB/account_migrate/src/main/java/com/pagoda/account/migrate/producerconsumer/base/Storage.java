package com.pagoda.account.migrate.producerconsumer.base;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * 数据仓库
 * @author  wfg
 * @param <T>
 */
public class Storage<T> {

    private RedisTemplate redisTemplate;


    //TODO 目前放在本地  以后放在 redis
    private BlockingQueue<T> queues = new LinkedBlockingQueue<>(10);


    private BlockingQueue<T> exeFailQueues = new LinkedBlockingQueue<>(30);



    public void setQueues(BlockingQueue<T> queues) {
        this.queues = queues;
    }
    public BlockingQueue<T> getQueues() {
        //TODO 目前放在本地  以后放在 redis
       // String obj = redisTemplate.opsForValue().get("queues").toString();
       // JSONUtil.toBean(obj, BlockingQueue.class)
        return queues;
    }

    public BlockingQueue<T> getExeFailQueues() {
        return exeFailQueues;
    }

    public void setExeFailQueues(BlockingQueue<T> exeFailQueues) {
        this.exeFailQueues = exeFailQueues;
    }


    /**
     * 生产
     *
     * @param obj
     *            产品
     * @throws InterruptedException
     */
    public void put(T obj) throws InterruptedException {
        getQueues().put(obj);

    }

    /**
     * 消费
     *
     * @return 产品
     * @throws InterruptedException
     */
    public T take() throws InterruptedException {
        return getQueues().take();
    }
    /**
     * 消费
     *
     * @return 产品
     * @throws InterruptedException
     */
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return getQueues().poll(timeout, unit);
    }

    /**
     * 是否为空
     *
     * @return isEmpty

     */
    public boolean isEmpty() {
        return getQueues().isEmpty();
    }


    public Storage setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        //redisTemplate.opsForValue().set("queues", JSONUtil.toJsonStr(queues));
        return this;
    }
}

