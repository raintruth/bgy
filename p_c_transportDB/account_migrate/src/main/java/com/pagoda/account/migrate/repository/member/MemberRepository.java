package com.pagoda.account.migrate.repository.member;


import com.pagoda.account.migrate.dataobject.member.Member;
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
public interface MemberRepository extends JpaRepository<Member, Integer> {

    /**
     * 利用原生的SQL进行查询操作
     * @return  minUpdateTime maxUpdateTime memberCount
     */
    @Query(value = "SELECT (SELECT m.modifyTime FROM member m  ORDER BY modifyTime LIMIT 1) AS minUpdateTime, (SELECT m.modifyTime FROM member m ORDER BY modifyTime DESC LIMIT 1) AS maxUpdateTime, (SELECT COUNT(*) FROM member m ) AS memberCount FROM DUAL", nativeQuery = true)
    Map<String, Object> findMinAndMaxAndCountModifyTime();

    /**
     * 利用原生的SQL进行查询操作
     * @return  minMemberId maxMemberId
     */
    @Query(value = "SELECT ( SELECT * from (SELECT m.memberId FROM member m  WHERE m.memberId >= :startMemberId ORDER BY memberId LIMIT 0, :totalCount )T  LIMIT 1) AS minMemberId, ( SELECT * from (SELECT m.memberId FROM member m  WHERE m.memberId >= :startMemberId ORDER BY memberId LIMIT 0, :totalCount )T ORDER BY memberId desc LIMIT 1) AS maxMemberId FROM DUAL", nativeQuery = true)
    Map<String, Object> findMinAndMaxMemberId(@Param(value = "startMemberId") Long startMemberId, @Param(value = "totalCount") Long totalCount);

    /**
     * 利用原生的SQL进行查询操作
     * @return  minMemberId maxMemberId validMemberCount
     */
    @Query(value = "SELECT (SELECT m.memberId FROM member m  ORDER BY memberId LIMIT 1) AS minMemberId, (SELECT m.memberId FROM member m ORDER BY memberId DESC LIMIT 1) AS maxMemberId, (SELECT COUNT(m.memberId) FROM member m ) AS memberCount FROM DUAL", nativeQuery = true)
    Map<String, Object> findMinAndMaxAndCountMemberId();

    /**
     * 利用原生的SQL进行查询操作 联表member_account
     * @param minMainAccountBalance 账户余额
     * @return  minMemberId maxMemberId validMemberCount
     */
    @Query(value = "SELECT (SELECT m.memberId FROM member m WHERE m.memberId = (SELECT  ma.memberId FROM member_account ma WHERE ma.mainAccountBalance > :minMainAccountBalance ORDER BY memberId LIMIT 1)) AS minMemberId, (SELECT m.memberId FROM member m WHERE m.memberId = (SELECT  ma.memberId FROM member_account ma WHERE ma.mainAccountBalance > :minMainAccountBalance ORDER BY memberId DESC LIMIT 1)) AS maxMemberId, (SELECT COUNT(m.memberId) FROM member m, (SELECT  ma.memberId FROM member_account ma WHERE ma.mainAccountBalance > :minMainAccountBalance ) T WHERE m.memberId = T.memberId ) AS memberCount FROM DUAL", nativeQuery = true)
    Map<String, Object> findValidMinAndMaxAndCountMemberId(@Param(value = "minMainAccountBalance") int minMainAccountBalance);

    /**
     * 利用原生的SQL获取有效的 分页数据list
     * @param startUpdateTimeStr 起始日期字符值
     * @param endUpdateTimeStr   结束日期字符值
     * @return
     */
    //@Query(value = "SELECT * FROM member m WHERE m.modifyTime >= :startUpdateTimeStr AND m.modifyTime < :endUpdateTimeStr   ", nativeQuery = true)
    @Query(value = "SELECT * FROM (select m.* from member m WHERE m.modifyTime >= :startUpdateTimeStr AND m.modifyTime < :endUpdateTimeStr )T WHERE EXISTS ( SELECT 0 FROM member_account ma, member_wallet_record w WHERE w.memberId = T.memberId AND w.memberId = ma.memberId AND ma.mainAccountBalance > 0 ) ", nativeQuery = true)
    List<Member> findMemberPageList(@Param(value = "startUpdateTimeStr") String startUpdateTimeStr, @Param(value = "endUpdateTimeStr") String endUpdateTimeStr, Pageable pageable);

    /**
     * 利用原生的SQL获取有效的 分段数据list
     * @param startUpdateTimeStr 起始日期字符值
     * @param endUpdateTimeStr   结束日期字符值
     * @return
     */
    //@Query(value = "SELECT count(*) FROM member m WHERE m.modifyTime >= :startUpdateTimeStr AND m.modifyTime < :endUpdateTimeStr ", nativeQuery = true)
    @Query(value = "SELECT count(*) FROM (select m.* from member m WHERE m.modifyTime >= :startUpdateTimeStr AND m.modifyTime < :endUpdateTimeStr )T WHERE EXISTS ( SELECT 0 FROM member_account ma, member_wallet_record w WHERE w.memberId = T.memberId AND w.memberId = ma.memberId AND ma.mainAccountBalance > 0 ) ", nativeQuery = true)
    Long countMemberPartList(@Param(value = "startUpdateTimeStr") String startUpdateTimeStr, @Param(value = "endUpdateTimeStr") String endUpdateTimeStr);

    /**
     * 利用原生的SQL获取有效的 分段数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @return
     */
    @Query(value = "SELECT * FROM member m WHERE m.memberId >= :startMemberId AND m.memberId < :endMemberId ORDER BY m.memberId ", nativeQuery = true)
    List<Member> findMemberPartList(@Param(value = "startMemberId") Integer startMemberId, @Param(value = "endMemberId") Integer endMemberId);

    /**
     * 利用原生的SQL获取有效的 分页数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @return
     */
    @Query(value = "SELECT * FROM member m WHERE m.memberId >= :startMemberId AND m.memberId < :endMemberId ORDER BY m.memberId ", nativeQuery = true)
    List<Member> findMemberPageList(@Param(value = "startMemberId") Long startMemberId, @Param(value = "endMemberId") Long endMemberId, Pageable pageable);

    /**
     * 利用原生的SQL获取有效的 分段数据list  联表member_account
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    @Query(value = "SELECT * FROM member m WHERE m.memberId IN ( SELECT ma.memberId FROM member_account ma WHERE ma.memberId >= :startMemberId AND ma.memberId < :endMemberId  AND  ma.mainAccountBalance > :minMainAccountBalance) ORDER BY m.memberId ", nativeQuery = true)
    List<Member> findMemberValidPartList(@Param(value = "startMemberId") Integer startMemberId, @Param(value = "endMemberId") Integer endMemberId, @Param(value = "minMainAccountBalance") int minMainAccountBalance);


}
