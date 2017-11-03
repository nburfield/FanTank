package com.fantank.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fantank.config.Routes;
import com.fantank.dto.UserDto;
import com.fantank.model.Offering;
import com.fantank.model.Role;
import com.fantank.model.User;
import com.fantank.repository.OfferingRepository;
import com.fantank.repository.RoleRepository;
import com.fantank.repository.UserRepository;
import com.fantank.service.ISecurityService;
import com.fantank.service.IUserService;

@Controller
public class MainController {

	@Autowired
	private IUserService userService;

	@Autowired
	private ISecurityService securityService;
	
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
	@Autowired
	private OfferingRepository offeringRepository;

	@GetMapping(Routes.INDEX)
	public String welcome(Model model) {
		User user = userService.findByEmail(securityService.findLoggedInUsername());
		model.addAttribute("user", user);

		return "index";
	}

	@GetMapping("/invest")
	public String explore(Model model) {
		User user = userService.findByEmail(securityService.findLoggedInUsername());
		model.addAttribute("user", user);

		return "invest0";
	}

	@GetMapping("/invest/{name}")
	public String getFundingDetails(Model model, @PathVariable("name") final String project) {
		Offering offering = offeringRepository.findByOfferingId(project);
		if(offering == null) {
			offering = offeringRepository.findById(project.replace("-", ""));
		}
		
		if(offering == null) {
			return "projects/template";
		}
		
		return "projects/" + project;
	}

	@GetMapping(Routes.LOGIN)
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
		User userData = userService.findByEmail(securityService.findLoggedInUsername());
		final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
		if(userData.getRoles().contains(adminRole)) {
			return "adminDashboard";
		}
		
		return "dashboard";
	}

	@GetMapping(Routes.USERDATA)
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
	
	@GetMapping("/user/all")
	@ResponseBody
	public List<UserDto> getAllUsers() {		
		User userData = userService.findByEmail(securityService.findLoggedInUsername());
		final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
		if(userData.getRoles().contains(adminRole)) {
			List<UserDto> users = new ArrayList<UserDto>();
			for(User user : userRepository.findAll()) {
				System.out.println(user.getFirstName());
				UserDto u = new UserDto();
				u.setId(user.getId());
				u.setEmail(user.getEmail());
				u.setFirstName(user.getFirstName());
				u.setLastName(user.getLastName());
				users.add(u);
			}
			return users;
		}
		return null;
	}

	@GetMapping("/account")
	public String getAngularAccount() {
		return "angular/account";
	}
	
	@GetMapping("/users")
	public String getAngularUsers() {
		return "angular/users";
	}
}
