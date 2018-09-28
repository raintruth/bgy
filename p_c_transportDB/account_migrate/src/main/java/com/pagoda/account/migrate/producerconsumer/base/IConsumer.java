package com.pagoda.account.migrate.producerconsumer.base;

import com.pagoda.account.migrate.producerconsumer.base.normal.ResultCode;

import java.util.Map;

/**
 * 消费者
 * @author  wfg
 */
public interface IConsumer {
    /**
     * 消费货物
     * @param goods
     * @return ResultCode
     */
    ResultCode consumeGoods(Map<String, Object> goods);
}
