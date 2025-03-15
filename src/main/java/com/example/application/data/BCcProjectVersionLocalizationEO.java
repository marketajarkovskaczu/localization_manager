package com.example.application.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: {@link BCcProjectVersionLocalizationEO}
 * </p>
 * <p>
 * Description: Project version localization
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
@Table(name = "project_version_localization")
public class BCcProjectVersionLocalizationEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_version_localization_idgenerator")
	@SequenceGenerator(name = "project_version_localization_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "project_version_localization_id")
	private Long projectVersionLocalizationId;

	@ManyToOne
	@JoinColumn(name = "project_version_id", nullable = false)
	private BCcProjectVersionEO projectVersion;

	@ManyToOne
	@JoinColumn(name = "localization_id", nullable = false)
	private BCcLocalizationEO localization;

	@OneToMany(mappedBy = "projectVersionLocalization", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcProjectVersionLocalizationBundleEO> projectVersionLocalizationBundleList = new ArrayList<>();

	/**
	 * @return the projectVersionLocalizationId
	 */
	public Long getProjectVersionLocalizationId() {
		return projectVersionLocalizationId;
	}

	/**
	 * @return the projectVersion
	 */
	public BCcProjectVersionEO getProjectVersion() {
		return projectVersion;
	}

	/**
	 * @param aProjectVersion the projectVersion
	 */
	public void setProjectVersion(BCcProjectVersionEO aProjectVersion) {
		projectVersion = aProjectVersion;
	}

	/**
	 * @return the localization
	 */
	public BCcLocalizationEO getLocalization() {
		return localization;
	}

	/**
	 * @param aLocalization the localization
	 */
	public void setLocalization(BCcLocalizationEO aLocalization) {
		localization = aLocalization;
	}
}
