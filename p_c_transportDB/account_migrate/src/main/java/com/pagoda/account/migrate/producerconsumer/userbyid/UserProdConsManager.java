package com.pagoda.account.migrate.producerconsumer.userbyid;

import cn.hutool.core.util.NumberUtil;
import com.pagoda.account.common.util.BeanUtils;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseConsumer;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseProdConsManager;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseProducer;
import com.pagoda.account.migrate.service.account.UserService;
import com.pagoda.account.migrate.service.member.IMemberService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @author wfg
 * @since 山竹
 */
@Slf4j
public class UserProdConsManager extends BaseProdConsManager {

    private IMemberService memberService;
    private UserService userService;

    private Long startMemberId;
    private Long endMemberId;


    @Override
    protected Long getTaskPageSize() {

        //TODO 这里是 user表的最大最小区间
        Long startMemberId = ProdConsWorkerConstants.User.START_MEMBER_ID; //

        Long totalCount = ProdConsWorkerConstants.User.TOTAL_COUNT;//115002L

        int batchCount = ProdConsWorkerConstants.User.BATCH_COUNT.intValue() ;

        Map<String, Object> minAndMaxMemberIdMap = memberService.findMinAndMaxMemberId(startMemberId, totalCount);

        Long minMemberId = new BigInteger(minAndMaxMemberIdMap.get("minMemberId").toString()).longValue();
        Long maxMemberId = new BigInteger(minAndMaxMemberIdMap.get("maxMemberId").toString()).longValue();
        log.info("minAndMaxMemberIdMap={}", minAndMaxMemberIdMap.entrySet().toString());


        this.startMemberId = minMemberId;
        this.endMemberId = maxMemberId;

        Long pageCount = totalCount;
        Long pageSize = NumberUtil.round(pageCount * 1D/ batchCount, 0,RoundingMode.UP).longValue();

        log.info("【startMemberId={}, endMemberId={}, pageCount={}】", startMemberId, endMemberId, pageCount);
        return pageSize;
    }

    @Override
    protected BaseProducer buildProducer() {
        UserProducer userProducer = BeanUtils.fastClone(UserProducer.class);
        userProducer.setMemberService(memberService)
                .setStartMemberId(startMemberId).setEndMemberId(endMemberId)
                    .setBatchCount(ProdConsWorkerConstants.User.MEMBER_ID_COUNT)
                    .setGoodsMapMainDataMapKey(ProdConsWorkerConstants.User.GOODS_MAP_LIST_KEY)
        ;


        return userProducer;
    }

    @Override
    protected BaseConsumer buildConsumer() {
        UserConsumer userConsumer = BeanUtils.fastClone(UserConsumer.class);
        userConsumer.setUserService(userService)
                .setStartMemberId(startMemberId).setEndMemberId(endMemberId)
                    .setBatchCount(ProdConsWorkerConstants.User.MEMBER_ID_COUNT)
                    .setGoodsMapMainDataMapKey(ProdConsWorkerConstants.User.GOODS_MAP_LIST_KEY)
        ;

        return userConsumer;
    }






    public IMemberService getMemberService() {
        return memberService;
    }

    public UserProdConsManager setMemberService(IMemberService memberService) {
        this.memberService = memberService;
        return this;
    }


    public UserService getUserService() {
        return userService;
    }

    public UserProdConsManager setUserService(UserService userService) {
        this.userService = userService;
        return this;
    }



}
