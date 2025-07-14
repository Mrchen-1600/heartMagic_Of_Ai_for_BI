package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 交换机: 一个生产者给多个队列发消息,1个生产者对多个队列
 *  交换机的作用:类似网路由器.提供转发功能
 *  要解决问题: 怎么把消息转发到不同的队列上,让消费者从不同队列消费
 *  交换机的类别：fanout、direct, topic, headers
 *
 * 发布订阅（fanout）：生产者
 * 扇出：将消息广播到所有绑定到该交换机的队列的过程。它得名于扇形扩散的形状，类似于把一条消息从交换机传播到多个队列，就像扇子展开一样。
 * 广播：将消息发送到所有与该交换机绑定的队列的过程。当发布者将消息发送到 fanout 交换机时，交换机会立即将该消息复制并传递给所有绑定的队列，无论目标队列的数量是多少。
 * 特点：消息会被转发到所有绑定到该交换机的队列。
 * 场景：很适用于发布订阅的场景。比如写日志，可以多个系统间共享。
 */
public class FanoutProducer {

    // 定义要使用的交换机名名称
    private static final String EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        //1.创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //2.创建连接和通道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            //3.创建交换机  声明fanout类型的交换机
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String message = scanner.nextLine();

                //4.将消息发送到指定的交换机（fanout交换机），因为是给所有的消费者发消息,所以不指定路由键（空字符串）
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
                // 打印发送的消息内容
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
}