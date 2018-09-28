package com.pagoda.account.migrate.producerconsumer.old.memberaccount;

import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.service.account.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */

@Slf4j
public class ConsumerThread implements Callable {

    private int taskNum;
    private int taskNum2;
    private BlockingQueue queue;
    private CountDownLatch productGate;
    private UserAccountService userAccountService;

    private String startUpdateTimeStr;
    private String endUpdateTimeStr;

    private Pageable pageable;

    public ConsumerThread(int taskNum, BlockingQueue queue, UserAccountService userAccountService) {
        this.taskNum = taskNum;
        this.queue = queue;
        this.userAccountService = userAccountService;
    }

    public ConsumerThread(int taskNum, BlockingQueue queue, UserAccountService userAccountService, String startUpdateTimeStr, String endUpdateTimeStr, Pageable pageable) {
        this.taskNum = taskNum;
        this.queue = queue;
        this.userAccountService = userAccountService;
        this.startUpdateTimeStr = startUpdateTimeStr;
        this.endUpdateTimeStr = endUpdateTimeStr;
        this.pageable = pageable;
    }

    public ConsumerThread(int taskNum, int taskNum2, BlockingQueue queue, CountDownLatch productGate, UserAccountService userAccountService, String startUpdateTimeStr, String endUpdateTimeStr, Pageable pageable) {
        this.taskNum = taskNum;
        this.taskNum2 = taskNum2;
        this.queue = queue;
        this.productGate = productGate;
        this.userAccountService = userAccountService;
        this.startUpdateTimeStr = startUpdateTimeStr;
        this.endUpdateTimeStr = endUpdateTimeStr;
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
            log.info("taskNum={} taskNum2={}号准备消费 ing-- before queue 货物数量={} ", taskNum, taskNum2, queue.size());
            Map<String, Object> storageInfoMap = null;
            while(productGate.getCount() > 0 || queue.size() > 0){
                //take 取不到数据会一直阻塞，不好 Map<String, Object> storageInfoMap = (Map<String, Object>) queue.take();

                storageInfoMap = (Map<String, Object>) queue.poll(5, TimeUnit.SECONDS);
                if(storageInfoMap != null){

                    break;
                }else {
                    try {
                        log.info("taskNum={} ={}号 等了5秒没有发现货物，等待1秒继续--, 生产者还有{}个忙碌中-", taskNum, taskNum2, productGate.getCount());
                        Thread.sleep(1000);//每次轮询休息1毫秒（CPU纳秒级），避免CPU高速轮循耗空CPU-
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(storageInfoMap == null){
                log.info("taskNum={} taskNum2={}号 没有东西可消费 已停止！");
                sb.append(" empty");
            }else {

                List<MemberAccount> memberAccountValidPartList = (List<MemberAccount>) storageInfoMap.get(ProdConsWorkerConstants.Common.GOODS_MAP_LIST_KEY);
                Map<String, Long> memberIdLinkUserIdMap = (Map<String, Long>) storageInfoMap.get("memberIdLinkUserIdMap");


                String modifyTime = memberAccountValidPartList.get(memberAccountValidPartList.size() - 1).getModifyTime();
                int count = userAccountService.saveUserAccountByMemberAccountList(memberAccountValidPartList, memberIdLinkUserIdMap);
                if(count > 0){

                    log.info("taskNum={} taskNum2={}号消费了 货物={}个, lastModifyTime={}", taskNum, taskNum2, memberAccountValidPartList.size(), modifyTime);
                    sb.append(" listSize=").append(memberAccountValidPartList.size());
                    sb.append(" success");
                }else {
                    log.info("taskNum={} taskNum2={}号消费了 货物={}个失败, lastModifyTime={}", taskNum, taskNum2, memberAccountValidPartList.size(), modifyTime);
                    sb.append(" fail");
                }
            }

        } catch (Exception e) {
            log.error(" taskNum={} taskNum2={}号异常 消费 ", taskNum, taskNum2, e);
            sb.append(" fail");
        }

        long endTime = System.currentTimeMillis();
        sb.append(" 耗时：").append((endTime-startTime)/1000.0).append("s");
        return sb.toString();
    }
}
