package com.fantank.controller;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fantank.model.User;
import com.fantank.security.ActiveUserStore;
import com.fantank.service.ISecurityService;
import com.fantank.service.IUserService;

@Controller
public class MainController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ISecurityService securityService;
	
	@Autowired
    private ActiveUserStore activeUserStore;
	
	@GetMapping("/")
	public String welcome(Model model) {
		
		System.out.println(activeUserStore.getUsers());
		System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		User user = userService.findByEmail(securityService.findLoggedInUsername());
		model.addAttribute("user", user);
		
		return "index";
	}
	
	@GetMapping("/explore")
	public String explore(Model model) {
		User user = userService.findByEmail(securityService.findLoggedInUsername());
		model.addAttribute("user", user);
		
		return "explore";
	}
	
	@GetMapping("/explore/{id}")
	public String getFundingDetails(Model model) {
		User user = userService.findByEmail(securityService.findLoggedInUsername());
		model.addAttribute("user", user);
		
		return "fundingDetails";
	}
	
	@GetMapping("/login")
	public String getLogin(HttpServletRequest request) {
		String referrer = request.getHeader("Referer");
	    request.getSession().setAttribute("url_prior_login", referrer);
		
		return "login";
	}
}
