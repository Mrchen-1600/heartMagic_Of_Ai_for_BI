package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * 交换机: 一个生产者给多个队列发消息,1个生产者对多个队列
 *  交换机的作用:类似网路由器.提供转发功能
 *  要解决问题: 怎么把消息转发到不同的队列上,让消费者从不同队列消费
 *
 * 发布订阅（fanout）：消费者
 */
public class FanoutConsumer {
    private static final String EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // 建立连接
        Connection connection = factory.newConnection();
        // 创建两个通道,对应两个消息队列
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();

        //声明交换机
        channel1.exchangeDeclare(EXCHANGE_NAME, "fanout");

        // 创建队列1，随机分配一个队列名称
        String queueName1 = "小王";
        channel1.queueDeclare(queueName1, true, false, false, null);
        //将队列1绑定到交换机上
        channel1.queueBind(queueName1, EXCHANGE_NAME, "");

        // 创建队列2
        String queueName2 = "小李";
        channel2.queueDeclare(queueName2, true, false, false, null);
        //将队列2绑定到交换机上
        channel2.queueBind(queueName2, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //定义队列1如何处理消息
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [小王] Received '" + message + "'");
        };
        //定义队列2如何处理消息
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [小李] Received '" + message + "'");
        };

        //开始消费监听队列1中的消息
        channel1.basicConsume(queueName1, true, deliverCallback1, consumerTag -> {
        });
        //开始消费监听队列2中的消息
        channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
        });
    }
}