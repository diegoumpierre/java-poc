package br.dev.guereguere.converter;

import br.dev.guereguere.dto.SecurityUserAuthorizationDTO;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SecurityUserConverter {

//    public SecurityUserAuthorizationDTO convertDomainToDTO(SecurityUserDetailsOld securityUserDetails) {
//        SecurityUserAuthorizationDTO securityUserDTO = new SecurityUserAuthorizationDTO();
//        if (securityUserDetails == null) return securityUserDTO;
//        securityUserDTO.setId(securityUserDetails.getId());
//        securityUserDTO.setMail(securityUserDetails.getMail());
//        //securityUserDTO.setCredentials(securityUserDetails.getCredentials());
//        securityUserDTO.setEnabled(securityUserDetails.isEnabled());
//        securityUserDTO.setAccountNonExpired(securityUserDetails.isAccountNonExpired());
//        securityUserDTO.setAccountNonLocked(securityUserDetails.isAccountNonLocked());
//        securityUserDTO.setCredentialsNonExpired(securityUserDetails.isCredentialsNonExpired());
//
//        return (securityUserDTO);
//    }





//    public SecurityUserDetailsOld convertDTOtoDomain(SecurityUserAuthorizationDTO securityUserDTO) {
//
//        SecurityUserDetailsOld securityUserDetails = new SecurityUserDetailsOld();
//        if (securityUserDTO == null) return securityUserDetails;
//
//        securityUserDetails.setId(securityUserDTO.getId());
//        securityUserDetails.setMail(securityUserDTO.getMail());
//
//        //Set<SecurityUserCredentialEnum> credentials = securityUserDTO.getCredentials();
//
//        Set<Integer> integers = new HashSet<>();
//        //for (SecurityUserCredentialEnum securityUserCredentialEnum : credentials){
//        //    integers.add(securityUserCredentialEnum.getCode());
//        //}
//        securityUserDetails.setCredentials(integers);
//        securityUserDetails.setEnabled(securityUserDTO.isEnabled());
//        securityUserDetails.setAccountNonExpired(securityUserDTO.isAccountNonExpired());
//        securityUserDetails.setAccountNonLocked(securityUserDTO.isAccountNonLocked());
//        securityUserDetails.setCredentialsNonExpired(securityUserDTO.isCredentialsNonExpired());
//
//        return (securityUserDetails);
//    }

}
