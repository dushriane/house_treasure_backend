package com.housetreasure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.housetreasure.repository.jpa")
@EnableMongoRepositories(basePackages = "com.housetreasure.repository.mongodb")
public class HouseTreasureBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(HouseTreasureBackendApplication.class, args);
    }
}
