package com.poc.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[\\p{L}\\s'-]*$", message = "Name contains invalid characters")
    private String name;

    @Size(max = 50, message = "Nickname must be at most 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Nickname can only contain letters, numbers, underscores and hyphens")
    private String nickname;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;
}
