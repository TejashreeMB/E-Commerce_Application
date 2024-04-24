package com.retail.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.retail.jwt.JwtFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
	private UserDetailsService userDetailService;
	private JwtFilter jwtFilter;

	public SecurityConfig(UserDetailsService userDetailService, JwtFilter jwtFilter) {
		super();
		this.userDetailService = userDetailService;
		this.jwtFilter = jwtFilter;
	}

	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	AuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailService);
		return provider;
	}	


	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		return http.authorizeHttpRequests(auth->auth.requestMatchers("/**")
				.permitAll()
				.anyRequest().authenticated())
				.csrf(csrf->csrf.disable())
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Bean
	AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception
	{
		return configuration.getAuthenticationManager();
	}
}

