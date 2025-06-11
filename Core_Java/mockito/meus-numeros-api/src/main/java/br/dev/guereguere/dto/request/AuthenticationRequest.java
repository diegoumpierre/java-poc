package br.dev.guereguere.dto.request;

import io.swagger.annotations.Api;
import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}