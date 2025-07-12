package com.poc;

import com.poc.model.enums.SecurityUserCredentialEnum;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class SecurityUserAuthorization {

    private Integer id;
    private String mail;
    private Set<SecurityUserCredentialEnum> credentials = new HashSet<>();
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;



}
