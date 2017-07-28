package com.fantank.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Service;

import com.fantank.dto.UserDto;
import com.fantank.model.User;
import com.fantank.repository.UserRepository;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {
	
	@Autowired
	private IUserService userService;

	@Override
	public String execute(Connection<?> connection) {
		System.out.println("Calling the signup");
		
		UserDto user = new UserDto();
		UserProfile userProfile = connection.fetchUserProfile();
		//Facebook facebook = (Facebook) connection.getApi();
		//String [] fields = { "id", "email",  "first_name", "last_name" };
		//org.springframework.social.facebook.api.User userProfile = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);
		
		user.setEmail(userProfile.getEmail());
		user.setFirstName(userProfile.getFirstName());
		user.setLastName(userProfile.getLastName());
		user.setPassword(UUID.randomUUID().toString());
		userService.registerNewUserAccountSocial(user);
		return user.getEmail();
	}
}
