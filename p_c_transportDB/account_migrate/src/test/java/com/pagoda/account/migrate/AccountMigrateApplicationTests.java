package com.pagoda.account.migrate;

import com.pagoda.account.migrate.common.Constants;
import com.pagoda.account.migrate.repository.account.UserAccountRecordRepository;
import com.pagoda.account.migrate.repository.account.UserAccountRepository;
import com.pagoda.account.migrate.repository.member.MemberWalletRecordRepository;
import com.pagoda.account.migrate.service.account.IUserAccountRecordService;
import com.pagoda.account.migrate.service.account.UserAccountService;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
import com.pagoda.account.migrate.service.member.IMemberService;
import com.pagoda.account.migrate.service.member.IMemberWalletRecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountMigrateApplicationTests {

  /*  @Autowired
    private UserService userService;*/

    @Autowired
    private IMemberWalletRecordService iMemberWalletRecordService;

    @Autowired
    private IUserAccountRecordService iUserAccountRecordService;

    @Autowired
    private IMemberService iMemberService;


    @Autowired
    private IMemberAccountService memberAccountService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private MemberWalletRecordRepository memberWalletRecordRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountRecordRepository userAccountRecordRepositroy;


    /**
     * 迁移记录方法
     */
    @Test
    public void contextLoadsMember() {

        List<Map> userAndAccountDtoList = userAccountRepository.findUserAndAccountDtoList(Constants.ACCOUNT_TYPE_CM, 0, 1000);
        System.out.println(userAndAccountDtoList);


        /**
         * 测试按照之前时间的方式进行
         */
        //List<MemberWalletRecord> memberWalletRecordList = iMemberWalletRecordService.getMemberWalletRecordById("CM",null,null,null);
        //System.out.println(memberWalletRecordList);

        /**
         *测试按照会员系统之后的时间进行测试
         */
        //List<MemberWalletRecord> memberWalletRecordByDate = iMemberWalletRecordService.getMemberWalletRecordByDate("201809141715", null, null, null);

        //System.out.println(memberWalletRecordByDate);

        /**
         * 数据校核
         */

        /*
        ArrayList<Integer> userId = new ArrayList<>();
        userId.add(69758969);
        userId.add(100006853);
        userId.add(69755360);
        ArrayList<Integer> accountId = new ArrayList<>();
        accountId.add(69758968);
        accountId.add(100002807);
        accountId.add(69755359);
        int i = userAccountRecordRepositroy.deleteUserRecordByUserIdAndAccountId(userId, accountId, DateUtils.parseDateAll(Constants.DATE_TIME_IMPORT));
        System.out.println(i);
        iUserAccountRecordService.getUserAccountRecordDtoList();
        System.out.println();
        */

    }

}
