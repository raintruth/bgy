package com.pagoda.account.migrate.producerconsumer.userbyid;

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

    private Long startMemberId;

    private Long endMemberId;

    private Long batchCount;


    @Override
    public Map<String, Object> productGoods() {
        HashMap<String, Object> goodsMap = new HashMap<>(1);

        Pageable pageable = PageRequest.of(getTaskNum(), batchCount.intValue());

        List<Member> memberPartList =
                memberService.findMemberPageList(startMemberId, endMemberId, pageable);
        if(!memberPartList.isEmpty()){

            goodsMap.put(ProdConsWorkerConstants.User.GOODS_MAP_LIST_KEY, memberPartList);
        }

        return goodsMap;
    }

    @Override
    protected void recordWorkingInfo(StringBuilder workingInfo, Map<String, Object> goodsMap) {
        workingInfo.append("<").append(getIdentity()).append(" 查询总数据区间[").append(startMemberId).append(", ").append(endMemberId).append("] >");
        List<Member> memberPartList = (List<Member>) goodsMap.get(goodsMapMainDataMapKey);

        Long partBeginMemberId = memberPartList.get(0).getMemberId().longValue();
        Long partEndMemberId = memberPartList.get(memberPartList.size() - 1).getMemberId().longValue();
        log.info(" {} 生产了 一个 货物包({}件), partBeginMemberId,partEndMemberId[{}, {}]", getIdentity(), memberPartList.size(), partBeginMemberId, partEndMemberId);
        workingInfo.append("<").append(getIdentity()).append(" 生产 一个 货物包裹(").append(memberPartList.size()).append("个), MemberId=[")
                .append(partBeginMemberId).append(", ").append(partEndMemberId).append("]").append(">");
    }


    public IMemberService getMemberService() {
        return memberService;
    }

    public UserProducer setMemberService(IMemberService memberService) {
        this.memberService = memberService;
        return this;
    }

    public Long getStartMemberId() {
        return startMemberId;
    }

    public UserProducer setStartMemberId(Long startMemberId) {
        this.startMemberId = startMemberId;
        return this;
    }

    public Long getEndMemberId() {
        return endMemberId;
    }

    public UserProducer setEndMemberId(Long endMemberId) {
        this.endMemberId = endMemberId;
        return this;
    }


    public Long getBatchCount() {
        return batchCount;
    }

    public UserProducer setBatchCount(Long batchCount) {
        this.batchCount = batchCount;
        return this;
    }

}
