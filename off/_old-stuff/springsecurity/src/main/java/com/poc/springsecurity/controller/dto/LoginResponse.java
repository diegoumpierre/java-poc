package com.poc.springsecurity.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
