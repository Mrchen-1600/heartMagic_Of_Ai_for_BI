package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * 单个消费者
 */
public class SingleConsumer {

    // 定义我们正在监听的队列名称
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        //1.创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置连接工厂的主机名，这里我们连接的是本地的RabbitMQ服务器
        factory.setHost("localhost");

        //2.建立链接、创建频道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {


            //3.创建消息队列（注意：同名称的消息队列如果已经存在，再次执行声明队列方法传入的参数必须和之前一致）
            /*
            这里创建队列主要是为了确保该队列的存在，否则在后续的操作中可能会出现错误。即便你的队列原本并不存在，此语句也能够帮你创建一个新的队列。
            但是需要特别注意一点，如果你的队列已经存在，并且你想再次执行声明队列的操作，那么所有的参数必须与之前的设置完全一致。
            这是因为一旦一个队列已经被创建，就不能再创建一个与其参数不一致的同名队列。
            要确保消费队列的名称与发送消息的队列名称保持一致。所以在这里，我们统一使用"hello"作为队列名。
            */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 在控制台打印等待接收消息的提示
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


            //4.定义如何处理消息,创建一个新的DeliverCallback来处理接收到的消息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                // 将消息体转换为字符串
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                // 在控制台打印已接收消息的提示
                System.out.println(" [x] Received '" + message + "'");
            };

            //5.在频道上开始消费队列中的消息，接收到的消息会传递给deliverCallback来处理,会持续阻塞
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        }
    }
}