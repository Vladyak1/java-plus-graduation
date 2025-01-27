package ru.practicum;

import client.UserServiceClient;
import client.EventServiceClient;
import ru.practicum.client.StatsClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {
        EventServiceClient.class,
        UserServiceClient.class,
        StatsClient.class
})
public class CommentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApplication.class, args);
    }
}
