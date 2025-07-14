package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 消息过期机制：
 * https://blog.csdn.net/weixin_38361347/article/details/119302257
 * <li>给队列中所有消息指定过期时间 {@link this#testQueueTTL()}</li>
 * <li>给某条消息指定过期时间 {@link this#testMessageTTL()}</li>
 * 结论：
 * <li>如果在过期时间内，还没有消费者取消息，消息才会过期。</li>
 * <li>如果消息已经接收到，但是没确认，是不会过期的。</li>
 */
public class TTLProducer {

    private static final String QUEUE_NAME = "queue_ttl";
    private static final String MESSAGE_TTL_NAME = "message_ttl";

    public static void main(String[] argv) throws Exception {
//        testQueueTTL();
        testMessageTTL();
    }


    //方式1: 给队列指定过期时间
    private static void testQueueTTL() throws IOException, TimeoutException {
        //创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //建立链接、创建频道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // 消息虽然可以重复声明,但是必须指定相同的参数,在消费者的创建队列要指定过期时间,
            // 后面要放args,在生产者又想重新创建队列，又不指定参数，那肯定会有问题，
            // 所以要把这里的创建队列注释掉。
            //channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            String message = "test ttl queue";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }


    //方式2: 给消息指定过期时间
    private static void testMessageTTL() throws IOException, TimeoutException {
        //创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //建立链接、创建频道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(MESSAGE_TTL_NAME, false, false, false, null);
            String message = "test message ttl";

            //指定消息的过期时间为10秒
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .expiration("10000")
                    .build();

            channel.basicPublish("", MESSAGE_TTL_NAME, properties, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}