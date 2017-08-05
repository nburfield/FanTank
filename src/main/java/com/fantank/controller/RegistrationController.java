package com.fantank.controller;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.google.api.Google;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.fantank.dto.PasswordDto;
import com.fantank.dto.UserDto;
import com.fantank.model.User;
import com.fantank.service.AuthUtil;
import com.fantank.service.GenericResponse;
import com.fantank.service.ISecurityService;
import com.fantank.service.IUserService;
import com.fantank.service.OnRegistrationCompleteEvent;
import com.fantank.service.TwitterProfileWithEmail;

@Controller
public class RegistrationController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ISecurityService securityService;
	
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	
	@Autowired
    private MessageSource messages;
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Autowired
    private Environment env;
	
	@Autowired
	private ProviderSignInUtils signInUtils;
	
	@GetMapping("/register")
	public String getRegistration(HttpServletRequest request) {
		return "registration";
	}
	
	@PostMapping("/register")
	@ResponseBody
	public GenericResponse registration(@Valid UserDto userForm, HttpServletRequest request) {
		User registered = userService.registerNewUserAccount(userForm);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse(registered.getEmail());
	}
	
	@GetMapping("/signup")
	public String socialRegistration(Locale locale, WebRequest request, Model model) {
		System.out.println("Calling Social Registration");

		Connection<?> connection = signInUtils.getConnectionFromSession(request);
		if(connection != null) {
			UserProfile userProfile = connection.fetchUserProfile();
			UserDto user = new UserDto();
			user.setEmail(userProfile.getEmail());
			
			if(userProfile.getEmail() == null) {
				if(connection.getKey().getProviderId() == "twitter") {
					Twitter twitter = (Twitter) connection.getApi();
					RestOperations restOperations = twitter.restOperations();
			        TwitterProfileWithEmail response = restOperations.getForObject("https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true", TwitterProfileWithEmail.class);
			        user.setEmail(response.getEmail());
	        	}
	        	else {
	        		throw new RuntimeException("User Social Email not available");
	        	}
			}
			
			user.setFirstName(userProfile.getFirstName());
			user.setLastName(userProfile.getLastName());
			user.setPassword(UUID.randomUUID().toString());
			userService.registerNewUserAccountSocial(user);
			AuthUtil.authenticate(connection);
			signInUtils.doPostSignUp(user.getEmail(), request);
	        return "redirect:/";
		}
		
		model.addAttribute("message", "Failed to Authenticate Login");
		return "redirect:/login";
	}
	
	@GetMapping("/temporary/userValidation")
	public ModelAndView userValidation(@RequestParam("email") String username, HttpServletRequest request) {
		User user = userService.findByEmail(username);
		return new ModelAndView("temporaryEmail", "verification", userService.getVerificationToken(user));
	}
	
	@GetMapping("/temporary/passwordResetEmail")
	public ModelAndView passwordResetEmail(@RequestParam("email") String username, HttpServletRequest request) {
		User user = userService.findByEmail(username);
		return new ModelAndView("temporaryPassowrdReset", "verification", userService.getPasswordResetTokenForUser(user));
	}
	
	@GetMapping("/registrationConfirm")
    public String confirmRegistration(Locale locale, Model model, @RequestParam("token") final String token) throws UnsupportedEncodingException {
        String result = userService.validateVerificationToken(token);
        if (result.equals("valid")) {
            model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
            return "redirect:/login";
        }

        model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return "redirect:/badUser.html";
    }
	
	@GetMapping("user/reset")
	public String passwordReset() {
		return "passwordReset";
	}
	
	@PostMapping("user/reset")
	@ResponseBody
	public GenericResponse passwordReset(HttpServletRequest request, @RequestParam("email") final String userEmail) {
		User user = userService.findByEmail(userEmail);
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
        }
        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
	}
	
    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(final Locale locale, final Model model, @RequestParam("id") final long id, @RequestParam("token") final String token) {
        final String result = securityService.validatePasswordResetToken(id, token);
        if (result != null) {
            model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
            return "redirect:/login";
        }
        return "updatePassword";
    }
    
    @PostMapping("/user/savePassword")
    @ResponseBody
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changeUserPassword(user, passwordDto.getPassword());
        return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    }
	
	@GetMapping("/logout") 
	public String confirmLogout(Locale locale, Model model) {
		model.addAttribute("message", messages.getMessage("message.accountLogout", null, locale));
        return "login";
	}
	
	private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
	
	private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
        final String url = contextPath + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }
	
	private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}
