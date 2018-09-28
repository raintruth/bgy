package com.pagoda.account.migrate.dataobject.account;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户表(user)
 * 
 * @author wfg
 * @version 1.0.0 2018-09-13
 */
@Data
@Entity
@Table(name = "user")
public class User implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = -7577074523076092200L;

    /** 用户ID, 自维护序列 */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /** 会员ID, 唯一 */
    @Column(name = "member_id")
    private Long memberId;

    /** 手机号 */
    @Column(name = "phone_no")
    private String phoneNo;

    /** 邮箱, 唯一 */
    @Column(name = "email")
    private String email;

    /** 真实姓名 */
    @Column(name = "real_name")
    private String realName;

    /** 身份证号 */
    @Column(name = "identity_card_no")
    private String identityCardNo;

    /** 消费密码 */
    @Column(name = "consume_password")
    private String consumePassword;

    /** 用户状态: N-正常, L-锁定, D-禁用, C-注销 */
    @Column(name = "status")
    private String status;

    /** 所属品牌: P-百果园, G-果多美 */
    @Column(name = "brand_type")
    private String brandType;

    /** 创建时间 */
    @Column(name = "create_time")
    private Date createTime;

    /** 更新时间 */
    @Column(name = "update_time")
    private Date updateTime;

    /** memberNo */
    @Column(name = "member_no")
    private String memberNo;


}