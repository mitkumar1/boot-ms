package com.kumar.mithlesh.own.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableScheduling
public class AppConfig {

    // Example scheduled method
    @Scheduled(fixedRate = 60000)
    public void scheduledTask() {
        System.out.println("Scheduled task running: " + java.time.LocalDateTime.now());
    }

    // Spring Security configuration (permit all for actuator/health, require auth for others)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic();
        return http.build();
    }
}
