package org.dream.rmq.consumer.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private static final String RMQ_HOST = "localhost";
    private static final int    RMQ_PORT = 5672;
    private static final String RMQ_USER = "rmqdemo";
    private static final String RMQ_PASS = "rmqdemo";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RMQ_HOST);
        factory.setPort(RMQ_PORT);
        factory.setPassword(RMQ_PASS);
        factory.setUsername(RMQ_USER);

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                log.info("[Consumer] - Received '" + message + "'");
                connection.close();
            };
            log.info("[Consumer] - Started consuming ...");

            channel.queueDeclare("DEMO_QUEUE", false, false, false, null);
            channel.basicConsume("DEMO_QUEUE", true, deliverCallback, consumerTag -> {});
        }
        catch(IOException | TimeoutException ex) {
            log.error("Error during message publish", ex);
        }

    }

}
