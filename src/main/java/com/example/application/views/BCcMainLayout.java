package com.example.application.views;

import java.util.List;
import java.util.Optional;

import com.example.application.data.BCcUserEO;
import com.example.application.security.BCcAuthenticatedUser;
import com.example.application.security.BCcDataService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * <p>
 * Title: {@link BCcMainLayout}
 * </p>
 * <p>
 * Description: The main view is a top-level placeholder for other views.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 01.03.2025 21:33
 */
@Layout
@AnonymousAllowed
public class BCcMainLayout extends AppLayout {

	private H1 viewTitle;

	private BCcAuthenticatedUser authenticatedUser;
	private AccessAnnotationChecker accessChecker;
	private final BCcDataService dataService;

	public BCcMainLayout(BCcAuthenticatedUser aAuthenticatedUser, AccessAnnotationChecker aAccessChecker, BCcDataService aDataService) {
		this.authenticatedUser = aAuthenticatedUser;
		this.accessChecker = aAccessChecker;
		this.dataService = aDataService;

		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
	}

	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.setAriaLabel("Menu toggle");

		viewTitle = new H1();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

		addToNavbar(true, toggle, viewTitle);
	}

	private void addDrawerContent() {
		Span appName = new Span("LocalizationManager");
		appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
		Header header = new Header(appName);

		Scroller scroller = new Scroller(createNavigation());

		addToDrawer(header, scroller, createFooter());
	}

	private SideNav createNavigation() {
		SideNav nav = new SideNav();

		List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
		menuEntries.forEach(entry -> {
			if (entry.icon() != null) {
				nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
			}
			else {
				nav.addItem(new SideNavItem(entry.title(), entry.path()));
			}
		});

		return nav;
	}

	private Footer createFooter() {
		Footer layout = new Footer();

		Optional<BCcUserEO> maybeUser = authenticatedUser.get();
		if (maybeUser.isPresent()) {
			BCcUserEO user = maybeUser.get();

			MenuBar userMenu = new MenuBar();
			userMenu.setThemeName("tertiary-inline contrast");

			MenuItem userName = userMenu.addItem("");
			Div div = new Div();
			div.add(user.getUserName());
			div.add(new Icon("lumo", "dropdown"));
			div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
			userName.add(div);
			userName.getSubMenu().addItem("Sign out", e -> {
				authenticatedUser.logout();
			});

			layout.add(userMenu);
		}
		else {
			Anchor loginLink = new Anchor("login", "Sign in");
			layout.add(loginLink);
		}

		return layout;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		viewTitle.setText(getCurrentPageTitle());
	}

	private String getCurrentPageTitle() {
		return MenuConfiguration.getPageHeader(getContent()).orElse("");
	}
}
