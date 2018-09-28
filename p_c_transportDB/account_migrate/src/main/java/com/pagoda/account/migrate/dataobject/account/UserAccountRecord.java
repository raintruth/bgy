package com.pagoda.account.migrate.dataobject.account;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author zfr
 * 消费记录实体类
 */
@Data
@Entity
public class UserAccountRecord implements Serializable{

    private static final long serialVersionUID = -8667361920337402290L;

    /**
     * 记录id
     */
    @Id
    private Long recordId;
    /**
     * 账户id
     */
    private Integer accountId;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 账户类型
     */
    private String accountType;
    /**
     * 更改类型
     */
    private String modifyType;
    /**
     * 状态
     */
    private String status;
    /**
     * 变更金额
     */
    private Integer money;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 商户订单号
     */
    private String merchantOrderNo;
    /**
     * 源编号, 如为优惠券账户则为优惠券编号, 果币账户则为果币编号
     */
    private String originalCode;
    /**
     * 活动编号
     */
    private String activityCode;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 渠道值
     */
    private String channelValue;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 变动前主余额
     */
    private Integer balanceBeforeModify;
    /**
     * 变动后主余额
     */
    private Integer balanceAfterModify;

}
