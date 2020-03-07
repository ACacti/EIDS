package com.shj.eids;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@PropertySource("classpath:db.properties")
public class EidsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EidsApplication.class, args);
    }

}
