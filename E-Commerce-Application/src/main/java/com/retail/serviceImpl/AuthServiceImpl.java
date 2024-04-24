package com.retail.serviceImpl;

import java.time.Duration;
import java.util.Date;
import java.util.Random;

import org.apache.naming.factory.SendMailFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.retail.cache.CacheStore;
import com.retail.enums.UserRole;
import com.retail.exception.InvalidOTPException;
import com.retail.exception.InvalidPasswordException;
import com.retail.exception.InvalidUserCredentialsException;
import com.retail.exception.InvalidUserEmailException;
import com.retail.exception.InvalidUserRoleException;
import com.retail.exception.OTPExpiredException;
import com.retail.exception.RegistraionSessionExpiredException;
import com.retail.exception.UserAlreadyExistByEmailException;
import com.retail.jwt.JwtService;
import com.retail.mail_service.MailService;
import com.retail.mail_service.MessageModel;
import com.retail.model.AccessToken;
import com.retail.model.Customer;
import com.retail.model.RefreshToken;
import com.retail.model.Seller;
import com.retail.model.User;
import com.retail.repository.AccessTokenRepository;
import com.retail.repository.RefreshTokenRepository;
import com.retail.repository.UserRepository;
import com.retail.requestdto.AuthRequest;
import com.retail.requestdto.OtpRequest;
import com.retail.requestdto.UserRequest;
import com.retail.responsedto.AuthResponse;
import com.retail.responsedto.UserResponse;
import com.retail.service.AuthService;
import com.retail.util.ResponseStructure;
import com.retail.util.SimpleResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AuthServiceImpl implements AuthService {

	private ResponseStructure<UserResponse> responseStructure;
	private UserRepository userRepo;
	private CacheStore<String> otpCache;
	private CacheStore<User> userCache;
	private SimpleResponseStructure simpleResponseStructure;
	private MailService mailService;
	private AuthenticationManager authenticationManager;
	private JwtService jwtService;
	private ResponseStructure<AuthResponse> authStructure;
	private AccessTokenRepository accessRepo;
	private RefreshTokenRepository refreshRepo;
	private PasswordEncoder passwordEncoder;
	
	public AuthServiceImpl(ResponseStructure<UserResponse> responseStructure, UserRepository userRepo,
			CacheStore<String> otpCache, CacheStore<User> userCache, SimpleResponseStructure simpleResponseStructure,
			MailService mailService, AuthenticationManager authenticationManager, JwtService jwtService,
			ResponseStructure<AuthResponse> authStructure, AccessTokenRepository accessRepo,
			RefreshTokenRepository refreshRepo, PasswordEncoder passwordEncoder) {
		super();
		this.responseStructure = responseStructure;
		this.userRepo = userRepo;
		this.otpCache = otpCache;
		this.userCache = userCache;
		this.simpleResponseStructure = simpleResponseStructure;
		this.mailService = mailService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.authStructure = authStructure;
		this.accessRepo = accessRepo;
		this.refreshRepo = refreshRepo;
		this.passwordEncoder = passwordEncoder;
	}
	@Value("${myapp.jwt.access.expiration}")
	private long accessExpiration;

	@Value("${myapp.jwt.refresh.expiration}")
	private long refreshExpiration;

	@Override
	public ResponseEntity<SimpleResponseStructure> userRegistration(UserRequest userRequest) {
		if(userRepo.existsByEmail(userRequest.getEmail()))
			throw new UserAlreadyExistByEmailException("Failed to register user");
		User user = mapToChildEntity(userRequest);	
		String otp = generateOTP();

		otpCache.add(user.getEmail(), otp);
		userCache.add(user.getEmail(), user);

		System.out.println(otp);

		//send mail with otp
		try {
			SendOTP(user,otp);
		} catch (MessagingException e) {
			throw new InvalidUserEmailException("Invalid Otp");
		}

		// return user data
		return ResponseEntity.ok(simpleResponseStructure.setStatus(HttpStatus.ACCEPTED.value())
				.setMessage("Verify OTP sent through mail to complete registration |"+"OTP expires in 1 min"));

	}

	private void SendOTP(User user, String otp) throws MessagingException  {
		MessageModel model = new MessageModel();
		model.setTo(user.getEmail());
		model.setSubject("Verify Your OTP");
		model.setText(
				"<p>Hi,<br>"
						+"Thanks for your interest in E-Com,"
						+"please verify your mail Id using the OTP given below.</p>"
						+"<br>"
						+"<h1>"+otp+"</h1>"
						+"<br>"
						+"<p>Please ignore if its not you </p>"
						+"<br>"
						+"with best regards"
						+"<h3>E-Com</h3>"
				);
		mailService.sendMailMessage(model);
	}

	private String generateOTP() {
		return String.valueOf(new Random().nextInt(100000,999999));
	}

	private <T extends User> T mapToChildEntity(UserRequest userRequest) {
		UserRole role = userRequest.getUserRole();

		User user = null;
		switch(role) {
		case SELLER ->user = new Seller();
		case CUSTOMER -> user = new Customer();
		default -> throw new InvalidUserRoleException("");
		}

		user.setDisplayName(userRequest.getName());
		user.setUsername(userRequest.getEmail().split("@gmail.com")[0]);
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setUserRole(role);
		user.setEmailVerified(false);

		return (T) user;
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OtpRequest otpRequest) {
		if(otpCache.get(otpRequest.getEmail()) == null) throw new OTPExpiredException("OTP already expired");
		if(!otpCache.get(otpRequest.getEmail()).equals(otpRequest.getOtp())) throw new InvalidOTPException("Verification failed");
		User user = userCache.get(otpRequest.getEmail());
		if(user == null) throw new RegistraionSessionExpiredException("Registration is expired");
		user.setEmailVerified(true);
		user = userRepo.save(user);
 
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(responseStructure.setStatus(HttpStatus.CREATED.value())
						.setMessage("otp verified successfully")
						.setData(mapToUserResponse(user)));
	}

	private UserResponse mapToUserResponse(User user) {
		UserResponse userResponse = new UserResponse();
		userResponse.setUserId(user.getUserId());
		userResponse.setUserName(user.getUsername());
		userResponse.setName(user.getDisplayName());
		userResponse.setEmail(user.getEmail());
		userResponse.setDeleted(user.isDeleted());
		userResponse.setEmailVerified(user.isEmailVerified());
		userResponse.setUserRole(user.getUserRole());
		return userResponse;
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> userLogin(AuthRequest authRequest) {
		String username = authRequest.getUsername().split("@gmail.com") [0];

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));

		if(!authentication.isAuthenticated()) throw new InvalidUserCredentialsException("User is not authenticated");

		SecurityContextHolder.getContext().setAuthentication(authentication);
	//	if(!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) throw new InvalidUserCredentialsException("User is not authenticated");

		//generate access and refresh token
		HttpHeaders headers = new HttpHeaders();
		
		return userRepo.findByUsername(username).map(user -> {
			generateAccessToken(user,user.getUserRole().toString(),headers);
			generateRefreshToken(user,user.getUserRole().toString(),headers);
			return ResponseEntity.ok().headers(headers).body(authStructure.setStatus(HttpStatus.OK.value())
					.setMessage("Authentication SuccessFully")
					.setData(mapToAuthResponse(user)));
		}).orElseThrow();
	}

	private AuthResponse mapToAuthResponse(User user) {
		AuthResponse authResponse = new AuthResponse();
		authResponse.setUserid(user.getUserId());
		authResponse.setUsername(user.getUsername());
		authResponse.setUserRole(user.getUserRole());
		authResponse.setAccessExpiration(accessExpiration/1000);
		authResponse.setRefreshExpiration(refreshExpiration/1000);
		return authResponse;
		}

	private String configureCookie(String name, String value, long maxAge) {
		return ResponseCookie.from(name,value)
				.domain("localhost")
				.path("/")
				.httpOnly(true)
				.secure(false)
				.maxAge(Duration.ofMillis(maxAge))
				.sameSite("Lax")
				.build().toString();
	}

	private void generateRefreshToken(User user,String role, HttpHeaders headers) {
		String token = jwtService.generateRefreshToken(user.getUsername(),role);
		headers.add(HttpHeaders.SET_COOKIE, configureCookie("rt",token,refreshExpiration));
		RefreshToken rt = new RefreshToken();
		rt.setTokenId(0);
		rt.setToken(token);
		rt.setExpiration(refreshExpiration);
		rt.setBlocked(false);
		refreshRepo.save(rt);
		
		// store token to the db
	}
	private void generateAccessToken(User user,String role, HttpHeaders headers) {
		String token = jwtService.generateAccessToken(user.getUsername(),role);
		headers.add(HttpHeaders.SET_COOKIE, configureCookie("at",token,accessExpiration));
		AccessToken at = new AccessToken();
		at.setTokenId(0);
		at.setToken(token);
		at.setBlocked(false);
		at.setExpiration(accessExpiration);
		accessRepo.save(at);
	}
}