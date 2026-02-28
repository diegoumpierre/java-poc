package com.poc.chat.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChannelRequest {

    @NotBlank(message = "Channel name is required")
    private String name;

    @NotNull(message = "Channel type is required")
    private String type;

    private String description;

    private List<Long> memberIds;
}
