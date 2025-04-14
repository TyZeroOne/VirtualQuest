package org.virtualquest.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ к Swagger и API-документации без авторизации
                        .requestMatchers(
                                "/",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                // Отключаем CSRF для удобства тестирования API
                .csrf(csrf -> csrf.disable())
                // Отключаем форму входа (используем API-аутентификацию)
                .formLogin(form -> form.disable());

        return http.build();
    }
}