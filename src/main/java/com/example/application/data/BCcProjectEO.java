package com.example.application.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Title: {@link BCcProjectEO}
 * </p>
 * <p>
 * Description: Project
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 14:14
 */
@Entity
@Table(name = "project")
public class BCcProjectEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_idgenerator")
	@SequenceGenerator(name = "project_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "project_id")
	private Long projectId;

	@Column(name = "project_name", nullable = false)
	private String projectName;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcProjectVersionEO> projectVersionList = new ArrayList<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcLanguageProjectEO> languageProjectList = new ArrayList<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcUserProjectEO> userProjectList = new ArrayList<>();

	/**
	 * @return the projectId
	 */
	public Long getProjectId() {
		return projectId;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param aProjectName the projectName
	 */
	public void setProjectName(String aProjectName) {
		projectName = aProjectName;
	}

	/**
	 * @return the projectVersionList
	 */
	public List<BCcProjectVersionEO> getProjectVersionList() {
		return projectVersionList;
	}

	/**
	 * @return the languageProjectList
	 */
	public List<BCcLanguageProjectEO> getLanguageProjectList() {
		return languageProjectList;
	}

	/**
	 * @return the userProjectList
	 */
	public List<BCcUserProjectEO> getUserProjectList() {
		return userProjectList;
	}
}
