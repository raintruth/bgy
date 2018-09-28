package com.pagoda.account.migrate.service.member;


import com.pagoda.account.migrate.dataobject.member.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author wfg
 */
public interface IMemberService {

    /**
     * 获取 源库表 分段所需中基本信息
     * @return
     */
    Map<String, Object> findMinAndMaxAndCountModifyTime();

    /**
     * 获取 源库表 分段所需中基本信息
     * @return
     */
    Map<String, Object> findMinAndMaxMemberId(Long startMemberId, Long totalCount);
    /**
     * 获取 源库表 分段所需中基本信息
     * @return
     */
    Map<String, Object> findMinAndMaxAndCountMemberId();


    /**
     * 获取 源库表有效的 分段数据list
     * @param startUpdateTimeStr 起始日期字符串值 例子 20161019101010
     * @param endUpdateTimeStr   结束日期字符串值 例子 20161019101010
     * @return
     */
    List<Member> findMemberPageList(String startUpdateTimeStr, String endUpdateTimeStr, Pageable pageable);
    /**
     * 获取 源库表有效的 分段数据 count
     * @param startUpdateTimeStr 起始日期字符串值 例子 20161019101010
     * @param endUpdateTimeStr   结束日期字符串值 例子 20161019101010
     * @return
     */
    Long countMemberPartList(String startUpdateTimeStr, String endUpdateTimeStr);
    /**
     * 获取 源库表有效的 分段数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @return
     */
    List<Member> findMemberPartList(Integer startMemberId, Integer endMemberId);

    /**
     * 获取 源库表有效的 分页数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @return
     */
    List<Member> findMemberPageList(Long startMemberId, Long endMemberId, Pageable pageable);

    /**
     * 获取 源库表有效的 分段数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    List<Member> findMemberValidPartList(Integer startMemberId, Integer endMemberId, int minMainAccountBalance);

}
