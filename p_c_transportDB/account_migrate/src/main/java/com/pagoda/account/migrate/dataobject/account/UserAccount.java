package com.pagoda.account.migrate.dataobject.account;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户账户表(user_account)
 * 
 * @author wfg
 * @version 1.0.0 2018-09-13
 */
@Data
@Entity
@Table(name = "user_account")
public class UserAccount implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = -5726018645973908627L;

    /** 用户账户ID, 自维护序列 */
    @Id
    @Column(name = "account_id")
    private Long accountId;

    /** 用户ID */
    @Column(name = "user_id")
    private Long userId;

    /** 用户银行卡ID */
    @Column(name = "bankcard_id")
    private Long bankcardId;

    /** 账户类型, 由账户类别和账户类型代码拼接 */
    @Column(name = "account_type")
    private String accountType;

    /** 账号 */
    @Column(name = "account_no")
    private Long accountNo;

    /** 外部托管账号 */
    @Column(name = "deposit_no")
    private String depositNo;

    /** 账户余额 */
    @Column(name = "balance")
    private Long balance;

    /** 可用余额 */
    @Column(name = "available")
    private Long available;

    /** 费率 */
    @Column(name = "fee_rate")
    private Integer feeRate;

    /** 账户状态: N-正常, L-锁定, D-禁用, C-注销 */
    @Column(name = "status")
    private String status;

    /** 支付优先级, 值越大优先级越高 */
    @Column(name = "pay_rank")
    private Integer payRank;

    /** 单笔限额 */
    @Column(name = "order_limit")
    private Long orderLimit;

    /** 单日限额 */
    @Column(name = "daily_limit")
    private Long dailyLimit;

    /** 创建时间 */
    @Column(name = "create_time")
    private Date createTime;

    /** 更新时间 */
    @Column(name = "update_time")
    private Date updateTime;


}