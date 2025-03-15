package com.example.application.views;

import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.example.application.data.BCcBundleEO;
import com.example.application.data.BCcLocalizationEO;
import com.example.application.data.BCcProjectEO;
import com.example.application.data.BCcProjectVersionEO;
import com.example.application.security.BCcDataService;
import com.example.application.services.BCcExportService;
import com.example.application.services.BCcImportService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

/**
 * <p>
 * Title: {@link BCcImportExportViewAdmin}
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
@Route("import-export")
@Menu(order = 1, icon = LineAwesomeIconUrl.FILE_UPLOAD_SOLID)
@RolesAllowed({ "ADMIN" })
public class BCcImportExportViewAdmin extends VerticalLayout {

	private final BCcDataService dataService;
	private final BCcImportService importService;
	private final BCcExportService exportService;

	private final ComboBox<BCcProjectEO> projectComboBox = new ComboBox<>("Select Project");
	private final ComboBox<BCcProjectVersionEO> projectVersionComboBox = new ComboBox<>("Select Project Version");
	private final ComboBox<BCcBundleEO> bundleComboBox = new ComboBox<>("Select Bundle");

	@Autowired
	public BCcImportExportViewAdmin(BCcDataService aDataService, BCcImportService aImportService, BCcExportService aExportService) {
		this.dataService = aDataService;
		this.importService = aImportService;
		this.exportService = aExportService;

		cleanLocalizations();

		refreshProjectComboBox();
		refreshBundleComboBox();
		projectComboBox.addValueChangeListener(event -> refreshProjectVersionComboBox());

		FlexLayout projectSettingsLayout = new FlexLayout();
		projectSettingsLayout.addClassName("responsive-layout");
		projectSettingsLayout.add(projectComboBox, projectVersionComboBox, bundleComboBox, createBundleCreationButton());
		projectSettingsLayout.setAlignItems(Alignment.END);
		projectSettingsLayout.setWidthFull();
		projectSettingsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		add(projectSettingsLayout);

		createUploadButton();
		createExportButton();
	}

	private void cleanLocalizations() {
		List<BCcLocalizationEO> localizationWithoutProjectVersionList = dataService.getLocalizationWithoutProjectVersionList();

		for (BCcLocalizationEO localizationEO : localizationWithoutProjectVersionList) {
			dataService.deleteLocalization(localizationEO.getLocalizationId());
		}
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

		refreshProjectVersionComboBox();
	}

	private void refreshProjectVersionComboBox() {

		BCcProjectVersionEO projectVersionEO = projectVersionComboBox.getValue();
		BCcProjectEO projectEO = projectComboBox.getValue();

		if (projectEO != null) {
			projectVersionComboBox.clear();
			projectVersionComboBox.setItemLabelGenerator(BCcProjectVersionEO::getProjectVersionName);
			projectVersionComboBox.setReadOnly(false);

			List<BCcProjectVersionEO> projectVersionList = dataService.getProjectVersionList(projectEO.getProjectId());
			projectVersionComboBox.setItems(projectVersionList);

			List<Long> projectVersionIdList = projectVersionList.stream().map(BCcProjectVersionEO::getProjectVersionId).toList();

			if (projectVersionEO != null && projectVersionIdList.contains(projectVersionEO.getProjectVersionId())) {
				projectVersionComboBox.setValue(projectVersionEO);
			}
		}
		else {
			projectVersionComboBox.clear();
			projectVersionComboBox.setReadOnly(true);
		}
	}

	private void refreshBundleComboBox() {

		BCcBundleEO bundleEO = bundleComboBox.getValue();

		List<BCcBundleEO> bundleList = dataService.getAllBundleList();
		bundleComboBox.setItems(bundleList);
		bundleComboBox.setItemLabelGenerator(BCcBundleEO::getBundleName);

		List<Long> bundleIdList = bundleList.stream().map(BCcBundleEO::getBundleId).toList();

		if (bundleEO != null && bundleIdList.contains(bundleEO.getBundleId())) {
			bundleComboBox.setValue(bundleEO);
		}

		refreshProjectVersionComboBox();
	}

	private Component createBundleCreationButton() {

		Button projectCreationButton = new Button("Create Bundle", e -> {

			Dialog creationDialog = new Dialog();

			TextField bundleField = new TextField("Bundle Name");

			Button confirmButton = new Button("Create", event -> {
				String bundleName = bundleField.getValue().trim();
				if (!bundleName.isEmpty()) {
					List<BCcBundleEO> bundleList = dataService.getAllBundleList();
					bundleList.stream().filter(a -> a.getBundleName().equals(bundleName)).findAny().ifPresentOrElse(
							a -> Notification.show("Bundle \"" + bundleName + "\" already exists."),
							() -> {
								BCcBundleEO bundleEO = new BCcBundleEO();
								bundleEO.setBundleName(bundleName);
								dataService.createBundle(bundleEO);

								refreshBundleComboBox();
								bundleField.clear();
								creationDialog.close();
								Notification.show("Bundle \"" + bundleName + "\" created.");
							}
					);
				}
			});

			Button cancelButton = new Button("Cancel", event -> creationDialog.close());

			HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
			buttonLayout.setWidthFull();
			buttonLayout.setAlignItems(Alignment.CENTER);
			buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

			VerticalLayout dialogLayout = new VerticalLayout();
			dialogLayout.add(new H3("Create Bundle"), bundleField, buttonLayout);

			creationDialog.add(dialogLayout);
			creationDialog.open();
		});

		return projectCreationButton;
	}

	private void createUploadButton() {
		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setEnabled(false);
		upload.setAcceptedFileTypes(".xlsx", ".xls");

		projectVersionComboBox.addValueChangeListener(event -> {
			BCcProjectVersionEO projectVersionEO = event.getValue();
			if (projectVersionEO != null && bundleComboBox.getValue() != null) {
				upload.setEnabled(true);
			}
			else {
				upload.setEnabled(false);
			}
		});

		bundleComboBox.addValueChangeListener(event -> {
			BCcBundleEO bundleEO = event.getValue();
			if (bundleEO != null && projectVersionComboBox.getValue() != null) {
				upload.setEnabled(true);
			}
			else {
				upload.setEnabled(false);
			}
		});

		upload.addSucceededListener(event -> {
			String fileName = event.getFileName();
			try (InputStream inputStream = buffer.getInputStream(event.getFileName())) {

				BCcProjectEO projectEO = projectComboBox.getValue();
				BCcProjectVersionEO projectVersionEO = projectVersionComboBox.getValue();
				BCcBundleEO bundleEO = bundleComboBox.getValue();

				if (projectEO == null || projectVersionEO == null || bundleEO == null) {
					Notification.show("Please select project, version and bundle", 3000, Notification.Position.MIDDLE);
					return;
				}

				importService.importFromExcelAsync(inputStream, fileName, projectEO, projectVersionEO, bundleEO);

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
			BCcProjectVersionEO projectVersionEO = projectVersionComboBox.getValue();
			BCcBundleEO bundleEO = bundleComboBox.getValue();

			if (projectEO == null || projectVersionEO == null || bundleEO == null) {
				Notification.show("Please select project, version and bundle", 3000, Notification.Position.MIDDLE);
				return;
			}

			StreamResource resource = new StreamResource(
					"localizations_"
							+ "PROJECT_" + projectEO.getProjectName().replaceAll("\\s+", "_")
							+ "_PROJECT_VERSION_" + projectVersionEO.getProjectVersionName().replaceAll("\\s+", "_")
							+ "_BUNDLE_" + bundleEO.getBundleName().replaceAll("\\s+", "_")
							+ ".xlsx",
					() -> exportService.exportToExcel(projectEO, projectVersionEO, bundleEO));
			Anchor downloadLink = new Anchor(resource, "Download Excel");
			downloadLink.getElement().setAttribute("download", true);
			add(downloadLink);
		});
		exportButton.setEnabled(false);

		projectVersionComboBox.addValueChangeListener(event -> {
			BCcProjectVersionEO projectVersionEO = event.getValue();
			if (projectVersionEO != null && bundleComboBox.getValue() != null) {
				exportButton.setEnabled(true);
			}
			else {
				exportButton.setEnabled(false);
			}
		});

		bundleComboBox.addValueChangeListener(event -> {
			BCcBundleEO bundleEO = event.getValue();
			if (bundleEO != null && projectVersionComboBox.getValue() != null) {
				exportButton.setEnabled(true);
			}
			else {
				exportButton.setEnabled(false);
			}
		});

		add(exportButton);
	}
}
