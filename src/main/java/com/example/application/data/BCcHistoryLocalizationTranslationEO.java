package com.example.application.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * <p>
 * Title: {@link BCcHistoryLocalizationTranslationEO}
 * </p>
 * <p>
 * Description: History localization translation
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 23:56
 */
@Entity
@Table(name = "history_localization_translation")
public class BCcHistoryLocalizationTranslationEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_localization_translation_idgenerator")
	@SequenceGenerator(name = "history_localization_translation_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "history_localization_translation_id")
	private Long historyLocalizationTranslationId;

	@ManyToOne
	@JoinColumn(name = "localization_translation_id", nullable = false)
	private BCcLocalizationTranslationEO localizationTranslation;

	@Column(name = "translation_value", nullable = false)
	private String translationValue;

	@Column(name = "create_date")
	private LocalDateTime createDate;

	@PrePersist
	protected void onCreate() {
		createDate = LocalDateTime.now();
	}

	/**
	 * @return the historyLocalizationTranslationId
	 */
	public Long getHistoryLocalizationTranslationId() {
		return historyLocalizationTranslationId;
	}

	/**
	 * @return the localizationTranslation
	 */
	public BCcLocalizationTranslationEO getLocalizationTranslation() {
		return localizationTranslation;
	}

	/**
	 * @param aLocalizationTranslation the localizationTranslation
	 */
	public void setLocalizationTranslation(BCcLocalizationTranslationEO aLocalizationTranslation) {
		localizationTranslation = aLocalizationTranslation;
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
}
