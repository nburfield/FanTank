package com.fantank.config;

import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SignInAdapter;

import com.fantank.service.AuthUtil;

@Configuration
@EnableWebSecurity
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
	
    @Autowired
    private DataSource dataSource;

//	@Autowired
//    private ConnectionFactoryLocator connectionFactoryLocator;
	
//	@Autowired
//	private SignInAdapter signInAdapter;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/", "/user/reset*", "/signin/facebook*", "/signup*", "/user/changePassword*", "/login*", "/logout*", "/explore", "/explore/*", "/register*", "/registrationConfirm*", "/css/**/*", "/images/**/*", 
                		"/js/**/*", "/fonts/**/*", "/temporary/userValidation*", "/temporary/passwordResetEmail*").permitAll()
                .antMatchers("/invalidSession*").anonymous()
                .antMatchers("/user/updatePassword*","/user/savePassword*","/updatePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                .anyRequest().hasAuthority("READ_PRIVILEGE")
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
            .permitAll()
                .and()
            .sessionManagement()
                .invalidSessionUrl("/login")
                .maximumSessions(1).sessionRegistry(sessionRegistry()).and()
                .sessionFixation().none()
            .and()
            .logout()
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(false)
                .logoutSuccessUrl("/login?message=" + messages.getMessage("message.accountLogout", null, Locale.ENGLISH))
                .deleteCookies("JSESSIONID")
                .permitAll();
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
//    public ProviderSignInController providerSignInController() {
//    	ProviderSignInController providerSignInController = new ProviderSignInController(
//    			connectionFactoryLocator, usersConnectionRepository());
//    	providerSignInController.setSignUpUrl("/signup");
//        return providerSignInController;
//    }
    
//    @Bean
//    public SocialConfigurer socialConfigurerAdapter(DataSource dataSource) {
//        return new DatabaseSocialConfigurer(dataSource);
//    }
   
//    @Bean
//    @Scope(value="singleton", proxyMode=ScopedProxyMode.INTERFACES)
//    public UsersConnectionRepository usersConnectionRepository() {
//    	JdbcUsersConnectionRepository jdbcUsersConnectionRepository = new JdbcUsersConnectionRepository(
//    			dataSource, connectionFactoryLocator, Encryptors.noOpText());
//        return jdbcUsersConnectionRepository;
//    }
    
    
    @Bean
    public SocialConfigurer socialConfigurerAdapter() {
        return new DatabaseSocialConfigurer(dataSource);
    }

    @Bean
    public SignInAdapter authSignInAdapter() {
        return (email, connection, request) -> {
            AuthUtil.authenticate(connection);
            return null;
        };
    }
}
