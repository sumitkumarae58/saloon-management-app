package com.salonbooking.service;

import com.salonbooking.dto.LoginRequest;
import com.salonbooking.dto.LoginResponse;
import com.salonbooking.dto.RegisterRequest;
import com.salonbooking.entity.Role;
import com.salonbooking.entity.User;
import com.salonbooking.exception.BadRequestException;
import com.salonbooking.repository.RoleRepository;
import com.salonbooking.repository.UserRepository;
import com.salonbooking.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new BadRequestException("Email is already registered.");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new BadRequestException("Specified role does not exist."));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(role)
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmailActive(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials."));

        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), user.getRole().getName());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole().getName())
                .build();
    }
}
