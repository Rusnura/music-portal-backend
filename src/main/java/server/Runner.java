package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Runner extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
    }
}