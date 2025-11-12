package com.example.medappoint.config.jwt;

import com.example.medappoint.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        // Pomiń filtr dla publicznych endpointów
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Jeśli nie ma nagłówka Authorization lub nie zaczyna się od "Bearer ",
        // przekazujemy żądanie dalej (użytkownik nie jest uwierzytelniony)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Wyciągamy token (pomijając "Bearer ")
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);

            // Jeśli mamy email z tokenu i użytkownik nie jest jeszcze "zalogowany" w kontekście Springa
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Jeśli token jest poprawny
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Stwórz obiekt logowania dla Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Hasło nie jest potrzebne, mamy token
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    // Zapisz zalogowanego użytkownika w kontekście bezpieczeństwa
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Jeśli token jest nieprawidłowy, po prostu kontynuujemy - użytkownik nie będzie uwierzytelniony
            // Spring Security zwróci 403 jeśli endpoint wymaga autoryzacji
        }
        // Przekaż żądanie do kolejnego filtra
        filterChain.doFilter(request, response);
    }
}