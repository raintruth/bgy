package com.pagoda.account.migrate.service.account.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.pagoda.account.common.util.DateUtils;
import com.pagoda.account.migrate.common.Constants;
import com.pagoda.account.migrate.dataobject.account.UserAccountRecord;
import com.pagoda.account.migrate.dataobject.member.MemberWalletRecord;
import com.pagoda.account.migrate.repository.account.UserAccountRecordRepository;
import com.pagoda.account.migrate.repository.account.UserAccountRecordBatchRepository;
import com.pagoda.account.migrate.repository.account.UserAccountRepository;
import com.pagoda.account.migrate.repository.member.MemberWalletRecordRepository;
import com.pagoda.account.migrate.service.account.IUserAccountRecordService;
import com.pagoda.account.migrate.task.CompletionServiceRecordTask;
import com.pagoda.account.migrate.util.kafka.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author zfr
 */
@Service
@Slf4j
public class UserAccountRecordServiceImpl implements IUserAccountRecordService {

    @Autowired
    private UserAccountRecordRepository userAccountRecordRepository;

    @Resource(name = "consumerUserAccountRecordPool")
    private ExecutorService buildConsumerQueueThreadPool;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserAccountRecordBatchRepository userAccountRecordBatchRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Resource(name = "requestUserAccountRecordPool")
    private ExecutorService executorService;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private MemberWalletRecordRepository memberWalletRecordRepository;


    @Override
    public void insertUserAccountRecordList(Object message, String type) {
        Map map = JSONUtil.toBean(message.toString(), Map.class);
        Object id = map.get(Constants.KAFKA_RESULT_ID);
        Object msg = map.get(Constants.KAFKA_RESULT_MSG);
        log.info("已经接受到会员系统查询出的数据，准备保存数据，data:{}", message);
        if (null != id && msg != null) {
            JSONArray objects = JSONUtil.parseArray(msg.toString());
            List<MemberWalletRecord> recordList = JSONUtil.toList(objects, MemberWalletRecord.class);
            if (null != recordList && recordList.size() > 0) {
                //0.查询已经有的用户账户，根据用户账户进行导入数据
                int size = recordList.size();
                List<Map> recordData = null;
                if (Objects.equals(Constants.IMPORT_RECORD_TYPE_B, type)) {
                    //有多少用户需要导入数据确认数据-根据用户id来
                    Object o = redisTemplate.opsForValue().get(String.valueOf(id));
                    log.info("已经消费的会员账户信息记录，data:{}", id.toString());
                    if (o != null) {
                        JSONArray objectMap = JSONUtil.parseArray(o.toString());
                        recordData = JSONUtil.toList(objectMap, Map.class);
                    }
                }
                if (Objects.equals(Constants.IMPORT_RECORD_TYPE_A, type)) {
                    Set<String> set = new HashSet<>();
                    //去user 表查询用户数据
                    for (MemberWalletRecord memberWalletRecord : recordList) {
                        if (null != memberWalletRecord.getMemberId()) {
                            set.add(String.valueOf(memberWalletRecord.getMemberId()));
                        }
                    }
                    //根据id进行相对应的账户查询
                    recordData = userAccountRepository.findUserAndAccountByMemberId(new ArrayList<>(set), "CM");
                }

                //1.根据mainMoneyAfterModify - mainMoneyBeforeModify进行过滤数据
                if (null != recordData && size > 0 && recordData.size() > 0) {
                    saveRecordData(recordData,recordList);
                }
            }
        }
    }

