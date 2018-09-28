package com.pagoda.account.migrate.producerconsumer.base;

import com.pagoda.account.migrate.producerconsumer.base.normal.ResultCode;

/**
 * 消费者
 * @author  wfg
 */
public interface IWorker {
    /**
     * 获取 该工作者的 工作状态
     * @param
     * @return ResultCode
     */
    int getStatus();
    /**
     * 工作
     * @return ResultCode 执行结果
     */
    ResultCode working();
}
