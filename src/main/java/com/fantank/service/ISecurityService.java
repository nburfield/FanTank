package com.fantank.service;

public interface ISecurityService {
	String findLoggedInUsername();
	String validatePasswordResetToken(long id, String token);
}
