package com.fantank.controller;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fantank.config.Routes;
import com.fantank.config.ThymeleafTemplateNames;
import com.fantank.config.captcha.ICaptchaService;
import com.fantank.dto.PasswordDto;
import com.fantank.dto.UserDto;
import com.fantank.model.User;
import com.fantank.service.GenericResponse;
import com.fantank.service.ISecurityService;
import com.fantank.service.IUserService;
import com.fantank.service.OnRegistrationCompleteEvent;

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
    private ICaptchaService captchaService;
	
	@GetMapping(Routes.REGISTER)
	public String getRegistration(HttpServletRequest request) {
		return ThymeleafTemplateNames.REGISTER;
	}
  
	
	@PostMapping(Routes.REGISTER)
	@ResponseBody
	public GenericResponse registration(@Valid UserDto userForm, HttpServletRequest request) {
		
		final String response = request.getParameter("g-recaptcha-response");
        captchaService.processResponse(response);
		
		User registered = userService.registerNewUserAccount(userForm);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
	}
	
	@GetMapping("/signin")
	public String socialError(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("calling social error");
		String errorMessage = "Social authentication failed";
		request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
		return "redirect:/login?error=true";
	}
	
	@GetMapping("/registrationConfirm")
    public String confirmRegistration(HttpServletRequest request, Locale locale, Model model, @RequestParam("token") final String token) throws UnsupportedEncodingException {
		String result = userService.validateVerificationToken(token);
        if (result.equals("valid")) {
            model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
            return "redirect:/login";
        }

        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, messages.getMessage("auth.message." + result, null, locale));
        return "redirect:/login?error=true";
    }
	
	@GetMapping("user/token")
	public String tokenReset() {
		return "resendConfirmation";
	}
	
	@PostMapping("user/token")
	@ResponseBody
	public GenericResponse tokenReset(HttpServletRequest request, @RequestParam("email") final String userEmail) {
		User user = userService.findByEmail(userEmail);
        if (user != null) {
        	if(!user.getEnabled()) {
        		eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), getAppUrl(request)));
        		return new GenericResponse(messages.getMessage("message.resetTokenEmail", null, request.getLocale()));
        	}
        	else {
        		return new GenericResponse(messages.getMessage("message.userEnabled", null, request.getLocale()));
        	}
        }
        return new GenericResponse(messages.getMessage("message.userNotFound", null, request.getLocale()));
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
