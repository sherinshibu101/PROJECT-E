package com.emart.invoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("🚀 eMart Invoice Generator Started!");
        System.out.println("📊 Access at: http://localhost:8080");
    }
}