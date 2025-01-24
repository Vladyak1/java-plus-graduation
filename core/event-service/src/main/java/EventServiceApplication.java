import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import client.CommentServiceClient;
import client.RequestServiceClient;
import client.UserServiceClient;
import stat.client.MainStatsClient;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.core.event", "ru.practicum.client"})
@EnableFeignClients(clients = {
        MainStatsClient.class, UserServiceClient.class,
        CommentServiceClient.class, RequestServiceClient.class})
public class EventServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }
}
