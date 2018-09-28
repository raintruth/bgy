package com.pagoda.account.migrate.util.kafka.consumer;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.pagoda.account.migrate.common.Constants;
import com.pagoda.account.migrate.dataobject.member.MemberWalletRecord;
import com.pagoda.account.migrate.repository.member.MemberWalletRecordRepository;
import com.pagoda.account.migrate.service.account.IUserAccountRecordService;
import com.pagoda.account.migrate.util.kafka.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
public class KafkaReceiver {

    @Autowired
    private MemberWalletRecordRepository memberWalletRecordRepository;
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IUserAccountRecordService iUserAccountRecordService;


    /**
     * 案例
     * @param record
     */
    @KafkaListener(topics = {"migrate"})
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("----------------- record =" + record);
            log.info("------------------ message =" + message);
        }

    }

    /**
     * 查询user and account 信息
     * @param record
     */
    @KafkaListener(topics = {"userAndAccount"})
    public void listenUserAndAccount(ConsumerRecord<?, ?> record) {
        long l = System.currentTimeMillis();
        try {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            if (kafkaMessage.isPresent()) {
                Object message = kafkaMessage.get();
                log.info("已经查询出用户和账户信息信息，发送出消息，并且已经接受到,data:{}",message);
                Map map = JSONUtil.toBean(message.toString(), Map.class);
                if(null != map && null !=map.get(Constants.KAFKA_RESULT_MSG)){
                    JSONArray objects = JSONUtil.parseArray(map.get("msg").toString());
                    List<Map> msg = JSONUtil.toList(objects, Map.class);
                    //查询
                    if(null != msg && msg.size()>0){
                        List<Integer> list = new ArrayList<>();
                        for(Map m : msg){
                            Object mumberNo = m.get(Constants.MEMBER_MUMBERNO);
                            list.add(Integer.valueOf(mumberNo.toString()));
                        }
                        //TODO 原生sql 这个需要处理-> 查询会员系统
                        List<MemberWalletRecord> byMemberIdIn = memberWalletRecordRepository.findByUpdateTimeLessThanEqualAndMemberIdIn(Constants.DATE_TIME_IMPORT,list);
                        if(null !=byMemberIdIn && byMemberIdIn.size()>0 ){
                            iUserAccountRecordService.SaveUserAccountRecord(msg,byMemberIdIn);
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("线程保存数据的时间：=====================>"+(System.currentTimeMillis() - l));

    }

    /**
     *
     * @param record
     */
    @KafkaListener(topics = {"berWalletRecord"})
    public void listenBerWalletRecord(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            iUserAccountRecordService.insertUserAccountRecordList(message,"B");
        }

    }

    /**
     * 根据时间去查询相关的record记录
     * @param record
     */
    @KafkaListener(topics = {"walletRecordByTime"})
    public void listenWalletRecordByTime(ConsumerRecord<?, ?> record){
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            iUserAccountRecordService.insertUserAccountRecordList(message,"A");
        }
    }

    /**
     * 校验相关的记录数据
     * @param record
     */
    @KafkaListener(topics = {"userAccountRecordCheck"})
    public void listenUserAccountRecordCheck(ConsumerRecord<?, ?> record){
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            iUserAccountRecordService.updateUserAccountRecordCheck(message);
        }
    }


}
