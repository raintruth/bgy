package com.pagoda.account.migrate.repository.account;

import com.pagoda.account.common.util.DateUtils;
import com.pagoda.account.migrate.dataobject.account.UserAccountRecord;
import com.pagoda.account.migrate.repository.base.AbstractBatchDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;


@Slf4j(topic = "UserAccountRecordRepositroy")
@Repository
public class UserAccountRecordBatchRepository extends AbstractBatchDao<UserAccountRecord> {

    @Transactional(rollbackFor=Exception.class)
    @Override
    public int batchInsertIfExistUpdate(List<UserAccountRecord> list) {
        String sql = " INSERT INTO user_account_record " +
                " (record_id, account_id, user_id, account_type, modify_type, status, balance_before_modify, money, balance_after_modify, expire_time, merchant_order_no, original_code, activity_code, channel, channel_value, operator, create_time, update_time)  " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?) " +
                " ON DUPLICATE KEY UPDATE record_id = VALUES(record_id), account_id = VALUES(account_id), user_id = VALUES(user_id), account_type = VALUES(account_type), modify_type = VALUES(modify_type), status = VALUES(status), balance_before_modify = VALUES(balance_before_modify), money = VALUES(money), balance_after_modify = VALUES(balance_after_modify), expire_time = VALUES(expire_time), merchant_order_no = VALUES(merchant_order_no), original_code = VALUES(original_code), activity_code = VALUES(activity_code), channel = VALUES(channel), channel_value = VALUES(channel_value), operator = VALUES(operator), create_time = VALUES(create_time), update_time = VALUES(update_time) ";
        int[] updateCountArray = primaryJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserAccountRecord accountRecord = list.get(i);
                ps.setLong(1, accountRecord.getRecordId());
                ps.setLong(2, accountRecord.getAccountId());
                ps.setLong(3, accountRecord.getUserId());

                ps.setObject(4, accountRecord.getAccountType(),Types.CHAR);
                ps.setObject(5, accountRecord.getModifyType(),Types.CHAR);
                ps.setObject(6, accountRecord.getStatus(),Types.CHAR);
                ps.setLong(7, accountRecord.getBalanceBeforeModify());
                ps.setLong(8, accountRecord.getMoney());
                ps.setLong(9, accountRecord.getBalanceAfterModify());
                if(null != accountRecord.getExpireTime()){
                    ps.setDate(10, new Date(accountRecord.getExpireTime().getTime()));
                }else {
                    ps.setObject(10, accountRecord.getExpireTime(),Types.TIME);
                }
                ps.setString(11, accountRecord.getMerchantOrderNo());
                ps.setString(12, accountRecord.getOriginalCode());
                ps.setString(13, accountRecord.getActivityCode());

                ps.setObject(14, accountRecord.getChannel(),Types.CHAR);
                ps.setString(15, accountRecord.getChannelValue());
                ps.setString(16, accountRecord.getOperator());
                ps.setTimestamp(17, DateUtils.utilDateTransSqlTimestamp(accountRecord.getCreateTime()));
                ps.setTimestamp(18, DateUtils.utilDateTransSqlTimestamp(accountRecord.getUpdateTime()));
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });

        log.info("此次userList大小={}, 共影响了数据库条数={}", list.size(), updateCountArray.length);
        return updateCountArray.length;
    }
}
