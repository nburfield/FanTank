package com.fantank.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fantank.service.ISecurityService;

public class AuthInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.out.println(userDetails.toString());
		if(userDetails.toString() == "anonymousUser") {
			return true;
		}
		
		response.sendRedirect("/");
		return false;
	}

}
