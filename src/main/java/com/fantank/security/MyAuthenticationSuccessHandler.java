package com.fantank.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    ActiveUserStore activeUserStore;
    
    public MyAuthenticationSuccessHandler() {
    	super();
    	//setUseReferer(true);
    }
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        handle(request, response, authentication);
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(30 * 60);
            LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
            session.setAttribute("user", user);
        }
        clearAuthenticationAttributes(request);
    }

	protected void clearAuthenticationAttributes(HttpServletRequest request) {
		final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		final String targetUrl = determineTargetUrl(request, authentication);

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
	}
	
	protected String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
		HttpSession session = request.getSession(false);
		if(session != null) {
			String redirectUrl = (String) session.getAttribute("url_prior_login");
	        if (redirectUrl != null) {
	            session.removeAttribute("url_prior_login");
	            return redirectUrl;
	        }
		}
		
		return determineTargetUrl(authentication);
	}

	protected String determineTargetUrl(Authentication authentication) {
		boolean isUser = false;
        boolean isAdmin = false;
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("READ_PRIVILEGE")) {
                isUser = true;
            } else if (grantedAuthority.getAuthority().equals("WRITE_PRIVILEGE")) {
                isAdmin = true;
                isUser = false;
                break;
            }
        }
        if (isUser) {
            return "/homepage.html?user=" + authentication.getName();
        } else if (isAdmin) {
            return "/console.html";
        } else {
        	return "/#MYAuthenticationSuccessHandler";
            //throw new IllegalStateException();
        }
	}

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
}
