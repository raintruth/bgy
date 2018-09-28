package com.pagoda.account.migrate.dataobject.member;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * member
 * 
 * @author wfg
 * @version 1.0.0 2018-09-07
 */
@Data
@Entity
@Table(name = "member")
public class Member implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = 4660933311737867246L;

    /** 会员ID， 来自自维护的序列 */
    @Id
    @Column(name = "memberId")
    private Integer memberId;

    /** 会员类型 P: 手机会员 C: 实体卡会员 */
    @Column(name = "memberType")
    private String memberType;

    /** 会员账号 */
    @Column(name = "memberNum")
    private String memberNum;

    /** 状态  N：正常 L：挂失 D：注销 S：锁定 U永久锁定 */
    @Column(name = "status")
    private String status;

    /** 等级ID */
    @Column(name = "levelId")
    private Integer levelId;

    /** 会员名称 */
    @Column(name = "memberName")
    private String memberName;

    /** 会员性别 M：男 F：女 */
    @Column(name = "memberSex")
    private String memberSex;

    /** 身份证 */
    @Column(name = "identityCardNum")
    private String identityCardNum;

    /** 手机号码 */
    @Column(name = "phoneNum")
    private String phoneNum;

    /** 生日 yyyyMMdd */
    @Column(name = "memberBirthday")
    private String memberBirthday;

    /** 会员标签 */
    @Column(name = "memberLabel")
    private String memberLabel;

    /** 会员职务 */
    @Column(name = "memberJob")
    private String memberJob;

    /** 收入 */
    @Column(name = "incoming")
    private Integer incoming;

    /** 会员图像 */
    @Column(name = "memberIcon")
    private String memberIcon;

    /** 会员邮箱 */
    @Column(name = "memberEmail")
    private String memberEmail;

    /** 推荐人账号 */
    @Column(name = "referrerNum")
    private String referrerNum;

    /** 注册渠道  电商：A，微信：W，POS系统：P，一体化会员:Y,采配销系统：E，一体化短信平台：F，客服平台：G，三方电商：O */
    @Column(name = "registerChannel")
    private String registerChannel;

    /** 渠道取值 */
    @Column(name = "channelValue")
    private String channelValue;

    /** 注册时间 yyyyMMddHHmmss */
    @Column(name = "registerTime")
    private String registerTime;

    /** 修改时间 yyyyMMddHHmmss */
    @Column(name = "modifyTime")
    private String modifyTime;

    /** 会员备注 */
    @Column(name = "memberNote")
    private String memberNote;

    /** 会员消费密码 */
    @Column(name = "consumePasswd")
    private String consumePasswd;

    /** regFlag */
    @Column(name = "regFlag")
    private Integer regFlag;

    /** 首次APP登录时间 */
    @Column(name = "firstLoginTime")
    private String firstLoginTime;

    /** 首次APP登录门店 */
    @Column(name = "firstLoginStore")
    private String firstLoginStore;

    /** belongBrand */
    @Column(name = "belongBrand")
    private Integer belongBrand;


}