package com.retail.responsedto;

import com.retail.enums.UserRole;

public class AuthResponse {
	private int userid;
	private String username;
	private long accessExpiration;
	private long refreshExpiration;
	private UserRole userRole;
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getAccessExpiration() {
		return accessExpiration;
	}
	public void setAccessExpiration(long accessExpiration) {
		this.accessExpiration = accessExpiration;
	}
	public long getRefreshExpiration() {
		return refreshExpiration;
	}
	public void setRefreshExpiration(long refreshExpiration) {
		this.refreshExpiration = refreshExpiration;
	}
	public UserRole getUserRole() {
		return userRole;
	}
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
	
	
	

}
