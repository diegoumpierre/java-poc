package br.dev.guereguere.entity;

import java.util.Collection;
import java.util.stream.Collectors;

import br.dev.guereguere.entity.enums.SecurityUserCredentialEnum;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Data
public class SecurityUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private SecurityUser securityUser;

    private Collection<? extends GrantedAuthority> authorities;

    public SecurityUserDetails(SecurityUser securityUser) {
        super();
        this.securityUser = securityUser;
        this.authorities = this.securityUser.getCredentials()
                                    .stream().map(x -> new SimpleGrantedAuthority(x.getDescription())).collect(Collectors.toList());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return securityUser.getPassword();
    }

    @Override
    public String getUsername() {
        return securityUser.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return securityUser.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return securityUser.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return securityUser.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return securityUser.isEnabled();
    }

    public boolean hasRole(SecurityUserCredentialEnum credentialEnum) {
        return getAuthorities().contains(new SimpleGrantedAuthority(credentialEnum.getDescription()));
    }
}
