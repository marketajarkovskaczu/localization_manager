package com.example.application.views;

import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.example.application.data.BCcLocalizationEO;
import com.example.application.data.BCcProjectEO;
import com.example.application.data.BCcUserEO;
import com.example.application.security.BCcDataService;
import com.example.application.services.BCcExportService;
import com.example.application.services.BCcImportService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

/**
 * <p>
 * Title: {@link BCcImportExportViewTranslator}
 * </p>
 * <p>
 * Description: Import export view
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 05.03.2025 21:39
 */
@PageTitle("Import and Export Excel")
@Route("translator")
@Menu(order = 1, icon = LineAwesomeIconUrl.FILE_UPLOAD_SOLID)
@RolesAllowed({ "USER" })
public class BCcImportExportViewTranslator extends VerticalLayout {

	private final BCcDataService dataService;
	private final BCcImportService importService;
	private final BCcExportService exportService;

	private final ComboBox<BCcProjectEO> projectComboBox = new ComboBox<>("Select Project");

	@Autowired
	public BCcImportExportViewTranslator(BCcDataService aDataService, BCcImportService aImportService, BCcExportService aExportService) {
		this.dataService = aDataService;
		this.importService = aImportService;
		this.exportService = aExportService;

		refreshProjectComboBox();

		add(projectComboBox);
		createUploadButton();
		createExportButton();
	}

	private void refreshProjectComboBox() {

		BCcProjectEO projectEO = projectComboBox.getValue();

		List<BCcProjectEO> projectList = dataService.getAllProjectList();
		projectComboBox.setItems(projectList);
		projectComboBox.setItemLabelGenerator(BCcProjectEO::getProjectName);

		List<Long> projectIdList = projectList.stream().map(BCcProjectEO::getProjectId).toList();

		if (projectEO != null && projectIdList.contains(projectEO.getProjectId())) {
			projectComboBox.setValue(projectEO);
		}
		else {
			projectComboBox.setValue(null);
		}
	}

	private void createUploadButton() {
		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setEnabled(false);
		upload.setAcceptedFileTypes(".xlsx", ".xls");

		projectComboBox.addValueChangeListener(event -> {
			upload.setEnabled(projectComboBox.getValue() != null);
		});

		upload.addSucceededListener(event -> {
			String fileName = event.getFileName();
			try (InputStream inputStream = buffer.getInputStream(event.getFileName())) {
				BCcProjectEO projectEO = projectComboBox.getValue();

				if (projectEO == null) {
					Notification.show("Please select project", 3000, Notification.Position.MIDDLE);
					return;
				}

				importService.importFromExcelAsync(inputStream, fileName, projectEO);

				Notification.show("Import started...", 3000, Notification.Position.TOP_END);
			}
			catch (IOException e) {
				Notification.show("Error importing file: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
			}
		});

		upload.addFileRejectedListener(event -> {
			String fileName = event.getFileName();
			if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
				Notification.show("Invalid file type! Please upload an Excel file. (.xlsx, .xls)", 3000, Notification.Position.MIDDLE);
			}
			else {
				Notification.show("Upload failed", 3000, Notification.Position.MIDDLE);
			}
		});

		upload.addFailedListener(event -> System.err.println("Upload failed: " + event.getReason().getMessage()));

		add(upload);
	}

	private void createExportButton() {
		Button exportButton = new Button("Export to Excel", event -> {

			BCcProjectEO projectEO = projectComboBox.getValue();

			if (projectEO == null) {
				Notification.show("Please select project", 3000, Notification.Position.MIDDLE);
				return;
			}

			StreamResource resource = new StreamResource(
					"localizations_" + projectEO.getProjectName().replaceAll("\\s+", "_") + ".xlsx",
					() -> exportService.exportToExcel(projectEO));
			Anchor downloadLink = new Anchor(resource, "Download Excel");
			downloadLink.getElement().setAttribute("download", true);
			add(downloadLink);
		});
		exportButton.setEnabled(false);

		projectComboBox.addValueChangeListener(event -> {
			exportButton.setEnabled(projectComboBox.getValue() != null);
		});

		add(exportButton);
	}
}