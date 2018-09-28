package com.pagoda.account.migrate.service.account.impl;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.pagoda.account.common.util.JdbcSqlUtil;
import com.pagoda.account.migrate.dataobject.account.User;
import com.pagoda.account.migrate.dataobject.account.UserAccount;
import com.pagoda.account.common.util.BeanUtils;
import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import com.pagoda.account.migrate.repository.account.UserAccountBatchRepository;
import com.pagoda.account.migrate.repository.account.UserAccountRepository;
import com.pagoda.account.migrate.service.account.UserAccountService;
import com.pagoda.account.migrate.service.account.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Lixh
 * @Date: 2018/9/6 15:12
 * @Description:
 */
@Slf4j
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountBatchRepository userAccountBatchRepository;

    @Autowired
    private UserService userService;

    @Override
    public UserAccount findUserAccountByAccountId(Long accountId) {
        return userAccountRepository.findUserAccountByAccountId(accountId);
    }

    /**
     * 						源表名	源表字段
     * 用户账户ID, 自维护序列	account_id	bigint(16)				member_account	memberAccountId
     * 用户ID	user_id	int(10)				member_account	memberId
     * 用户银行卡ID	bankcard_id	int(10)				user_bankcard
     * 账户类型, 由账户类别和账户类型代码拼接	account_type	char(2)		自维护		account_type
     * 账号	account_no	int(10)		自维护
     * 外部托管账号	deposit_no	varchar(64)		通联给
     * 账户余额	balance	int(10)				member_account	mainAccountBalance  多字段合并 1
     * 可用余额	available	int(10)				member_account	mainAccountBalance
     * 费率	fee_rate	smallint(6)				member_account	feeRate
     * 账户状态: N-正常, L-锁定, D-禁用, C-注销	status	char(1)		N
     * 支付优先级, 值越大优先级越高	pay_rank	tinyint(1)		0 后期you
     * 单笔限额	order_limit	int(10)		null后期you
     * 单日限额	daily_limit	int(10)		null后期you
     * 创建时间	createTime	datetime
     * 更新时间	update_time	datetime				member_account	modifyTime
     * @param memberAccountList
     * @param memberIdLinkUserIdMap
     * @return
     */
    @Override
    public int saveUserAccountByMemberAccountList(List<MemberAccount> memberAccountList, Map<String, Long> memberIdLinkUserIdMap){

        //因为 userAccount需要 user的 userId值做关联
        //Map<String, Long> userIdHashMap = getUserIdMapByMemberIdList(memberAccountList);
        if(memberIdLinkUserIdMap == null){
            log.error("未保存提前结束！ ");
            return 0;
        }


        //List<UserAccount> userAccountList = BeanUtils.fastCopyProperties(memberAccountList, UserAccount.class);
        List<UserAccount> userAccountList = new ArrayList(memberAccountList.size());

        for(MemberAccount memberAccount : memberAccountList){
            UserAccount userAccount = BeanUtils.fastClone(UserAccount.class);
            Integer randomInt10 = Integer.valueOf(RandomUtil.randomNumbers(8));
            String randomStr10 = RandomUtil.randomString(10);
            Date date = new Date();

            //TODO 用户账户ID, 自维护序列
            userAccount.setAccountId(memberAccount.getMemberAccountId().longValue());

            Long userId = memberIdLinkUserIdMap.get(memberAccount.getMemberId() + "");
            if(StrUtil.isEmptyIfStr(userId)){
                log.error("通过userAccount MemberId={} 在userIdHashMap中未查到相应userId ", memberAccount.getMemberId());
                //userId = randomInt10.longValue();
                continue;
            }
            userAccount.setUserId(userId);
            userAccount.setBankcardId(randomInt10.longValue());
            //TODO 账户类型, 由账户类别和账户类型代码拼接
            userAccount.setAccountType("CM");
            //TODO 账号 自维护
            userAccount.setAccountNo(randomInt10.longValue());
            //TODO 外部托管账号  通联给 randomStr10
            userAccount.setDepositNo("");
            userAccount.setBalance(memberAccount.getMainAccountBalance().longValue());
            //TODO 暂置为空
            userAccount.setAvailable(0L);
            userAccount.setFeeRate(memberAccount.getFeeRate());
            userAccount.setStatus("N");
            userAccount.setPayRank(0);
            //TODO 单笔限额
            userAccount.setOrderLimit(1000L);
            userAccount.setDailyLimit(1000L);

            userAccount.setCreateTime(date);
            userAccount.setUpdateTime(DateUtil.parse(memberAccount.getModifyTime(), DatePattern.PURE_DATETIME_PATTERN));

            userAccountList.add(userAccount);
        }

        userAccountBatchRepository.batchInsertIfExistUpdate(userAccountList);

        String startUpdateTimeStr = memberAccountList.get(0).getModifyTime();
        String endUpdateTimeStr = memberAccountList.get(memberAccountList.size()-1).getModifyTime();
        log.info("保存了【" + startUpdateTimeStr + ", "+ endUpdateTimeStr + "】");

        return userAccountList.size();
    }



    private Map<String,Long> getUserIdMapByMemberUpdateTime(String startUpdateTimeStr, String endUpdateTimeStr, Integer size) {

        Map<String, Long> userIdHashMap = new HashMap<>(size);

        Date startUpdateTime = DateUtil.parse(startUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        DateTime startUpdateTimeSub1 = DateUtil.offsetHour(startUpdateTime, -1);
        Date endUpdateTime = DateUtil.parse(endUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        DateTime endUpdateTimeAdd1 = DateUtil.offsetHour(endUpdateTime, 1);

        String startUpdateTimeSub1Str = DateUtil.format(startUpdateTimeSub1, DatePattern.NORM_DATETIME_PATTERN);
        String endUpdateTimeAdd1Str = DateUtil.format(endUpdateTimeAdd1, DatePattern.NORM_DATETIME_PATTERN);
        log.info("getUserIdMapByMemberUpdateTime startUpdateTimeSub1Str={}, endUpdateTimeAdd1Str={}", startUpdateTimeSub1Str, endUpdateTimeAdd1Str);
        List<User> userPartList = userService.findUserPartList(startUpdateTimeSub1Str, endUpdateTimeAdd1Str);
        if(userPartList.isEmpty()){
            log.info("通过startMemberId={}, endMemberId={}未查到数据！", startUpdateTimeSub1Str, endUpdateTimeAdd1Str);
            return null;
        }

        for (User user : userPartList) {
            userIdHashMap.put(user.getMemberId().toString(), user.getUserId().longValue());
        }
        return userIdHashMap;
    }
}

