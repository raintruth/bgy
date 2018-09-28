package com.pagoda.account.migrate.repository.account;


import com.pagoda.account.migrate.dataobject.account.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Lixh
 * @Date: 2018/9/6 10:14
 * @Description:
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    User findUserByUserId(Long userId);

    /**
     * 利用原生的SQL获取有效的 分段数据list
     * @param startMemberId 起始id值
     * @param endMemberId   结束id
     * @return
     */
    @Query(value = "SELECT * FROM user u WHERE u.member_id  >= :startMemberId AND u.member_id <= :endMemberId ORDER BY u.member_id ", nativeQuery = true)
    List<User> findUserPartList(@Param(value = "startMemberId") Integer startMemberId, @Param(value = "endMemberId") Integer endMemberId);

    /**
     * 利用原生的SQL获取有效的 分页数据list
     * @param pageable
     * @return
     */
    //@Query(value = "SELECT u.user_id, u.member_id FROM user u WHERE 1=1 ", nativeQuery = true)
    @Query(value = "SELECT * FROM user u WHERE 1=1 ", nativeQuery = true)
    List<User> findUserPageList(Pageable pageable);

    /**
     * 利用原生的SQL获取有效的 分段数据list
     * @param startUpdateTimeStr 起始日期值
     * @param endUpdateTimeStr   结束日期值
     * @return
     */
    @Query(value = "SELECT * FROM user u WHERE u.update_time  >= :startUpdateTimeStr AND u.update_time <= :endUpdateTimeStr ORDER BY u.update_time ", nativeQuery = true)
    List<User> findUserPartList(@Param(value = "startUpdateTimeStr") String startUpdateTimeStr, @Param(value = "endUpdateTimeStr") String endUpdateTimeStr);

    /**
     * 利用原生的SQL获取有效的 分段数据 count
     * @param startUpdateTimeStr 起始日期值
     * @param endUpdateTimeStr   结束日期值
     * @return
     */
    @Query(value = "SELECT count(*) FROM user u WHERE u.update_time  >= :startUpdateTimeStr AND u.update_time <= :endUpdateTimeStr ", nativeQuery = true)
    Long countUserPartList(@Param(value = "startUpdateTimeStr") String startUpdateTimeStr, @Param(value = "endUpdateTimeStr") String endUpdateTimeStr);


    /**
     * 利用原生的SQL获取有效的 分段数据list
     * @param memberIdList   MemberId 串
     * @return
     */
    @Query(value = "SELECT * FROM user u WHERE u.member_id in (:memberIdList) ", nativeQuery = true)
    List<User> findUserPartListByMemberIds(@Param(value = "memberIdList") String memberIdList);
}
