package com.fantank.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.web.client.RestOperations;

import com.fantank.config.social.AuthUtil;
import com.fantank.config.social.TwitterProfileWithEmail;
import com.fantank.dto.UserDto;
import com.fantank.service.IUserService;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {
	
	@Autowired
    private DataSource dataSource;
	
	@Autowired
	private IUserService userService;

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
		for (Provider provider: socialProviders(environment)) {
			connectionFactoryConfigurer.addConnectionFactory(provider.createConnectionFactory(provider.getAppId(environment), provider.getAppSecret(environment)));
		}
	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		TextEncryptor textEncryptor = Encryptors.noOpText();
		JdbcUsersConnectionRepository jdbcConnection = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, textEncryptor);
		jdbcConnection.setConnectionSignUp(new CustomConnectionSignUp());
        return jdbcConnection;
	}
	
	@Bean
	public List<Provider> socialProviders(Environment environment) {
		List<Provider> activeProviders = new ArrayList<>();
		for (Provider provider: Provider.values()) {
			if (provider.getAppId(environment) != null && provider.getAppSecret(environment) != null) activeProviders.add(provider);
		}
		return activeProviders;
	}
	
	public enum Provider {
		
		//facebook("facebook"),
		//twitter("twitter"),
		google("google");
		//linkedin("linkedin");
		
		private String label;
		
		private Provider(String label) {
			this.label = label;
		}
		
		public ConnectionFactory<?> createConnectionFactory(String appId, String appSecret) {
			switch(this) {
//			case facebook:
//				return new FacebookConnectionFactory(appId, appSecret);
//			case twitter:
//				return new TwitterConnectionFactory(appId, appSecret);
			case google:
				return new GoogleConnectionFactory(appId, appSecret);
//			case linkedin:
//				return new LinkedInConnectionFactory(appId, appSecret);
			default:
				return null;
			}
		}
		
		public String label() {
			return label;
		}
		
		public String getAppId(Environment environment) {
			return environment.getProperty(String.format("spring.social.%s.appId", this.toString()));
		}
		
		public String getAppSecret(Environment environment) {
			return environment.getProperty(String.format("spring.social.%s.appSecret", this.toString()));
		}
	}
	
//	@Bean
//	public ProviderSignInUtils RegistrationController() {
//		return new ProviderSignInUtils(connectionFactoryLocator, userConnectionRepository);
//	}

    @Bean
    public SignInAdapter authSignInAdapter() {
        return (email, connection, request) -> {
            AuthUtil.authenticate(connection);
            return null;
        };
    }
    
    public final class CustomConnectionSignUp implements ConnectionSignUp {

		public String execute(Connection<?> connection) {
			System.out.println("Running the connection signup");
			
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
				//AuthUtil.authenticate(connection);
				return connection.getDisplayName();
			}
			return null;
		}

	}
}
