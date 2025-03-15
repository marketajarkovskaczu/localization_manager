package com.example.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.application.views.BCcLoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

/**
 * <p>
 * Title: {@link BCcSecurityConfiguration}
 * </p>
 * <p>
 * Description: Security configuration
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 01.03.2025 21:23
 */
@EnableWebSecurity
@Configuration
public class BCcSecurityConfiguration extends VaadinWebSecurity {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity aHttp) throws Exception {

		aHttp.authorizeHttpRequests(
				authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll());

		aHttp.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll());

		super.configure(aHttp);
		setLoginView(aHttp, BCcLoginView.class);
	}

}
