package com.pagoda.account.migrate.repository.account;


import com.pagoda.account.common.util.DateUtils;
import com.pagoda.account.migrate.dataobject.account.UserAccount;
import com.pagoda.account.migrate.repository.base.AbstractBatchDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

/**
 *
 * @author  wfg
 * Version  1.1.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j(topic = "UserAccountBatchRepository")
@Repository
public class UserAccountBatchRepository extends AbstractBatchDao<UserAccount> {


    /**
     *原生sql 批量更新数据
     * @param userAccountList
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public int batchInsertIfExistUpdate( List<UserAccount> userAccountList){

        String sql = " INSERT INTO user_account " +
                " (account_id, user_id, bankcard_id, account_type, account_no, deposit_no, balance, available, fee_rate, status, pay_rank, order_limit, daily_limit, create_time, update_time)  values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                " ON DUPLICATE KEY UPDATE user_id = VALUES(user_id), bankcard_id = VALUES(bankcard_id), account_type = VALUES(account_type), account_no = VALUES(account_no), deposit_no = VALUES(deposit_no), balance = VALUES(balance), available = VALUES(available), fee_rate = VALUES(fee_rate), status = VALUES(status), pay_rank = VALUES(pay_rank), order_limit = VALUES(order_limit), daily_limit = VALUES(daily_limit), create_time = VALUES(create_time), update_time = VALUES(update_time) ";

        int[] updateCountArray = primaryJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserAccount userAccount = userAccountList.get(i);
                ps.setLong(1, userAccount.getAccountId());
                ps.setLong(2, userAccount.getUserId());
                ps.setLong(3, userAccount.getBankcardId());

                ps.setString(4, userAccount.getAccountType());
                ps.setLong(5, userAccount.getAccountNo());
                ps.setString(6, userAccount.getDepositNo());
                ps.setLong(7, userAccount.getBalance());
                ps.setLong(8, userAccount.getAvailable());
                ps.setObject(9, userAccount.getFeeRate(), Types.SMALLINT);

                ps.setString(10, userAccount.getStatus());
                ps.setObject(11, userAccount.getPayRank(), Types.TINYINT);
                ps.setLong(12, userAccount.getOrderLimit());
                ps.setLong(13, userAccount.getDailyLimit());

                ps.setTimestamp(14, DateUtils.utilDateTransSqlTimestamp(userAccount.getCreateTime()));
                ps.setTimestamp(15, DateUtils.utilDateTransSqlTimestamp(userAccount.getUpdateTime()));

            }

            @Override
            public int getBatchSize() {
                return userAccountList.size();
            }
        });

        log.info("此次userList大小={}, 共影响了数据库条数={}", userAccountList.size(), updateCountArray.length);
        return updateCountArray.length;
    }


}
