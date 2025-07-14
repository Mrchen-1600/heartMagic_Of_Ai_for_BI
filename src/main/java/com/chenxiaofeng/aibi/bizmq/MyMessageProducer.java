package com.chenxiaofeng.aibi.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 测试程序： 消息生产者
 * @author 尘小风
 */

@Component
public class MyMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param exchange    指定发送到哪个交换机
     * @param routeingKey 发送到那个哪个键
     * @param message     要发送的消息
     */
    public void sendMessage(String exchange, String routeingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routeingKey, message);
    }
}