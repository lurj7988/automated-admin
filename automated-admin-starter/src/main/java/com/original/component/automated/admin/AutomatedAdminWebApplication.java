package com.original.component.automated.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.original.component.automated.admin"})
@EnableJpaRepositories(basePackages = {"com.original"})
@EntityScan(basePackages = {"com.original"})
@EnableCaching
public class AutomatedAdminWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomatedAdminWebApplication.class, args);
    }
}
