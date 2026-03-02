package com.poc.chat.dto.chat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChannelRequest {

    private String name;
    private String description;
    private String avatarUrl;
}
