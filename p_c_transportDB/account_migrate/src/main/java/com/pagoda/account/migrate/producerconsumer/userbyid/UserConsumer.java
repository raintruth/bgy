package com.pagoda.account.migrate.producerconsumer.userbyid;

import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.producerconsumer.base.impl.BaseConsumer;
import com.pagoda.account.migrate.producerconsumer.base.normal.ResultCode;
import com.pagoda.account.migrate.service.account.UserService;
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
public class UserConsumer extends BaseConsumer {

    private UserService userService;

    private Long startMemberId;

    private Long endMemberId;

    private Long batchCount;


    @Override
    public ResultCode consumeGoods(Map<String, Object> storageInfoMap) {
//int i= 1/0;
        List<Member> memberValidPartList = (List<Member>) storageInfoMap.get(ProdConsWorkerConstants.User.GOODS_MAP_LIST_KEY);
        int count = userService.saveUserByMemberList(memberValidPartList);

        if(count == 0){
            log.info("{}消费了 一个 货物包({}件),失败", getIdentity(), memberValidPartList.size());
            return ResultCode.EMPTY;
        }

        return ResultCode.SUCCESS;
    }

    @Override
    protected void recordWorkingInfo(StringBuilder workingInfo, Map<String, Object> goodsMap) {
        workingInfo.append("<").append(getIdentity()).append(" 查询总数据区间[").append(startMemberId).append(", ").append(endMemberId).append("] >");
        List<Member> memberPartList = (List<Member>) goodsMap.get(goodsMapMainDataMapKey);

        Long partBeginMemberId = memberPartList.get(0).getMemberId().longValue();
        Long partEndMemberId = memberPartList.get(memberPartList.size() - 1).getMemberId().longValue();
        log.info(" {} 消费了 一个 货物包({}件), partBeginMemberId,partEndMemberId[{}, {}]", getIdentity(), memberPartList.size(), partBeginMemberId, partEndMemberId);
        workingInfo.append("<").append(getIdentity()).append(" 消费 一个 货物包裹(").append(memberPartList.size()).append("个), MemberId=[")
                .append(partBeginMemberId).append(", ").append(partEndMemberId).append("]").append(">");
    }


    public UserService getUserService() {
        return userService;
    }

    public UserConsumer setUserService(UserService userService) {
        this.userService = userService;
        return this;
    }


    public Long getStartMemberId() {
        return startMemberId;
    }

    public UserConsumer setStartMemberId(Long startMemberId) {
        this.startMemberId = startMemberId;
        return this;
    }

    public Long getEndMemberId() {
        return endMemberId;
    }

    public UserConsumer setEndMemberId(Long endMemberId) {
        this.endMemberId = endMemberId;
        return this;
    }


    public Long getBatchCount() {
        return batchCount;
    }

    public UserConsumer setBatchCount(Long batchCount) {
        this.batchCount = batchCount;
        return this;
    }


}
