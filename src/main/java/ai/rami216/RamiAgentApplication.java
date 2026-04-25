package ai.rami216;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:deepseek.properties")
public class RamiAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(RamiAgentApplication.class, args);
    }
}