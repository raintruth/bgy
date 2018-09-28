package com.pagoda.account.migrate.producerconsumer.user;

import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseProducer;
import com.pagoda.account.migrate.service.member.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j
public class UserProducer extends BaseProducer {

    private IMemberService memberService;


    private String startUpdateTimeStr;
    private String endUpdateTimeStr;

    private Long memberIdCount;


    @Override
    public Map<String, Object> productGoods() {
        HashMap<String, Object> memberInfoMap = new HashMap<>(1);

        Pageable pageable = PageRequest.of(getTaskNum(), memberIdCount.intValue());//报
        // 错 FIXME Sort.by("modifyTime")
        List<Member> memberPartList =
                memberService.findMemberPageList(startUpdateTimeStr, endUpdateTimeStr, pageable);
        if(!memberPartList.isEmpty()){
            memberInfoMap.put(ProdConsWorkerConstants.Common.GOODS_MAP_LIST_KEY, memberPartList);
        }

        return memberInfoMap;
    }

    @Override
    protected void recordWorkingInfo(StringBuilder workingInfo, Map<String, Object> goodsMap) {
        workingInfo.append("<").append(getIdentity()).append(" 查询总数据区间[").append(startUpdateTimeStr).append(", ").append(endUpdateTimeStr).append("] >");
        List<Member> memberPartList = (List<Member>) goodsMap.get(ProdConsWorkerConstants.User.GOODS_MAP_LIST_KEY);

        String partBeginModifyTime = memberPartList.get(0).getModifyTime();
        String partEndModifyTime = memberPartList.get(memberPartList.size() - 1).getModifyTime();
        log.info(" {} 生产了 一个 货物包({}件), partBeginModifyTime,partEndModifyTime[{}, {}]", getIdentity(), memberPartList.size(), partBeginModifyTime, partEndModifyTime);
        workingInfo.append("<").append(getIdentity()).append(" 生产 一个 货物包裹(").append(memberPartList.size()).append("个), ModifyTime=[")
                .append(partBeginModifyTime).append(", ").append(partEndModifyTime).append("]").append(">");
    }


    public IMemberService getMemberService() {
        return memberService;
    }

    public UserProducer setMemberService(IMemberService memberService) {
        this.memberService = memberService;
        return this;
    }


    public String getEndUpdateTimeStr() {
        return endUpdateTimeStr;
    }

    public UserProducer setEndUpdateTimeStr(String endUpdateTimeStr) {
        this.endUpdateTimeStr = endUpdateTimeStr;
        return this;
    }

    public String getStartUpdateTimeStr() {
        return startUpdateTimeStr;
    }

    public UserProducer setStartUpdateTimeStr(String startUpdateTimeStr) {
        this.startUpdateTimeStr = startUpdateTimeStr;
        return this;
    }


    public Long getMemberIdCount() {
        return memberIdCount;
    }

    public UserProducer setMemberIdCount(Long memberIdCount) {
        this.memberIdCount = memberIdCount;
        return this;
    }


}
