package com.pagoda.account.migrate.dataobject.member;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * member_account
 * 
 * @author wfg
 * @version 1.0.0 2018-09-07
 */
@Data
@Entity
@Table(name = "member_account")
public class MemberAccount implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = 685829032688844055L;

    /** 账户ID，来自自维护的序列 */
    @Id
    @Column(name = "memberAccountId")
    private Integer memberAccountId;

    /** 会员ID */
    @Column(name = "memberId")
    private Integer memberId;

    /** memberNum */
    @Column(name = "memberNum")
    private String memberNum;

    /** 主账户余额 单位分 */
    @Column(name = "mainAccountBalance")
    private Integer mainAccountBalance;

    /** 主账户历史总额 单位分 */
    @Column(name = "mainAccountTotal")
    private Integer mainAccountTotal;

    /** 费率 */
    @Column(name = "feeRate")
    private Integer feeRate;

    /** 零钱包余额 单位分 */
    @Column(name = "changeAccountBalance")
    private Integer changeAccountBalance;

    /** 积分账户余额 */
    @Column(name = "integralAccountBalance")
    private Integer integralAccountBalance;

    /** 积分账户总额 */
    @Column(name = "integralAccountTotal")
    private Integer integralAccountTotal;

    /** 果币账户余额 */
    @Column(name = "fruitCoinAccountBalance")
    private Integer fruitCoinAccountBalance;

    /** 优惠券账户余额 */
    @Column(name = "couponAccountBalance")
    private Integer couponAccountBalance;

    /** 可开发票账户余额 */
    @Column(name = "invoiceAccountBalance")
    private Integer invoiceAccountBalance;

    /** 修改时间 */
    @Column(name = "modifyTime")
    private String modifyTime;

}