package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * direct 交换机：可以指定消息发送到哪个队列,相较于fanout交换机更加灵活
 */
public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // 建立连接
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //创建队列1
        String queueName1 = "hong-queue";
        channel.queueDeclare(queueName1, true, false, false, null);
        //给队列1绑定交换机,指定路由键：routingKey
        channel.queueBind(queueName1, EXCHANGE_NAME, "hong");

        ////创建队列2
        String queueName2 = "hei-queue";
        channel.queueDeclare(queueName2, true, false, false, null);
        //给队列2绑定交换机,指定路由键：routingKey
        channel.queueBind(queueName2, EXCHANGE_NAME, "hei");

        //定义队列1如何处理消息
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [小红] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        //定义队列2如何处理消息
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [小黑] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        //消费消息
        channel.basicConsume(queueName1, true, deliverCallback1, consumerTag -> {
        });
        channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
        });
    }
}