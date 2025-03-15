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
 * Title: {@link BCcProjectVersionEO}
 * </p>
 * <p>
 * Description: Project version
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 02.03.2025 16:23
 */
@Entity
@Table(name = "project_version")
public class BCcProjectVersionEO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_version_idgenerator")
	@SequenceGenerator(name = "project_version_idgenerator", initialValue = 1000, allocationSize = 1)
	@Column(name = "project_version_id")
	private Long projectVersionId;

	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private BCcProjectEO project;

	@ManyToOne
	@JoinColumn(name = "parent_project_version_id")
	private BCcProjectVersionEO parentProjectVersion;

	@Column(name = "project_version_name", nullable = false)
	private String projectVersionName;

	@Column(name = "last_merge_date")
	private LocalDateTime lastMergeDate;

	@Column(name = "create_date")
	private LocalDateTime createDate;

	@Column(name = "change_date")
	private LocalDateTime changeDate;

	@OneToMany(mappedBy = "parentProjectVersion")
	private List<BCcProjectVersionEO> childProjectVersionList = new ArrayList<>();

	@OneToMany(mappedBy = "projectVersion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BCcProjectVersionLocalizationEO> projectVersionLocalizationList = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		lastMergeDate = LocalDateTime.now();
		createDate = LocalDateTime.now();
		changeDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		changeDate = LocalDateTime.now();
	}

	/**
	 * @return the projectVersionId
	 */
	public Long getProjectVersionId() {
		return projectVersionId;
	}

	/**
	 * @return the project
	 */
	public BCcProjectEO getProject() {
		return project;
	}

	/**
	 * @param aProject the project
	 */
	public void setProject(BCcProjectEO aProject) {
		project = aProject;
	}

	/**
	 * @return the parentProjectVersion
	 */
	public BCcProjectVersionEO getParentProjectVersion() {
		return parentProjectVersion;
	}

	/**
	 * @param aParentProjectVersion the parentProjectVersion
	 */
	public void setParentProjectVersion(BCcProjectVersionEO aParentProjectVersion) {
		parentProjectVersion = aParentProjectVersion;
	}

	/**
	 * @return the projectVersionName
	 */
	public String getProjectVersionName() {
		return projectVersionName;
	}

	/**
	 * @param aProjectVersionName the projectVersionName
	 */
	public void setProjectVersionName(String aProjectVersionName) {
		projectVersionName = aProjectVersionName;
	}

	/**
	 * @return the lastMergeDate
	 */
	public LocalDateTime getLastMergeDate() {
		return lastMergeDate;
	}

	/**
	 * @param aLastMergeDate the lastMergeDate
	 */
	public void setLastMergeDate(LocalDateTime aLastMergeDate) {
		lastMergeDate = aLastMergeDate;
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
