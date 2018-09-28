package com.pagoda.account.migrate.dto.account;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lixh
 * @Date: 2018/9/6 09:22
 * @Description:
 */
@Data
public class UserAccountDTO implements Serializable {

    private static final long serialVersionUID = -8031857397778429522L;

    /** 用户账户ID, 自维护序列 */
    private Long accountId;
    /** 用户ID */
    private Integer userId;
    /** 用户银行卡ID */
    private Integer bankcardId;
    /** 账户类型, 由账户类别和账户类型代码拼接 */
    private String accountType;
    /** 账号 */
    private Integer accountNo;
    /** 外部托管账号 */
    private String depositNo;
    /** 账户余额 */
    private Integer balance;
    /** 可用余额 */
    private Integer available;
    /** 费率 */
    private Integer feeRate;
    /** 账户状态: N-正常, L-锁定, D-禁用, C-注销 */
    private String status;
    /** 支付优先级, 值越大优先级越高 */
    private Integer payRank;
    /** 单笔限额 */
    private Integer order_limit;
    /** 单日限额 */
    private Integer dailyLimit;

    private Date create_time;
    private Date updateTime;
}
