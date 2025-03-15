package com.example.application.views;

import com.example.application.security.BCcAuthenticatedUser;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
/**
 * <p>
 * Title: {@link BCcLoginView}
 * </p>
 * <p>
 * Description: Login view
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 Baader Computer
 * </p>
 * <p>
 * Company: Baader Computer
 * </p>
 *
 * @author marketa.jarkovska
 * @date 01.03.2025 21:32
 */
@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class BCcLoginView extends LoginOverlay implements BeforeEnterObserver {

    private final BCcAuthenticatedUser authenticatedUser;

    public BCcLoginView(BCcAuthenticatedUser aAuthenticatedUser) {
        this.authenticatedUser = aAuthenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Localization\nManager");
//        i18n.getHeader().setDescription("Login using user/user or admin/admin");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent aEvent) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            aEvent.forwardTo("");
        }

        setError(aEvent.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
