package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 消息过期机制
 */
public class TTLConsumer {

    private static final String QUEUE_NAME = "queue_ttl";
    private static final String MESSAGE_TTL_NAME = "message_ttl";

    public static void main(String[] argv) throws Exception {
//        testQueueTTL();
        testMessageTL();
    }


    //给队列设置过期时间
    private static void testQueueTTL() throws IOException, TimeoutException {
        //创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //建立链接、创建频道
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();


        //channel.queueDeclare()方法中最后一个参数arguments是Map<String, Object>类型的,
        //通过这个参数指定队列的过期时间,所以我们在这新建一个Map<String, Object>类型的对象
        Map<String, Object> args = new HashMap<>();
        //设置队列的过期时间为10s
        args.put("x-message-ttl", 10000);
        //创建消息队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, args);

        // 打印等待消息的提示信息
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //定义如何处理消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };

        //消费消息，持续阻塞
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });
        }


    //给消息设置过期时间
    private static void testMessageTL() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //建立链接、创建频道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(MESSAGE_TTL_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            //定义如何处理消息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
            };

            //消费消息，持续阻塞
            channel.basicConsume(MESSAGE_TTL_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }
}