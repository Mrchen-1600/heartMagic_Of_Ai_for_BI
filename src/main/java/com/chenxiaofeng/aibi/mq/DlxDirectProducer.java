package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/**
 * 死信队列：存放 过期消息、拒收消息、消息队列满了、处理失败的消息 的队列
 */
public class DlxDirectProducer {

    private static final String DEAD_EXCHANGE_NAME = "dlx_direct_exchange";
    private static final String WORK_EXCHANGE_NAME = "work_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            //声明死信交换机
            channel.exchangeDeclare(DEAD_EXCHANGE_NAME, "direct");

            //声明死信队列1
            String queueName1 = "tom_dlx_queue";
            channel.queueDeclare(queueName1, true, false, false, null);
            //给死信队列1绑定路由键dlx-tom
            channel.queueBind(queueName1, DEAD_EXCHANGE_NAME, "dlx-tom");

            //声明死信队列2
            String queueName2 = "jack_dlx_queue";
            channel.queueDeclare(queueName2, true, false, false, null);
            //给死信队列2绑定路由键dlx-jack
            channel.queueBind(queueName2, DEAD_EXCHANGE_NAME, "dlx-jack");

            //指定如何处理死信队列1消息
            DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                // 拒绝消息，并且不要重新将消息放回队列，只拒绝当前消息
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.out.println(" [dlx-tom] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };

            //指定如何处理死信队列2消息
            DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                // 拒绝消息，并且不要重新将消息放回队列，只拒绝当前消息
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.out.println(" [dlx-jack] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };

            //消费死信队列消息
            channel.basicConsume(queueName1, false, deliverCallback1, consumerTag -> {
            });
            channel.basicConsume(queueName2, false, deliverCallback2, consumerTag -> {
            });



            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String userInput = scanner.nextLine();
                if (userInput.length() < 1) {
                    continue;
                }
                String[] inputStr = userInput.split(" ");
                String message = inputStr[ 0 ];
                String routeKey = inputStr[ 1 ];

                //注意发消息时发给正常的工作队列，不要发给死信队列，正常的工作队列拒绝消息后，消息会自动发给所绑定的死信队列
                channel.basicPublish(WORK_EXCHANGE_NAME, routeKey, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + routeKey + "':'" + message + "'");
            }
        }
    }
}