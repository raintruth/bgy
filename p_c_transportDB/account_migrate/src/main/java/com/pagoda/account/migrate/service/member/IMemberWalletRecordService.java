package com.pagoda.account.migrate.service.member;

import com.pagoda.account.migrate.dataobject.member.MemberWalletRecord;

import java.util.List;

/**
 * @author zfr
 */
public interface IMemberWalletRecordService {
    /**
     * 根据id查询账户记录
     * @param id
     * @return
     */
    MemberWalletRecord getMemberWalletRecordById(Integer id);

    /**
     * 查询所有用户数据
     * @param accountType 类型
     * @param page 第几页开始导入
     * @param size 需要导入的数据，null表示全部导入
     * @param pageSize 分页大小
     * @return
     */
    List<MemberWalletRecord> getMemberWalletRecordById(String accountType,Integer page,Long size,Integer pageSize);

    /**
     * 按照分页大小和数量进行查询最新的Sql
     * @param time 时间
     * @param page 第几页
     * @param count 大小
     * @param pageSize 分页大小
     * @return
     */
    List<MemberWalletRecord> getMemberWalletRecordByDate(String time,Integer page,Long count,Integer pageSize);



}
