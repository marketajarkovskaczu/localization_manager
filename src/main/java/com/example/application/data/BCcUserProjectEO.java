package com.example.application.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * <p>
 * Title: {@link BCcUserProjectEO}
 * </p>
 * <p>
 * Description: User project
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 22:52
 */
@Entity
@Table(name = "user_project")
public class BCcUserProjectEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_project_idgenerator")
	@SequenceGenerator(name = "user_project_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "user_project_id")
	private Long userProjectId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private BCcUserEO user;

	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private BCcProjectEO project;

	/**
	 * @return the userProjectId
	 */
	public Long getUserProjectId() {
		return userProjectId;
	}

	/**
	 * @return the user
	 */
	public BCcUserEO getUser() {
		return user;
	}

	/**
	 * @param aUser the user
	 */
	public void setUser(BCcUserEO aUser) {
		user = aUser;
	}

	/**
	 * @return the project
	 */
	public BCcProjectEO getProject() {
		return project;
	}

	/**
	 * @param aProject the project
	 */
	public void setProject(BCcProjectEO aProject) {
		project = aProject;
	}
}
