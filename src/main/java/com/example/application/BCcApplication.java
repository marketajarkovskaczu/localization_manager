package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

/**
 * <p>
 * Title: {@link BCcApplication}
 * </p>
 * <p>
 * Description: The entry point of the Spring Boot application.
 * Use the @PWA annotation make the application installable on phones, tablets and some desktop browsers.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 01.03.2025 21:34
 */
@SpringBootApplication
@EnableAsync  // Povolí asynchronní metody v aplikaci
@Theme(value = "localizationmanager")
public class BCcApplication implements AppShellConfigurator {

	public static void main(String[] aArgs) {
		SpringApplication.run(BCcApplication.class, aArgs);
	}
}
