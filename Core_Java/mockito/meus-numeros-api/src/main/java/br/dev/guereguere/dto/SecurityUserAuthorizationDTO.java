package br.dev.guereguere.dto;

import lombok.Data;


import java.util.HashSet;
import java.util.Set;


@Data
public class SecurityUserAuthorizationDTO {

    private Integer id;
    private String mail;
    private Set<String> credentials = new HashSet<>();
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;


    public SecurityUserAuthorizationDTO(){}

}
