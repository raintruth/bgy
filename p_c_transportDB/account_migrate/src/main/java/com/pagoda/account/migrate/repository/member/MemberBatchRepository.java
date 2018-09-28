package com.pagoda.account.migrate.repository.member;


import com.pagoda.account.common.util.BeanUtils;
import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.repository.base.AbstractBatchDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  wfg
 * Version  1.1.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j
@Repository
public class MemberBatchRepository extends AbstractBatchDao<Member> {

/*
    @Override
    public List<Member> batchQuery(@Param(value = "startMemberId") Long startMemberId, @Param(value = "endMemberId") Long endMemberId, Pageable pageable){

        String sql = "SELECT * FROM member m WHERE m.memberId >= ? AND m.memberId < ? ORDER BY m.memberId ";
        Object[] params = new Object[] { map.get("") };
        List<Member> memberList = primaryJdbcTemplate.query(sql, params, new MemberRowMapper());

        return memberList;
    }
    */
    @Override
    public List<Member> batchQuery(Map map){

        String sql = "SELECT * FROM member m WHERE m.memberId >= ? AND m.memberId < ? ORDER BY m.memberId ";
        Object[] params = new Object[] { map.get("startMemberId"), map.get("endMemberId")  };
        List<Member> memberList = secondaryJdbcTemplate.query(sql, params, new MemberRowMapper());

        return memberList;
    }

    @Override
    public List<Member> batchQuery(Map map, Pageable pageable){

        String sql = "SELECT * FROM member m WHERE m.memberId >= ? AND m.memberId < ? ORDER BY m.memberId limit ?, ? ";
        Object[] params = new Object[] { map.get("startMemberId"), map.get("endMemberId"), pageable.getPageNumber(), pageable.getPageSize() };
        List<Member> memberList = secondaryJdbcTemplate.query(sql, params, new MemberRowMapper());

        return memberList;
    }


    public class MemberRowMapper implements RowMapper<Member> {
        /**
         *
         * @param rs
         * @param rowNum
         * @return
         * @throws SQLException
         *
         *
         * memberId         int(20)       (NULL)     NO      PRI     (NULL)           select,insert,update,references  会员ID， 来自自维护的序列
         * memberType       char(1)       utf8_bin   NO              (NULL)           select,insert,update,references  会员类型 P: 手机会员 C: 实体卡会员
         * memberNum        varchar(32)   utf8_bin   NO      UNI     (NULL)           select,insert,update,references  会员账号
         * status           char(1)       utf8_bin   NO              (NULL)           select,insert,update,references  状态  N：正常 L：挂失 D：注销 S：锁定 U永久锁定
         * levelId          int(11)       (NULL)     YES             (NULL)           select,insert,update,references  等级ID
         * memberName       varchar(32)   utf8_bin   YES             (NULL)           select,insert,update,references  会员名称
         * memberSex        char(1)       utf8_bin   YES             (NULL)           select,insert,update,references  会员性别 M：男 F：女
         * identityCardNum  varchar(32)   utf8_bin   YES             (NULL)           select,insert,update,references  身份证
         * phoneNum         varchar(32)   utf8_bin   YES     MUL     (NULL)           select,insert,update,references  手机号码
         * memberBirthday   char(8)       utf8_bin   YES     MUL     (NULL)           select,insert,update,references  生日 yyyyMMdd
         * memberLabel      varchar(128)  utf8_bin   YES             (NULL)           select,insert,update,references  会员标签
         * memberJob        varchar(32)   utf8_bin   YES             (NULL)           select,insert,update,references  会员职务
         * incoming         int(11)       (NULL)     YES             (NULL)           select,insert,update,references  收入
         * memberIcon       varchar(512)  utf8_bin   YES             (NULL)           select,insert,update,references  会员图像
         * memberEmail      varchar(64)   utf8_bin   YES             (NULL)           select,insert,update,references  会员邮箱
         * referrerNum      varchar(32)   utf8_bin   YES             (NULL)           select,insert,update,references  推荐人账号
         * registerChannel  char(1)       utf8_bin   YES             (NULL)           select,insert,update,references  注册渠道  电商：A，微信：W，POS系统：P，一体化会员:Y,采配销系统：E，一体化短信平台：F，客服平台：G，三方电商：O
         * channelValue     varchar(32)   utf8_bin   YES             (NULL)           select,insert,update,references  渠道取值
         * registerTime     char(14)      utf8_bin   NO      MUL     (NULL)           select,insert,update,references  注册时间 yyyyMMddHHmmss
         * modifyTime       char(14)      utf8_bin   NO      MUL     (NULL)           select,insert,update,references  修改时间 yyyyMMddHHmmss
         * memberNote       varchar(512)  utf8_bin   YES             (NULL)           select,insert,update,references  会员备注
         * consumePasswd    varchar(32)   utf8_bin   YES             (NULL)           select,insert,update,references  会员消费密码
         * regFlag          char(1)       utf8_bin   YES             (NULL)           select,insert,update,references
         * firstLoginTime   varchar(14)   utf8_bin   YES             (NULL)           select,insert,update,references  首次APP登录时间
         * firstLoginStore  varchar(256)  utf8_bin   YES             (NULL)           select,insert,update,references  首次APP登录门店
         * belongBrand      char(1)       utf8_bin   YES             (NULL)           select,insert,update,references
         */
        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {

            Member member = BeanUtils.fastClone(Member.class);
            member.setMemberId(rs.getInt("memberId"));
            member.setMemberType(rs.getString("memberType"));
            member.setMemberNum(rs.getString("memberNum"));
            member.setStatus(rs.getString("status"));
            member.setLevelId(rs.getInt("memberId"));
            member.setMemberName(rs.getString("memberName"));
            member.setMemberSex(rs.getString("memberSex"));
            member.setIdentityCardNum(rs.getString("identityCardNum"));
            member.setPhoneNum(rs.getString("phoneNum"));
            member.setMemberBirthday(rs.getString("memberBirthday"));
            member.setMemberLabel(rs.getString("memberLabel"));
            member.setMemberJob(rs.getString("memberJob"));
            member.setIncoming(rs.getInt("incoming"));
            member.setMemberIcon(rs.getString("memberIcon"));
            member.setMemberEmail(rs.getString("memberEmail"));
            member.setReferrerNum(rs.getString("referrerNum"));
            member.setRegisterChannel(rs.getString("registerChannel"));
            member.setChannelValue(rs.getString("channelValue"));
            member.setRegisterTime(rs.getString("registerTime"));
            member.setModifyTime(rs.getString("modifyTime"));
            member.setMemberNote(rs.getString("memberNote"));
            member.setConsumePasswd(rs.getString("consumePasswd"));
            member.setRegFlag(rs.getInt("regFlag"));
            member.setFirstLoginTime(rs.getString("firstLoginTime"));
            member.setFirstLoginStore(rs.getString("firstLoginStore"));
            member.setBelongBrand(rs.getInt("belongBrand"));

            return member;
        }

    }

}
