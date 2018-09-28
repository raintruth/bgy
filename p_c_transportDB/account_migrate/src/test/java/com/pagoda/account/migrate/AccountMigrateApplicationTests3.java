package com.pagoda.account.migrate;


import com.pagoda.account.migrate.producerconsumer.useraccountbyid.UserAccountProdConsManager;
import com.pagoda.account.migrate.producerconsumer.userbyid.UserProdConsManager;
import com.pagoda.account.migrate.service.account.UserAccountService;
import com.pagoda.account.migrate.service.account.UserService;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
import com.pagoda.account.migrate.service.member.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountMigrateApplicationTests3 {

    @Autowired
    private UserService userService;
    @Autowired
    private IMemberService memberService;

    @Autowired
    private IMemberAccountService memberAccountService;
    @Autowired
    private UserAccountService userAccountService;



    @Resource(name = "userProducerPool")
    private ExecutorService userProducerPool;

    @Resource(name = "userConsumerPool")
    private ExecutorService userConsumerPool;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void  userByIDTest1() {

        UserProdConsManager userProdConsManager = new UserProdConsManager();
        userProdConsManager.setMemberService(memberService).setUserService(userService)
                .setProducerPool(userProducerPool).setConsumerPool(userConsumerPool).setRedisTemplate(redisTemplate);

        userProdConsManager.start();
    }

    @Test
    public void  userAccountByIDTest2() {

        UserAccountProdConsManager userAccountProdConsManager = new UserAccountProdConsManager();
        userAccountProdConsManager.setMemberAccountService(memberAccountService).setUserAccountService(userAccountService).setUserService(userService);
                userAccountProdConsManager.setProducerPool(userProducerPool).setConsumerPool(userConsumerPool).setRedisTemplate(redisTemplate);

        userAccountProdConsManager.start();
    }




}
