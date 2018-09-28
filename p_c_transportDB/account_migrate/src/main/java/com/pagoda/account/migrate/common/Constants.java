package com.pagoda.account.migrate.common;

/**
 * @author zfr
 * 常量池
 */
public interface Constants {
    /**
     * 导入数据的分页大小
     */
    Integer MEMBER_WALLET_RECORD_SIZE =500;
    /**
     * 根据时间导入数据-时间
     */
    String DATE_TIME_IMPORT="201809141715";
    /**
     * kafka返回参数-数据
     */
    String KAFKA_RESULT_MSG="msg";
    /**
     * kafka返回参数-id
     */
    String KAFKA_RESULT_ID="id";
    /**
     * 会员账号-id名称
     */
    String MEMBER_MUMBERNO="mumberNo";
    /**
     * 会员数据导入类型
     */
    String IMPORT_RECORD_TYPE_A="A";
    /**
     * 会员数据导入类型
     */
    String IMPORT_RECORD_TYPE_B="B";
    /**
     * 数据校验-大小
     */
    Integer CHECK_SIZE_USERACCOUNT= 1000;
    /**
     * 账户类型-钱包账户
     */
    String ACCOUNT_TYPE_CM="CM";
    /**
     * 账户返回结果-userId
     */
    String MEMBER_RESULT_ACCOUNT_USER_ID="userId";

    String MEMBER_RESULT_ACCOUNT_ACCOUNT_ID="accountId";

}
