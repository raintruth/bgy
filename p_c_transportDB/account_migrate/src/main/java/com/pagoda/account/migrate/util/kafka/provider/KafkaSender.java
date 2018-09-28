package com.pagoda.account.migrate.util.kafka.provider;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagoda.account.migrate.util.kafka.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class KafkaSender {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    private Gson gson = new GsonBuilder().create();

    /**
     * 发送消息方法
     */
    public void send() {
        Message message = new Message();
        message.setId(String.valueOf(System.currentTimeMillis()));
        message.setMsg(UUID.randomUUID().toString());
        message.setSendTime(new Date());
        log.info("+++++++++++++++++++++  message = {}", gson.toJson(message));
        kafkaTemplate.send("migrate",gson.toJson(message));
        log.info("发送成功  message = {}", gson.toJson(message));
    }
}
