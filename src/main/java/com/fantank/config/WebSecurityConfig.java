package com.fantank.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.session.data.redis.config.ConfigureRedisAction;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableWebSecurity
//@EnableRedisHttpSession
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;
	
	@Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
	
	@Autowired
    private LogoutSuccessHandler myLogoutSuccessHandler;
	
	@Autowired
    private MessageSource messages;
	
	@Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers(Routes.WEBHOOK);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
    	//@formatter: off
        http
        	//.csrf().disable()
            	.authorizeRequests()
                	.antMatchers(
                		Routes.INDEX,
                		"/signup*",
                		"/offerings/*",
                		Routes.CHANGEPASSWORD,
                		Routes.USERDATA,
                		Routes.CSRF,
                		Routes.ROBOTS,
                		Routes.SOCIALERROR,
                		Routes.RESET,
                		Routes.TOKEN,
                		Routes.CONFIRM,
                		Routes.ABOUT,
                		Routes.CONTACT,
                		Routes.DISCLAIMER,
                		Routes.HOWITWORKS,
                		Routes.INVEST,
                		Routes.INVESTMENTS,
                		Routes.JOBS,
                		Routes.LOGIN,
                		Routes.PRIVACY,
                		Routes.REGISTER,
                		Routes.TERMS,
                		Routes.PROJECTS,
                		Routes.CSS,
                		Routes.JS,
                		Routes.FONTS,
                		Routes.IMAGES
                		).permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage(Routes.LOGIN)
                .defaultSuccessUrl(Routes.INDEX)
                .failureUrl(Routes.LOGIN + "?error=true")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll()
            .and()
            .sessionManagement()            	
                .invalidSessionUrl(Routes.LOGIN)
                .maximumSessions(1).sessionRegistry(sessionRegistry()).and()
                .sessionFixation().none()
            .and()
            .logout()
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(false)
                .logoutSuccessUrl(Routes.LOGIN + "?message=" + messages.getMessage("message.accountLogout", null, Locale.ENGLISH))
                .deleteCookies("JSESSIONID")
                .permitAll();
        // @formatter: on
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }
    
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    
//    @Bean
//    public static ConfigureRedisAction configureRedisAction() {
//        return ConfigureRedisAction.NO_OP;
//    }
}
