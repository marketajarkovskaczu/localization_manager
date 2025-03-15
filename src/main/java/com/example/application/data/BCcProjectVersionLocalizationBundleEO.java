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
 * Title: {@link BCcProjectVersionLocalizationBundleEO}
 * </p>
 * <p>
 * Description: Project version localization bundle
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
@Table(name = "project_version_localization_bundle")
public class BCcProjectVersionLocalizationBundleEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_version_localization_bundle_idgenerator")
	@SequenceGenerator(name = "project_version_localization_bundle_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "project_version_localization_bundle_id")
	private Long projectVersionLocalizationBundleId;

	@ManyToOne
	@JoinColumn(name = "project_version_localization_id", nullable = false)
	private BCcProjectVersionLocalizationEO projectVersionLocalization;

	@ManyToOne
	@JoinColumn(name = "bundle_id", nullable = false)
	private BCcBundleEO bundle;

	/**
	 * @return the projectVersionLocalizationBundleId
	 */
	public Long getProjectVersionLocalizationBundleId() {
		return projectVersionLocalizationBundleId;
	}

	/**
	 * @return the projectVersionLocalization
	 */
	public BCcProjectVersionLocalizationEO getProjectVersionLocalization() {
		return projectVersionLocalization;
	}

	/**
	 * @param aProjectVersionLocalization the projectVersionLocalization
	 */
	public void setProjectVersionLocalization(BCcProjectVersionLocalizationEO aProjectVersionLocalization) {
		projectVersionLocalization = aProjectVersionLocalization;
	}

	/**
	 * @return the bundle
	 */
	public BCcBundleEO getBundle() {
		return bundle;
	}

	/**
	 * @param aBundle the bundle
	 */
	public void setBundle(BCcBundleEO aBundle) {
		bundle = aBundle;
	}
}
