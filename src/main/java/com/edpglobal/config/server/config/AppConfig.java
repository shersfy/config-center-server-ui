package com.edpglobal.config.server.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.edpglobal.config.server.filter.ConfigServerFilter;

@Configuration
public class AppConfig {
	
	@Bean
	public FilterRegistrationBean<ConfigServerFilter> configServerFilter(){
		ConfigServerFilter filter = new ConfigServerFilter();
		FilterRegistrationBean<ConfigServerFilter> bean = new FilterRegistrationBean<>();
		
		bean.setOrder(1);
		bean.setFilter(filter);
		bean.setName("configServerFilter");
		bean.addUrlPatterns("/*");
		
		addInitParameters(bean);
		return bean;
	}
	
	private void addInitParameters(FilterRegistrationBean<? extends Filter> bean) {
		
	}
}
