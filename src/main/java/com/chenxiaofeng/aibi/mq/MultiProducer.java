package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;

import java.util.Scanner;

/**
 * 多生产者
 */
public class MultiProducer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        //1.创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");


        //2.建立链接、创建频道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {


            //3.创建消息队列   队列持久化：durable为true   保证服务器重启后队列消息不丟失
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

            Scanner scanner = new Scanner(System.in);

            // 使用循环，每当用户在控制台输入一行文本，就将其作为消息发送
            while (scanner.hasNext()) {
                // 读取用户在控制台输入的下一行文本
                String message = scanner.nextLine();

                //4.发送消息   消息持久化：将props指定为 MessageProperties.PERSISTENT_TEXT_PLAIN   在系统重启后，消息仍然不会丢失
                channel.basicPublish("", TASK_QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
}