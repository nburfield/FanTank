package com.fantank.service;

import com.fantank.dto.UserDto;
import com.fantank.error.UserAlreadyExistException;
import com.fantank.model.PasswordResetToken;
import com.fantank.model.User;
import com.fantank.model.VerificationToken;

public interface IUserService {
	User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException;
	void createVerificationTokenForUser(User user, String token);
	User findByEmail(String email);
	VerificationToken getVerificationToken(String VerificationToken);
	VerificationToken getVerificationToken(User user);
	String validateVerificationToken(String token);
	User getUser(String token);
	void createPasswordResetTokenForUser(User user, String token);
	PasswordResetToken getPasswordResetTokenForUser(User user);
	void changeUserPassword(User user, String password);
}
