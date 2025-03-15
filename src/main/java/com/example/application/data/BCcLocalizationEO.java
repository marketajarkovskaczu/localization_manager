package com.example.application.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: {@link BCcLocalizationEO}
 * </p>
 * <p>
 * Description: Localization
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 22:43
 */
@Entity
@Table(name = "localization")
public class BCcLocalizationEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localization_idgenerator")
	@SequenceGenerator(name = "localization_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "localization_id")
	private Long localizationId;

	@Column(name = "file")
	private String file;

	@Column(name = "const")
	private String constant;

	@Column(name = "key_loc")
	private Integer localizationKey;

	@Column(name = "default_loc")
	private String defaultLocalization;

	@Column(name = "create_date")
	private LocalDateTime createDate;

	@Column(name = "change_date")
	private LocalDateTime changeDate;

	@OneToMany(mappedBy = "localization", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcLocalizationTranslationEO> localizationTranslationList = new ArrayList<>();

	@OneToMany(mappedBy = "localization", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcProjectVersionLocalizationEO> projectVersionLocalizationList = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createDate = LocalDateTime.now();
		changeDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		changeDate = LocalDateTime.now();
	}

	/**
	 * @return the localizationId
	 */
	public Long getLocalizationId() {
		return localizationId;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param aFile the file
	 */
	public void setFile(String aFile) {
		file = aFile;
	}

	/**
	 * @return the constant
	 */
	public String getConstant() {
		return constant;
	}

	/**
	 * @param aConstant the constant
	 */
	public void setConstant(String aConstant) {
		constant = aConstant;
	}

	/**
	 * @return the localizationKey
	 */
	public Integer getLocalizationKey() {
		return localizationKey;
	}

	/**
	 * @param aLocalizationKey the localizationKey
	 */
	public void setLocalizationKey(Integer aLocalizationKey) {
		localizationKey = aLocalizationKey;
	}

	/**
	 * @return the defaultLocalization
	 */
	public String getDefaultLocalization() {
		return defaultLocalization;
	}

	/**
	 * @param aDefaultLocalization the defaultLocalization
	 */
	public void setDefaultLocalization(String aDefaultLocalization) {
		defaultLocalization = aDefaultLocalization;
	}

	/**
	 * @return the createDate
	 */
	public LocalDateTime getCreateDate() {
		return createDate;
	}

	/**
	 * @return the changeDate
	 */
	public LocalDateTime getChangeDate() {
		return changeDate;
	}
}
