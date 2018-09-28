package com.pagoda.account.migrate.producerconsumer.base.constant;

/**
 * @author wfg
 * 常量
 */
public class ProdConsWorkerConstants {

    /**
     *============================================公共常量=====================================
     */
    public interface Common {

        /**
         * 货物 list标识
         */
        String GOODS_MAP_LIST_KEY = "list";
        /**
         * 货物 map标识
         */
        String GOODS_MAP_MAP_KEY = "map";
        /**
         * 异常是否重试
         */
        boolean IS_EXCEPTION_RE_TRY= true;

        /**
         * 重试最大次数
         */
        int MAX_RETRY_TIMES = 3;

        /**
         *  任务异常 联系邮箱
         */
        String CONTACT_EMAIL = "610317497@qq.com";


        /**
         * 迁移表数据的 每批次分页数量-memberId
         */
        Long MEMBER_ID_COUNT = 5000L;
        /**
         * 迁移表数据的 每批次分页数量
         */
        Long BATCH_COUNT = 5000L ;
        /**
         * 迁移表数据的  计划总数量
         */
        Long TOTAL_COUNT = 115000L;

        /**
         * 迁移表数据的 起始时间 包含
         */
        String BEGIN_UPDATE_TIME_STR = "20161101000000";
        /**
         * 迁移表数据的 终止时间 不包含
         */
        String TERMINAL_UPDATE_TIME_STR = "20161102000000";

        /**
         * 迁移表数据的 起始member 包含
         */
        Long START_MEMBER_ID = 0L;

    }



    /**
     *============================================user =====================================
     */
    public static final class User implements Common {

        public static final String GOODS_MAP_LIST_KEY = "userList";

        public static final String BEGIN_UPDATE_TIME_STR = "20161101000000";
        //20161103000000 数据量大 查不出
        public static final String TERMINAL_UPDATE_TIME_STR = "20161102000000";
        //115002L
        public static final Long TOTAL_COUNT = 100000L;//1000000L
        //
        public static final Long START_MEMBER_ID = 0L;

    }



    /**
     *============================================UserAccount 表=====================================
     */
    public static final class UserAccount implements Common {

        public static final String GOODS_MAP_LIST_KEY = "userAccountList";

        /**
         * 迁移表数据的 起始时间 包含
         */
        public static final String BEGIN_UPDATE_TIME_STR = "20161101000000";
        /**
         * 迁移表数据的 终止时间 不包含
         */
        public static final String TERMINAL_UPDATE_TIME_STR = "20161102000000";

        /**
         * 货物memberId list标识
         */
        public static final  String GOODS_MAP_MEMBER_ID_LIST_KEY = "memberIdList";
        /**
         * 货物 map标识
         */
        public static final String GOODS_MAP_MEMBER_ID_LINK_USER_ID_MAP_KEY = "memberIdLinkUserIdMap";




    }





}
