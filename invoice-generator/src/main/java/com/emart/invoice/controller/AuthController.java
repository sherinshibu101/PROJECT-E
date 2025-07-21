package com.emart.invoice.controller;

import com.emart.invoice.dto.LoginRequest;
import com.emart.invoice.dto.LoginResponse;
import com.emart.invoice.entity.User;
import com.emart.invoice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Username: " + loginRequest.getUsername());
            
            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
            
            if (!userOpt.isPresent()) {
                System.out.println("User not found: " + loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid username or password"));
            }
            
            User user = userOpt.get();
            System.out.println("User found: " + user.getUsername());
            
            // Check password
            System.out.println("Checking password...");
            System.out.println("Input password: " + loginRequest.getPassword());
            System.out.println("Stored hash: " + user.getPasswordHash());

            boolean passwordMatches = false;

            // Try BCrypt first
            try {
                passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash());
                System.out.println("BCrypt match result: " + passwordMatches);
            } catch (Exception e) {
                System.out.println("BCrypt failed: " + e.getMessage());
            }

            // For demo purposes, also allow plain text password comparison
            if (!passwordMatches && loginRequest.getPassword().equals("emartadmin@123") && user.getUsername().equals("admin")) {
                passwordMatches = true;
                System.out.println("Demo password accepted (plain text fallback)");
            }

            // Also try direct hash comparison for testing
            if (!passwordMatches && user.getPasswordHash().equals("$2a$10$N9qo8uLOickgx2ZMRZoMye1VdLSnqpjx.ocs.hpvZu.1yNjHdyJrq")) {
                if (loginRequest.getPassword().equals("emartadmin@123")) {
                    passwordMatches = true;
                    System.out.println("Direct hash comparison successful");
                }
            }
            
            if (!passwordMatches) {
                System.out.println("Password mismatch for user: " + loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid username or password"));
            }
            
            // Generate simple token (in production, use JWT)
            String token = UUID.randomUUID().toString();
            
            // Update last login
            user.setLastLogin(java.time.LocalDateTime.now());
            userRepository.save(user);
            
            // Create response
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUser(createUserResponse(user));
            
            System.out.println("Login successful for user: " + user.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Login failed"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a real application, you would invalidate the token here
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-user")
    public ResponseEntity<?> testUser() {
        try {
            System.out.println("=== TESTING USER LOOKUP ===");
            Optional<User> userOpt = userRepository.findByUsername("admin");

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("User found: " + user.getUsername());
                System.out.println("Password hash: " + user.getPasswordHash());
                System.out.println("Role: " + user.getRole());

                Map<String, Object> response = new HashMap<>();
                response.put("found", true);
                response.put("username", user.getUsername());
                response.put("role", user.getRole());
                response.put("passwordHashLength", user.getPasswordHash().length());

                return ResponseEntity.ok(response);
            } else {
                System.out.println("User 'admin' not found in database");
                return ResponseEntity.ok(Map.of("found", false, "message", "User not found"));
            }
        } catch (Exception e) {
            System.err.println("Error testing user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin() {
        try {
            System.out.println("=== CREATING ADMIN USER ===");

            // Check if admin already exists
            if (userRepository.findByUsername("admin").isPresent()) {
                return ResponseEntity.ok(Map.of("message", "Admin user already exists"));
            }

            // Create new admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("emartadmin@123"));
            admin.setRole("admin");
            admin.setCreatedAt(java.time.LocalDateTime.now());

            userRepository.save(admin);

            System.out.println("Admin user created successfully");
            return ResponseEntity.ok(Map.of("message", "Admin user created successfully"));

        } catch (Exception e) {
            System.err.println("Error creating admin user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update-admin-password")
    public ResponseEntity<?> updateAdminPassword() {
        try {
            System.out.println("=== UPDATING ADMIN PASSWORD ===");

            Optional<User> adminOpt = userRepository.findByUsername("admin");
            if (!adminOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Admin user not found"));
            }

            User admin = adminOpt.get();
            admin.setPasswordHash(passwordEncoder.encode("emartadmin@123"));
            userRepository.save(admin);

            System.out.println("Admin password updated successfully");
            return ResponseEntity.ok(Map.of("message", "Admin password updated to emartadmin@123"));

        } catch (Exception e) {
            System.err.println("Error updating admin password: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("role", user.getRole());
        return userResponse;
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
