package com.example.medappoint.config;

import com.example.medappoint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.medappoint.model.User appUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    return User.builder()
            .username(appUser.getEmail())
            .password(appUser.getPasswordHash())
            .roles(appUser.getRole().name().replace("ROLE_",""))
            .build();
    }

}
