package com.pagoda.account.migrate.producerconsumer.base.constant;

/**
 * @author wfg
 * 常量
 */
public class ProdConsPoolConstants {

    /**
     *============================================公共常量=====================================
     */
    public interface Common {

        /**
         * 核心线程数
         */
        int CORE_POOL_SIZE = 8;

        /**
         * 最大线程数
         */
        int MAXIMUM_POOL_SIZE = 10;

        /**
         * 生存时间 秒
         */
        long KEEP_ALIVE_TIME = 300;

        /**
         * 线程队列容量
         */
        int BLOCKING_QUEUE_SIZE = 2;
    }

    /**
     *============================================UserAccount 表=====================================
     */
    public interface Producer extends Common {


        /**
         * 核心线程数
         */
        int CORE_POOL_SIZE = 3;

        /**
         * 最大线程数
         */
        int MAXIMUM_POOL_SIZE = 3;



    }


    /**
     *============================================user =====================================
     */
    public interface Consumer extends Common {

        /**
         * 核心线程数
         */
        int CORE_POOL_SIZE = 3;

        /**
         * 最大线程数
         */
        int MAXIMUM_POOL_SIZE = 3;

        int BLOCKING_QUEUE_SIZE = 3;

    }








}
