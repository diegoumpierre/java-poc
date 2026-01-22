package com.poc.auth.security;

import com.poc.auth.client.dto.InternalUserDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final String name;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    // Multi-tenancy context
    private UUID tenantId;
    private UUID membershipId;

    public CustomUserDetails(InternalUserDto user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.enabled = user.getEnabled() != null ? user.getEnabled() : true;
        this.authorities = Collections.emptySet();
    }

    public CustomUserDetails(InternalUserDto user, UUID tenantId, UUID membershipId) {
        this(user);
        this.tenantId = tenantId;
        this.membershipId = membershipId;
    }

    /**
     * Minimal constructor for token refresh when only basic fields are needed.
     */
    public CustomUserDetails(UUID id, String email, String password, String name, boolean enabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.enabled = enabled;
        this.authorities = Collections.emptySet();
    }

    public void setTenantContext(UUID tenantId, UUID membershipId) {
        this.tenantId = tenantId;
        this.membershipId = membershipId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
