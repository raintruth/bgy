package com.pagoda.account.migrate;

import cn.hutool.core.date.TimeInterval;
import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.producerconsumer.old.member.Member4ProdConsManager;
import com.pagoda.account.migrate.producerconsumer.old.memberaccount.ProdConsManager;
import com.pagoda.account.migrate.producerconsumer.user.UserProdConsManager;
import com.pagoda.account.migrate.producerconsumer.useraccount.UserAccountProdConsManager;
import com.pagoda.account.migrate.repository.member.MemberRepository;
import com.pagoda.account.migrate.service.account.UserAccountService;
import com.pagoda.account.migrate.service.account.UserService;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
import com.pagoda.account.migrate.service.member.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountMigrateApplicationTests2 {

    @Autowired
    private UserService userService;
    @Autowired
    private IMemberService memberService;

    @Autowired
    private IMemberAccountService memberAccountService;
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private MemberRepository memberRepository;


    @Resource(name = "userProducerPool")
    private ExecutorService userProducerPool;

    @Resource(name = "userConsumerPool")
    private ExecutorService userConsumerPool;

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void  findMemberPageListTest1() {
       String startUpdateTimeStr = "20160101000000";
       String endUpdateTimeStr = "20160102000000";//20220218173328

        Long count = memberRepository.countMemberPartList(startUpdateTimeStr, endUpdateTimeStr);
        for(int i = 0; i < count; i++){
            Pageable pageable = PageRequest.of(i, 10, Sort.by("modifyTime"));
            List<Member> memberPartList =
                    memberRepository.findMemberPageList(startUpdateTimeStr, endUpdateTimeStr, pageable);
            log.info("memberPartList={}",memberPartList.size());
        }



    }


    @Test
    public void  redisTest1() {
        List clientList = redisTemplate.getClientList();
        //redisTemplate.opsForList().leftPop();
        log.info("clientList={}",clientList.size());
    }

    /**
     * by NodifyTime 重构后的  生产者消费者
     */
    @Test
    public void  userTest1() {

        UserProdConsManager userProdConsManager = new UserProdConsManager();
        userProdConsManager.setMemberService(memberService).setUserService(userService)
                .setProducerPool(userProducerPool).setConsumerPool(userConsumerPool).setRedisTemplate(redisTemplate);

        userProdConsManager.start();
    }


    /**
     * by NodifyTime 重构后的  生产者消费者
     */
    @Test
    public void  userAccountTest2() {

        UserAccountProdConsManager userAccountProdConsManager = new UserAccountProdConsManager();
        userAccountProdConsManager.setMemberAccountService(memberAccountService).setUserAccountService(userAccountService).setUserService(userService);
                userAccountProdConsManager.setProducerPool(userProducerPool).setConsumerPool(userConsumerPool).setRedisTemplate(redisTemplate);

        userAccountProdConsManager.start();
    }




    /**
     * old的 生产者消费者
     */
    @Test
    public void  MemberTest1() {

        //TODO 改成 基于id的方式
        Member4ProdConsManager member4ProdConsManager = new Member4ProdConsManager(memberService, userService);
        member4ProdConsManager.run();
    }
    @Test
    public void  MemberAccountTest1() {

        ProdConsManager prodConsManager = new ProdConsManager(memberAccountService, userAccountService, userService);
        prodConsManager.run();

    }



    /**
     * 测试 高并发批量操作时 jpa查询快 还是 JdbcTemplate 查询快 结果jpa胜出
     * @throws InterruptedException
     */
    @Test
    public void  memberPageRepositoryTest1() throws InterruptedException {
        TimeInterval interval = new TimeInterval();
        int pageSize = 10;
        final CountDownLatch producerGate = new CountDownLatch(pageSize);
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("startMemberId", 0L);
        hashMap.put("endMemberId", 105000L);
        for(int i = 0; i < pageSize; i++){
            Pageable pageable = PageRequest.of(i, 5000);
            userConsumerPool.execute(
                    () -> {
                        long startTime = System.currentTimeMillis();
                        //jpa
                        List<Member> memberPageList = memberRepository.findMemberPageList(0L, 105000L, pageable);
                        //JdbcTemplate
                        //List<Member> memberPageList = memberBatchRepository.batchQuery(hashMap, pageable);
                        log.info("size={}", memberPageList.size());

                        long endTime = System.currentTimeMillis();
                        log.info("第{}次 耗时：{}", pageable.getPageNumber(), (endTime-startTime)/1000.0+"s");

                        producerGate.countDown();
                    }

            );

        }

        producerGate.await();
        long intervalMs = interval.intervalMs();
        log.info("总计  intervalMs={}", intervalMs/1000.0);

    }

    @Test
    public void  memberRepositoryTest1() {
        Map<String, Object> minAndMaxMemberId = memberRepository.findMinAndMaxMemberId(0L, 10000L);

        log.info("minAndMaxMemberId={}", minAndMaxMemberId.entrySet().toString());

    }



}
