package com.pagoda.account.migrate.task;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.pagoda.account.common.util.DateUtils;
import com.pagoda.account.migrate.dataobject.account.UserAccountRecord;
import com.pagoda.account.migrate.dataobject.member.MemberWalletRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 线程池任务处理器
 */
public class CompletionServiceRecordTask implements Callable<UserAccountRecord>{

    MemberWalletRecord memberWalletRecord;
    Map<String,Object> userAccount;

    public CompletionServiceRecordTask(MemberWalletRecord memberWalletRecord, Map<String,Object> userAccount){
        super();
        this.memberWalletRecord= memberWalletRecord;
        this.userAccount = userAccount;
    }

    @Override
    public UserAccountRecord call() throws Exception {
        if(null != memberWalletRecord && null!= userAccount){
            //同一个用户的才会进行保存操作
            Integer mainMoneyAfterModify = memberWalletRecord.getMainMoneyAfterModify();
            Integer mainMoneyBeforeModify = memberWalletRecord.getMainMoneyBeforeModify();
            if(null != mainMoneyAfterModify && null != mainMoneyBeforeModify){
                int money = memberWalletRecord.getMainMoneyAfterModify() - memberWalletRecord.getMainMoneyBeforeModify();
                if(money != 0 && null != memberWalletRecord.getModifyType()){
                    UserAccountRecord accountRecord = new UserAccountRecord();
                    accountRecord.setChannel(memberWalletRecord.getUpdateChannel());
                    accountRecord.setChannelValue(memberWalletRecord.getChannelValue());
                    accountRecord.setAccountType(userAccount.get("accountType").toString());
                    accountRecord.setModifyType(memberWalletRecord.getModifyType());
                    accountRecord.setActivityCode(memberWalletRecord.getActivityCode());
                    accountRecord.setOperator(memberWalletRecord.getOperator());
                    accountRecord.setMoney(Math.abs(money));
                    accountRecord.setUpdateTime(DateUtil.parse(memberWalletRecord.getUpdateTime(), DatePattern.PURE_DATETIME_PATTERN));
                    accountRecord.setCreateTime(DateUtil.parse(memberWalletRecord.getModifyTime(), DatePattern.PURE_DATETIME_PATTERN));
                    //原编码
                    accountRecord.setOriginalCode("");
                    accountRecord.setMerchantOrderNo(memberWalletRecord.getDealSerialNum());
                    accountRecord.setUserId(Integer.valueOf(userAccount.get("userId").toString()));
                    accountRecord.setAccountId(Integer.valueOf(userAccount.get("accountId").toString()));
                    accountRecord.setBalanceAfterModify(memberWalletRecord.getMainMoneyAfterModify());
                    accountRecord.setBalanceBeforeModify(memberWalletRecord.getMainMoneyBeforeModify());
                    accountRecord.setRecordId(Long.valueOf(memberWalletRecord.getRecordId()));
                    accountRecord.setStatus("N");
                    return accountRecord;
                }
            }
        }
        return null;
    }


}
