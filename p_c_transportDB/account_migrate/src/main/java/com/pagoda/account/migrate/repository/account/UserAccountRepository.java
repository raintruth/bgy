package com.pagoda.account.migrate.repository.account;


import com.pagoda.account.migrate.dataobject.account.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Lixh
 * @Date: 2018/9/6 10:10
 * @Description:
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * 根据账户id查询账户
     * @param accountId
     * @return
     */
    UserAccount findUserAccountByAccountId(Long accountId);
    /**
     * 获取账户和用户信息
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT u.user_id as userId, ua.account_id as accountId,ua.account_no as accountNo,ua.account_type as accountType," +
            "u.member_id as mumberNo from user u ,user_account ua where u.user_id = ua.user_id  ")
    List<Map> findUserAndAccountDto(String accountType, Pageable pageable);

    /**
     * 获取账户和用户信息-根据between来查询
     * @param accountType 账户类型
     * @param start 开始大小
     * @param end 结束大小
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT u.user_id as userId, ua.account_id as accountId,ua.account_no as accountNo,ua.account_type as accountType,u.member_id as mumberNo from user u ,user_account ua where u.user_id = ua.user_id " +
            "and ua.account_type = ?1 and u.user_id between ?2 and ?3  ")
    List<Map> findUserAndAccountDtoList(String accountType, Integer start, Integer end);


    /**
     * 查询所有的数据大小
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT count(*) from user u ,user_account ua where u.user_id = ua.user_id and ua.account_type = ?1 ")
    Long findCountAccount(String accountType);

    /**
     * 查询所有的数据分页查询
     * @param pageable
     * @return
     */
    @Override
    Page<UserAccount> findAll(Pageable pageable);

    /**
     * 根据memberId进行查询数据
     * @param mIds memberId-list
     * @param accountType 账户类型
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT u.user_id as userId, ua.account_id as accountId,ua.account_no as accountNo,ua.account_type as accountType," +
            "u.member_id as mumberNo from user u ,user_account ua where u.user_id = ua.user_id and u.member_id in (:mIds) and ua.account_type = :accountType")
    List<Map> findUserAndAccountByMemberId(@Param("mIds") List<String> mIds,@Param("accountType") String accountType);


}
