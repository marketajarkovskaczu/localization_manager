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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: {@link BCcLocalizationTranslationEO}
 * </p>
 * <p>
 * Description: Localization translation
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 23:47
 */
@Entity
@Table(name = "localization_translation")
public class BCcLocalizationTranslationEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localization_translation_idgenerator")
	@SequenceGenerator(name = "localization_translation_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "localization_translation_id")
	private Long localizationTranslationId;

	@ManyToOne
	@JoinColumn(name = "localization_id", nullable = false)
	private BCcLocalizationEO localization;

	@ManyToOne
	@JoinColumn(name = "language_id", nullable = false)
	private BCcLanguageEO language;

	@ManyToOne
	@JoinColumn(name = "last_history_localization_translation_id", nullable = true)
	private BCcHistoryLocalizationTranslationEO lastHistoryLocalizationTranslation;

	@Column(name = "translation_value", nullable = false)
	private String translationValue;

	@Column(name = "create_date")
	private LocalDateTime createDate;

	@Column(name = "change_date")
	private LocalDateTime changeDate;

	@OneToMany(mappedBy = "localizationTranslation", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcHistoryLocalizationTranslationEO> historyLocalizationTranslationList = new ArrayList<>();

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
	 * @return the localizationTranslationId
	 */
	public Long getLocalizationTranslationId() {
		return localizationTranslationId;
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
	 * @return the lastHistoryLocalizationTranslation
	 */
	public BCcHistoryLocalizationTranslationEO getLastHistoryLocalizationTranslation() {
		return lastHistoryLocalizationTranslation;
	}

	/**
	 * @param aLastHistoryLocalizationTranslation the lastHistoryLocalizationTranslation
	 */
	public void setLastHistoryLocalizationTranslation(BCcHistoryLocalizationTranslationEO aLastHistoryLocalizationTranslation) {
		lastHistoryLocalizationTranslation = aLastHistoryLocalizationTranslation;
	}

	/**
	 * @return the translationValue
	 */
	public String getTranslationValue() {
		return translationValue;
	}

	/**
	 * @param aTranslationValue the translationValue
	 */
	public void setTranslationValue(String aTranslationValue) {
		translationValue = aTranslationValue;
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
