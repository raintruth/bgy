package com.pagoda.account.migrate.dto.member;

import lombok.Data;

/**
 * member
 * 
 * @author wfg
 * @version 1.0.0 2018-09-07
 */
@Data
public class MemberDTO implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = 3582814140016748293L;

    /**
     * 会员ID， 来自自维护的序列
     */
    private Integer memberId;

    /**
     * 会员类型 P: 手机会员 C: 实体卡会员
     */
    private String memberType;

    /**
     * 会员账号
     */
    private String memberNum;

    /**
     * 状态  N：正常 L：挂失 D：注销 S：锁定 U永久锁定
     */
    private String status;

    /**
     * 等级ID
     */
    private Integer levelId;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员性别 M：男 F：女
     */
    private String memberSex;

    /**
     * 身份证
     */
    private String identityCardNum;

    /**
     * 手机号码
     */
    private String phoneNum;

    /**
     * 生日 yyyyMMdd
     */
    private String memberBirthday;

    /**
     * 会员标签
     */
    private String memberLabel;

    /**
     * 会员职务
     */
    private String memberJob;

    /**
     * 收入
     */
    private Integer incoming;

    /**
     * 会员图像
     */
    private String memberIcon;

    /**
     * 会员邮箱
     */
    private String memberEmail;

    /**
     * 推荐人账号
     */
    private String referrerNum;

    /**
     * 注册渠道  电商：A，微信：W，POS系统：P，一体化会员:Y,采配销系统：E，一体化短信平台：F，客服平台：G，三方电商：O
     */
    private String registerChannel;

    /**
     * 渠道取值
     */
    private String channelValue;

    /**
     * 注册时间 yyyyMMddHHmmss
     */
    private String registerTime;

    /**
     * 修改时间 yyyyMMddHHmmss
     */
    private String modifyTime;

    /**
     * 会员备注
     */
    private String memberNote;

    /**
     * 会员消费密码
     */
    private String consumePasswd;

    /**
     * regFlag
     */
    private Integer regFlag;

    /**
     * 首次APP登录时间
     */
    private String firstLoginTime;

    /**
     * 首次APP登录门店
     */
    private String firstLoginStore;

    /**
     * belongBrand
     */
    private Integer belongBrand;

}