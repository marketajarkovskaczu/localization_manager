package com.example.application.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.data.BCcUserEO;
import com.example.application.data.BCiUserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;

/**
 * <p>
 * Title: {@link BCcAuthenticatedUser}
 * </p>
 * <p>
 * Description: Authenticated user
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 01.03.2025 21:22
 */
@Component
public class BCcAuthenticatedUser {

	private final BCiUserRepository userRepository;
	private final AuthenticationContext authenticationContext;

	public BCcAuthenticatedUser(AuthenticationContext aAuthenticationContext, BCiUserRepository aUserRepository) {
		this.userRepository = aUserRepository;
		this.authenticationContext = aAuthenticationContext;
	}

	@Transactional
	public Optional<BCcUserEO> get() {
		return authenticationContext.getAuthenticatedUser(UserDetails.class)
				.flatMap(userDetails -> userRepository.findByUsername(userDetails.getUsername()));
	}

	public void logout() {
		authenticationContext.logout();
	}
}
