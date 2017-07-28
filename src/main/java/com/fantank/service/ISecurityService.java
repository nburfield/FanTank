package com.fantank.service;

public interface ISecurityService {
	String findLoggedInUsername();
	String validatePasswordResetToken(long id, String token);
	boolean autologin(String email, String password);
}
