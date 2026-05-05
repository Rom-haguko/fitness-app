package com.fitness.fitnessapp.security;

import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.security.UserPrincipal;
import com.fitness.fitnessapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("User login attempt", kv("username", username));
        return userRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found", kv("username", username));
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
    }
}