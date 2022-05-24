package com.qrcodeemargement.app.controllers;


import com.qrcodeemargement.app.models.Role;
import com.qrcodeemargement.app.models.fabrick.FabricUser;
import com.qrcodeemargement.app.models.User;
import com.qrcodeemargement.app.payload.request.LoginRequest;
import com.qrcodeemargement.app.payload.request.SignupRequest;
import com.qrcodeemargement.app.payload.response.ApiErrorResponse;
import com.qrcodeemargement.app.payload.response.JwtResponse;
import com.qrcodeemargement.app.security.jwt.JwtUtils;
import com.qrcodeemargement.app.security.services.UserDetailsImpl;
import com.qrcodeemargement.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(AuthController.URL_AUTH)
public class AuthController {
	public static final String URL_AUTH = "/api/auth";
	public static final String ERROR_MISSING_ROLES = "Error: Missing roles";
	public static final String ERROR_EMAIL_IS_ALREADY_IN_USE = "Error: Email is already in use!";
	public static final String L_UTILISATEUR_AVEC_LE_ROLE_ETUDIENT_N_A_PAS_DE_NUMERO_ETUDIENT = "L'utilisateur avec le role etudient n'a pas de numéro etudient";
	public static final String UN_UTILISATEUR_SANS_LE_ROLE_ROLE_STUDENT_NE_PEUT_PAS_AVOIR_UN_NUMERO_ETUDIENT = "Un utilisateur sans le role ROLE_STUDENT ne peut pas avoir un numero etudient";
	public static final String SIGNIN = "/signIn";
	public static final String SIGNUP = "/signUp";

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserService userService;

	@Autowired
	FabricUser fabricUser;

	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping(SIGNIN)
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).toList();

		return ResponseEntity.ok(new JwtResponse(jwt,
												 userDetails.getId(), 
												 userDetails.getUsername(),
												 Role.valueOf(roles.get(0))));
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping(SIGNUP)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (Boolean.TRUE.equals(userService.existsByEmail(signUpRequest.getEmail()))) {
			return ResponseEntity.status(409)
					.body(new ApiErrorResponse(HttpStatus.resolve(409),ERROR_EMAIL_IS_ALREADY_IN_USE));
		}

		if (signUpRequest.getRole() == null) {
			return ResponseEntity
					.badRequest()
					.body(new ApiErrorResponse(BAD_REQUEST,ERROR_MISSING_ROLES));
		} else {
			if (signUpRequest.getRole().equals(Role.ROLE_STUDENT) && (signUpRequest.getNumEtu() == null || signUpRequest.getNumEtu().isEmpty())){
				return ResponseEntity.badRequest().body(new ApiErrorResponse(BAD_REQUEST, L_UTILISATEUR_AVEC_LE_ROLE_ETUDIENT_N_A_PAS_DE_NUMERO_ETUDIENT));
			}
			if (!signUpRequest.getRole().equals(Role.ROLE_STUDENT) && (signUpRequest.getNumEtu() != null && !signUpRequest.getNumEtu().isEmpty())){
				return ResponseEntity.badRequest().body(new ApiErrorResponse(BAD_REQUEST, UN_UTILISATEUR_SANS_LE_ROLE_ROLE_STUDENT_NE_PEUT_PAS_AVOIR_UN_NUMERO_ETUDIENT));
			}
			User user = fabricUser.build(signUpRequest.getEmail(),
					encoder.encode(signUpRequest.getPassword()),
					signUpRequest.getFirstName(),
					signUpRequest.getLastName(),
					signUpRequest.getNumEtu(),
					signUpRequest.getRole());

			User user1 = userService.save(user);

			return ResponseEntity.created(URI.create("/api/user/"+ user1.getId())).build();

		}
	}
}