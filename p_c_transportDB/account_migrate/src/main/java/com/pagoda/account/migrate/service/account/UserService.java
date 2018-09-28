package com.pagoda.account.migrate.service.account;


import com.pagoda.account.migrate.dataobject.account.User;
import com.pagoda.account.migrate.dataobject.member.Member;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Lixh
 * @Date: 2018/9/4 15:33
 * @Description:
 */
public interface UserService {

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    User findUserByUserId(Long userId);

    /**
     * 将 源Member 数据保存到新的User 表中
     * @param memberList
     * @return
     */
    int saveUserByMemberList(List<Member> memberList);

    /**
     * 获取 源库表有效的 分段数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @return
     */
    List<User> findUserPartList(Integer startMemberId, Integer endMemberId);

    /**
     * 获取 源库表有效的 分段数据list
     * @param startUpdateTimeStr 起始日期串
     * @param endUpdateTimeStr   结束日期串
     * @return
     */
    List<User> findUserPartList(String startUpdateTimeStr, String endUpdateTimeStr);



    /**
     * 获取 源库表有效的 分段数据 count
     * @param startUpdateTimeStr 起始日期串
     * @param endUpdateTimeStr   结束日期串
     * @return
     */
    Long countUserPartList(String startUpdateTimeStr, String endUpdateTimeStr);

    /**
     * 获取 源库表有效的 分段数据list
     * @param memberIdList   MemberId 串
     * @return
     */
    List<User> findUserPartListByMemberIds(String memberIdList);

    /**
     * 获取user表memberId数据
     * @param pageable
     * @return
     */
    Map<String, Object> findMemberIdInfoMapByPage(Pageable pageable);
}
