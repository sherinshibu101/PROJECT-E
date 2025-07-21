package com.emart.invoice.controller;

import com.emart.invoice.entity.User;
import com.emart.invoice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "🚀 eMart Invoice Generator is Running!";
    }

    @GetMapping("/test")
    public String test() {
        return "✅ Spring Boot + MySQL + JPA is working perfectly!";
    }

    @GetMapping("/health")
    public String health() {
        return "💚 Application is healthy and ready for invoice generation!";
    }

    @GetMapping("/test/users")
    public String testUsers() {
        try {
            List<User> users = userRepository.findAll();
            return "Found " + users.size() + " users in database: " + users.toString();
        } catch (Exception e) {
            return "Error accessing users table: " + e.getMessage();
        }
    }
}