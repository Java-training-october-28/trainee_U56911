package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try by email first, then by username
        User user = userRepository.findByEmail(usernameOrEmail)
                .orElseGet(() -> userRepository.findByUsername(usernameOrEmail).orElse(null));

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email or username: " + usernameOrEmail);
        }

        // Map role to authority (ROLE_<NAME>)
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        // Build Spring Security UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail() != null ? user.getEmail() : user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(Boolean.TRUE.equals(user.getAccountLocked()))
                .disabled(!Boolean.TRUE.equals(user.getIsActive()))
                .build();
    }
}
