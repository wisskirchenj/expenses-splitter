package de.cofinpro.splitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ExpensesSplitterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpensesSplitterApplication.class, args);
    }
}
