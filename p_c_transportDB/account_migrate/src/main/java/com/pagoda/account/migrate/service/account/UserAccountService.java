package com.pagoda.account.migrate.service.account;


import com.pagoda.account.migrate.dataobject.account.UserAccount;
import com.pagoda.account.migrate.dataobject.member.MemberAccount;

import java.util.List;
import java.util.Map;

/**
 * @author Lixh
 * @Date: 2018/9/6 15:12
 * @Description:
 */
public interface UserAccountService {

    /**
     * 根据用户id查询用户
     * @param accountId
     * @return
     */
    UserAccount findUserAccountByAccountId(Long accountId);

    /**
     * 将 源MemberAccount 数据保存到新的UserAccount 表中
     * @param memberAccountList
     * @param memberIdLinkUserIdMap
     * @return
     */
    int saveUserAccountByMemberAccountList(List<MemberAccount> memberAccountList, Map<String, Long> memberIdLinkUserIdMap);


}
