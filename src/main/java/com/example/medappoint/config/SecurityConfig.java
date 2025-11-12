package com.example.medappoint.config;

import com.example.medappoint.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Włącza adnotacje @PreAuthorize itd.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService; // Nasz UserDetailsServiceImpl

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Ten Bean jest potrzebny do weryfikacji użytkownika przy logowaniu
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Ten Bean jest potrzebny do zarządzania procesem logowania
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Wyłączamy CSRF (nie jest potrzebne przy bezstanowym API z JWT)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(toH2Console())
                        .ignoringRequestMatchers("/api/auth/**") // Ignorujemy ścieżki logowania/rejestracji
                )
                // 2. Definiujemy "publiczne" i "prywatne" endpointy
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(toH2Console()).permitAll() // Zezwól na H2
                        .requestMatchers("/api/auth/**").permitAll() // Zezwól na /api/auth/register i /api/auth/login
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Zezwól na Swaggera
                        .anyRequest().authenticated() // Wszystkie inne żądania muszą być uwierzytelnione
                )
                // 3. Ustawiamy zarządzanie sesją na "BEZSTANOWE" (STATELESS)
                // Spring nie będzie tworzył sesji HTTP
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. Mówimy Springowi, jakiego dostawcy uwierzytelniania ma użyć
                .authenticationProvider(authenticationProvider())
                // 5. Dodajemy nasz filtr JWT *przed* standardowym filtrem logowania
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Wyłączenie ramki dla H2 (jak wcześniej)
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
        );

        return http.build();
    }
}