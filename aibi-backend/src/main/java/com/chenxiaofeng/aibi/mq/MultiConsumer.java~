package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * 多消费者
 */
public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        for (int i = 0; i < 2; i++) {
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            channel.basicQos(1);

            //如何处理消息
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + "编号:" + finalI + ":" + message + "'");
                try {
                    try {
                        Thread.sleep(5000);
                        /**
                         * 指定确认某条消息 {@link Channel#basicAck(long, boolean)}
                         * 参数：
                         * deliveryTag：消息标签
                         * multiple：批量确认，是指是否要一次性确认所有的历史消息直到当前这条
                         */
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (InterruptedException e) {
                        /**
                         * 指定某条消息消费失败 {@link Channel#basicNack(long, boolean, boolean)}
                         * 参数：
                         * deliveryTag：消息标签
                         * multiple：批量失败，是指是否要一次性失败所有的历史消息直到当前这条
                         * requeue：是否从新放入队列中，可用于重试
                         */
                        channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                        /**
                         * 指定拒绝某条消息 {@link Channel#basicReject(long, boolean)}
                         * 参数：
                         * deliveryTag：消息标签
                         * requeue：是否从新放入队列中，可用于重试
                         */
                        channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
                        throw new RuntimeException(e);
                    }
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            //开启消费监听
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }
}