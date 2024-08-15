package com.poc.springsecurity.controller.dto;

public record LoginResponse(String accesToken, Long expiresIn) {
}
