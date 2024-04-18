package com.retail.serviceImpl;

import java.util.Date;
import java.util.Random;

import org.apache.naming.factory.SendMailFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.retail.cache.CacheStore;
import com.retail.enums.UserRole;
import com.retail.exception.InvalidOTPException;
import com.retail.exception.InvalidUserEmailException;
import com.retail.exception.InvalidUserRoleException;
import com.retail.exception.OTPExpiredException;
import com.retail.exception.RegistraionSessionExpiredException;
import com.retail.exception.UserAlreadyExistByEmailException;
import com.retail.mail_service.MailService;
import com.retail.mail_service.MessageModel;
import com.retail.model.Customer;
import com.retail.model.Seller;
import com.retail.model.User;
import com.retail.repository.UserRepository;
import com.retail.requestdto.OtpRequest;
import com.retail.requestdto.UserRequest;
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
	
	public AuthServiceImpl(ResponseStructure<UserResponse> responseStructure, UserRepository userRepo,
			CacheStore<String> otpCache, CacheStore<User> userCache, SimpleResponseStructure simpleResponseStructure,
			MailService mailService) {
		super();
		this.responseStructure = responseStructure;
		this.userRepo = userRepo;
		this.otpCache = otpCache;
		this.userCache = userCache;
		this.simpleResponseStructure = simpleResponseStructure;
		this.mailService = mailService;
	}

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
		user.setPassword(userRequest.getPassword());
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
		//	user = userRepo.save(user);

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
	
}