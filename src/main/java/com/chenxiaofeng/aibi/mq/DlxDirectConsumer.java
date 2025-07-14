package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列
 */
public class DlxDirectConsumer {

    private static final String DEAD_EXCHANGE_NAME = "dlx_direct_exchange";
    private static final String WORK_EXCHANGE_NAME = "work_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //声明正常工作的交换机
        channel.exchangeDeclare(WORK_EXCHANGE_NAME, "direct");


        //指定队列1的arguments参数，在参数中指定该队列绑定哪个死信交换机，死信消息发给哪个死信队列
        Map<String, Object> args1 = new HashMap<>();
        //绑定指定的死信交换机
        args1.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        //通过指定死信路由键来指定死信要发送到的死信队列
        args1.put("x-dead-letter-routing-key", "dlx-tom");

        //创建工作队列1 该正常队列的路由键指定为tom（队列的args参数中设置了绑定的死信交换机以及死信路由键）
        String queueName1 = "tom_queue";
        channel.queueDeclare(queueName1, true, false, false, args1);
        channel.queueBind(queueName1, WORK_EXCHANGE_NAME, "tom");


        //指定死信队列2的arguments参数，在参数中指定该队列绑定哪个死信交换机，死信消息发给哪个死信队列
        Map<String, Object> args2 = new HashMap<>();
        args2.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        args2.put("x-dead-letter-routing-key", "dlx-jack");

        //创建工作队列2 该正常队列的路由键指定为jack（队列的args参数中设置了绑定的死信交换机以及死信路由键）
        String queueName2 = "jack_queue";
        channel.queueDeclare(queueName2, true, false, false, args2);
        channel.queueBind(queueName2, WORK_EXCHANGE_NAME, "jack");


        //指定工作队列1处理消息的方式
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            //拒绝消息，从而让消息发送给死信交换机
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);

            System.out.println(" [tom] reject '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        //指定工作队列2处理消息的方式
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            //拒绝消息，从而让消息发送给死信交换机
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            System.out.println(" [jack] reject '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        //消费消息
        channel.basicConsume(queueName1, false, deliverCallback1, consumerTag -> {
        });
        channel.basicConsume(queueName2, false, deliverCallback2, consumerTag -> {
        });
    }
}