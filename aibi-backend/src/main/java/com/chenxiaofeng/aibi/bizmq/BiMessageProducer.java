package com.chenxiaofeng.aibi.bizmq;

import com.chenxiaofeng.aibi.constant.BiMqConstant;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * bi 消息生产者
 * @author 尘小风
 */
@Component
public class BiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param message 消息
     */
    public void sendMessage(String message) {
//        rabbitTemplate.convertAndSend(BiMqConstant.BI_CHART_EXCHANGE_NAME, BiMqConstant.BI_CHART_ROUTING_KEY, message);
        rabbitTemplate.convertAndSend(BiMqConstant.BI_CHART_EXCHANGE_NAME, BiMqConstant.BI_CHART_ROUTING_KEY, message,
                // 设置消息过期时间： 单位：毫秒
                message1 -> {
                    message1.getMessageProperties().setExpiration(BiMqConstant.BI_CHART_MESSAGE_EXPIRED);// 消息过期时间
                    message1.getMessageProperties().setDeliveryMode(MessageDeliveryMode.fromInt(2)); // 持久化
                    // 返回消息对象
                    return message1;
                });
    }
}