    /**
     * 保存用户记录
     * @param recordData 用户数据
     * @param recordList 用户记录数据
     */
    private void saveRecordData(List<Map> recordData, List<MemberWalletRecord> recordList) {
        //结果集
        List<UserAccountRecord> userAccountRecords = new ArrayList<>();
        CompletionService<UserAccountRecord> completionService = new ExecutorCompletionService<>(buildConsumerQueueThreadPool);
        List<Future<UserAccountRecord>> futureList = new ArrayList<>();
        for (Map m : recordData) {
            //开启多线程进行数据的过滤
            for (MemberWalletRecord memberWalletRecord : recordList) {
                if (m != null && memberWalletRecord != null) {
                    if (Objects.equals(m.get(Constants.MEMBER_MUMBERNO).toString(), memberWalletRecord.getMemberId().toString())) {
                        futureList.add(completionService.submit(new CompletionServiceRecordTask(memberWalletRecord, m)));
                    }
                }
            }
        }
        //使用内部阻塞队列的take()
        for (int i = 0; i < futureList.size(); i++) {
            try {
                UserAccountRecord accountRecord = completionService.take().get();
                if (null != accountRecord) {
                    userAccountRecords.add(accountRecord);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("进行数据保存操作！，data:{}", userAccountRecords);
        //TODO 3.保存数据,更改为原生sql-批量处理
        if(null != userAccountRecords && userAccountRecords.size()>0) {
            userAccountRecordBatchRepository.batchInsertIfExistUpdate(userAccountRecords);
        }
        //userAccountRecordRepository.saveAll(userAccountRecords);
    }

    @Override
    public void getUserAccountRecordDtoList() {
        //1.查询userAccountRecord数据记录条数
        Long cCount = userAccountRecordRepository.getUserAccountRecordCount(Constants.ACCOUNT_TYPE_CM, Constants.DATE_TIME_IMPORT);
        long l = cCount / Constants.CHECK_SIZE_USERACCOUNT;
        for (long i = 0, size = l; i <= size; i++) {
            Integer integer = Integer.valueOf(String.valueOf(i)) * Constants.CHECK_SIZE_USERACCOUNT;
            Integer endSize = (Integer.valueOf(String.valueOf(i)) + 1) * Constants.CHECK_SIZE_USERACCOUNT;
            executorService.submit(()->{
                //TODO  1.查询出了该批次的用户数据 -这个sql需要改变
                List<Map> cm = userAccountRecordRepository.getUserAccountRecordDtoListByUserIdBetween(integer,endSize,Constants.ACCOUNT_TYPE_CM,Constants.DATE_TIME_IMPORT);
                if (null != cm && cm.size() > 0) {
                    //1. 编码相关的id，方便后面查询出id
                    Set<Integer> userIdSet = new HashSet<>();
                    Set<Integer> accountIdSet = new HashSet<>();
                    Set<Integer> memberIdSet = new HashSet<>();
                    getDataId(cm,userIdSet,accountIdSet,memberIdSet);
                    // 2.查询账户系统的数据
                    Map totalRecordAccount = userAccountRecordRepository.getTotalRecord(new ArrayList<>(userIdSet), new ArrayList<>(accountIdSet), Constants.DATE_TIME_IMPORT);
                    // 3.查询会员系统的数据
                    Map totalRecordMember = memberWalletRecordRepository.getTotalRecord(new ArrayList<>(memberIdSet), Constants.DATE_TIME_IMPORT);
                    //4.进行对比
                    if (null != totalRecordAccount && totalRecordAccount.size() > 0 && null != totalRecordMember && totalRecordMember.size() > 0) {
                        Object totalBalanceBeforeModify = totalRecordAccount.get("totalBalanceBeforeModify");
                        Object totalMainMoneyBeforeModify = totalRecordMember.get("totalMainMoneyBeforeModify");
                        Object totalBalanceAfterModify = totalRecordAccount.get("totalBalanceAfterModify");
                        Object totalMainMoneyAfterModify = totalRecordMember.get("totalMainMoneyAfterModify");
                        Object moneyAccount = totalRecordAccount.get("money");
                        Object moneyMoney = totalRecordMember.get("money");

                        if(null != totalBalanceBeforeModify && null != totalBalanceAfterModify && null != moneyAccount) {
                            //第一种情况
                            if( null != totalMainMoneyBeforeModify && null != totalMainMoneyAfterModify && null!= moneyMoney){
                                if (!Objects.equals(totalRecordAccount.get("totalBalanceBeforeModify").toString(), totalRecordMember.get("totalMainMoneyBeforeModify").toString()) ||
                                        !Objects.equals(totalRecordAccount.get("totalBalanceAfterModify").toString(), totalRecordMember.get("totalMainMoneyAfterModify").toString()) ||
                                        !Objects.equals(totalRecordAccount.get("money").toString(), totalRecordMember.get("money").toString())) {
                                    SaveKafkaData(integer,cm);
                                }
                            }
                        }else{
                            if( null != totalMainMoneyBeforeModify && null != totalMainMoneyAfterModify && null!= moneyMoney){
                                SaveKafkaData(integer,cm);
                            }
                        }
                    }
                }
            });
        }

    }

    private void SaveKafkaData(Integer integer, List<Map> cm) {
        log.info("当前批次数据不正确，批次数据：data：{}", cm);
        //5.进行数据清理和保存
        Message message = new Message();
        message.setId(String.valueOf(integer));
        message.setMsg(JSONUtil.parseArray(cm).toString());
        message.setSendTime(new Date());
        kafkaTemplate.send("userAccountRecordCheck", JSONUtil.toJsonStr(message));
    }

    /**
     * 组装数据
     * @param cm 数据map
     * @param userIdSet 用户-list
     * @param accountIdSet 账户-list
     * @param memberIdSet 会员-list
     */
    private void getDataId(List<Map> cm, Set<Integer> userIdSet, Set<Integer> accountIdSet, Set<Integer> memberIdSet) {
        for (Map m : cm) {
            Object userId = m.get(Constants.MEMBER_RESULT_ACCOUNT_USER_ID);
            Object accountId = m.get(Constants.MEMBER_RESULT_ACCOUNT_ACCOUNT_ID);
            Object memberId = m.get(Constants.MEMBER_MUMBERNO);
            if(null != userId && null!=accountId && null!=memberId ) {
                userIdSet.add(Integer.valueOf(userId.toString()));
                accountIdSet.add(Integer.valueOf(accountId.toString()));
                memberIdSet.add(Integer.valueOf(memberId.toString()));
            }
        }
    }

    @Override
    public void updateUserAccountRecordCheck(Object message) {
        Map map = JSONUtil.toBean(message.toString(), Map.class);
        Object id = map.get(Constants.KAFKA_RESULT_ID);
        Object msg = map.get(Constants.KAFKA_RESULT_MSG);
        log.info("已经接受到了校验数据，准备进行校验数据，data:{}", message);
        if (id != null && id != msg) {
            JSONArray objects = JSONUtil.parseArray(msg.toString());
            List<Map> userAccount = JSONUtil.toList(objects, Map.class);
            if (null != userAccount && userAccount.size() > 0) {
                //0.组装id
                Set<Integer> userIdSet = new HashSet<>();
                Set<Integer> accountIdSet = new HashSet<>();
                Set<Integer> memberIdSet = new HashSet<>();
                getDataId(userAccount,userIdSet,accountIdSet,memberIdSet);
                //1.先删除账户系统的记录
                int delDate = userAccountRecordRepository.deleteUserRecordByUserIdAndAccountId(new ArrayList<>(userIdSet), new ArrayList<>(accountIdSet), DateUtils.parseDateAll(Constants.DATE_TIME_IMPORT));
                if( delDate > 0 ){
                    //删除成功
                    //2.查询会员系统的记录
                    List<MemberWalletRecord> lists = memberWalletRecordRepository.findByUpdateTimeLessThanEqualAndMemberIdIn(Constants.DATE_TIME_IMPORT, new ArrayList<>(memberIdSet));
                    //3.保存记录
                    saveRecordData(userAccount,lists);
                }
            }
        }

    }

    @Override
    public void SaveUserAccountRecord(List<Map> recordData, List<MemberWalletRecord> recordList) {
        if(null!= recordData && recordData.size()>0 && null!=recordData && recordList.size()>0){
            saveRecordData(recordData,  recordList);
        }
    }

}
