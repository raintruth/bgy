package com.pagoda.account.migrate.service.member;

import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author wfg
 */
public interface IMemberAccountService {


    /**
     * 获取 钱包源账户list
     * @return
     */
    List<MemberAccount> getMemberAccountList();
    List<MemberAccount> getMemberAccountList(int taskNum);

    /**
     * 获取 源库表 分段所需中基本信息
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    Map<String, Object> findMinAndMaxAndCountModifyTime(Integer minMainAccountBalance);
    /**
     * 获取 源库表 分段所需中基本信息
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    Map<String, Object> findMinAndMaxAndCountMemberId(Integer minMainAccountBalance);

    /**
     * 获取 源库表有效的 分段数据list
     * @param startUpdateTimeStr 起始id值
     * @param startUpdateTimeStr   结束id
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    List<MemberAccount> findMemberAccountValidPageList(String startUpdateTimeStr, String endUpdateTimeStr, int minMainAccountBalance, Pageable pageable);

    /**
     * 获取 源库表有效的 Page数据list

     * @param minMainAccountBalance 最小的账户余额
     * @param memberIdList memberId
     * @return
     */
    List<MemberAccount> findMemberAccountValidPageList(int minMainAccountBalance, List<Long> memberIdList);

    /**
     * 获取 源库表有效的 Page数据list
     * @param startUpdateTimeStr 起始id值
     * @param startUpdateTimeStr   结束id
     * @param minMainAccountBalance 最小的账户余额
     * @param memberIdList memberId
     * @return
     */
    List<MemberAccount> findMemberAccountValidPageList(String startUpdateTimeStr, String endUpdateTimeStr, int minMainAccountBalance, List<Long> memberIdList, Pageable pageable);

    /**
     * 获取 源库表有效的 分段数据 count
     * @param startUpdateTimeStr 起始id值
     * @param startUpdateTimeStr   结束id
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    Long countMemberAccountValidPartList(String startUpdateTimeStr, String endUpdateTimeStr, int minMainAccountBalance);

    /**
     * 获取 源库表有效的 分段数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @param minMainAccountBalance 最小的账户余额
     * @return
     */
    List<MemberAccount> findMemberAccountValidPartList(Integer startMemberId, Integer endMemberId, int minMainAccountBalance);
}
