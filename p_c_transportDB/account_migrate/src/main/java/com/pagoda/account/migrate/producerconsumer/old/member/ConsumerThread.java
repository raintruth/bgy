package com.pagoda.account.migrate.producerconsumer.old.member;

import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.service.account.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */

@Slf4j
public class ConsumerThread implements Callable {

    private int taskNum;
    private BlockingQueue queue;
    private UserService userService;

    private String startUpdateTimeStr;
    private String endUpdateTimeStr;

    private Pageable pageable;

    public ConsumerThread(int taskNum, BlockingQueue queue, UserService userService) {
        this.taskNum = taskNum;
        this.queue = queue;
        this.userService = userService;
    }

    public ConsumerThread(int taskNum, BlockingQueue queue, UserService userService, String startUpdateTimeStr, String endUpdateTimeStr, Pageable pageable) {
        this.taskNum = taskNum;
        this.queue = queue;
        this.userService = userService;
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
        List<Member> memberValidPartList;
        try {
            log.info("准备消费  taskNum= " + taskNum + "  before queue 货物数量= " + queue.size());
            memberValidPartList = (List<Member>) queue.take();
            userService.saveUserByMemberList(memberValidPartList);

            String modifyTime = memberValidPartList.get(memberValidPartList.size() - 1).getModifyTime();
            log.info("taskNum={} 号消费了 货物={}个, lastModifyTime={}", taskNum, memberValidPartList.size(), modifyTime);
            sb.append(" listSize=").append(memberValidPartList.size());
            sb.append(" success");
        } catch (Exception e) {
            log.error(" taskNum={} 号异常 消费 ", taskNum, e);
            sb.append(" fail");
        }

        long endTime = System.currentTimeMillis();
        sb.append(" 耗时：").append((endTime-startTime)/1000.0).append("s");
        return sb.toString();
    }
}
