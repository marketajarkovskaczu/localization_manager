package com.example.application.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.data.BCcUserEO;
import com.example.application.data.BCiUserRepository;

/**
 * <p>
 * Title: {@link BCcUserDetailsServiceImpl}
 * </p>
 * <p>
 * Description: User details service impl
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 01.03.2025 21:24
 */
@Service
public class BCcUserDetailsServiceImpl implements UserDetailsService {

	private final BCiUserRepository userRepository;

	public BCcUserDetailsServiceImpl(BCiUserRepository aUserRepository) {
		this.userRepository = aUserRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String aUsername) throws UsernameNotFoundException {
		BCcUserEO user = userRepository.findByUsername(aUsername)
				.orElseThrow(() -> new UsernameNotFoundException("No user present with username: " + aUsername));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHashedPassword(),
				getAuthorities(user));
	}

	private static List<GrantedAuthority> getAuthorities(BCcUserEO aUser) {
		return aUser.getUserRoleSet().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());

	}

}
