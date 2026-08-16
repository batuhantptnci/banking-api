package com.batuhan.bankingapi;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApiApplication.class, args);
    }
    @Bean
    CommandLineRunner
    checkEntities(EntityManagerFactory entityManagerFactory){
        return args -> entityManagerFactory
                .getMetamodel()
                .getEntities()
                .forEach(entity -> System.out.println("JPA ENTITY -> " + entity.getJavaType().getName()));
    }
}
