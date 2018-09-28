package com.pagoda.account.migrate.producerconsumer.useraccountbyid;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.pagoda.account.common.util.BeanUtils;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseConsumer;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseProdConsManager;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseProducer;
import com.pagoda.account.migrate.service.account.UserAccountService;
import com.pagoda.account.migrate.service.account.UserService;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
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
public class UserAccountProdConsManager extends BaseProdConsManager {

    private IMemberAccountService memberAccountService;

    private IMemberService memberService;

    private UserAccountService userAccountService;

    private UserService userService;

    private String startUpdateTimeStr;
    private String endUpdateTimeStr;


    @Override
    protected Long getTaskPageSize() {

        //1 首先 userAccount 依赖于user  所以 按照user 全量 分页 memberIds+ userAccount的modifyTime时间段
        Map<String, Object> minAndMaxAndCountModifyTimeMap = memberAccountService.findMinAndMaxAndCountModifyTime(0);


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
        String beginUpdateTimeStr = ProdConsWorkerConstants.UserAccount.BEGIN_UPDATE_TIME_STR; //20161102050540 20161101000000
        String terminalUpdateTimeStr = ProdConsWorkerConstants.UserAccount.TERMINAL_UPDATE_TIME_STR;//"20160201000000" 20161101000000-20170101000000

        //TODO
       // int memberIdCount = ProdConsWorkerConstants.UserAccount.MEMBER_ID_COUNT.intValue() ;
        //int batchCount = ProdConsWorkerConstants.UserAccount.BATCH_COUNT.intValue() ;
       // Long totalCount = ProdConsWorkerConstants.UserAccount.TOTAL_COUNT;//115002L
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
/*
        if(beginUpdateTimeMs > minUpdateTime){
            minUpdateTime = beginUpdateTimeMs;
        }
        if(terminalUpdateTimeMs < maxUpdateTime){
            maxUpdateTime = terminalUpdateTimeMs;
        }*/


        String startUpdateTimeStr = DateUtil.format(DateUtil.date(minUpdateTime), DatePattern.PURE_DATETIME_PATTERN);
        String endUpdateTimeStr = DateUtil.format(DateUtil.date(maxUpdateTime), DatePattern.PURE_DATETIME_PATTERN);

        this.startUpdateTimeStr = startUpdateTimeStr;
        this.endUpdateTimeStr = endUpdateTimeStr;




        //Long pageCount = userService.countUserPartList(beginUpdateTimeStr, terminalUpdateTimeStr);

        //基于user表分页
        Long startMemberId = ProdConsWorkerConstants.User.START_MEMBER_ID; //

        Long totalCount = ProdConsWorkerConstants.User.TOTAL_COUNT;//115002L

        int batchCount = ProdConsWorkerConstants.User.BATCH_COUNT.intValue() ;

        //Long pageCount = userService.countUserPartList(beginUpdateTimeStr, terminalUpdateTimeStr);
        Long pageCount = totalCount;
        if(pageCount == 0){
            log.error("通过时间段-【beginUpdateTimeStr={}, terminalUpdateTimeStr={}】查询数据为空！", beginUpdateTimeStr, terminalUpdateTimeStr);
            return 0L;
        }
        Long pageSize = NumberUtil.round(pageCount * 1D/ batchCount, 0,RoundingMode.UP).longValue();
        Long totalSize = NumberUtil.round(totalCount * 1D/ batchCount, 0,RoundingMode.UP).longValue();
/*        if(totalCount < pageCount2){
            pageCount2 = totalCount;
        }*/

        log.info("【startUpdateTime={}, endUpdateTime={}, pageSize={}】", startUpdateTimeStr, endUpdateTimeStr, pageSize);


        return pageSize;
    }

    @Override
    protected BaseProducer buildProducer() {
        UserAccountProducer userAccountProducer = BeanUtils.fastClone(UserAccountProducer.class);
        userAccountProducer.setMemberAccountService(memberAccountService).setUserService(userService)
                .setStartUpdateTimeStr(startUpdateTimeStr).setEndUpdateTimeStr(endUpdateTimeStr)
                    .setMemberIdCount(ProdConsWorkerConstants.User.BATCH_COUNT)
                    .setGoodsMapMainDataMapKey(ProdConsWorkerConstants.UserAccount.GOODS_MAP_LIST_KEY)
        ;

        return userAccountProducer;
    }

    @Override
    protected BaseConsumer buildConsumer() {
        UserAccountConsumer userAccountConsumer = BeanUtils.fastClone(UserAccountConsumer.class);
        userAccountConsumer.setUserAccountService(userAccountService)
                .setStartUpdateTimeStr(startUpdateTimeStr).setEndUpdateTimeStr(endUpdateTimeStr)
                    .setGoodsMapMainDataMapKey(ProdConsWorkerConstants.UserAccount.GOODS_MAP_LIST_KEY)
        ;

        return userAccountConsumer;
    }


    public IMemberAccountService getMemberAccountService() {
        return memberAccountService;
    }

    public UserAccountProdConsManager setMemberAccountService(IMemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
        return this;
    }

    public IMemberService getMemberService() {
        return memberService;
    }

    public UserAccountProdConsManager setMemberService(IMemberService memberService) {
        this.memberService = memberService;
        return this;
    }

    public UserAccountService getUserAccountService() {
        return userAccountService;
    }

    public UserAccountProdConsManager setUserAccountService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
        return this;
    }

    public UserService getUserService() {
        return userService;
    }

    public UserAccountProdConsManager setUserService(UserService userService) {
        this.userService = userService;
        return this;
    }

    public String getStartUpdateTimeStr() {
        return startUpdateTimeStr;
    }

    public UserAccountProdConsManager setStartUpdateTimeStr(String startUpdateTimeStr) {
        this.startUpdateTimeStr = startUpdateTimeStr;
        return this;
    }

    public String getEndUpdateTimeStr() {
        return endUpdateTimeStr;
    }

    public UserAccountProdConsManager setEndUpdateTimeStr(String endUpdateTimeStr) {
        this.endUpdateTimeStr = endUpdateTimeStr;
        return this;
    }

}
