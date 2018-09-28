package com.pagoda.account.migrate.dataobject.member;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zfr
 * 账户记录
 */
@Data
@Entity
@Table(name="member_wallet_record")
public class MemberWalletRecord implements Serializable {


    private static final long serialVersionUID = 7124016709688266770L;
    /**
     * 主键id
     */
    @Id
    private Integer recordId;
    /**
     * 修改状态
     */
    private String modifyType;
    /**
     * 会员id
     */
    private Integer memberId;
    /**
     * 会员编码
     */
    private String memberNum;
    /**
     * 手机号
     */
    private String phoneNumber;

  /*  private Integer mainAccountTotalBeforeModify;

    private Integer mainAccountTotalAfterModify;*/

    /**
     * 变动前主余额
     */
    private Integer mainMoneyBeforeModify;
    /**
     * 变动后主余额
     */
    private Integer mainMoneyAfterModify;
    /**
     * 变动前费率
     */
    private Integer feeRateBeforeModify;
    /**
     * 变动后费率
     */
    private Integer feeRateAfterModify;
    /**
     * 更新时间
     */
    private String updateTime;
    /**
     * 变更渠道 电商：A，微信：W，POS系统：P，一体化会员:Y,采配销系统：E，一体化短信平台：F，客服平台：G，三方电商：O
     */
    private String updateChannel;
    /**
     * 渠道取值
     */
    private String channelValue;
    /**
     * 操作人
     */
    private String operator;
    /**
     * dealSerialNum
     */
    private String dealSerialNum;
    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 交易备注
     */
    private String dealNote;
    /**
     * 变更时间
     */
    private String modifyTime;

}
