package com.example.application.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.data.BCcBundleEO;
import com.example.application.data.BCcHistoryLocalizationTranslationEO;
import com.example.application.data.BCcLanguageEO;
import com.example.application.data.BCcLocalizationEO;
import com.example.application.data.BCcLocalizationTranslationEO;
import com.example.application.data.BCcProjectEO;
import com.example.application.data.BCcProjectVersionEO;
import com.example.application.data.BCcProjectVersionLocalizationBundleEO;
import com.example.application.data.BCcProjectVersionLocalizationEO;
import com.example.application.data.BCcUserEO;
import com.example.application.security.BCcDataService;
import com.vaadin.flow.component.notification.Notification;

/**
 * <p>
 * Title: {@link BCcImportService}
 * </p>
 * <p>
 * Description: Import service
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 13.03.2025 3:16
 */
@Service
public class BCcImportService {
	private final BCcDataService dataService;

	public BCcImportService(BCcDataService aDataService) {
		this.dataService = aDataService;
	}

	@Async
	@Transactional
	public void importFromExcelAsync(InputStream aInputStream, String aFileName, BCcProjectEO aProjectEO) {

		try {

			importFromExcel(aInputStream, aFileName, aProjectEO);
			Notification.show("Import completed successfully", 3000, Notification.Position.TOP_END);
		}
		catch (Exception e) {
			Notification.show("Import failed: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
			throw new RuntimeException("Import error", e);
		}
	}

	private void importFromExcel(
			InputStream aInputStream,
			String aFileName,
			BCcProjectEO aProjectEO) throws Exception {

		if (aInputStream == null) {
			throw new IllegalArgumentException("InputStream is null");
		}

		List<BCcLocalizationEO> localizationList = dataService.getLocalizationList(aProjectEO.getProjectId());

		Map<String, BCcLocalizationEO> localizationMap = localizationList.stream()
				.collect(Collectors.toMap(this::createKeyLocalization, a -> a));

		Workbook workbook = getWorkbook(aInputStream, aFileName);

		Sheet sheet = workbook.getSheetAt(0);

		Map<Integer, String> headerMap = new HashMap<>();
		headerMap.put(0, "FILE");
		headerMap.put(1, "JAVA_CONST");
		headerMap.put(2, "KEY");
		headerMap.put(3, "DEFAULT");

		Map<String, BCcLanguageEO> isolanguageMap = new HashMap<>();
		Map<Integer, BCcLanguageEO> languageMap = new HashMap<>();

		Map<String, BCcLanguageEO> projectLanguageMap = dataService.findLanguageList(aProjectEO.getProjectId()).stream()
				.collect(Collectors.toMap(BCcLanguageEO::getLanguageIso, a -> a));

		BCcUserEO currentUserEO = getCurrentUser();
		Map<String, BCcLanguageEO> userLanguageMap = dataService.findLanguageByUserList(currentUserEO.getUserId()).stream()
				.collect(Collectors.toMap(BCcLanguageEO::getLanguageIso, a -> a));

		for (Row row : sheet) {
			if (row.getRowNum() == 0) {
				AtomicInteger atomicInteger = new AtomicInteger();
				row.cellIterator().forEachRemaining(cell -> {
					int index = atomicInteger.getAndIncrement();
					String expectedValue = headerMap.get(index);
					String cellValue = cell.getStringCellValue().trim();
					if (expectedValue != null) {
						if (!expectedValue.equals(cellValue)) {
							throw new IllegalArgumentException("Column number " + (index + 1) + " should be " + expectedValue + " but is " + cellValue);
						}
					}
					else if (cellValue.length() == 2) {

						BCcLanguageEO projectLanguageEO = projectLanguageMap.get(cellValue.toLowerCase());
						BCcLanguageEO userLanguageEO = userLanguageMap.get(cellValue.toLowerCase());

						if (projectLanguageEO != null && userLanguageEO != null) {
							headerMap.put(index, cellValue.toLowerCase());
							isolanguageMap.put(cellValue.toLowerCase(), projectLanguageEO);
							languageMap.put(index, projectLanguageEO);
						}
					}
				});
			}
			else {
				String file = null;
				String constant = null;
				Integer localizationKey = null;
				String defaultLocalization = null;
				try {
					file = row.getCell(0).getStringCellValue();
					constant = row.getCell(1).getStringCellValue();
					localizationKey = (int) row.getCell(2).getNumericCellValue();
					defaultLocalization = row.getCell(3).getStringCellValue();
				}
				catch (Exception e) {
					Notification.show(
							"Error importing file: Empty values. Row number " + (row.getRowNum() + 1),
							3000,
							Notification.Position.MIDDLE);
				}

				if (file == null || file.isEmpty() || constant == null || constant.isEmpty() || defaultLocalization == null
						|| defaultLocalization.isEmpty() || localizationKey == null) {
					Notification.show(
							"Error importing file: Empty values. Row number " + (row.getRowNum() + 1),
							3000,
							Notification.Position.MIDDLE);
				}
				else {

					String keyLocalization = createKeyLocalization(file, constant, localizationKey, defaultLocalization);
					BCcLocalizationEO localizationEO = localizationMap.remove(keyLocalization);

					if (localizationEO != null) {
						row.cellIterator().forEachRemaining(cell -> {
							int index = cell.getColumnIndex();
							if (index > 3) {
								BCcLanguageEO languageEO = languageMap.get(index);
								if (languageEO != null) {
									updateLocalizationTransaction(localizationEO, languageEO, cell.getStringCellValue());
								}
							}
						});
					}
				}
			}
		}
		workbook.close();
	}

	private Workbook getWorkbook(InputStream aInputStream, String aFileName) throws Exception {
		if (aFileName.endsWith(".xls")) {
			return new HSSFWorkbook(aInputStream);
		}
		else if (aFileName.endsWith(".xlsx")) {
			return new XSSFWorkbook(aInputStream);
		}
		else {
			throw new IllegalArgumentException("Invalid file format. Please upload an Excel file.");
		}
	}

	@Async
	@Transactional
	public void importFromExcelAsync(
			InputStream aInputStream, String aFileName, BCcProjectEO aProjectEO, BCcProjectVersionEO aProjectVersionEO, BCcBundleEO aBundleEO) {

		try {
			importFromExcel(aInputStream, aFileName, aProjectEO, aProjectVersionEO, aBundleEO);
			Notification.show("Import completed successfully", 3000, Notification.Position.TOP_END);
		}
		catch (Exception e) {
			Notification.show("Import failed: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
			throw new RuntimeException("Import error", e);
		}
	}

	private void importFromExcel(
			InputStream aInputStream,
			String aFileName,
			BCcProjectEO aProjectEO,
			BCcProjectVersionEO aProjectVersionEO,
			BCcBundleEO aBundleEO) throws Exception {

		if (aInputStream == null) {
			throw new IllegalArgumentException("InputStream is null");
		}

		List<BCcLocalizationEO> localizationList = dataService.getLocalizationList(aProjectEO.getProjectId(), aBundleEO.getBundleId());

		Map<String, BCcLocalizationEO> localizationMap = localizationList.stream()
				.collect(Collectors.toMap(this::createKeyLocalization, a -> a));

		Workbook workbook = getWorkbook(aInputStream, aFileName);

		Sheet sheet = workbook.getSheetAt(0);

		Map<Integer, String> headerMap = new HashMap<>();
		headerMap.put(0, "FILE");
		headerMap.put(1, "JAVA_CONST");
		headerMap.put(2, "KEY");
		headerMap.put(3, "DEFAULT");

		Map<String, BCcLanguageEO> isolanguageMap = new HashMap<>();
		Map<Integer, BCcLanguageEO> languageMap = new HashMap<>();

		Map<String, BCcLanguageEO> supportedLanguageMap = dataService.findLanguageList(aProjectEO.getProjectId()).stream()
				.collect(Collectors.toMap(BCcLanguageEO::getLanguageIso, a -> a));

		for (Row row : sheet) {

			if (row.getRowNum() == 0) {
				List<String> unsupportedLanguageList = new ArrayList<>();
				AtomicInteger atomicInteger = new AtomicInteger();
				row.cellIterator().forEachRemaining(cell -> {
					int index = atomicInteger.getAndIncrement();
					String expectedValue = headerMap.get(index);
					String cellValue = cell.getStringCellValue().trim();
					if (expectedValue != null) {
						if (!expectedValue.equals(cellValue)) {
							throw new IllegalArgumentException("Column number " + (index + 1) + " should be " + expectedValue + " but is " + cellValue);
						}
					}
					else if (cellValue.length() == 2) {

						BCcLanguageEO languageEO = supportedLanguageMap.get(cellValue.toLowerCase());

						if (languageEO == null) {
							unsupportedLanguageList.add(cellValue);
						}
						else {
							headerMap.put(index, cellValue.toLowerCase());
							isolanguageMap.put(cellValue.toLowerCase(), languageEO);
							languageMap.put(index, languageEO);
						}
					}
					else {
						unsupportedLanguageList.add(cellValue);
					}
				});

				if (!unsupportedLanguageList.isEmpty()) {
					String languagesText = String.join(", ", unsupportedLanguageList);
					Notification.show(
							"These columns were not imported [" + languagesText + "]! Languages have to be connected to project!",
							10000,
							Notification.Position.MIDDLE);
				}
			}
			else {
				String file = null;
				String constant = null;
				Integer localizationKey = null;
				String defaultLocalization = null;
				try {
					file = row.getCell(0).getStringCellValue();
					constant = row.getCell(1).getStringCellValue();
					localizationKey = (int) row.getCell(2).getNumericCellValue();
					defaultLocalization = row.getCell(3).getStringCellValue();
				}
				catch (Exception e) {
					Notification.show(
							"Error importing file: Empty values. Row number " + (row.getRowNum() + 1),
							3000,
							Notification.Position.MIDDLE);
				}

				if (file == null || file.isEmpty() || constant == null || constant.isEmpty() || defaultLocalization == null
						|| defaultLocalization.isEmpty() || localizationKey == null) {
					Notification.show(
							"Error importing file: Empty values. Row number " + (row.getRowNum() + 1),
							3000,
							Notification.Position.MIDDLE);
				}
				else {

					BCcLocalizationEO localizationEO = updateLocalization(
							aProjectVersionEO,
							aBundleEO,
							file,
							constant,
							localizationKey,
							defaultLocalization,
							localizationMap);

					row.cellIterator().forEachRemaining(cell -> {
						int index = cell.getColumnIndex();
						if (index > 3) {
							BCcLanguageEO languageEO = languageMap.get(index);
							if (languageEO != null) {
								updateLocalizationTransaction(localizationEO, languageEO, cell.getStringCellValue());
							}
						}
					});
				}
			}
		}

		cleanLocalizations(localizationMap, aProjectVersionEO.getProjectVersionId());
		workbook.close();
	}

	private String createKeyLocalization(BCcLocalizationEO aLocalization) {
		return createKeyLocalization(
				aLocalization.getFile(),
				aLocalization.getConstant(),
				aLocalization.getLocalizationKey(),
				aLocalization.getDefaultLocalization());
	}

	private String createKeyLocalization(
			String aFile,
			String aConstant,
			Integer aLocalizationKey,
			String aDefaultLocalization) {
		return aFile.toLowerCase() + aConstant.toLowerCase() + aLocalizationKey + aDefaultLocalization.toLowerCase();
	}

	private BCcLocalizationEO updateLocalization(
			BCcProjectVersionEO aProjectVersionEO,
			BCcBundleEO aBundleEO,
			String aFile,
			String aConstant,
			Integer aLocalizationKey,
			String aDefaultLocalization,
			Map<String, BCcLocalizationEO> aLocalizationMap) {

		String keyLocalization = createKeyLocalization(aFile, aConstant, aLocalizationKey, aDefaultLocalization);
		BCcLocalizationEO localizationEO = aLocalizationMap.remove(keyLocalization);

		BCcProjectVersionLocalizationEO projectVersionLocalizationEO = null;
		if (localizationEO == null) {
			localizationEO = new BCcLocalizationEO();
			localizationEO.setFile(aFile);
			localizationEO.setConstant(aConstant);
			localizationEO.setLocalizationKey(aLocalizationKey);
			localizationEO.setDefaultLocalization(aDefaultLocalization);
			dataService.createLocalization(localizationEO);
		}
		else {
			projectVersionLocalizationEO = dataService.findProjectVersionLocalization(
					aProjectVersionEO.getProjectVersionId(),
					localizationEO.getLocalizationId());
		}

		BCcProjectVersionLocalizationBundleEO projectVersionLocalizationBundleEO = null;
		if (projectVersionLocalizationEO == null) {
			projectVersionLocalizationEO = new BCcProjectVersionLocalizationEO();
			projectVersionLocalizationEO.setLocalization(localizationEO);
			projectVersionLocalizationEO.setProjectVersion(aProjectVersionEO);
			dataService.createProjectVersionLocalization(projectVersionLocalizationEO);
		}
		else {
			projectVersionLocalizationBundleEO = dataService.findProjectVersionLocalizationBundle(
					projectVersionLocalizationEO.getProjectVersionLocalizationId(),
					aBundleEO.getBundleId());
		}

		if (projectVersionLocalizationBundleEO == null) {
			projectVersionLocalizationBundleEO = new BCcProjectVersionLocalizationBundleEO();
			projectVersionLocalizationBundleEO.setProjectVersionLocalization(projectVersionLocalizationEO);
			projectVersionLocalizationBundleEO.setBundle(aBundleEO);
			dataService.createProjectVersionLocalizationBundle(projectVersionLocalizationBundleEO);
		}

		return localizationEO;
	}

	private void updateLocalizationTransaction(BCcLocalizationEO aLocalizationEO, BCcLanguageEO aLanguageEO, String aTranslationValue) {
		BCcLocalizationTranslationEO localizationTranslationEO = dataService.findLocalizationTranslation(
				aLocalizationEO.getLocalizationId(),
				aLanguageEO.getLanguageId());

		if (localizationTranslationEO == null) {
			localizationTranslationEO = new BCcLocalizationTranslationEO();
			localizationTranslationEO.setLocalization(aLocalizationEO);
			localizationTranslationEO.setLanguage(aLanguageEO);
			localizationTranslationEO.setTranslationValue(aTranslationValue == null ? "" : aTranslationValue);
			localizationTranslationEO.setLastHistoryLocalizationTranslation(null);
			dataService.createLocalizationTranslation(localizationTranslationEO);
		}
		else {
			String lastTranslationValue = localizationTranslationEO.getTranslationValue();
			if (!lastTranslationValue.trim().isEmpty()) {
				BCcHistoryLocalizationTranslationEO historyLocalizationTranslationEO = new BCcHistoryLocalizationTranslationEO();
				historyLocalizationTranslationEO.setLocalizationTranslation(localizationTranslationEO);
				historyLocalizationTranslationEO.setTranslationValue(lastTranslationValue);
				dataService.createHistoryLocalizationTranslation(historyLocalizationTranslationEO);
				localizationTranslationEO.setLastHistoryLocalizationTranslation(historyLocalizationTranslationEO);
			}

			localizationTranslationEO.setTranslationValue(aTranslationValue);
			dataService.updateLocalizationTranslation(localizationTranslationEO);
		}
	}

	private void cleanLocalizations(Map<String, BCcLocalizationEO> aLocalizationMap, Long aProjectVersionId) {
		if (!aLocalizationMap.isEmpty()) {
			for (BCcLocalizationEO localizationEO : aLocalizationMap.values()) {
				BCcProjectVersionLocalizationEO projectVersionLocalizationEO = dataService.findProjectVersionLocalization(
						aProjectVersionId, localizationEO.getLocalizationId());

				if (projectVersionLocalizationEO != null) {
					dataService.deleteProjectVersionLocalization(projectVersionLocalizationEO.getProjectVersionLocalizationId());
				}
			}
		}
		cleanLocalizations();
	}

	private void cleanLocalizations() {
		List<BCcLocalizationEO> localizationWithoutProjectVersionList = dataService.getLocalizationWithoutProjectVersionList();
		for (BCcLocalizationEO localizationEO : localizationWithoutProjectVersionList) {
			dataService.deleteLocalization(localizationEO.getLocalizationId());
		}
	}

	public String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			return authentication.getName();
		}
		return null;
	}

	public BCcUserEO getCurrentUser() {
		String currentUsername = getCurrentUsername();
		if (currentUsername == null) {
			return null;
		}
		BCcUserEO userEO = dataService.findUserByUsername(currentUsername);

		return userEO;
	}
}

