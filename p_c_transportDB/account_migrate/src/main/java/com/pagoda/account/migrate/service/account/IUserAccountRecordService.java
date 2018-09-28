package com.pagoda.account.migrate.service.account;


import com.pagoda.account.migrate.dataobject.member.MemberWalletRecord;

import java.util.List;
import java.util.Map;

/**
 *消费记录处理类
 */
public interface IUserAccountRecordService {
    /**
     * 保存账户信息的方法
     * @param m 消息
     * @param type 类型
     */
    void insertUserAccountRecordList(Object m,String type);

    /**
     * 校验userAccountRecord数据
     */
    void getUserAccountRecordDtoList();

    /**
     * 校验userAccountRecord数据
     * @param message 消息数据
     */
    void updateUserAccountRecordCheck(Object message);

    /**
     * 保存数据
     * @param recordData 用户数据
     * @param recordList 记录数据
     */
    void SaveUserAccountRecord(List<Map>recordData, List<MemberWalletRecord> recordList);

}
