package com.example.medappoint.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Ten statyczny import jest najczystszą metodą odwołania się do konsoli H2
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Zezwól na wszystkie żądania do konsoli H2
                        .requestMatchers(toH2Console()).permitAll()

                        // 2. Cała reszta żądań wymaga logowania (na razie)
                        .anyRequest().authenticated()
                )
                // 3. Włącz domyślny formularz logowania Springa
                .formLogin(form -> form.loginPage("/login").permitAll());

        // 4. Konfiguracja *wymagana* przez konsolę H2
        // H2 Console działa w ramce (frame) i domyślnie Spring Security to blokuje
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(toH2Console())
        );
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable()) // Wyłączamy 'X-Frame-Options' dla H2
        );

        return http.build();
    }
}