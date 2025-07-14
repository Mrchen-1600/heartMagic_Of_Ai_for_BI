package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/**
 * direct 交换机：可以指定消息发送到哪个队列,相较于fanout交换机更加灵活
 */
public class DirectProducer {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {

        //1.创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //2.创建连接和通道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            //3.创建交换机  声明direct类型的交换机
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String userInput = scanner.nextLine();
                if (userInput.length() < 1) {
                    continue;
                }
                String[] inputStr = userInput.split(" ");
                //取要发的消息
                String message = inputStr[ 0 ];
                //取指定的路由键(通过路由键把消息转发给指定的队列)
                String routeKey = inputStr[ 1 ];

                //4.将消息发送到指定的交换机（direct交换机）和指定的队列(通过路由键指定)
                channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + routeKey + "':'" + message + "'");
            }
        }
    }
}