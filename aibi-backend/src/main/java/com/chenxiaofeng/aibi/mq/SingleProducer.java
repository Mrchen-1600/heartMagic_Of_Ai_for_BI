package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 单个生产者
 */

// 定义一个名为SingleProducer的公开类，用于实现消息发送功能
public class SingleProducer {

    // 定义一个静态常量字符串QUEUE_NAME，它的值为"hello"，表示我们要向名为"hello"的队列发送消息
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        //1.创建链接工厂，这个对象可以用于创建到RabbitMQ服务器的连接
        ConnectionFactory factory = new ConnectionFactory();
        // 设置ConnectionFactory的主机名为"localhost"，这表示我们将连接到本地运行的RabbitMQ服务器
        factory.setHost("localhost");
        //rabbitmq默认的用户名和密码都是guest，如果我们修改了默认的就需要指定成我们自己的用户名/密码/端口号
//        factory.setUsername();
//        factory.setPassword();
//        factory.setPort();


        //2.建立链接、创建频道
        // 使用ConnectionFactory创建一个新的连接,这个连接用于和RabbitMQ服务器进行交互
        try (Connection connection = factory.newConnection();
             // 通过已建立的连接创建一个新的频道
             //这里的channel可以理解为操作消息队列的客户端client（就像jdbcClient），提供了和消息队列server建立通信的传输方法
             Channel channel = connection.createChannel()) {

            /**
             * 3.创建消息队列 {@link Channel#queueDeclare(String, boolean, boolean, boolean, Map)}
             * 参数：
             * queue：队列名称
             * durable：消息队列重启后，消息是否丢失（持久化）
             * exclusive：是否只允许当前这个创建消息队列的连接操作消息队列
             * autoDelete：没有人用队列后，是否要删除队列
             * arguments：是否要携带参数
             */
            // 在通道上声明一个队列，我们在此指定的队列名为"hello"
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 创建要发送的消息
            String message = "Hello World!";


            /**
             * 4.发送消息 {@link Channel#basicPublish(String, String, AMQP.BasicProperties, byte[])}
             * 参数：
             * exchange：交换机名称
             * routingKey：发送到哪个队列名
             * props：消息的其他属性 – 路由标头等
             * body：消息正文
             */
            //使用channel.basicPublish方法将消息发布到指定的队列中。这里我们指定的队列名为"hello"
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}