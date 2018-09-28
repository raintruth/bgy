package com.pagoda.account.migrate.producerconsumer.base;

import java.util.Map;

/**
 * 生产者
 * @author  wfg
 */
public interface IProducer {
    /**
     * 生产货物
     * @return GoodsMap
     */
    Map<String, Object>  productGoods();
}
