package com.fantank.config;

import java.util.Locale;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.fantank.security.ActiveUserStore;

@Configuration
@EnableWebMvc
@EnableScheduling
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	public MvcConfig() {
        super();
    }
	
    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }
    
    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
    
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations("classpath:/static/app/");
         }
    }
    
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController(Routes.ROOT).setViewName(ThymeleafTemplateNames.INDEX);
        registry.addViewController(Routes.LOGIN).setViewName(ThymeleafTemplateNames.LOGIN);
        registry.addViewController(Routes.ABOUT).setViewName(ThymeleafTemplateNames.ABOUT);
        registry.addViewController(Routes.INVEST).setViewName(ThymeleafTemplateNames.INVEST);
        registry.addViewController(Routes.HOWITWORKS).setViewName(ThymeleafTemplateNames.HOWITWORKS);
        registry.addViewController(Routes.JOBS).setViewName(ThymeleafTemplateNames.JOBS);
        registry.addViewController(Routes.CONTACT).setViewName(ThymeleafTemplateNames.CONTACT);
        registry.addViewController(Routes.TERMS).setViewName(ThymeleafTemplateNames.TERMS);
        registry.addViewController(Routes.PRIVACY).setViewName(ThymeleafTemplateNames.PRIVACY);
        registry.addViewController(Routes.DISCLAIMER).setViewName(ThymeleafTemplateNames.DISCLAIMER);

        
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/login*", "/register*", "/user/reset*");
    }
}
