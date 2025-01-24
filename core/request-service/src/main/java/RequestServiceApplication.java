import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import client.EventServiceClient;
import client.UserServiceClient;

@SpringBootApplication
@EnableFeignClients(clients = {EventServiceClient.class, UserServiceClient.class})
public class RequestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApplication.class, args);
    }
}