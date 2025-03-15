package com.example.application.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.application.data.BCcBundleEO;
import com.example.application.data.BCcLanguageEO;
import com.example.application.data.BCcLocalizationEO;
import com.example.application.data.BCcLocalizationTranslationEO;
import com.example.application.data.BCcProjectEO;
import com.example.application.data.BCcProjectVersionEO;
import com.example.application.data.BCcUserEO;
import com.example.application.security.BCcDataService;
import com.vaadin.flow.component.notification.Notification;

/**
 * <p>
 * Title: {@link BCcExportService}
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
public class BCcExportService {
	private final BCcDataService dataService;

	public BCcExportService(BCcDataService aDataService) {
		this.dataService = aDataService;
	}

	public InputStream exportToExcel(BCcProjectEO aProjectEO) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Localizations");
			sheet.protectSheet("nshjaieoslakwmnf??UNBREAKABLE??P@ssw0rdjkhskdas&^@(jksd");

			CellStyle lockedStyle = workbook.createCellStyle();
			lockedStyle.setLocked(true);

			CellStyle unlockedStyle = workbook.createCellStyle();
			unlockedStyle.setLocked(false);

			Row header = sheet.createRow(0);
			Cell cell = header.createCell(0);
			cell.setCellStyle(lockedStyle);
			cell.setCellValue("FILE");
			cell = header.createCell(1);
			cell.setCellStyle(lockedStyle);
			cell.setCellValue("JAVA_CONST");
			cell = header.createCell(2);
			cell.setCellStyle(lockedStyle);
			cell.setCellValue("KEY");
			cell = header.createCell(3);
			cell.setCellStyle(lockedStyle);
			cell.setCellValue("DEFAULT");

			BCcUserEO currentUser = getCurrentUser();

			List<BCcLanguageEO> languageList = dataService.findLanguageList(aProjectEO.getProjectId());
			List<BCcLanguageEO> languageByUserList = dataService.findLanguageByUserList(currentUser.getUserId());

			List<String> supportedLanguageList = languageByUserList.stream().map(BCcLanguageEO::getLanguageIso).toList();

			int index = 4;
			for (BCcLanguageEO languageEO : languageList) {
				header.createCell(index).setCellValue(languageEO.getLanguageIso().toUpperCase());
				index++;
			}

			List<BCcLocalizationEO> localizationList = dataService.getLocalizationList(aProjectEO.getProjectId());

			for (BCcLocalizationEO localizationEO : localizationList) {
				Row row = sheet.createRow(localizationList.indexOf(localizationEO) + 1);
				row.createCell(0).setCellValue(localizationEO.getFile());
				row.createCell(1).setCellValue(localizationEO.getConstant());
				row.createCell(2).setCellValue(localizationEO.getLocalizationKey());
				row.createCell(3).setCellValue(localizationEO.getDefaultLocalization());

				List<BCcLocalizationTranslationEO> localizationTranslationList = dataService.getLocalizationTranslationList(localizationEO.getLocalizationId());
				Map<String, String> translationMap = localizationTranslationList.stream()
						.collect(Collectors.toMap(a -> a.getLanguage().getLanguageIso(), BCcLocalizationTranslationEO::getTranslationValue));

				index = 4;
				for (BCcLanguageEO languageEO : languageList) {
					String translationValue = translationMap.get(languageEO.getLanguageIso());
					boolean contains = supportedLanguageList.contains(languageEO.getLanguageIso());
					cell = row.createCell(index);
					cell.setCellStyle(contains ? unlockedStyle : lockedStyle);
					cell.setCellValue(translationValue != null ? translationValue : "");
					index++;
				}
			}

			for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		}
		catch (IOException e) {
			Notification.show("Error exporting file");
			return new ByteArrayInputStream(new byte[0]);
		}
	}

	public InputStream exportToExcel(BCcProjectEO aProjectEO, BCcProjectVersionEO aProjectVersionEO, BCcBundleEO aBundleEO) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Localizations");

			Row header = sheet.createRow(0);
			Cell cell = header.createCell(0);
			cell.setCellValue("FILE");
			cell = header.createCell(1);
			cell.setCellValue("JAVA_CONST");
			cell = header.createCell(2);
			cell.setCellValue("KEY");
			cell = header.createCell(3);
			cell.setCellValue("DEFAULT");

			List<BCcLanguageEO> languageList = dataService.findLanguageList(aProjectEO.getProjectId());

			int index = 4;
			for (BCcLanguageEO languageEO : languageList) {
				header.createCell(index).setCellValue(languageEO.getLanguageIso().toUpperCase());
				index++;
			}

			List<BCcLocalizationEO> localizationList = dataService.getLocalizationList(
					aProjectEO.getProjectId(), aProjectVersionEO.getProjectVersionId(), aBundleEO.getBundleId());

			for (BCcLocalizationEO localizationEO : localizationList) {
				Row row = sheet.createRow(localizationList.indexOf(localizationEO) + 1);
				row.createCell(0).setCellValue(localizationEO.getFile());
				row.createCell(1).setCellValue(localizationEO.getConstant());
				row.createCell(2).setCellValue(localizationEO.getLocalizationKey());
				row.createCell(3).setCellValue(localizationEO.getDefaultLocalization());

				List<BCcLocalizationTranslationEO> localizationTranslationList = dataService.getLocalizationTranslationList(localizationEO.getLocalizationId());
				Map<String, String> translationMap = localizationTranslationList.stream()
						.collect(Collectors.toMap(a -> a.getLanguage().getLanguageIso(), BCcLocalizationTranslationEO::getTranslationValue));

				index = 4;
				for (BCcLanguageEO languageEO : languageList) {
					String translationValue = translationMap.get(languageEO.getLanguageIso());
					cell = row.createCell(index);
					cell.setCellValue(translationValue != null ? translationValue : "");
					index++;
				}
			}

			for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		}
		catch (IOException e) {
			Notification.show("Error exporting file");
			return new ByteArrayInputStream(new byte[0]);
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

