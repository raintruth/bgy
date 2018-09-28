package com.pagoda.account.migrate.repository.member;


import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wfg
 * @version 1.0.0 2018-09-07
 */
public interface MemberAccountRepository extends JpaRepository<MemberAccount, Integer> {

    /**
     * 利用原生的SQL进行查询操作
     * @param minMainAccountBalance 账户余额
     * @return  minMemberId maxMemberId validMemberCount
     */
    @Query(value = "SELECT (SELECT m.modifyTime FROM member_account m WHERE m.mainAccountBalance > :minMainAccountBalance ORDER BY modifyTime LIMIT 1) AS minUpdateTime, (SELECT m.modifyTime FROM member_account m WHERE m.mainAccountBalance > :minMainAccountBalance ORDER BY modifyTime DESC LIMIT 1) AS maxUpdateTime, (SELECT COUNT(*) FROM member_account m WHERE m.mainAccountBalance > :minMainAccountBalance ) AS memberCount FROM DUAL", nativeQuery = true)
    Map<String, Object> findMinAndMaxAndCountModifyTime(@Param(value = "minMainAccountBalance") int minMainAccountBalance);

    /**
     * 利用原生的SQL进行查询操作
     * @param minMainAccountBalance 账户余额
     * @return  minMemberId maxMemberId validMemberCount
     */
    @Query(value = "SELECT (SELECT m.memberId FROM member_account m WHERE m.mainAccountBalance > :minMainAccountBalance ORDER BY memberId LIMIT 1) AS minMemberId, (SELECT m.memberId FROM member_account m WHERE m.mainAccountBalance > :minMainAccountBalance ORDER BY memberId DESC LIMIT 1) AS maxMemberId, (SELECT COUNT(m.memberId) FROM member_account m WHERE m.mainAccountBalance > :minMainAccountBalance ) AS validMemberCount FROM DUAL", nativeQuery = true)
    Map<String, Object> findMinAndMaxAndCountMemberId(@Param(value = "minMainAccountBalance") int minMainAccountBalance);


    /**
     * 利用原生的SQL获取有效的 分段数据count
     * @param startUpdateTimeStr 起始日期值
     * @param endUpdateTimeStr   结束日期值
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    @Query(value = "SELECT count(*) FROM member_account m WHERE m.modifyTime >=:startUpdateTimeStr AND m.modifyTime <:endUpdateTimeStr  AND  m.mainAccountBalance > :minMainAccountBalance  ", nativeQuery = true)
    Long countMemberAccountValidPartList(@Param(value = "startUpdateTimeStr") String startUpdateTimeStr, @Param(value = "endUpdateTimeStr") String endUpdateTimeStr, @Param(value = "minMainAccountBalance") int minMainAccountBalance);

    /**
     * 利用原生的SQL获取有效的 分段数据list
     * @param startUpdateTimeStr 起始日期值
     * @param endUpdateTimeStr   结束日期值
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    @Query(value = "SELECT * FROM member_account m WHERE m.modifyTime >=:startUpdateTimeStr AND m.modifyTime <:endUpdateTimeStr  AND  m.mainAccountBalance > :minMainAccountBalance  ", nativeQuery = true)
    List<MemberAccount> findMemberAccountValidPageList(@Param(value = "startUpdateTimeStr") String startUpdateTimeStr, @Param(value = "endUpdateTimeStr") String endUpdateTimeStr, @Param(value = "minMainAccountBalance") int minMainAccountBalance, Pageable pageable);


    /**
     *
     * @param minMainAccountBalance 最小的账户余额
     * @param memberIdList  memberId 串
     * @return
     */
    @Query(value = "SELECT * FROM member_account m WHERE  m.mainAccountBalance > :minMainAccountBalance AND m.memberId in (:memberIdList) ", nativeQuery = true)
    List<MemberAccount> findMemberAccountValidPageList(@Param(value = "minMainAccountBalance") int minMainAccountBalance, @Param(value = "memberIdList") List<Long> memberIdList);

    /**
     *
     * @param startUpdateTimeStr 起始日期值
     * @param endUpdateTimeStr 结束日期值
     * @param minMainAccountBalance 最小的账户余额
     * @param memberIdList  memberId 串
     * @param pageable 分页
     * @return
     */
    @Query(value = "SELECT * FROM member_account m WHERE m.modifyTime >=:startUpdateTimeStr AND m.modifyTime <:endUpdateTimeStr  AND  m.mainAccountBalance > :minMainAccountBalance AND m.memberId in (:memberIdList) ", nativeQuery = true)
    List<MemberAccount> findMemberAccountValidPageList(@Param(value = "startUpdateTimeStr") String startUpdateTimeStr, @Param(value = "endUpdateTimeStr") String endUpdateTimeStr, @Param(value = "minMainAccountBalance") int minMainAccountBalance, @Param(value = "memberIdList") List<Long> memberIdList, Pageable pageable);


    /**
     * 利用原生的SQL获取有效的 分段数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    @Query(value = "SELECT * FROM member_account m WHERE m.memberId >=:startMemberId AND m.memberId <:endMemberId  AND  m.mainAccountBalance > :minMainAccountBalance ORDER BY memberId  ", nativeQuery = true)
    List<MemberAccount> findMemberAccountValidPartList(@Param(value = "startMemberId") Integer startMemberId, @Param(value = "endMemberId") Integer endMemberId, @Param(value = "minMainAccountBalance") int minMainAccountBalance);


}
