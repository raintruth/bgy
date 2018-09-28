package com.pagoda.account.migrate.repository.account;

import com.pagoda.account.migrate.dataobject.account.UserAccountRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zfr
 */
public interface UserAccountRecordRepository extends JpaRepository<UserAccountRecord, Long> {

    /**
     * 根据账户类型和时间查询相关记录-count
     * @param accountType 账户类型
     * @param time 时间
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT count(*) from user u,user_account ua where u.user_id = ua.user_id and ua.account_type = ?1 and ua.create_time <= ?2 ")
    Long getUserAccountRecordCount(String accountType,String time);

    /**
     * 根据账户类型和时间查询相关记录-明细
     * @param accountType 账户类型
     * @param time 时间
     * @param pageable 分页
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT user.member_id as mumberNo,user.user_id as userId,ua.account_id as accountId,ua.account_type as accountType from user,user_account ua where user.user_id = ua.user_id and ua.account_type = ?1 and ua.create_time <= ?2 ")
    List<Map> getUserAccountRecordDtoList(String accountType, String time,Pageable pageable);


    /**
     * 使用between获取用户信息和账户信息
     * @param start 开始大笑
     * @param end 结束大笑
     * @param accountType 类型
     * @param time 时间
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT user.member_id as mumberNo,user.user_id as userId,ua.account_id as accountId,ua.account_type as accountType from user,user_account ua where user.user_id = ua.user_id " +
            "and user.user_id BETWEEN ?1 and ?2 and ua.account_type = ?3 and ua.update_time <= ?4 ")
    List<Map> getUserAccountRecordDtoListByUserIdBetween(Integer start,Integer end , String accountType,String time);

    /**
     * 查询所有记录的账户数
     * @param userIdList 用户id
     * @param accountId 账户id
     * @param time 钱
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT sum(balance_before_modify) as totalBalanceBeforeModify,sum(balance_after_modify) as totalBalanceAfterModify , ABS(sum(cast(balance_after_modify as signed)-cast(balance_before_modify as signed))) as money" +
            " from user_account_record ur where ur.user_id in (:userIdList) and ur.account_id in (:accountId) and  ur.create_time <= :time ")
    Map getTotalRecord(@Param("userIdList") List<Integer> userIdList,@Param("accountId") List<Integer> accountId,@Param("time")String time);

    /**
     * 根据用户id和账户id-删除用户记录
     * @param userIdList 用户id
     * @param accountId 账户id
     * @param time 时间
     * @return
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE from user_account_record  where user_id in(:userIdList) and account_id in(:accountId) and  create_time <= :time")
    int deleteUserRecordByUserIdAndAccountId(@Param("userIdList") List<Integer> userIdList,@Param("accountId") List<Integer> accountId,@Param("time") Date time);

}
