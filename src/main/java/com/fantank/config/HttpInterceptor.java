package com.fantank.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.apache.commons.lang3.StringUtils;

public class HttpInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		if(request.getHeader("X-Forwarded-Proto") == null ) {
			return true;
		}
		if(!StringUtils.equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"), "https")){
			response.sendRedirect(StringUtils.replaceFirst(request.getRequestURL().toString(), "http://", "https://"));
			response.setContentType(request.getContentType());
			return false;
		}
		return true;
	}
}
