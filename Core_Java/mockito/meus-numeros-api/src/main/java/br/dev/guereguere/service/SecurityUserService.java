package br.dev.guereguere.service;

import br.dev.guereguere.entity.SecurityUser;
import br.dev.guereguere.entity.SecurityUserDetails;
import br.dev.guereguere.repository.SecurityUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class SecurityUserService {

	@Autowired
	SecurityUserRepository securityUserRepository;


	public SecurityUser loadByEmail(String email) throws UsernameNotFoundException {
		SecurityUser securityUser = securityUserRepository.findByEmail(email);
		if (securityUser == null) {
			throw new UsernameNotFoundException("User not found: " + email);
		}
		return new SecurityUser(
				securityUser.getEmail(),
				securityUser.getPassword(),
				new ArrayList<>()
		);
	}


	public SecurityUserDetails authenticated(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
		try {
			return (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		catch (Exception e) {
			return null;
		}
	}
}
