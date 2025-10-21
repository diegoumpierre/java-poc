package com.br.dev.authapi.controllers;

import com.br.dev.authapi.entities.User;
import com.br.dev.authapi.dtos.LoginUserDto;
import com.br.dev.authapi.dtos.RegisterUserDto;
import com.br.dev.authapi.responses.LoginResponse;
import com.br.dev.authapi.services.AuthenticationService;
import com.br.dev.authapi.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/auth")
@RestController
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        // Optionally associate tenantId with user here if needed
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user and returns JWT token")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateTokenWithTenant(authenticatedUser, loginUserDto.getTenantId());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password", description = "Resets the password for a user by email")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        boolean success = authenticationService.resetPassword(resetPasswordDto.getEmail(), resetPasswordDto.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successful");
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}