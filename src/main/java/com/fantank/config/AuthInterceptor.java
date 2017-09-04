package com.fantank.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuthInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(userDetails.toString() == "anonymousUser") {
			return true;
		}
		
		response.sendRedirect("/");
		return false;
	}

}
