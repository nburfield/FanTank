package com.fantank.service;

import java.util.Arrays;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.fantank.model.PasswordResetToken;
import com.fantank.model.User;
import com.fantank.repository.PasswordResetTokenRepository;

@Service
public class SecurityService implements ISecurityService {

	@Autowired
	public AuthenticationManager authenticationManager;
	
	@Autowired
	public UserDetailsService userDetailsService;

	@Autowired
	public PasswordResetTokenRepository passwordTokenRepository;
	
	@Override
	public String validatePasswordResetToken(long id, String token) {
		final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if ((passToken == null) || (passToken.getUser().getId() != id)) {
            return "invalidToken";
        }

        final Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "expired";
        }

        final User user = passToken.getUser();
        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
	}
	
	@Override
	public String findLoggedInUsername() {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(userDetails.toString() == "anonymousUser") {
			return null;
		}
		
		if(userDetails instanceof UserDetails) {
			return ((UserDetails)userDetails).getUsername();
		}
		
		return userDetails.toString();
	}
}
