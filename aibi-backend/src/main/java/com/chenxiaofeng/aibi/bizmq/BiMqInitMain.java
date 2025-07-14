package com.chenxiaofeng.aibi.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 创建测试程序用到的交换机和队列
 */
public class BiMqInitMain {

    public static void main(String[] argv) throws Exception {
        //创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
       factory.setHost("localhost");
        //创建云服务器上的rabbitmq 的交换机和队列
//        factory.setHost("云服务器ip地址");
//        factory.setPort(5672); //默认端口5672
//        factory.setUsername("管理员用户名");
//        factory.setPassword("管理员密码");

        //建立链接、创建频道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            String exchangeName = "test_exchange";
            channel.exchangeDeclare(exchangeName, "direct");
            String queueName = "test_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, "test_routing_key");
        }
    }
}