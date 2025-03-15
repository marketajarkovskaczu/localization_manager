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
 * Title: {@link BCcLanguageEO}
 * </p>
 * <p>
 * Description: Language
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 23:45
 */
@Entity
@Table(name = "language_t")
public class BCcLanguageEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "language_idgenerator")
	@SequenceGenerator(name = "language_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "language_id")
	private Long languageId;

	@Column(name = "language_name", nullable = false)
	private String languageName;

	@Column(name = "language_iso", nullable = false)
	private String languageIso;

	@OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcLocalizationTranslationEO> localizationTranslationList = new ArrayList<>();

	@OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcLanguageProjectEO> languageProjectList = new ArrayList<>();

	@OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcTranslatorEO> translatorList = new ArrayList<>();

	/**
	 * @return the languageId
	 */
	public Long getLanguageId() {
		return languageId;
	}

	/**
	 * @return the languageName
	 */
	public String getLanguageName() {
		return languageName;
	}

	/**
	 * @param aLanguageName the languageName
	 */
	public void setLanguageName(String aLanguageName) {
		languageName = aLanguageName;
	}

	/**
	 * @return the languageIso
	 */
	public String getLanguageIso() {
		return languageIso;
	}

	/**
	 * @param aLanguageIso the languageIso
	 */
	public void setLanguageIso(String aLanguageIso) {
		languageIso = aLanguageIso;
	}
}
