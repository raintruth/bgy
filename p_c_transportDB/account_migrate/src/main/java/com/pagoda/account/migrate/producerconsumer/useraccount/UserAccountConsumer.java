package com.pagoda.account.migrate.producerconsumer.useraccount;

import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.producerconsumer.base.normal.ResultCode;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseConsumer;
import com.pagoda.account.migrate.service.account.UserAccountService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */

@Slf4j
public class UserAccountConsumer extends BaseConsumer {

    private UserAccountService userAccountService;

    private String startUpdateTimeStr;
    private String endUpdateTimeStr;


    @Override
    public ResultCode consumeGoods(Map<String, Object> goodsMap) {

        List<MemberAccount> memberAccountValidPartList = (List<MemberAccount>) goodsMap.get(ProdConsWorkerConstants.Common.GOODS_MAP_LIST_KEY);
        Map<String, Long> memberIdLinkUserIdMap = (Map<String, Long>) goodsMap.get("memberIdLinkUserIdMap");
        //int i = 1/0;

        int count = userAccountService.saveUserAccountByMemberAccountList(memberAccountValidPartList, memberIdLinkUserIdMap);
        if(count == 0){

            log.info("{}消费了 货物={}个失败!", getIdentity(), memberAccountValidPartList.size());
            return ResultCode.EMPTY;
        }

        return ResultCode.SUCCESS;
    }

    @Override
    protected void recordWorkingInfo(StringBuilder workingInfo, Map<String, Object> goodsMap) {
        workingInfo.append("<").append(getIdentity()).append(" 查询总数据区间[").append(startUpdateTimeStr).append(", ").append(endUpdateTimeStr).append("] >");
        List<MemberAccount> memberAccountValidPartList = (List<MemberAccount>) goodsMap.get(ProdConsWorkerConstants.UserAccount.GOODS_MAP_LIST_KEY);

        String partBeginModifyTime = memberAccountValidPartList.get(0).getModifyTime();
        String partEndModifyTime = memberAccountValidPartList.get(memberAccountValidPartList.size() - 1).getModifyTime();
        log.info(" {} 消费了 一个 货物包({}件), partBeginModifyTime,partEndModifyTime[{}, {}]", getIdentity(), memberAccountValidPartList.size(), partBeginModifyTime, partEndModifyTime);
        workingInfo.append("<").append(getIdentity()).append(" 消费 一个 货物包裹(").append(memberAccountValidPartList.size()).append("个), ModifyTime=[")
                .append(partBeginModifyTime).append(", ").append(partEndModifyTime).append("]").append(">");
    }


    public UserAccountService getUserAccountService() {
        return userAccountService;
    }

    public UserAccountConsumer setUserAccountService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
        return this;
    }

    public String getStartUpdateTimeStr() {
        return startUpdateTimeStr;
    }

    public UserAccountConsumer setStartUpdateTimeStr(String startUpdateTimeStr) {
        this.startUpdateTimeStr = startUpdateTimeStr;
        return this;
    }

    public String getEndUpdateTimeStr() {
        return endUpdateTimeStr;
    }

    public UserAccountConsumer setEndUpdateTimeStr(String endUpdateTimeStr) {
        this.endUpdateTimeStr = endUpdateTimeStr;
        return this;
    }


}
