package com.bookexchange;

import com.bookexchange.model.User;
import com.bookexchange.model.enums.Role;
import com.bookexchange.repository.UserRepository;
import com.bookexchange.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class BookExchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookExchangeApplication.class, args);
    }

    @Bean
    @ConditionalOnBean(PasswordEncoder.class)
    CommandLineRunner dataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // Admin user
                User admin = User.builder()
                    .username("admin")
                    .email("admin@bookexchange.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .dateOfBirth(LocalDate.of(1980, 1, 1))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();
                userRepository.save(admin);

                // Sample members
                User member1 = User.builder()
                    .username("member1")
                    .email("member1@bookexchange.com")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("John")
                    .lastName("Doe")
                    .dateOfBirth(LocalDate.of(1990, 5, 15))
                    .role(Role.MEMBER)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();
                userRepository.save(member1);

                User member2 = User.builder()
                    .username("member2")
                    .email("member2@bookexchange.com")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Jane")
                    .lastName("Smith")
                    .dateOfBirth(LocalDate.of(1992, 8, 20))
                    .role(Role.MEMBER)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();
                userRepository.save(member2);

                log.info("Initial data loaded: admin/admin123, member1/password123, member2/password123");
            }
        };
    }
}