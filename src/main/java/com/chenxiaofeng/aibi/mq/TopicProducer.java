package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * topic交换机：消息会根据一个模糊的路由键转发到指定的队列,特定的一类消息发给特定的队列
 *
 * 场景举例:如果我们希望把同一个消息转发给多个消息队列中的两个,如果使用direct交换机我们需要把同一条消息发两遍
 * 两次指定不同的路由键才可以,比较麻烦,因此我们就可以使用主题交换机
 */
public class TopicProducer {

    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String userInput = scanner.nextLine();
                if (userInput.length() < 1) {
                    continue;
                }
                String[] inputStr = userInput.split(" ");

                String message = inputStr[ 0 ];
                String routeKey = inputStr[ 1 ];

                channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + routeKey + "':'" + message + "'");
            }
        }
    }
}