package com.pagoda.account.migrate.producerconsumer.old.member;

import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.service.member.IMemberService;
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
public class ProductThread implements Callable {

    private int taskNum;
    private BlockingQueue queue;
    private IMemberService memberService;

    private String startUpdateTimeStr;
    private String endUpdateTimeStr;

    private Pageable pageable;

    public ProductThread(int taskNum, BlockingQueue queue, IMemberService memberService) {
        this.taskNum = taskNum;
        this.queue = queue;
        this.memberService = memberService;
    }

    public ProductThread(int taskNum, BlockingQueue queue, IMemberService memberService, String startUpdateTimeStr, String endUpdateTimeStr, Pageable pageable) {
        this.taskNum = taskNum;
        this.queue = queue;
        this.memberService = memberService;
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
            log.info("taskNum={} 号生产 ing--", taskNum);

            List<Member> memberPartList =
                    memberService.findMemberPageList(startUpdateTimeStr, endUpdateTimeStr, pageable);
            if(!memberPartList.isEmpty()){

                String modifyTime = memberPartList.get(memberPartList.size() - 1).getModifyTime();
                log.info("taskNum={} 号 生产了 货物={}个, lastModifyTime={}", taskNum, memberPartList.size(), modifyTime);

                queue.put(memberPartList);
                sb.append(" listSize=").append(memberPartList.size());
            }else {
                log.info("taskNum={} 号生产 货物=  Empty ", taskNum);
            }
            sb.append(" success");
        } catch (Exception e) {
            log.error(" taskNum={} 号异常 生产 ", taskNum, e);
            sb.append(" fail");

        }

        long endTime = System.currentTimeMillis();
        sb.append(" 耗时：").append((endTime-startTime)/1000.0).append("s");
        return sb.toString();
    }

}
