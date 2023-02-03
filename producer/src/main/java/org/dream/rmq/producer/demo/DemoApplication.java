package org.dream.rmq.producer.demo;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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

        Connection connection = null;
        Channel channel = null;

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            String message = "Hello World";
            channel.queueDeclare("DEMO_QUEUE", false, false, false, null);
            channel.basicPublish("", "DEMO_QUEUE", null, message.getBytes());

            log.info("[Producer] - Message sent : {}", message);
        }
        catch(IOException | TimeoutException ex) {
            log.error("Error during message publish", ex);
        }
        finally {
            if(!Objects.isNull(connection)) {
                connection.close();
            }
        }
    }
}
