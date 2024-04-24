package com.retail.jwt;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.retail.repository.AccessTokenRepository;
import com.retail.repository.RefreshTokenRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{
	private AccessTokenRepository accessRepo;
	private RefreshTokenRepository refreshRepo;
	private JwtService jwtService;
	
	public JwtFilter(AccessTokenRepository accessRepo, RefreshTokenRepository refreshRepo, JwtService jwtService) {
		super();
		this.accessRepo = accessRepo;
		this.refreshRepo = refreshRepo;
		this.jwtService = jwtService;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
         String at = null;
         String rt = null;
         
         Cookie[] cookies = request.getCookies();
         if(cookies!=null) {
	     for(Cookie cookie:cookies)
		   {
			   if(cookie.getName().equals("at")) at=cookie.getValue();
			   if(cookie.getName().equals("rt")) rt=cookie.getValue();	   
		   }
         }
	     try {
	     if(at !=null && rt!=null) {
	    	if(accessRepo.existsByTokenAndIsBlocked(at,true) && refreshRepo.existsByTokenAndIsBlocked(rt,true))
	    		throw new RuntimeException();
	    	String username = jwtService.getUsername(at);
	    	String userRole = jwtService.getuserRole(at);
	    	Set<SimpleGrantedAuthority> authority = Collections.singleton(new SimpleGrantedAuthority(userRole));
	     
	     if(username!= null && SecurityContextHolder.getContext().getAuthentication()==null)
	     {
	    	 UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,null,authority);
	    	 token.setDetails(new WebAuthenticationDetails(request));
	    	 SecurityContextHolder.getContext().setAuthentication(token);
	     }
	    	
	}
	     }catch(ExpiredJwtException ex) {
	    	 ex.printStackTrace();
	     }catch (JwtException ex){
	    	 ex.printStackTrace();{
	     }
			
		}
	     filterChain.doFilter(request, response);
	}

}