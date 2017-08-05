package com.fantank.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.web.client.RestOperations;

public class AuthUtil {
	
	public static void authenticate(Connection<?> connection) {
        System.out.println("Running auth");

        UserProfile userProfile = connection.fetchUserProfile();
        String username = userProfile.getEmail();
        if(userProfile.getEmail() == null) {
        	if(connection.getKey().getProviderId() == "twitter") {
				Twitter twitter = (Twitter) connection.getApi();
				RestOperations restOperations = twitter.restOperations();
		        TwitterProfileWithEmail response = restOperations.getForObject("https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true", TwitterProfileWithEmail.class);
	        	username = response.getEmail();
        	}
        	else {
        		throw new RuntimeException("User Social Email not available");
        	}
        }
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
