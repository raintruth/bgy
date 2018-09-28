package com.pagoda.account.migrate.dto.member;

import lombok.Data;

/**
 * member_account
 * 
 * @author wfg
 * @version 1.0.0 2018-09-07
 */
@Data
public class MemberAccountDTO implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = 685829032688844055L;

    /** 账户ID，来自自维护的序列 */
    private Integer memberAccountId;

    /** 会员ID */
    private Integer memberId;

    /** memberNum */
    private String memberNum;

    /** 主账户余额 单位分 */
    private Integer mainAccountBalance;

    /** 主账户历史总额 单位分 */
    private Integer mainAccountTotal;

    /** 费率 */
    private Integer feeRate;

    /** 零钱包余额 单位分 */
    private Integer changeAccountBalance;

    /** 积分账户余额 */
    private Integer integralAccountBalance;

    /** 积分账户总额 */
    private Integer integralAccountTotal;

    /** 果币账户余额 */
    private Integer fruitCoinAccountBalance;

    /** 优惠券账户余额 */
    private Integer couponAccountBalance;

    /** 可开发票账户余额 */
    private Integer invoiceAccountBalance;

    /** 修改时间 */
    private String modifyTime;


}