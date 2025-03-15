package com.example.application.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <p>
 * Title: {@link BCcUserEO}
 * </p>
 * <p>
 * Description: User
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 01.03.2025 21:18
 */
@Entity
@Table(name = "user_t")
public class BCcUserEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_idgenerator")
	@SequenceGenerator(name = "user_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "login_name", nullable = false)
	private String username;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(name = "email", nullable = false)
	private String email;

	@JsonIgnore
	@Column(name = "hashed_password", nullable = false)
	private String hashedPassword;

	@ElementCollection(targetClass = BCnUserRole.class)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Set<BCnUserRole> userRoleSet = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcUserProjectEO> userProjectList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcTranslatorEO> translatorList = new ArrayList<>();

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param aUsername the username
	 */
	public void setUsername(String aUsername) {
		username = aUsername;
	}

	/**
	 * @return the username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param aUserName the username
	 */
	public void setUserName(String aUserName) {
		userName = aUserName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param aEmail the email
	 */
	public void setEmail(String aEmail) {
		email = aEmail;
	}

	/**
	 * @return the hashedPassword
	 */
	public String getHashedPassword() {
		return hashedPassword;
	}

	/**
	 * @param aHashedPassword the hashedPassword
	 */
	public void setHashedPassword(String aHashedPassword) {
		hashedPassword = aHashedPassword;
	}

	/**
	 * @return the userRoleSet
	 */
	public Set<BCnUserRole> getUserRoleSet() {
		return userRoleSet;
	}
}
