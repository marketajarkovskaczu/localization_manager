package com.example.application.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.application.data.BCcBundleEO;
import com.example.application.data.BCcHistoryLocalizationTranslationEO;
import com.example.application.data.BCcLanguageEO;
import com.example.application.data.BCcLanguageProjectEO;
import com.example.application.data.BCcLocalizationEO;
import com.example.application.data.BCcLocalizationTranslationEO;
import com.example.application.data.BCcProjectEO;
import com.example.application.data.BCcProjectVersionEO;
import com.example.application.data.BCcProjectVersionLocalizationBundleEO;
import com.example.application.data.BCcProjectVersionLocalizationEO;
import com.example.application.data.BCcUserEO;
import com.example.application.data.BCcUserProjectEO;

/**
 * <p>
 * Title: {@link BCcDataService}
 * </p>
 * <p>
 * Description: Data service
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 14:31
 */

@Service
public class BCcDataService {

	@PersistenceContext
	private EntityManager entityManager;

	// USER --------------------------------------------------

	public List<BCcUserEO> getAllUserList() {
		return entityManager.createQuery("SELECT u FROM BCcUserEO u", BCcUserEO.class).getResultList();
	}

	public List<BCcUserEO> findUserList(Long aProjectId) {
		return entityManager.createQuery(
						"SELECT u FROM BCcUserEO u " +
								"WHERE EXISTS (SELECT 1 FROM BCcUserProjectEO up " +
								"WHERE up.user = u AND up.project.projectId = :projectId)",
						BCcUserEO.class)
				.setParameter("projectId", aProjectId)
				.getResultList();
	}

	public BCcUserEO findUserByUsername(String aUsername) {
		return entityManager.createQuery(
						"SELECT u FROM BCcUserEO u WHERE u.username = :username", BCcUserEO.class)
				.setParameter("username", aUsername)
				.getSingleResult();
	}

	// USER PROJECT--------------------------------------------------

	@Transactional
	public void createUserProject(BCcUserProjectEO aUserProjectEO) {
		entityManager.persist(aUserProjectEO);
	}

	@Transactional
	public void deleteUserProject(Long aUserProjectId) {
		BCcUserProjectEO userProjectEO = entityManager.find(BCcUserProjectEO.class, aUserProjectId);
		if (userProjectEO != null) {
			entityManager.remove(userProjectEO);
		}
	}

