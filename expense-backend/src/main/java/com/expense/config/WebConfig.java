package com.expense.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Конфигурационный класс диспетчера сервлетов DispatcherServlet.
 * @author Alexandr Trifonov
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan
@PropertySource(value= {"classpath:application.properties"})
public class WebConfig implements WebMvcConfigurer {

	@Bean
	StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".html");
		viewResolver.setRequestContextAttribute("requestContext");
		return viewResolver;
	}
	
	@Bean
	Validator validator() {
		final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		return validator;
	}
	
	@Override
	public Validator getValidator() {
		return validator();
	}
	
	
	@Bean
	ResourceBundleThemeSource themeSource() {
		return new ResourceBundleThemeSource();
	}

	@Bean
	ThemeChangeInterceptor themeChangeInterceptor() {
		return new ThemeChangeInterceptor();
	}

	@Bean
	WebContentInterceptor webChangeInterceptor() {
		WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
		webContentInterceptor.setCacheSeconds(0);
		webContentInterceptor.setSupportedMethods("GET", "POST", "PUT", "DELETE");
		return webContentInterceptor;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		//Используемый по умолчанию PathMatcher (AntPathMatcher) не поддерживает сравнение URL по регулярным выражениям, зато поддерживает регулярные выражения в PathVariable.
		//в данном случае page - это PathVarivale.
		//выражение перенаправляет все запросы вида /.+ на ViewController, обрабатывающий запросы по пути /
		registry.addViewController("/{page:.+}").setViewName("forward:/");
		registry.addViewController("/{page:.+}/{page:.+}").setViewName("forward:/");		
	}

	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(themeChangeInterceptor());
	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/").setCachePeriod(31556926);
	}
}
