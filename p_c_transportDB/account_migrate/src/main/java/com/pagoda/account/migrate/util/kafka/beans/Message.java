package com.pagoda.account.migrate.util.kafka.beans;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private String id;    //id

    private String msg; //消息

    private Date sendTime;  //时间戳
}
