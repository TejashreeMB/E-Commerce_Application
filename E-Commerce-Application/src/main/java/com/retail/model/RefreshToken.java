package com.retail.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tokenId;
	private String token;
	private long expiration;
	private boolean isBlocked;
	public int getTokenId() {
		return tokenId;
	}
	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getExpiration() {
		return expiration;
	}
	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	public boolean isBlocked() {
		return isBlocked;
	}
	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
	
}
