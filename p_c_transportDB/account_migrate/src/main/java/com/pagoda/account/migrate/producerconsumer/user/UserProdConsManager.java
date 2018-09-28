package com.pagoda.account.migrate.producerconsumer.user;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
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
import java.util.Date;
import java.util.Map;

/**
 * @author wfg
 * @since 山竹
 */
@Slf4j
public class UserProdConsManager extends BaseProdConsManager {

    private IMemberService memberService;
    private UserService userService;

    private String startUpdateTimeStr;
    private String endUpdateTimeStr;


    @Override
    protected Long getTaskPageSize() {

        Map<String, Object> minAndMaxAndCountModifyTimeMap = memberService.findMinAndMaxAndCountModifyTime();

        String minUpdateTimeStr = minAndMaxAndCountModifyTimeMap.get("minUpdateTime").toString();
        String maxUpdateTimeStr = minAndMaxAndCountModifyTimeMap.get("maxUpdateTime").toString();
        Long memberCount = new BigInteger(minAndMaxAndCountModifyTimeMap.get("memberCount").toString()).longValue();
        log.info("minAndMaxAndCountMemberIdMap={}", minAndMaxAndCountModifyTimeMap.entrySet().toString());

        Date minUpdateTimeD = DateUtil.parse(minUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        Date maxUpdateTimeD = DateUtil.parse(maxUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        //毫秒值
        long minUpdateTime = minUpdateTimeD.getTime();
        long maxUpdateTime = maxUpdateTimeD.getTime();


        long betweenDay = DateUtil.betweenDay(minUpdateTimeD, maxUpdateTimeD, true);
        log.info("betweenDay={}", betweenDay);


        //TODO 这里是 user表的最大最小区间
        String beginUpdateTimeStr = ProdConsWorkerConstants.User.BEGIN_UPDATE_TIME_STR; //20161102050540 20161101000000
        String terminalUpdateTimeStr = ProdConsWorkerConstants.User.TERMINAL_UPDATE_TIME_STR;//"20160201000000" 20161101000000-20170101000000


        //TODO
        int memberIdCount = ProdConsWorkerConstants.User.MEMBER_ID_COUNT.intValue() ;
        int batchCount = ProdConsWorkerConstants.User.BATCH_COUNT.intValue() ;
        Long totalCount = ProdConsWorkerConstants.User.TOTAL_COUNT;//115002L
//int terminalMemberId = 50000;


        if(StrUtil.isEmpty(beginUpdateTimeStr) ){
            beginUpdateTimeStr = minUpdateTimeStr;
        }
        if(StrUtil.isEmpty(terminalUpdateTimeStr) ){
            terminalUpdateTimeStr = maxUpdateTimeStr;
        }

        Date beginUpdateTimeD = DateUtil.parse(beginUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        long beginUpdateTimeMs = beginUpdateTimeD.getTime();

        Date terminalUpdateTimeD = DateUtil.parse(terminalUpdateTimeStr, DatePattern.PURE_DATETIME_PATTERN);
        long terminalUpdateTimeMs = terminalUpdateTimeD.getTime();


        if(beginUpdateTimeMs > terminalUpdateTimeMs){
            log.error("起始时间不能大于结束时间-beginUpdateTimeMs={}>terminalUpdateTimeMs={}", beginUpdateTimeMs, terminalUpdateTimeMs);
            return 0L;
        }

        if(beginUpdateTimeMs > minUpdateTime){
            minUpdateTime = beginUpdateTimeMs;
        }
        if(terminalUpdateTimeMs < maxUpdateTime){
            maxUpdateTime = terminalUpdateTimeMs;
        }


        String startUpdateTimeStr = DateUtil.format(DateUtil.date(minUpdateTime), DatePattern.PURE_DATETIME_PATTERN);
        String endUpdateTimeStr = DateUtil.format(DateUtil.date(maxUpdateTime), DatePattern.PURE_DATETIME_PATTERN);

        this.startUpdateTimeStr = startUpdateTimeStr;
        this.endUpdateTimeStr = endUpdateTimeStr;


        Long pageCount = memberService.countMemberPartList(startUpdateTimeStr, endUpdateTimeStr);
        if(pageCount == 0){
            log.error("通过时间段-【startUpdateTime={},endUpdateTime={}】查询数据为空！", startUpdateTimeStr, endUpdateTimeStr);
            return 0L;
        }
        Long pageSize = NumberUtil.round(pageCount * 1D/ batchCount, 0,RoundingMode.UP).longValue();
        Long totalSize = NumberUtil.round(totalCount * 1D/ batchCount, 0,RoundingMode.UP).longValue();
        if(totalSize < pageSize){
            pageSize = totalSize;
        }

        log.info("【startUpdateTime={}, endUpdateTime={}, pageCount={}】", startUpdateTimeStr, endUpdateTimeStr, pageCount);
        return pageSize;
    }

    @Override
    protected BaseProducer buildProducer() {
        UserProducer userProducer = BeanUtils.fastClone(UserProducer.class);
        userProducer.setMemberService(memberService)
                .setStartUpdateTimeStr(startUpdateTimeStr).setEndUpdateTimeStr(endUpdateTimeStr)
                    .setMemberIdCount(ProdConsWorkerConstants.User.MEMBER_ID_COUNT);


        return userProducer;
    }

    @Override
    protected BaseConsumer buildConsumer() {
        UserConsumer userConsumer = BeanUtils.fastClone(UserConsumer.class);
        userConsumer.setUserService(userService)
                .setStartUpdateTimeStr(startUpdateTimeStr).setEndUpdateTimeStr(endUpdateTimeStr);

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

    public String getStartUpdateTimeStr() {
        return startUpdateTimeStr;
    }

    public UserProdConsManager setStartUpdateTimeStr(String startUpdateTimeStr) {
        this.startUpdateTimeStr = startUpdateTimeStr;
        return this;
    }

    public String getEndUpdateTimeStr() {
        return endUpdateTimeStr;
    }

    public UserProdConsManager setEndUpdateTimeStr(String endUpdateTimeStr) {
        this.endUpdateTimeStr = endUpdateTimeStr;
        return this;
    }

}
