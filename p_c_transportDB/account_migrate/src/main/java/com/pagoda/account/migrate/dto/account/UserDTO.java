package com.pagoda.account.migrate.dto.account;

import lombok.Data;

import java.util.Date;

/**
 * @author Lixh
 * @Date: 2018/9/4 11:13
 * @Description:
 */
@Data
public class UserDTO {

    private Integer userId;
    private String memberNo;
    private String phoneNo;
    private String email;
    private String realName;
    private String identityCardNo;
    private String consumePassword;
    private String status;
    private String brandType;
    private Date createTime;
    private Date updateTime;

}
