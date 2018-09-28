package com.pagoda.account.migrate.repository.base;


import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 *
 * @author  wfg
 * Version  1.1.0
 * @since   2018教师节 下午2:28:58
 */
public interface BatchDao<T>  {

    /**
     * jpa 批量查询
     * @param obj
     * @return list
     */
    default List<T> batchQuery(T obj){
        System.out.println("batchQuery obj 空方法.....");
        return null;
    }

    default List<T> batchQuery(Map<?, ?> map){
        System.out.println("batchQuery map 空方法.....");
        return null;
    }

    default List<T> batchQuery(Map<?, ?> map, Pageable pageable){
        System.out.println("batchQuery map 空方法.....");
        return null;
    }

    /**
     * jpa 批量插入
     * @param list
     */
    default void batchInsert(List<T> list){
        System.out.println("batchInsert 空方法.....");
    }

    /**
     * jpa 批量更新
     * @param list
     */
    default void batchUpdate(List<T> list){
        System.out.println("batchUpdate 空方法.....");
    }

    /**
     * 原生sql 批量更新数据 比jpa快3倍
     * @param list
     */
    default int batchInsertIfExistUpdate(List<T> list){
        System.out.println("batchInsertIfExistUpdate 空方法.....");
        return 0;
    }
}
