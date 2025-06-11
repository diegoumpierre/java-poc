package br.dev.guereguere.controller;



import br.dev.guereguere.entity.SecurityUser;
import br.dev.guereguere.filter.JWTUtil;
import br.dev.guereguere.service.SecurityUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import br.dev.guereguere.dto.request.AuthenticationRequest;

@RestController
@RequestMapping(value = "/api/auth")
@Api
public class AuthController {

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private SecurityUserService securityUserService;


	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody SecurityUser user) {
//		securityUserService.save(user);
		return ResponseEntity.ok("User registered successfully!");
	}

	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody AuthenticationRequest request) {
		try {
			securityUserService.authenticated(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
			);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}

		SecurityUser securityUser = securityUserService.loadByEmail(request.getEmail());
		String token = jwtUtil.generateToken(securityUser.getEmail());

		return ResponseEntity.ok(token);
	}


	//https://jwt.io/
	//https://www.viralpatel.net/java-create-validate-jwt-token/





}
