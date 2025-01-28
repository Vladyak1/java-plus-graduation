package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import client.EventServiceClient;
import client.UserServiceClient;
import ru.practicum.client.StatsClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {EventServiceClient.class, UserServiceClient.class, StatsClient.class})
public class RequestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApplication.class, args);
    }
}