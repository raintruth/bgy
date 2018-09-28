package com.pagoda.account.migrate.producerconsumer.useraccount;

import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseProducer;
import com.pagoda.account.migrate.service.account.UserService;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j
public class UserAccountProducer extends BaseProducer {

    private IMemberAccountService memberAccountService;
    private UserService userService;

    private String startUpdateTimeStr;
    private String endUpdateTimeStr;

    private Long memberIdCount;


    @Override
    public Map<String, Object> productGoods() {

        Pageable pageable = PageRequest.of(getTaskNum(), memberIdCount.intValue(), Sort.by("member_id"));
        Map<String, Object> goodsMap = userService.findMemberIdInfoMapByPage(pageable);
        List<Long> memberIdList = (List<Long>) goodsMap.get("memberIdList");
        // List<MemberAccount> memberAccountValidPartList =
        //        memberAccountService.findMemberAccountValidPageList(startUpdateTimeStr, endUpdateTimeStr, 0, memberIdList, pageable);
        List<MemberAccount> memberAccountValidPartList =
                memberAccountService.findMemberAccountValidPageList(0, memberIdList);
        if(!memberAccountValidPartList.isEmpty()){

            goodsMap.put(ProdConsWorkerConstants.UserAccount.GOODS_MAP_LIST_KEY, memberAccountValidPartList);
        }

        return goodsMap;
    }

    @Override
    protected void recordWorkingInfo(StringBuilder workingInfo, Map<String, Object> goodsMap) {
        workingInfo.append("<").append(getIdentity()).append(" 查询总数据区间[").append(startUpdateTimeStr).append(", ").append(endUpdateTimeStr).append("] >");
        List<MemberAccount> memberPartList = (List<MemberAccount>) goodsMap.get(ProdConsWorkerConstants.UserAccount.GOODS_MAP_LIST_KEY);

        String partBeginModifyTime = memberPartList.get(0).getModifyTime();
        String partEndModifyTime = memberPartList.get(memberPartList.size() - 1).getModifyTime();
        log.info(" {} 生产了 一个 货物包({}件), partBeginModifyTime,partEndModifyTime[{}, {}]", getIdentity(), memberPartList.size(), partBeginModifyTime, partEndModifyTime);
        workingInfo.append("<").append(getIdentity()).append(" 生产 一个 货物包裹(").append(memberPartList.size()).append("个), ModifyTime=[")
                .append(partBeginModifyTime).append(", ").append(partEndModifyTime).append("]").append(">");
    }


    public IMemberAccountService getMemberAccountService() {
        return memberAccountService;
    }

    public UserAccountProducer setMemberAccountService(IMemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
        return this;
    }

    public UserService getUserService() {
        return userService;
    }

    public UserAccountProducer setUserService(UserService userService) {
        this.userService = userService;
        return this;
    }

    public String getEndUpdateTimeStr() {
        return endUpdateTimeStr;
    }

    public UserAccountProducer setEndUpdateTimeStr(String endUpdateTimeStr) {
        this.endUpdateTimeStr = endUpdateTimeStr;
        return this;
    }

    public String getStartUpdateTimeStr() {
        return startUpdateTimeStr;
    }

    public UserAccountProducer setStartUpdateTimeStr(String startUpdateTimeStr) {
        this.startUpdateTimeStr = startUpdateTimeStr;
        return this;
    }


    public Long getMemberIdCount() {
        return memberIdCount;
    }

    public UserAccountProducer setMemberIdCount(Long memberIdCount) {
        this.memberIdCount = memberIdCount;
        return this;
    }

}
