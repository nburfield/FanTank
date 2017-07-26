package com.fantank.service;

import java.util.Arrays;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fantank.dto.UserDto;
import com.fantank.error.UserAlreadyExistException;
import com.fantank.model.PasswordResetToken;
import com.fantank.model.User;
import com.fantank.model.VerificationToken;
import com.fantank.repository.PasswordResetTokenRepository;
import com.fantank.repository.RoleRepository;
import com.fantank.repository.UserRepository;
import com.fantank.repository.VerificationTokenRepository;

@Service
public class UserService implements IUserService{
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private RoleRepository roleRepository;
	
	@Autowired VerificationTokenRepository tokenRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private PasswordResetTokenRepository passwordTokenRepository;
	
    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

	@Override
	public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
		if (emailExist(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email adress: " + userDto.getEmail());
        }
		
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
	}
	
	@Override
    public void createVerificationTokenForUser(final User user, final String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }
	
	@Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
	
	@Override
	public VerificationToken getVerificationToken(String VerificationToken) {
		return tokenRepository.findByToken(VerificationToken);
	}
	
	@Override
	public VerificationToken getVerificationToken(User user) {
		return tokenRepository.findByUser(user);
	}
	
    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }
    
    @Override
    public User getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }
	
	private boolean emailExist(final String email) {
        return userRepository.findByEmail(email) != null;
    }

	@Override
	public void createPasswordResetTokenForUser(User user, String token) {
		PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordTokenRepository.delete(passwordTokenRepository.findByUser(user));
        passwordTokenRepository.save(myToken);
	}
	
	@Override
	public PasswordResetToken getPasswordResetTokenForUser(User user) {
		return passwordTokenRepository.findByUser(user);
	}
	
	@Override
	public void changeUserPassword(User user, String password) {
		user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
	}
}
