package com.pagoda.account.migrate.dto.account;

import lombok.Data;

/**
 * 账户类型数据和用户数据
 */
@Data
public class UserAndAccountDto {
    /** 用户账户ID, 自维护序列 */
    private Long accountId;
    /** 用户ID */
    private Integer userId;
    /**
     * 第三方账户id
     */
    private String memberNo;

    /** 账户类型, 由账户类别和账户类型代码拼接 */
    private String accountType;
    /** 账号 */
    private Integer accountNo;

}
