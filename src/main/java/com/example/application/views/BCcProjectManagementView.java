package com.example.application.views;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.example.application.data.BCcLanguageEO;
import com.example.application.data.BCcLanguageProjectEO;
import com.example.application.data.BCcLocalizationEO;
import com.example.application.data.BCcProjectEO;
import com.example.application.data.BCcProjectVersionEO;
import com.example.application.data.BCcUserEO;
import com.example.application.data.BCcUserProjectEO;
import com.example.application.security.BCcDataService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * <p>
 * Title: {@link BCcProjectManagementView}
 * </p>
 * <p>
 * Description: Project management view
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 05.03.2025 23:02
 */
@PageTitle("Project Management")
@Route("project")
@Menu(order = 0, icon = LineAwesomeIconUrl.EDIT_SOLID)
@RolesAllowed({ "ADMIN" })
public class BCcProjectManagementView extends VerticalLayout {

	private final BCcDataService dataService;

	private final ComboBox<BCcProjectEO> projectComboBox = new ComboBox<>("Select Project");
	private final ComboBox<BCcProjectVersionEO> projectVersionComboBox = new ComboBox<>("Select Project Version");
	private final MultiSelectComboBox<BCcLanguageEO> languageMultiSelectComboBox = new MultiSelectComboBox<>("Select Languages");
	private final MultiSelectComboBox<BCcUserEO> userMultiSelectComboBox = new MultiSelectComboBox<>("Select Users");