	public BCcUserProjectEO findUserProject(Long aProjectId, Long aUserId) {
		try {
			return entityManager.createQuery(
							"SELECT up FROM BCcUserProjectEO up " +
									"WHERE up.project.projectId = :projectId AND up.user.userId = :userId",
							BCcUserProjectEO.class)
					.setParameter("projectId", aProjectId)
					.setParameter("userId", aUserId)
					.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	// BUNDLE -----------------------------------------------------

	@Transactional
	public void createBundle(BCcBundleEO aBundleEO) {
		entityManager.persist(aBundleEO);
	}

	public List<BCcBundleEO> getAllBundleList() {
		return entityManager.createQuery("SELECT b FROM BCcBundleEO b", BCcBundleEO.class).getResultList();
	}

// LANGUAGE --------------------------------------------------

	@Transactional
	public void createLanguage(BCcLanguageEO aLanguageEO) {
		entityManager.persist(aLanguageEO);
	}

	@Transactional
	public void deleteLanguage(Long aLanguageId) {
		BCcLanguageEO languageEO = entityManager.find(BCcLanguageEO.class, aLanguageId);
		if (languageEO != null) {
			entityManager.remove(languageEO);
		}
	}

	public List<BCcLanguageEO> getAllLanguageList() {
		return entityManager.createQuery("SELECT l FROM BCcLanguageEO l", BCcLanguageEO.class).getResultList();
	}

	// LANGUAGE PROJECT--------------------------------------------------

	@Transactional
	public void createLanguageProject(BCcLanguageProjectEO aLanguageProjectEO) {
		entityManager.persist(aLanguageProjectEO);
	}

	@Transactional
	public void deleteLanguageProject(Long aLanguageProjectId) {
		BCcLanguageProjectEO languageProjectEO = entityManager.find(BCcLanguageProjectEO.class, aLanguageProjectId);
		if (languageProjectEO != null) {
			entityManager.remove(languageProjectEO);
		}
	}

	public BCcLanguageProjectEO findLanguageProject(Long aProjectId, Long aLanguageId) {
		try {
			return entityManager.createQuery(
							"SELECT lp FROM BCcLanguageProjectEO lp " +
									"WHERE lp.project.projectId = :projectId AND lp.language.languageId = :languageId",
							BCcLanguageProjectEO.class)
					.setParameter("projectId", aProjectId)
					.setParameter("languageId", aLanguageId)
					.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	public List<BCcLanguageEO> findLanguageList(Long aProjectId) {
		return entityManager.createQuery(
						"SELECT l FROM BCcLanguageEO l " +
								"WHERE EXISTS (SELECT 1 FROM BCcLanguageProjectEO lp WHERE lp.language = l AND lp.project.projectId = :projectId)",
						BCcLanguageEO.class)
				.setParameter("projectId", aProjectId)
				.getResultList();
	}

	public List<BCcLanguageEO> findLanguageByUserList(Long aUserId) {
		return entityManager.createQuery(
						"SELECT l FROM BCcLanguageEO l " +
								"WHERE EXISTS (SELECT 1 FROM BCcTranslatorEO t WHERE t.language = l AND t.user.userId = :userId)",
						BCcLanguageEO.class)
				.setParameter("userId", aUserId)
				.getResultList();
	}

	// PROJECT --------------------------------------------------

	@Transactional
	public void createProject(BCcProjectEO aProjectEO) {
		entityManager.persist(aProjectEO);
	}

	@Transactional
	public void updateProject(BCcProjectEO aProjectEO) {
		entityManager.merge(aProjectEO);
	}

	@Transactional
	public void deleteProject(Long aProjectId) {
		BCcProjectEO projectEO = entityManager.find(BCcProjectEO.class, aProjectId);
		if (projectEO != null) {
			entityManager.remove(projectEO);
		}
	}

	public List<BCcProjectEO> getAllProjectList() {
		return entityManager.createQuery("SELECT p FROM BCcProjectEO p", BCcProjectEO.class).getResultList();
	}

	// PROJECT VERSION --------------------------------------------------

	@Transactional
	public void createProjectVersion(BCcProjectVersionEO aProjectVersionEO) {
		entityManager.persist(aProjectVersionEO);
	}

	@Transactional
	public void updateProjectVersion(BCcProjectVersionEO aProjectVersionEO) {
		entityManager.merge(aProjectVersionEO);
	}

	@Transactional
	public void deleteProjectVersion(Long aProjectVersionId) {
		BCcProjectVersionEO projectVersionEO = entityManager.find(BCcProjectVersionEO.class, aProjectVersionId);
		if (projectVersionEO != null) {
			entityManager.remove(projectVersionEO);
		}
	}

	public List<BCcProjectVersionEO> getProjectVersionList(Long aProjectId) {
		return entityManager.createQuery(
						"SELECT pv FROM BCcProjectVersionEO pv WHERE pv.project.projectId = :projectId",
						BCcProjectVersionEO.class)
				.setParameter("projectId", aProjectId)
				.getResultList();
	}

	// PROJECT VERSION LOCALIZATION--------------------------------------------------

	@Transactional
	public void createProjectVersionLocalization(BCcProjectVersionLocalizationEO aProjectVersionLocalizationEO) {
		entityManager.persist(aProjectVersionLocalizationEO);
	}

	@Transactional
	public void deleteProjectVersionLocalization(Long aProjectVersionLocalizationId) {
		BCcProjectVersionLocalizationEO projectVersionLocalizationEO = entityManager.find(
				BCcProjectVersionLocalizationEO.class,
				aProjectVersionLocalizationId);
		if (projectVersionLocalizationEO != null) {
			entityManager.remove(projectVersionLocalizationEO);
		}
	}

	public BCcProjectVersionLocalizationEO findProjectVersionLocalization(Long aProjectVersionId, Long aLocalizationId) {
		try {
			return entityManager.createQuery(
							"SELECT pvl FROM BCcProjectVersionLocalizationEO pvl " +
									"WHERE pvl.projectVersion.projectVersionId = :projectVersionId AND pvl.localization.localizationId = :localizationId",
							BCcProjectVersionLocalizationEO.class)
					.setParameter("projectVersionId", aProjectVersionId)
					.setParameter("localizationId", aLocalizationId)
					.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	// LOCALIZATION --------------------------------------------------

	public BCcLocalizationEO findLocalization(
			Long aProjectId,
			String aFile,
			String aConstant,
			Integer aLocalizationKey,
			String aDefaultLocalization) {
		try {
			return entityManager.createQuery(
							"SELECT l FROM BCcLocalizationEO l " +
									"WHERE l.file = :file "
									+ "AND l.constant = :constant "
									+ "AND l.localizationKey = :localizationKey "
									+ "AND l.defaultLocalization = :defaultLocalization "
									+ "AND EXISTS (SELECT 1 FROM BCcProjectVersionLocalizationEO pvl WHERE pvl.localization = l "
									+ "AND EXISTS (SELECT 1 FROM BCcProjectVersionEO pv WHERE pvl.projectVersion = pv "
									+ "AND EXISTS (SELECT 1 FROM BCcProjectEO p WHERE pv.project = p and p.projectId = :projectId)))",
							BCcLocalizationEO.class)
					.setParameter("file", aFile)
					.setParameter("constant", aConstant)
					.setParameter("localizationKey", aLocalizationKey)
					.setParameter("defaultLocalization", aDefaultLocalization)
					.setParameter("projectId", aProjectId)
					.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	public List<BCcLocalizationEO> getLocalizationList(Long aProjectId, Long aProjectVersionId, Long aBundleId) {

		return entityManager.createQuery(
						"SELECT l FROM BCcLocalizationEO l " +
								"WHERE EXISTS (SELECT 1 FROM BCcProjectVersionLocalizationEO pvl WHERE pvl.localization = l "
								+ "AND EXISTS (SELECT 1 FROM BCcProjectVersionEO pv WHERE pvl.projectVersion = pv AND pv.projectVersionId = :projectVersionId)"
								+ "AND EXISTS (SELECT 1 FROM BCcProjectVersionLocalizationBundleEO pvlb WHERE pvlb.projectVersionLocalization = pvl "
								+ "AND pvlb.bundle.bundleId = :bundleId))",
						BCcLocalizationEO.class)
				.setParameter("projectVersionId", aProjectVersionId)
				.setParameter("bundleId", aBundleId)
				.getResultList();
	}

	public List<BCcLocalizationEO> getLocalizationList(Long aProjectId, Long aBundleId) {

		return entityManager.createQuery(
						"SELECT l FROM BCcLocalizationEO l " +
								"WHERE EXISTS (SELECT 1 FROM BCcProjectVersionLocalizationEO pvl WHERE pvl.localization = l "
								+ "AND EXISTS (SELECT 1 FROM BCcProjectVersionEO pv WHERE pvl.projectVersion = pv "
								+ "AND EXISTS (SELECT 1 FROM BCcProjectEO p WHERE pv.project = p and p.projectId = :projectId))"
								+ "AND EXISTS (SELECT 1 FROM BCcProjectVersionLocalizationBundleEO pvlb WHERE pvlb.projectVersionLocalization = pvl "
								+ "AND pvlb.bundle.bundleId = :bundleId))",
						BCcLocalizationEO.class)
				.setParameter("projectId", aProjectId)
				.setParameter("bundleId", aBundleId)
				.getResultList();
	}

	public List<BCcLocalizationEO> getLocalizationList(Long aProjectId) {

		return entityManager.createQuery(
						"SELECT l FROM BCcLocalizationEO l " +
								"WHERE EXISTS (SELECT 1 FROM BCcProjectVersionLocalizationEO pvl WHERE pvl.localization = l "
								+ "AND EXISTS (SELECT 1 FROM BCcProjectVersionEO pv WHERE pvl.projectVersion = pv "
								+ "AND EXISTS (SELECT 1 FROM BCcProjectEO p WHERE pv.project = p and p.projectId = :projectId)))",
						BCcLocalizationEO.class)
				.setParameter("projectId", aProjectId)
				.getResultList();
	}

	@Transactional
	public void createLocalization(BCcLocalizationEO aLocalizationEO) {
		entityManager.persist(aLocalizationEO);
	}

	@Transactional
	public void deleteLocalization(Long aLocalizationId) {
		BCcLocalizationEO localizationEO = entityManager.find(BCcLocalizationEO.class, aLocalizationId);
		if (localizationEO != null) {
			entityManager.remove(localizationEO);
		}
	}

	public List<BCcLocalizationEO> getLocalizationWithoutProjectVersionList() {
		return entityManager.createQuery(
						"SELECT l FROM BCcLocalizationEO l " +
								"WHERE NOT EXISTS (" +
								"   SELECT pv FROM BCcProjectVersionLocalizationEO pv " +
								"   WHERE pv.localization = l)",
						BCcLocalizationEO.class)
				.getResultList();
	}

	// PROJECT VERSION LOCALIZATION BUNDLE --------------------------------------------------

	@Transactional
	public void createProjectVersionLocalizationBundle(BCcProjectVersionLocalizationBundleEO aProjectVersionLocalizationBundleEO) {
		entityManager.persist(aProjectVersionLocalizationBundleEO);
	}

	public BCcProjectVersionLocalizationBundleEO findProjectVersionLocalizationBundle(Long aProjectVersionLocalizationId, Long aBundleId) {
		try {
			return entityManager.createQuery(
							"SELECT pvlb FROM BCcProjectVersionLocalizationBundleEO pvlb " +
									"WHERE pvlb.projectVersionLocalization.projectVersionLocalizationId = :projectVersionLocalizationId "
									+ "AND pvlb.bundle.bundleId = :bundleId",
							BCcProjectVersionLocalizationBundleEO.class)
					.setParameter("projectVersionLocalizationId", aProjectVersionLocalizationId)
					.setParameter("bundleId", aBundleId)
					.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	// LOCALIZATION TRANSLATION--------------------------------------------------

	@Transactional
	public void createLocalizationTranslation(BCcLocalizationTranslationEO aLocalizationTranslationEO) {
		entityManager.persist(aLocalizationTranslationEO);
	}

	@Transactional
	public void updateLocalizationTranslation(BCcLocalizationTranslationEO aLocalizationTranslationEO) {
		entityManager.merge(aLocalizationTranslationEO);
	}

	public List<BCcLocalizationTranslationEO> getLocalizationTranslationList(Long aLocalizationId) {
		return entityManager.createQuery(
						"SELECT lt FROM BCcLocalizationTranslationEO lt " +
								"WHERE lt.localization.localizationId = :localizationId",
						BCcLocalizationTranslationEO.class)
				.setParameter("localizationId", aLocalizationId)
				.getResultList();
	}

	public BCcLocalizationTranslationEO findLocalizationTranslation(Long aLocalizationId, Long aLanguageId) {
		try {
			return entityManager.createQuery(
							"SELECT lt FROM BCcLocalizationTranslationEO lt " +
									"WHERE lt.localization.localizationId = :localizationId "
									+ "AND lt.language.languageId = :languageId",
							BCcLocalizationTranslationEO.class)
					.setParameter("localizationId", aLocalizationId)
					.setParameter("languageId", aLanguageId)
					.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	// HISTORY LOCALIZATION TRANSLATION--------------------------------------------------

	@Transactional
	public void createHistoryLocalizationTranslation(BCcHistoryLocalizationTranslationEO aHistoryLocalizationTranslationEO) {
		entityManager.persist(aHistoryLocalizationTranslationEO);
	}
}
