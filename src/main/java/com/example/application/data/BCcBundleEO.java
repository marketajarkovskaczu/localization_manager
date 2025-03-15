package com.example.application.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: {@link BCcBundleEO}
 * </p>
 * <p>
 * Description: Bundle
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 17:57
 */
@Entity
@Table(name = "bundle")
public class BCcBundleEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bundle_idgenerator")
	@SequenceGenerator(name = "bundle_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "bundle_id")
	private Long bundleId;

	@Column(name = "bundle_name", nullable = false)
	private String bundleName;

	@OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcProjectVersionLocalizationBundleEO> projectVersionLocalizationBundleList = new ArrayList<>();

	/**
	 * @return the bundleId
	 */
	public Long getBundleId() {
		return bundleId;
	}

	/**
	 * @return the bundleName
	 */
	public String getBundleName() {
		return bundleName;
	}

	/**
	 * @param aBundleName the bundleName
	 */
	public void setBundleName(String aBundleName) {
		bundleName = aBundleName;
	}
}
