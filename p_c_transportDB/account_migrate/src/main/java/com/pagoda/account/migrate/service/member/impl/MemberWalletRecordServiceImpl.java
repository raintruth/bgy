package com.pagoda.account.migrate.service.member.impl;

import cn.hutool.json.JSONUtil;
import com.pagoda.account.migrate.common.Constants;
import com.pagoda.account.migrate.dataobject.member.MemberWalletRecord;
import com.pagoda.account.migrate.repository.account.UserAccountRepository;
import com.pagoda.account.migrate.repository.member.MemberWalletRecordRepository;
import com.pagoda.account.migrate.service.member.IMemberWalletRecordService;
import com.pagoda.account.migrate.util.kafka.beans.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 *
 */
@Service
public class MemberWalletRecordServiceImpl implements IMemberWalletRecordService {


    @Autowired
    private MemberWalletRecordRepository memberWalletRecordRepository;

    @Resource(name = "requestUserAndAccountPool")
    private ExecutorService executorService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Resource(name = "requestMemberRecordPool")
    private ExecutorService executorRecordService;

    @Override
    public MemberWalletRecord getMemberWalletRecordById(Integer id) {
        return memberWalletRecordRepository.findMemberWalletRecordByRecordId(id);
    }

    @Override
    public List<MemberWalletRecord> getMemberWalletRecordById(String accountType, Integer page, Long count, Integer pageSize) {
        //0.如果page为空表示按照数据库来导入用户数据，如果不为空表示按照实际的页数来进行分页。
        if (null == page) {
            //TODO 原生sql 1.查询有多少用户信息
            Long countAccount;

            if (null == count || count == 0) {
                countAccount = userAccountRepository.findCountAccount(accountType);
            } else {
                countAccount = count;
            }

            if (countAccount > 0) {
                long l = countAccount / 500;
                for (long i = 0, size = l; i <= size; i++) {
                    Integer integer = Integer.valueOf(String.valueOf(i)) * 500;
                    Integer sizePage = (Integer.valueOf(String.valueOf(i)) + 1) * 500;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getSendData(accountType, integer, sizePage);
               /* Pageable pageable = PageRequest.of(integer, 100, Sort.by("user_id"));
                List<Map> userAndAccountDto = userAccountRepository.findUserAndAccountDto(pageable);
                Message message = new Message();
                message.setId(String.valueOf(System.currentTimeMillis()));
                message.setMsg(JSONUtil.parseArray(userAndAccountDto).toString());
                message.setSendTime(new Date());
                kafkaTemplate.send("userAndAccount",JSONUtil.toJsonStr(message));*/

                }
                //executorService.shutdown();

            }
        } else {
            getSendData(accountType, page, pageSize);
        }
        return null;
    }

    @Override
    public List<MemberWalletRecord> getMemberWalletRecordByDate(String time, Integer page, Long count, Integer pageSize) {
        //1.根据时间查询出大于0的数据
        Long countRecordByUpdateTime = memberWalletRecordRepository.findCountRecordByUpdateTime(time);
        if (countRecordByUpdateTime > 0) {
            //2.表明有新的记录产生，按照分页大小进行分批次导入
            long l = countRecordByUpdateTime / 1000;
            for (long i = 0, size = l; i <= size; i++) {
                Integer integer = Integer.valueOf(String.valueOf(i));
                executorRecordService.submit(() -> {
                    Pageable pageable = PageRequest.of(integer, 1000, Sort.by("memberId"));
                    List<MemberWalletRecord> walletRecordList = memberWalletRecordRepository.findByUpdateTimeGreaterThanEqual(time, pageable);
                    if (null != walletRecordList && walletRecordList.size() > 0) {
                        Message message = new Message();
                        message.setId(String.valueOf(integer));
                        message.setMsg(JSONUtil.parseArray(walletRecordList).toString());
                        message.setSendTime(new Date());
                        kafkaTemplate.send("userAndAccount", JSONUtil.toJsonStr(message));
                    }
                });

            }
        }
        return null;
    }


    private void getSendData(String accountType, Integer integer, Integer sizePage) {
        //3.启动线程进行-进行拉取数据
        executorService.submit(() -> {
            try {
                //TODO 3.启动线程进行分页 需要进行原生sql
                //TODO 拉取数据较慢
                long l = System.currentTimeMillis();
                List<Map> userAndAccountDtoList = userAccountRepository.findUserAndAccountDtoList(accountType, integer, sizePage);
                Message message = new Message();
                message.setId(String.valueOf(integer));
                message.setMsg(JSONUtil.parseArray(userAndAccountDtoList).toString());
                message.setSendTime(new Date());
                kafkaTemplate.send("userAndAccount", JSONUtil.toJsonStr(message));
                System.out.println("前程请求时间=============================>"+(System.currentTimeMillis() - l));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
