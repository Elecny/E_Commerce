package com.example.e_commerce;

import com.example.e_commerce.entity.User;
import com.example.e_commerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableCaching
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }


    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            // Only insert if no users exist
            if (userRepository.count() == 0) {
                
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setEmail("admin@example.com");
                admin.setEnabled(true);
                admin.setCreateAt(LocalDateTime.now());
                userRepository.save(admin);
                
                User user1 = new User();
                user1.setUsername("john_doe");
                user1.setPassword("password123");
                user1.setEmail("john@example.com");
                user1.setEnabled(true);
                user1.setCreateAt(LocalDateTime.now());
                userRepository.save(user1);
                
                User user2 = new User();
                user2.setUsername("jane_smith");
                user2.setPassword("password456");
                user2.setEmail("jane@example.com");
                user2.setEnabled(true);
                user2.setCreateAt(LocalDateTime.now());
                userRepository.save(user2);
                
                User user3 = new User();
                user3.setUsername("bob_wilson");
                user3.setPassword("password789");
                user3.setEmail("bob@example.com");
                user3.setEnabled(true);
                user3.setCreateAt(LocalDateTime.now());
                userRepository.save(user3);
                
                System.out.println("✅ Sample users inserted!");
            }
        };
    }
}