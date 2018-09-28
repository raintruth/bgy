package com.pagoda.account.migrate.producerconsumer.useraccountbyid;

import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseConsumer;
import com.pagoda.account.migrate.producerconsumer.base.normal.ResultCode;
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

        List<MemberAccount> memberAccountList = (List<MemberAccount>) goodsMap.get(this.goodsMapMainDataMapKey);
        Map<String, Long> memberIdLinkUserIdMap = (Map<String, Long>) goodsMap.get(ProdConsWorkerConstants.UserAccount.GOODS_MAP_MEMBER_ID_LINK_USER_ID_MAP_KEY);
   // int i = 1/0;
        if(memberAccountList == null || memberAccountList.isEmpty()){
            log.error("memberAccountList 数据为空！ ");
            return ResultCode.EMPTY;
        }

        int count = userAccountService.saveUserAccountByMemberAccountList(memberAccountList, memberIdLinkUserIdMap);
        if(count == 0){

            log.info("{}消费了 货物={}个失败!", getIdentity(), memberAccountList.size());
            return ResultCode.EMPTY;
        }

        return ResultCode.SUCCESS;
    }

    @Override
    protected void recordWorkingInfo(StringBuilder workingInfo, Map<String, Object> goodsMap) {
        workingInfo.append("<").append(getIdentity()).append(" 查询总数据区间[").append(startUpdateTimeStr).append(", ").append(endUpdateTimeStr).append("] >");
        List<MemberAccount> memberAccountValidPartList = (List<MemberAccount>) goodsMap.get(goodsMapMainDataMapKey);

        Long partBeginMemberId = memberAccountValidPartList.get(0).getMemberId().longValue();
        Long partEndMemberId = memberAccountValidPartList.get(memberAccountValidPartList.size() - 1).getMemberId().longValue();
        log.info(" {} 消费了 一个 货物包({}件), partBeginMemberId,partEndMemberId[{}, {}]", getIdentity(), memberAccountValidPartList.size(), partBeginMemberId, partEndMemberId);
        workingInfo.append("<").append(getIdentity()).append(" 消费 一个 货物包裹(").append(memberAccountValidPartList.size()).append("个), MemberId=[")
                .append(partBeginMemberId).append(", ").append(partEndMemberId).append("]").append(">");
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
