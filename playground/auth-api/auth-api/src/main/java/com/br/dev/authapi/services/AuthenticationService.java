package com.br.dev.authapi.services;

import com.br.dev.authapi.dtos.LoginUserDto;
import com.br.dev.authapi.dtos.RegisterUserDto;
import com.br.dev.authapi.entities.Tenant;
import com.br.dev.authapi.entities.User;
import com.br.dev.authapi.repositories.TenantRepository;
import com.br.dev.authapi.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final TenantRepository tenantRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            TenantRepository tenantRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantRepository = tenantRepository;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        // Find tenant by id or name
        Tenant tenant = null;
        try {
            Long tenantIdLong = Long.valueOf(input.getTenantId());
            tenant = tenantRepository.findById(tenantIdLong).orElse(null);
        } catch (NumberFormatException e) {
            tenant = tenantRepository.findByName(input.getTenantId()).orElse(null);
        }
        user.setTenant(tenant);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        // Optionally use input.getTenantId() for tenant-aware authentication
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public boolean resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
