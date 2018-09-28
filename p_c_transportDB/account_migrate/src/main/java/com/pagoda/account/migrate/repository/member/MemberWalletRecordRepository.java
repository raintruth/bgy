package com.pagoda.account.migrate.repository.member;


import com.pagoda.account.migrate.dataobject.member.MemberWalletRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zfr
 * 账户记录
 */
public interface MemberWalletRecordRepository extends JpaRepository<MemberWalletRecord, Integer> {
    /**
     * 根据id查询记录
     * @param recordId 记录id
     * @return
     */
    MemberWalletRecord findMemberWalletRecordByRecordId(Integer recordId);

    /**
     * 根据id between查询
     * @param recordId1 第一个id
     * @param recordId2 第二个id
     * @return
     */
    List<MemberWalletRecord> findByRecordIdBetweenAndMainMoneyAfterModifyGreaterThanAndMemberIdIn(Integer recordId1,Integer recordId2,Integer money,List<Integer> lists);

    /**
     * 查询账户记录数据根据时间点来
     * @param date 时间
     * @param lists memberId
     * @return
     */
    List<MemberWalletRecord> findByUpdateTimeLessThanEqualAndMemberIdIn(String date, List<Integer> lists);

    /**
     * 查询账户记录
     * @param time 时间
     * @param pageable 分页
     * @return
     */
    List<MemberWalletRecord> findByUpdateTimeGreaterThanEqual(String time,Pageable pageable);


    /**
     * 根据时间查询会员系统的数据
     * @param time
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT count(*) from member_wallet_record where updateTime >= ?1")
    Long findCountRecordByUpdateTime(String time);

    /**
     * 查询账户记录
     * @param date 时间
     * @param lists list集合
     * @param pageable 分页
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT recordId, modifyType,memberId,mainMoneyBeforeModify,mainMoneyAfterModify,updateTime FROM member_wallet_record mw where mw.memberId in(:lists) and mw.updateTime<=:date and (mainMoneyBeforeModify - mainMoneyAfterModify)!=0 ")
    List<Map> findUserAccountRecordList(@Param("date") String date, @Param("lists") List<Integer> lists,Pageable pageable);

    /**
     * 根据会员id和时间进行record计算
     * @param memberId 会员id
     * @param time 时间id
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT sum(mainMoneyBeforeModify) as totalMainMoneyBeforeModify,sum(mainMoneyAfterModify) as totalMainMoneyAfterModify,ABS(sum(mainMoneyBeforeModify - mainMoneyAfterModify)) as money FROM member_wallet_record  " +
            "where memberId in(:memberId)  and (mainMoneyBeforeModify - mainMoneyAfterModify)!=0  and  updateTime <= :time")
    Map getTotalRecord(@Param("memberId") List<Integer> memberId,@Param("time") String time);
}
