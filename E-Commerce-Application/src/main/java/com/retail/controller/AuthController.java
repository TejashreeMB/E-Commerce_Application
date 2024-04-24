package com.retail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.retail.jwt.JwtService;
import com.retail.requestdto.AuthRequest;
import com.retail.requestdto.OtpRequest;
import com.retail.requestdto.UserRequest;
import com.retail.responsedto.AuthResponse;
import com.retail.responsedto.UserResponse;
import com.retail.service.AuthService;
import com.retail.util.ResponseStructure;
import com.retail.util.SimpleResponseStructure;

@RestController
@RequestMapping("/api/ev1")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
	private AuthService authService;
	private JwtService jwtService;

	public AuthController(AuthService authService, JwtService jwtService) {
		super();
		this.authService = authService;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ResponseEntity<SimpleResponseStructure> userRegistration(@RequestBody UserRequest userRequest)
	{
		return authService.userRegistration(userRequest);
	}
	
	@PostMapping("/verify_email")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OtpRequest otpRequest){
		return authService.verifyOtp(otpRequest);	
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> userLogin(@RequestBody AuthRequest authRequest)
	{
		return authService.userLogin(authRequest);
	}
}
