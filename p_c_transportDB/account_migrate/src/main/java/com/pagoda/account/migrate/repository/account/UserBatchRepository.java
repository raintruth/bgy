package com.pagoda.account.migrate.repository.account;


import com.pagoda.account.common.util.DateUtils;
import com.pagoda.account.migrate.dataobject.account.User;
import com.pagoda.account.migrate.repository.base.AbstractBatchDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author  wfg
 * Version  1.1.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j
@Repository
public class UserBatchRepository extends AbstractBatchDao<User> {

    /**
     *原生sql 批量更新数据
     * @param userList
     */
    @Transactional(rollbackFor=Exception.class)
    public void insertBatch(final List<User> userList){

        String sql = "INSERT INTO user " +
                "(user_id, member_id, phone_no, email, real_name, identity_card_no, consume_password, status, brand_type, create_time, update_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        primaryJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = userList.get(i);
                setPsByUser(ps, user);

            }

            @Override
            public int getBatchSize() {
                return userList.size();
            }
        });
    }

    private static void setPsByUser(PreparedStatement ps, User user)throws SQLException {
        ps.setLong(1, user.getUserId());
        ps.setLong(2, user.getMemberId());
        // ps.setLong(3, user.getPhoneNo());
        //ps.setObject(3, user.getPhoneNo(), Types.BIGINT);
        ps.setString(3, user.getPhoneNo());

        ps.setString(4, user.getEmail());
        ps.setString(5, user.getRealName());
        ps.setString(6, user.getIdentityCardNo());
        ps.setString(7, user.getConsumePassword());
        ps.setString(8, user.getStatus());
        ps.setString(9, user.getBrandType());

        ps.setTimestamp(10, DateUtils.utilDateTransSqlTimestamp(user.getCreateTime()));
        ps.setTimestamp(11, DateUtils.utilDateTransSqlTimestamp(user.getUpdateTime()));
    }
    /**
     *原生sql 批量更新数据
     * @param userList
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public int batchInsertIfExistUpdate(final List<User> userList){

        String sql = "INSERT INTO user " +
                "(user_id, member_id, phone_no, email, real_name, identity_card_no, consume_password, status, brand_type, create_time, update_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                " ON DUPLICATE KEY UPDATE member_id = VALUES(member_id), phone_no = VALUES(phone_no), email = VALUES(email), real_name = VALUES(real_name), identity_card_no = VALUES(identity_card_no), consume_password = VALUES(consume_password), status = VALUES(status), brand_type = VALUES(brand_type), create_time = VALUES(create_time), update_time = VALUES(update_time) ";

        int[] updateCountArray = primaryJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = userList.get(i);
                setPsByUser(ps, user);

            }


            @Override
            public int getBatchSize() {
                return userList.size();
            }
        });

        log.info("此次userList大小={}, 共影响了数据库条数={}", userList.size(), updateCountArray.length);
        return updateCountArray.length;
    }


}
