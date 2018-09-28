package com.pagoda.account.migrate.producerconsumer.old.memberaccount;

import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */

@Slf4j
public class ProductThread implements Callable {

    private int taskNum;
    private int taskNum2;
    private BlockingQueue queue;
    private CountDownLatch productGate;
    private IMemberAccountService memberAccountService;

    private String startUpdateTimeStr;
    private String endUpdateTimeStr;
    private Map<String, Object> storageInfoMap;

    private Pageable pageable;

    public ProductThread(int taskNum, BlockingQueue queue, IMemberAccountService memberAccountService) {
        this.taskNum = taskNum;
        this.queue = queue;
        this.memberAccountService = memberAccountService;
    }

    public ProductThread(int taskNum, BlockingQueue queue, IMemberAccountService memberAccountService, String startUpdateTimeStr, String endUpdateTimeStr, Pageable pageable) {
        this.taskNum = taskNum;
        this.queue = queue;
        this.memberAccountService = memberAccountService;
        this.startUpdateTimeStr = startUpdateTimeStr;
        this.endUpdateTimeStr = endUpdateTimeStr;
        this.pageable = pageable;
    }


    public ProductThread(int curIndex, int taskNum2, BlockingQueue publicBoxQueue, CountDownLatch productGate, IMemberAccountService memberAccountService, String startUpdateTimeStr, String endUpdateTimeStr, Map<String,Object> storageInfoMap, Object o) {
        this.taskNum = curIndex;
        this.taskNum2 = taskNum2;
        this.queue = publicBoxQueue;
        this.productGate = productGate;
        this.memberAccountService = memberAccountService;
        this.startUpdateTimeStr = startUpdateTimeStr;
        this.endUpdateTimeStr = endUpdateTimeStr;
        this.storageInfoMap = storageInfoMap;
        this.pageable = pageable;
    }

    public int getTaskNum() {
        return taskNum;
    }


    @Override
    public String call() throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(startUpdateTimeStr).append(", ").append(endUpdateTimeStr).append(")");

        long startTime = System.currentTimeMillis();
        try {
            log.info("taskNum={} taskNum2={}号准备生产 ing--", taskNum, taskNum2);
            List<Long> memberIdList = (List<Long>) storageInfoMap.get("memberIdList");
           // List<MemberAccount> memberAccountValidPartList =
            //        memberAccountService.findMemberAccountValidPageList(startUpdateTimeStr, endUpdateTimeStr, 0, memberIdList, pageable);
            List<MemberAccount> memberAccountValidPartList =
                    memberAccountService.findMemberAccountValidPageList(0, memberIdList);
            if(!memberAccountValidPartList.isEmpty()){

                String modifyTime = memberAccountValidPartList.get(memberAccountValidPartList.size() - 1).getModifyTime();
                log.info("taskNum={} taskNum2={}号 生产了 货物={}个, lastModifyTime={}", taskNum, taskNum2, memberAccountValidPartList.size(), modifyTime);
                storageInfoMap.put((ProdConsWorkerConstants.Common.GOODS_MAP_LIST_KEY), memberAccountValidPartList);
                //货物采用map结构，更灵活放置其他
                queue.put(storageInfoMap);
                sb.append(" listSize=").append(memberAccountValidPartList.size());
            }else {
                log.info(" taskNum={} taskNum2={}号生产 货物=  Empty ", taskNum, taskNum2);
            }
            sb.append(" success");
        } catch (Exception e) {
            log.error(" taskNum={} taskNum2={}号异常 生产 ", taskNum, taskNum2, e);
            sb.append(" fail");

        }finally {
            productGate.countDown();
        }

        long endTime = System.currentTimeMillis();
        sb.append(" 耗时：").append((endTime-startTime)/1000.0).append("s");
        return sb.toString();
    }

}
