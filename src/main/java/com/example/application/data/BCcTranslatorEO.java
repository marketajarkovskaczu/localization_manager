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
 * Title: {@link BCcTranslatorEO}
 * </p>
 * <p>
 * Description: Translator
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
@Table(name = "translator")
public class BCcTranslatorEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "translator_idgenerator")
	@SequenceGenerator(name = "translator_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "translator_id")
	private Long translatorId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private BCcUserEO user;

	@ManyToOne
	@JoinColumn(name = "language_id", nullable = false)
	private BCcLanguageEO language;

	/**
	 * @return the translatorId
	 */
	public Long getTranslatorId() {
		return translatorId;
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
}
