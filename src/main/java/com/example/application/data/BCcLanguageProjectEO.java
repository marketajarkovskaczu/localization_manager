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
 * Title: {@link BCcLanguageProjectEO}
 * </p>
 * <p>
 * Description: Language project
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
@Table(name = "language_project")
public class BCcLanguageProjectEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "language_project_idgenerator")
	@SequenceGenerator(name = "language_project_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "language_project_id")
	private Long languageProjectId;

	@ManyToOne
	@JoinColumn(name = "language_id", nullable = false)
	private BCcLanguageEO language;

	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private BCcProjectEO project;

	/**
	 * @return the languageProjectId
	 */
	public Long getLanguageProjectId() {
		return languageProjectId;
	}

	/**
	 * @return the language
	 */
	public BCcLanguageEO getLanguage() {
		return language;
	}

	/**
	 * @param aLanguage the language
	 */
	public void setLanguage(BCcLanguageEO aLanguage) {
		language = aLanguage;
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