	@Autowired
	public BCcProjectManagementView(BCcDataService aDataService) {
		this.dataService = aDataService;

		refreshProjectComboBox();

		FlexLayout firstLayout = new FlexLayout();
		firstLayout.addClassName("responsive-layout");
		firstLayout.add(
				createProjectCreationButton(),
				createLanguageCreationButton(),
				createLanguageDeletionButton());
		firstLayout.setAlignItems(Alignment.CENTER);
		firstLayout.setWidthFull();
		firstLayout.setJustifyContentMode(JustifyContentMode.CENTER);

		add(
				firstLayout,
				createProjectManagementSeparator(),
				createProjectManagementLayout(),
				createLanguageUserSeparator(),
				createLanguageUserLayout(),
				createProjectVersionManagementSeparator(),
				createProjectVersionManagementLayout()
		);
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
		refreshLanguageComboBox();
		refreshUserComboBox();
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

	private void refreshLanguageComboBox() {

		BCcProjectEO projectEO = projectComboBox.getValue();

		if (projectEO != null) {
			languageMultiSelectComboBox.clear();
			languageMultiSelectComboBox.setItemLabelGenerator(BCcLanguageEO::getLanguageName);
			languageMultiSelectComboBox.setReadOnly(false);

			List<BCcLanguageEO> allLanguageList = dataService.getAllLanguageList();
			languageMultiSelectComboBox.setItems(allLanguageList);

			Set<Long> languageIdSet = dataService.findLanguageList(projectEO.getProjectId()).stream()
					.map(BCcLanguageEO::getLanguageId).collect(Collectors.toSet());

			allLanguageList.stream().filter(a -> languageIdSet.contains(a.getLanguageId()))
					.forEach(languageMultiSelectComboBox::select);

			languageMultiSelectComboBox.select();
		}
		else {
			languageMultiSelectComboBox.setReadOnly(true);
			languageMultiSelectComboBox.clear();
		}
	}

	private void refreshUserComboBox() {

		BCcProjectEO projectEO = projectComboBox.getValue();

		if (projectEO != null) {
			userMultiSelectComboBox.clear();
			userMultiSelectComboBox.setItemLabelGenerator(BCcUserEO::getUserName);
			userMultiSelectComboBox.setReadOnly(false);

			List<BCcUserEO> allUserList = dataService.getAllUserList();
			userMultiSelectComboBox.setItems(allUserList);

			Set<Long> userIdSet = dataService.findUserList(projectEO.getProjectId()).stream()
					.map(BCcUserEO::getUserId).collect(Collectors.toSet());

			allUserList.stream().filter(a -> userIdSet.contains(a.getUserId()))
					.forEach(userMultiSelectComboBox::select);

			userMultiSelectComboBox.select();
		}
		else {
			userMultiSelectComboBox.setReadOnly(true);
			userMultiSelectComboBox.clear();
		}
	}

	//-------------------------------------- CREATE PROJECT -----------------------------------------------------
	private Component createProjectCreationButton() {

		Button projectCreationButton = new Button("Create Project", e -> {

			Dialog creationDialog = new Dialog();

			TextField projectNameField = new TextField("Project Name");

			Button confirmButton = new Button("Create", event -> {
				String projectName = projectNameField.getValue().trim();
				if (!projectName.isEmpty()) {
					List<BCcProjectEO> projectList = dataService.getAllProjectList();
					projectList.stream().filter(a -> a.getProjectName().equals(projectName)).findAny().ifPresentOrElse(
							a -> Notification.show("Project \"" + projectName + "\" already exists."),
							() -> {
								createProject(projectName);
								refreshProjectComboBox();
								projectNameField.clear();
								creationDialog.close();
								Notification.show("Project \"" + projectName + "\" created.");
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
			dialogLayout.add(new H3("Create Project"), projectNameField, buttonLayout);

			creationDialog.add(dialogLayout);
			creationDialog.open();
		});

		return projectCreationButton;
	}

	private void createProject(String aProjectName) {
		BCcProjectEO projectEO = new BCcProjectEO();
		projectEO.setProjectName(aProjectName);
		dataService.createProject(projectEO);

		BCcProjectVersionEO projectVersionEO = new BCcProjectVersionEO();
		projectVersionEO.setProject(projectEO);
		projectVersionEO.setProjectVersionName("main");
		projectVersionEO.setParentProjectVersion(null);
		dataService.createProjectVersion(projectVersionEO);
	}
	//-------------------------------------- CREATE LANGUAGE -----------------------------------------------------

	private Component createLanguageCreationButton() {

		Button languageCreationButton = new Button("Create Language", e -> {

			Dialog creationDialog = new Dialog();

			TextField languageNameField = new TextField("Language Name");
			TextField languageIsoField = new TextField("Language ISO");

			Button confirmButton = new Button("Create", event -> {
				String languageName = languageNameField.getValue().trim();
				String languageIso = languageIsoField.getValue().trim();
				if (!languageName.isEmpty() && !languageIso.isEmpty()) {

					BCcLanguageEO sameLanguageEO = dataService.getAllLanguageList().stream()
							.filter(a -> a.getLanguageName().equals(languageName) || a.getLanguageIso().equals(languageIso))
							.findAny()
							.orElse(null);

					if (sameLanguageEO != null) {
						Notification.show("Language \"" + languageName + ", " + languageIso + "\" already exists.");
					}
					else if (languageIso.length() != 2) {
						Notification.show("Language ISO must be 2 characters long.");
					}
					else {

						BCcLanguageEO languageEO = new BCcLanguageEO();
						languageEO.setLanguageName(languageName);
						languageEO.setLanguageIso(languageIso.toLowerCase());
						dataService.createLanguage(languageEO);

						refreshLanguageComboBox();

						languageNameField.clear();
						languageIsoField.clear();
						creationDialog.close();
						Notification.show("Language \"" + languageName + ", " + languageIso + "\" created.");
					}
				}
			});

			Button cancelButton = new Button("Cancel", event -> creationDialog.close());

			HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
			buttonLayout.setWidthFull();
			buttonLayout.setAlignItems(Alignment.CENTER);
			buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

			VerticalLayout dialogLayout = new VerticalLayout();
			dialogLayout.add(new H3("Create Language"), languageNameField, languageIsoField, buttonLayout);

			creationDialog.add(dialogLayout);
			creationDialog.open();
		});

		return languageCreationButton;
	}

	//-------------------------------------- DELETE LANGUAGE -----------------------------------------------------

	private Component createLanguageDeletionButton() {

		Button languageDeletionButton = new Button("Delete Language", e -> {

			Dialog deletionDialog = new Dialog();

			Button deleteButton = new Button("Delete Language");
			deleteButton.setEnabled(false);

			ComboBox<BCcLanguageEO> languageComboBox = new ComboBox<>("Select Language");
			languageComboBox.setItemLabelGenerator(BCcLanguageEO::getLanguageName);
			languageComboBox.setReadOnly(false);
			languageComboBox.setItems(dataService.getAllLanguageList());
			languageComboBox.addValueChangeListener(event -> {
				BCcLanguageEO languageEO = event.getValue();
				boolean hasLanguage = languageEO != null;
				deleteButton.setEnabled(hasLanguage);
			});

			deleteButton.addClickListener(ev -> {
				BCcLanguageEO languageEO = languageComboBox.getValue();
				if (languageEO != null) {
					ConfirmDialog deletionConfirmDialog = new ConfirmDialog();
					deletionConfirmDialog.setHeader("Confirm Deletion");
					deletionConfirmDialog.setText(
							"Are you sure you want to delete the language: " + languageEO.getLanguageName() + ", " + languageEO.getLanguageIso() + "? "
									+ "It will delete all translations in all projects!");

					deletionConfirmDialog.setCancelable(true);
					deletionConfirmDialog.setConfirmText("Delete");
					deletionConfirmDialog.setConfirmButtonTheme("error primary");
					deletionConfirmDialog.addConfirmListener(event -> {
						String languageName = languageEO.getLanguageName();
						dataService.deleteLanguage(languageEO.getLanguageId());
						refreshLanguageComboBox();
						Notification.show("Project \"" + languageName + "\" deleted.");
					});
					deletionConfirmDialog.open();
					deletionDialog.close();
				}
			});

			VerticalLayout dialogLayout = new VerticalLayout();
			dialogLayout.add(new H3("Create Language"), languageComboBox, deleteButton);

			deletionDialog.add(dialogLayout);
			deletionDialog.open();
		});

		return languageDeletionButton;
	}

	//---------------------------------------PROJECT SEPARATOR----------------------------------------------------
	private Component createProjectManagementSeparator() {
		Hr leftHr = new Hr();
		leftHr.setWidth("45%");

		Span title = new Span("PROJECT MANAGEMENT");
		title.setMinWidth("200px");
		Style style = title.getStyle();
		style.setPadding("0 10px");
		style.setFontWeight("bold");

		Hr rightHr = new Hr();
		rightHr.setWidth("45%");

		HorizontalLayout separatorLayout = new HorizontalLayout(leftHr, title, rightHr);
		separatorLayout.setWidthFull();
		separatorLayout.setAlignItems(Alignment.CENTER);
		separatorLayout.setJustifyContentMode(JustifyContentMode.CENTER);

		return separatorLayout;
	}

	//-------------------------------------PROJECT - SELECT, NAME EDIT, DELETE------------------------------------------------------
	private Component createProjectManagementLayout() {

		Component projectNameEditLayout = createProjectNameEditLayout();
		Component projectDeletionButton = createProjectDeletionButton();

		FlexLayout projectManagementLayout = new FlexLayout();
		projectManagementLayout.addClassName("responsive-layout");
		projectManagementLayout.add(projectComboBox, projectNameEditLayout, projectDeletionButton);
		projectManagementLayout.setAlignItems(Alignment.END);
		projectManagementLayout.setWidthFull();
		projectManagementLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

		return projectManagementLayout;
	}

	private Component createProjectNameEditLayout() {
		TextField projectNameField = new TextField("Edit project name");
		projectNameField.setPlaceholder("Enter new project name");
		projectNameField.setReadOnly(true);

		Button saveButton = new Button("Save");
		saveButton.setEnabled(false);

		projectComboBox.addValueChangeListener(event -> {
			BCcProjectEO projectEO = event.getValue();
			boolean hasProject = projectEO != null;
			projectNameField.setReadOnly(!hasProject);
			saveButton.setEnabled(hasProject);
			projectNameField.setValue(hasProject ? projectEO.getProjectName() : "");
		});

		saveButton.addClickListener(event -> {
			BCcProjectEO projectEO = projectComboBox.getValue();
			if (projectEO != null) {
				projectEO.setProjectName(projectNameField.getValue().trim());
				dataService.updateProject(projectEO);
				refreshProjectComboBox();
			}
		});

		HorizontalLayout projectNameEditLayout = new HorizontalLayout(projectNameField, saveButton);
		projectNameEditLayout.setAlignItems(Alignment.END);
		projectNameEditLayout.setSpacing(false);
		projectNameEditLayout.setPadding(false);

		return projectNameEditLayout;
	}

	private Component createProjectDeletionButton() {

		Button deleteButton = new Button("Delete Project");
		deleteButton.setEnabled(false);

		projectComboBox.addValueChangeListener(event -> {
			BCcProjectEO projectEO = event.getValue();
			boolean hasProject = projectEO != null;
			deleteButton.setEnabled(hasProject);
		});

		deleteButton.addClickListener(e -> {
			BCcProjectEO projectEO = projectComboBox.getValue();
			if (projectEO != null) {
				ConfirmDialog deletionDialog = new ConfirmDialog();
				deletionDialog.setHeader("Confirm Deletion");
				deletionDialog.setText("Are you sure you want to delete the project: " + projectEO.getProjectName() + "? "
						+ "It will delete all project versions and translations!");

				deletionDialog.setCancelable(true);
				deletionDialog.setConfirmText("Delete");
				deletionDialog.setConfirmButtonTheme("error primary");
				deletionDialog.addConfirmListener(event -> {
					String projectName = projectEO.getProjectName();
					dataService.deleteProject(projectEO.getProjectId());
					Notification.show("Project \"" + projectName + "\" deleted.");
					refreshProjectComboBox();
				});
				deletionDialog.open();
			}
		});

		return deleteButton;
	}

	//---------------------------------------LANGUAGE SEPARATOR----------------------------------------------------
	private Component createLanguageUserSeparator() {

		Span title = new Span("LANGUAGES AND USERS");
		title.setMinWidth("150px");
		Style style = title.getStyle();
		style.setPadding("0 10px");
		style.setFontWeight("bold");

		Hr rightHr = new Hr();
		rightHr.setWidth("70%");

		HorizontalLayout separatorLayout = new HorizontalLayout(title, rightHr);
		separatorLayout.setWidthFull();
		separatorLayout.setAlignItems(Alignment.START);
		separatorLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

		return separatorLayout;
	}

	//----------------------------------------LANGUAGE ADD, DELETE---------------------------------------------------

	private Component createLanguageUserLayout() {

		FlexLayout languageAssignmentLayout = new FlexLayout(createLanguageAssignmentLayout(), createUserAssignmentLayout());
		languageAssignmentLayout.addClassName("responsive-layout");
		languageAssignmentLayout.setAlignItems(Alignment.CENTER);
		languageAssignmentLayout.setWidthFull();
		languageAssignmentLayout.setJustifyContentMode(JustifyContentMode.CENTER);

		languageAssignmentLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);

		return languageAssignmentLayout;
	}

	private VerticalLayout createLanguageAssignmentLayout() {

		Button saveButton = new Button("Save");
		saveButton.setEnabled(false);

		H4 h4 = new H4("Dont forget to save languages!");
		h4.getStyle().set("color", "red");
		h4.setVisible(false);

		projectComboBox.addValueChangeListener(event -> {
			refreshLanguageComboBox();
			saveButton.setEnabled(false);
			h4.setVisible(false);
		});

		languageMultiSelectComboBox.addSelectionListener(aMultiSelectionEvent -> {
			BCcProjectEO projectEO = projectComboBox.getValue();
			if (projectEO != null
					&& (!aMultiSelectionEvent.getAddedSelection().isEmpty() || !aMultiSelectionEvent.getRemovedSelection().isEmpty())) {
				saveButton.setEnabled(true);
				h4.setVisible(true);
			}
			else {
				saveButton.setEnabled(false);
				h4.setVisible(false);
			}
		});

		saveButton.addClickListener(event -> {
			BCcProjectEO projectEO = projectComboBox.getValue();
			saveButton.setEnabled(false);
			h4.setVisible(false);
			if (projectEO != null) {

				Map<Long, BCcLanguageEO> languageMap = dataService.findLanguageList(projectEO.getProjectId()).stream()
						.collect(Collectors.toMap(BCcLanguageEO::getLanguageId, a -> a));

				for (BCcLanguageEO newLanguageEO : languageMultiSelectComboBox.getSelectedItems()) {
					BCcLanguageEO languageEO = languageMap.remove(newLanguageEO.getLanguageId());
					if (languageEO == null) {
						BCcLanguageProjectEO languageProjectEO = new BCcLanguageProjectEO();
						languageProjectEO.setLanguage(newLanguageEO);
						languageProjectEO.setProject(projectEO);
						dataService.createLanguageProject(languageProjectEO);
					}
				}

				if (!languageMap.isEmpty()) {
					ConfirmDialog deletionDialog = new ConfirmDialog();
					deletionDialog.setHeader("Confirm Deletion");

					String languagesText = languageMap.values().stream()
							.map(l -> l.getLanguageName() + " (" + l.getLanguageIso() + ")")
							.collect(Collectors.joining(", "));

					deletionDialog.setText(
							"Are you sure you want to remove languages: " + languagesText +
									" from the project: " + projectEO.getProjectName() + " ? It will delete all translations!");

					deletionDialog.setCancelable(true);
					deletionDialog.setConfirmText("Delete");
					deletionDialog.setConfirmButtonTheme("error primary");
					deletionDialog.addConfirmListener(e -> {
						for (BCcLanguageEO languageEO : languageMap.values()) {
							BCcLanguageProjectEO languageProjectEO = dataService.findLanguageProject(
									projectEO.getProjectId(), languageEO.getLanguageId());
							if (languageProjectEO != null) {
								dataService.deleteLanguageProject(languageProjectEO.getLanguageProjectId());
							}
						}
						refreshLanguageComboBox();
						saveButton.setEnabled(false);
						h4.setVisible(false);
					});
					deletionDialog.open();
				}
			}

		});

		languageMultiSelectComboBox.setMinWidth("350px");

		HorizontalLayout projectLanguageEditLayout = new HorizontalLayout(languageMultiSelectComboBox, saveButton);
		projectLanguageEditLayout.setAlignItems(Alignment.END);
		projectLanguageEditLayout.setSpacing(false);
		projectLanguageEditLayout.setPadding(false);

		VerticalLayout layout = new VerticalLayout(h4, projectLanguageEditLayout);
		layout.setWidth("40%");
		layout.setMinWidth("400px");
		layout.setFlexGrow(1);
		return layout;
	}

	//----------------------------------------USER ADD, DELETE---------------------------------------------------
	private VerticalLayout createUserAssignmentLayout() {

		Button saveButton = new Button("Save");
		saveButton.setEnabled(false);

		H4 h4 = new H4("Dont forget to save users!");
		h4.getStyle().set("color", "red");
		h4.setVisible(false);

		projectComboBox.addValueChangeListener(event -> {
			refreshUserComboBox();
			saveButton.setEnabled(false);
			h4.setVisible(false);
		});

		userMultiSelectComboBox.addSelectionListener(aMultiSelectionEvent -> {
			BCcProjectEO projectEO = projectComboBox.getValue();
			if (projectEO != null
					&& (!aMultiSelectionEvent.getAddedSelection().isEmpty() || !aMultiSelectionEvent.getRemovedSelection().isEmpty())) {
				saveButton.setEnabled(true);
				h4.setVisible(true);
			}
			else {
				saveButton.setEnabled(false);
				h4.setVisible(false);
			}
		});

		saveButton.addClickListener(event -> {
			BCcProjectEO projectEO = projectComboBox.getValue();
			saveButton.setEnabled(false);
			h4.setVisible(false);
			if (projectEO != null) {

				Map<Long, BCcUserEO> userMap = dataService.findUserList(projectEO.getProjectId()).stream()
						.collect(Collectors.toMap(BCcUserEO::getUserId, a -> a));

				for (BCcUserEO newUserEO : userMultiSelectComboBox.getSelectedItems()) {
					BCcUserEO userEO = userMap.remove(newUserEO.getUserId());
					if (userEO == null) {
						BCcUserProjectEO userProjectEO = new BCcUserProjectEO();
						userProjectEO.setUser(newUserEO);
						userProjectEO.setProject(projectEO);
						dataService.createUserProject(userProjectEO);
					}
				}

				if (!userMap.isEmpty()) {
					ConfirmDialog deletionDialog = new ConfirmDialog();
					deletionDialog.setHeader("Confirm Deletion");

					String usersText = userMap.values().stream()
							.map(l -> l.getUserName() + " (" + l.getEmail() + ")")
							.collect(Collectors.joining(", "));

					deletionDialog.setText(
							"Are you sure you want to remove users: " + usersText +
									" from the project: " + projectEO.getProjectName() + " ?");

					deletionDialog.setCancelable(true);
					deletionDialog.setConfirmText("Delete");
					deletionDialog.setConfirmButtonTheme("error primary");
					deletionDialog.addConfirmListener(e -> {
						for (BCcUserEO userEO : userMap.values()) {
							BCcUserProjectEO userProjectEO = dataService.findUserProject(
									projectEO.getProjectId(), userEO.getUserId());
							if (userProjectEO != null) {
								dataService.deleteUserProject(userProjectEO.getUserProjectId());
							}
						}
						refreshUserComboBox();
						saveButton.setEnabled(false);
						h4.setVisible(false);
					});
					deletionDialog.open();
				}
			}

		});

		userMultiSelectComboBox.setMinWidth("350px");

		HorizontalLayout projectUserEditLayout = new HorizontalLayout(userMultiSelectComboBox, saveButton);
		projectUserEditLayout.setAlignItems(Alignment.END);
		projectUserEditLayout.setSpacing(false);
		projectUserEditLayout.setPadding(false);

		VerticalLayout layout = new VerticalLayout(h4, projectUserEditLayout);
		layout.setWidth("40%");
		layout.setMinWidth("400px");
		layout.setFlexGrow(1);
		return layout;
	}

	//---------------------------------------PROJECT VERSION SEPARATOR----------------------------------------------------
	private Component createProjectVersionManagementSeparator() {

		Span title = new Span("PROJECT VERSION");
		title.setMinWidth("150px");
		Style style = title.getStyle();
		style.setPadding("0 10px");
		style.setFontWeight("bold");

		Hr rightHr = new Hr();
		rightHr.setWidth("90%");

		HorizontalLayout separatorLayout = new HorizontalLayout(title, rightHr);
		separatorLayout.setWidthFull();
		separatorLayout.setAlignItems(Alignment.CENTER);
		separatorLayout.setJustifyContentMode(JustifyContentMode.CENTER);

		return separatorLayout;
	}

//----------------------------------------PROJECT VERSION - SELECT, NAME EDIT, CREATE, DELETE---------------------------------------------------

	private Component createProjectVersionManagementLayout() {

		Component projectVersionNameEditLayout = createProjectVersionNameEditLayout();
		Component childProjectVersionCreationButton = createChildProjectVersionCreationButton();
		Component projectVersionDeletionButton = createProjectVersionDeletionButton();

		projectComboBox.addValueChangeListener(event -> refreshProjectVersionComboBox());

		FlexLayout projectVersionBasicManagementLayout = new FlexLayout();
		projectVersionBasicManagementLayout.addClassName("responsive-layout");
		projectVersionBasicManagementLayout.add(
				projectVersionComboBox,
				projectVersionNameEditLayout,
				childProjectVersionCreationButton,
				projectVersionDeletionButton);
		projectVersionBasicManagementLayout.setAlignItems(Alignment.END);
		projectVersionBasicManagementLayout.setWidthFull();
		projectVersionBasicManagementLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

		return projectVersionBasicManagementLayout;
	}

	private Component createProjectVersionNameEditLayout() {
		TextField projectVersionNameField = new TextField("Edit project version name");
		projectVersionNameField.setPlaceholder("Enter new project version name");
		projectVersionNameField.setReadOnly(true);

		Button saveButton = new Button("Save");
		saveButton.setEnabled(false);

		projectVersionComboBox.addValueChangeListener(event -> {
			BCcProjectVersionEO projectVersionEO = event.getValue();
			boolean hasProjectVersion = projectVersionEO != null;
			projectVersionNameField.setReadOnly(!hasProjectVersion);
			saveButton.setEnabled(hasProjectVersion);
			projectVersionNameField.setValue(hasProjectVersion ? projectVersionEO.getProjectVersionName() : "");
		});

		saveButton.addClickListener(event -> {
			BCcProjectVersionEO projectVersionEO = projectVersionComboBox.getValue();
			if (projectVersionEO != null) {
				projectVersionEO.setProjectVersionName(projectVersionNameField.getValue().trim());
				dataService.updateProjectVersion(projectVersionEO);
				refreshProjectVersionComboBox();
			}
		});

		HorizontalLayout projectVersionNameEditLayout = new HorizontalLayout(projectVersionNameField, saveButton);
		projectVersionNameEditLayout.setAlignItems(Alignment.END);
		projectVersionNameEditLayout.setSpacing(false);
		projectVersionNameEditLayout.setPadding(false);

		return projectVersionNameEditLayout;
	}

	private Component createChildProjectVersionCreationButton() {

		Button projectVersionCreationButton = new Button("Create child Project Version");
		projectVersionCreationButton.setEnabled(false);

		projectVersionComboBox.addValueChangeListener(event -> {
			BCcProjectVersionEO projectVersionEO = event.getValue();
			boolean hasProjectVersion = projectVersionEO != null;
			projectVersionCreationButton.setEnabled(hasProjectVersion);
		});

		projectVersionCreationButton.addClickListener(e -> {

			if (projectComboBox.getValue() != null && projectVersionComboBox.getValue() != null) {

				Dialog creationDialog = new Dialog();

				TextField projectVersionNameField = new TextField("Project Version Name");
				projectVersionNameField.setWidth("100%");

				Button confirmButton = new Button("Create", event -> {
					String projectVersionName = projectVersionNameField.getValue().trim();
					if (!projectVersionName.isEmpty()) {
						createProjectVersion(projectVersionName);
						refreshProjectVersionComboBox();
						projectVersionNameField.clear();
						creationDialog.close();
						Notification.show("Project Version \"" + projectVersionName + "\" created.");
					}
				});

				Button cancelButton = new Button("Cancel", event -> creationDialog.close());

				HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
				buttonLayout.setWidthFull();
				buttonLayout.setAlignItems(Alignment.CENTER);
				buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

				VerticalLayout dialogLayout = new VerticalLayout();
				dialogLayout.add(
						new H3("Create Project Version"),
						new H4("Parent project version (branch) will be: " + projectVersionComboBox.getValue().getProjectVersionName()),
						projectVersionNameField,
						buttonLayout);

				creationDialog.add(dialogLayout);
				creationDialog.open();
			}
		});

		return projectVersionCreationButton;
	}

	private void createProjectVersion(String aProjectVersionName) {
		BCcProjectVersionEO projectVersionEO = new BCcProjectVersionEO();
		projectVersionEO.setProject(projectComboBox.getValue());
		projectVersionEO.setProjectVersionName(aProjectVersionName);
		projectVersionEO.setParentProjectVersion(projectVersionComboBox.getValue());
		dataService.createProjectVersion(projectVersionEO);
	}

	private Component createProjectVersionDeletionButton() {

		Button deleteButton = new Button("Delete Project Version");
		deleteButton.setEnabled(false);

		projectVersionComboBox.addValueChangeListener(event -> {
			BCcProjectVersionEO projectVersionEO = event.getValue();
			boolean hasProjectVersion = projectVersionEO != null;
			deleteButton.setEnabled(hasProjectVersion);
		});

		deleteButton.addClickListener(e -> {
			BCcProjectEO projectEO = projectComboBox.getValue();
			BCcProjectVersionEO projectVersionEO = projectVersionComboBox.getValue();
			if (projectEO != null && projectVersionEO != null) {
				if (projectVersionEO.getParentProjectVersion() == null) {
					Notification.show("Cannot delete the main project version!", 3000, Notification.Position.TOP_CENTER);
				}
				else {
					ConfirmDialog deletionDialog = new ConfirmDialog();
					deletionDialog.setHeader("Confirm Deletion");
					deletionDialog.setText("Are you sure you want to delete the project version: " + projectVersionEO.getProjectVersionName() + "? "
							+ "It will delete all translations!");

					deletionDialog.setCancelable(true);
					deletionDialog.setConfirmText("Delete");
					deletionDialog.setConfirmButtonTheme("error primary");
					deletionDialog.addConfirmListener(event -> {
						String projectVersionName = projectVersionEO.getProjectVersionName();
						dataService.deleteProjectVersion(projectVersionEO.getProjectVersionId());
						Notification.show("Project Version \"" + projectVersionName + "\" deleted.");
						refreshProjectVersionComboBox();
					});
					deletionDialog.open();
				}
			}
		});

		return deleteButton;
	}
}
