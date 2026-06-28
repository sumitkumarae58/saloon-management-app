package com.salonbooking.security;

import com.salonbooking.entity.User;
import com.salonbooking.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailActive(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getIsBlocked()) {
            throw finalUserBlockedException();
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true, // Account Not Expired
                true, // Credentials Not Expired
                !user.getIsBlocked(), // Account Not Locked
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }

    private RuntimeException finalUserBlockedException() {
        return new UsernameNotFoundException("User account is blocked/suspended.");
    }
}
