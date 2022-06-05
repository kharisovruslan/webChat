package my.webChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@EnableScheduling
public class WebChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebChatApplication.class, args);
	}
}
