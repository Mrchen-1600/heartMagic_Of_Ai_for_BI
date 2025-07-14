package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * 消息确认机制--手动ack
 *      ack：消费成功
 *      nack：消费失败
 *      reject：拒绝
 *
 *  https://blog.csdn.net/qq_62939743/article/details/129352905
 *  如果没有及时进行ack，RabbitMQ会将来不及做ack的消息标记为unacked丢回RabbitMQ，
 *  被标记为unacked的消息无法被立刻重新消费，而是要等channel重启或者服务器重启才会变成ready（可消费的消息）。
 *  但等待服务器重启这个过程中如果积压了太多unacked消息，会导致MQ响应越来越慢，甚至崩溃的问题。
 *  解决方式就是及时处理消息
 *
 *  如果消息本身或者消息的处理过程出现问题怎么办？
 *      需要一种机制通知RabbitMQ，这个消息我无法处理，请让别的消费者处理。这里就有两种机制，Reject和Nack。
 *      reject和Nack的差别只有一种：reject一次只能拒绝一条消息，Nack支持批量拒绝。
 *      在拒绝消息时，可以使用requeue标识。
 *          requeue为true，被拒绝的消息会重新发送给别的队列（一般为死信队列），发送的消息在队首。
 *          requeue为false，不重新发送，这个消息就会被丢弃。
 */
public class MultiConsumer_autoack {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {

        //1.创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");


        //2.建立链接、创建频道
        final Connection connection = factory.newConnection();
        for (int i = 0; i < 2; i++) {
            final Channel channel = connection.createChannel();


            //3.创建（声明）消息队列
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            // 在控制台打印等待接收消息的提示
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            //控制每个消费者的处理任务积压数，这里即为设置每个消费者最多同时处理1个任务，这样RabbitMQ就会在给消费者新消息之前等待先前的消息被确认
            channel.basicQos(1);


            //4.定义如何处理消息,创建一个新的DeliverCallback来处理接收到的消息
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                // 将消息体转换为字符串
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);


                try {
                    System.out.println(" [x] Received '" + "编号:"+ finalI + ":" + message + "'");
                    //处理工作,模拟处理消息所花费的时间,机器处理能力有限(接收一条消息,5秒后再接收下一条消息)
                    Thread.sleep(10000);

                    /**
                     * 指定确认某条消息 {@link Channel#basicAck(long, boolean)}
                     * 参数：
                     * deliveryTag：消息标签   通过消息的元数据中的唯一标识符确认消息
                     * multiple：批量确认，指是否要一次性确认所有的历史消息直到当前这条(比如队列中积压了五条消息,指定为true就会一次性把所有消息全都确认)
                     */
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                } catch (InterruptedException e) {

                    /**
                     * 指定某条消息消费失败 {@link Channel#basicNack(long, boolean, boolean)}
                     * 参数：
                     * deliveryTag：消息标签
                     * multiple：批量失败，指是否要一次性失败所有的历史消息直到当前这条
                     * requeue：是否从新放入队列中，可用于重试
                     */
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);


                    /**
                     * 指定拒绝某条消息 {@link Channel#basicReject(long, boolean)}
                     * 参数：
                     * deliveryTag：消息标签
                     * requeue：是否从新放入队列中，可用于重试
                     */
//                    channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
                throw new RuntimeException(e);

                } finally {
                    System.out.println(" [x] Done");
                    // 手动发送应答,告诉RabbitMQ消息已经被处理
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };


            //5.开始消费监听队列中的消息
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }
}