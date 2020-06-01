package com.expense.init;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.expense.config.DataConfig;
import com.expense.config.WebConfig;

/**
 * Конфигурационный класс для конфигурирования компонентов в дескрипторе веб-развертывания.
 * @author Alexandr Trifonov
 *
 */
public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	
	private static final long MAX_FILE_SIZE = 5000000;
	private static final long MAX_REQUEST_SIZE = 5000000;

	private static final int FILE_SIZE_THRESHOLD = 0;
	
	private MultipartConfigElement getMultipartConfigElement() {
		return new MultipartConfigElement(null, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
	}
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] {DataConfig.class};
 	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] {WebConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {"/"};
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter cef = new CharacterEncodingFilter();
		cef.setEncoding("UTF-8");
		cef.setForceEncoding(true);
		return new Filter[] {new HiddenHttpMethodFilter(), cef};
	}

	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setMultipartConfig(getMultipartConfigElement());
	}
	
	

}
