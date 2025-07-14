package com.chenxiaofeng.aibi.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.chenxiaofeng.aibi.constant.BiMqConstant;

import java.util.HashMap;
import java.util.Map;


/**
 * 创建bi程序用到的交换机和队列
 */
public class BiInitMain {

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

            //创建Bi死信交换机   交换机名称 交换机类型
            channel.exchangeDeclare(BiMqConstant.BI_CHART_DLX_EXCHANGE_NAME, "direct");

            //创建Bi死信队列   队列名称 是否持久化 是否只允许当前这个创建消息队列的连接操作消息队列 没人使用时候是否删除队列 是否携带参数
            channel.queueDeclare(BiMqConstant.BI_CHART_DLX_QUEUE_NAME, true, false, false, null);
            //给死信队列绑定交换机   队列名 交换机名 死信路由键
            channel.queueBind(BiMqConstant.BI_CHART_DLX_QUEUE_NAME, BiMqConstant.BI_CHART_DLX_EXCHANGE_NAME, BiMqConstant.BI_CHART_DLX_ROUTING_KEY);


            //创建Bi正常工作的交换机
            channel.exchangeDeclare(BiMqConstant.BI_CHART_EXCHANGE_NAME, "direct");

            //指定死信队列参数
            Map<String, Object> args = new HashMap<>(2);
            //要给该正常队列绑定的死信交换机
            args.put("x-dead-letter-exchange", BiMqConstant.BI_CHART_DLX_EXCHANGE_NAME);
            //指定死信要发送到的死信队列
            args.put("x-dead-letter-routing-key", BiMqConstant.BI_CHART_DLX_ROUTING_KEY);
            //创建Bi正常工作的队列，并通过args参数给队列绑定死信交换机
            channel.queueDeclare(BiMqConstant.BI_CHART_QUEUE_NAME, true, false, false, args);
            //给工作队列绑定交换机   队列名 交换机名 指定的路由键
            channel.queueBind(
                    BiMqConstant.BI_CHART_QUEUE_NAME,
                    BiMqConstant.BI_CHART_EXCHANGE_NAME,
                    BiMqConstant.BI_CHART_ROUTING_KEY
            );


            //创建Bi重试交换机
            channel.exchangeDeclare(BiMqConstant.BI_CHART_RELOAD_EXCHANGE_NAME, "direct");
            //创建Bi重试队列并绑定死信交换机
            channel.queueDeclare(BiMqConstant.BI_CHART_RELOAD_QUEUE_NAME, true, false, false, args);
            channel.queueBind(
                    BiMqConstant.BI_CHART_RELOAD_QUEUE_NAME,
                    BiMqConstant.BI_CHART_RELOAD_EXCHANGE_NAME,
                    BiMqConstant.BI_CHART_RELOAD_ROUTING_KEY
            );
        }
    }
}