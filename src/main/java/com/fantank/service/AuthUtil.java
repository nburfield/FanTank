package com.fantank.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;

public class AuthUtil {
	
	public static void authenticate(Connection<?> connection) {
        System.out.println("Running auth");

        UserProfile userProfile = connection.fetchUserProfile();
        String username = userProfile.getEmail();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
