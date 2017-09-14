package com.fantank.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fantank.dto.UserDto;
import com.fantank.error.UserNotFoundException;
import com.fantank.model.User;
import com.fantank.service.ISecurityService;
import com.fantank.service.IUserService;

@Controller
public class MainController {

	@Autowired
	private IUserService userService;

	@Autowired
	private ISecurityService securityService;

	@GetMapping("/")
	public String welcome(Model model) {
		User user = userService.findByEmail(securityService.findLoggedInUsername());
		model.addAttribute("user", user);

		return "index";
	}

	@GetMapping("/invest")
	public String explore(Model model) {
		User user = userService.findByEmail(securityService.findLoggedInUsername());
		model.addAttribute("user", user);

		return "invest";
	}

	@GetMapping("/invest/{name}")
	public String getFundingDetails(Model model, @PathVariable("name") final String project) {
		User user = userService.findByEmail(securityService.findLoggedInUsername());
		model.addAttribute("user", user);

		return "projects/" + project;
	}

	@GetMapping("/login")
	public String getLogin(HttpServletRequest request, @RequestParam(value = "redirect", required = false, 	defaultValue = "false") Boolean redirect) {
		if(redirect) {
			System.out.println("Redirect enabled");
			String referrer = request.getHeader("Referer");
			request.getSession().setAttribute("url_prior_login", referrer);
		}

		return "login";
	}

	@GetMapping("/dashboard/*")
	public String getDashboardRedirect() {
		return "redirect:/dashboard";
	}

	@GetMapping("/dashboard")
	public String getDashboard() {
		return "dashboard";
	}

	@GetMapping("/user/data")
	@ResponseBody
	public UserDto getLoggedUser() {

		User userData = userService.findByEmail(securityService.findLoggedInUsername());
		if(userData != null) {
			UserDto user = new UserDto();
			user.setId(userData.getId());
			user.setEmail(userData.getEmail());
			user.setFirstName(userData.getFirstName());
			user.setLastName(userData.getLastName());
			return user;
		}
		return null;
	}

	@GetMapping("/account")
	public String getAccount() {
		return "angular/account";
	}
}
