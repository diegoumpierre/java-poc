package br.dev.guereguere.service.impl;

import java.util.Collection;

import br.dev.guereguere.entity.enums.SecurityUserCredentialEnum;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class SecurityUserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

//    private SecurityUserDetailsOld securityUserDetails;

    private Collection<? extends GrantedAuthority> authorities;
//
//    public SecurityUserDetailsImpl(SecurityUserDetailsOld securityUserDetails) {
//        super();
//        this.securityUserDetails = securityUserDetails;
//        this.authorities = this.securityUserDetails.getCredentials()
//                                    .stream().map(x -> new SimpleGrantedAuthority(x.getDescription())).collect(Collectors.toList());
//    }

    public SecurityUserDetailsImpl() { }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
//        return securityUserDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return "";
//        return securityUserDetails.getMail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
//        return securityUserDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
//        return securityUserDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
//        return securityUserDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

//    @Override
//    public boolean isEnabled() {
//
//        return securityUserDetails.isEnabled();
//    }

    public boolean hasRole(SecurityUserCredentialEnum credentialEnum) {
        return getAuthorities().contains(new SimpleGrantedAuthority(credentialEnum.getDescription()));
    }
}
