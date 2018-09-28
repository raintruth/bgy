package com.pagoda.account.migrate.producerconsumer.base;

import com.pagoda.account.migrate.producerconsumer.base.normal.Result;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author wfg
 * @since 山竹
 */
public interface IProdConsManager {

    /**
     * 创建线程池
     * @return
     */
    ExecutorService createProducerPool();

    /**
     *创建线程池
     * @return
     */
    ExecutorService createConsumerPool();

    /**
     *创建仓库
     * @return
     */
    Storage<Map<String, Object>> createStorage();

    /**
     * 启动初始化
     */
    void init();
    /**
     * 执行主逻辑
     */
    void run();

    /**
     * 获得执行结果
     * @return
     */
    Map<String, Future<Result>> getExecResult();

    /**
     * 启动初始化
     */
    void start();
}
