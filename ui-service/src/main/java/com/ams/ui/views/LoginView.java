package com.ams.ui.views;


import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.loginDto.ClientLoginRequest;
import com.ams.dtos.loginDto.ClientLoginResponse;
import com.ams.ui.api.clientside.LoginService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.ams.dtos.loginDto.LoginRequest;
import com.ams.dtos.loginDto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User logi view.
 * This view displays a login form and manages user authentication by sending login details to a server-side endpoint.
 *
 * @Route Marks this view as accessible at the 'login' URI.
 *
 * @AnonymousAllowed All roles allowed to route to login view.
 */
@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final JwtUtil jwtUtil;
    private LoginService loginService;
    private LoginOverlay loginOverlay;
    private String selectedRole = "";
    private Anchor registerLink;


    /**
     * Constructs the login view, setting up the UI components for user interaction.
     */
    public LoginView(@Autowired LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;

        setSizeFull();
        loginOverlay = new LoginOverlay();
        loginOverlay.setTitle("Ams");
        loginOverlay.setDescription("Accountant Management System");

        loginOverlay.addLoginListener(a -> authenticate(a.getUsername(), a.getPassword()));
        loginOverlay.setOpened(true);

        componentsStyles();

        add( registerLink, loginOverlay);

    }
    /**
     *Add listeners to buttons and component styles.
     */
    private void componentsStyles() {



        registerLink = new Anchor("/register", "אין לך חשבון? הירשם כאן");
        registerLink.getStyle()
                .set("position", "fixed")
                .set("bottom", "20px")
                .set("left", "50%")
                .set("transform", "translateX(-50%)")
                .set("z-index", "9999")
                .set("padding", "5px")
                .set("border-radius", "5px");


        registerLink.getElement().addEventListener("click", e -> {
            loginOverlay.setOpened(false);
            UI.getCurrent().navigate(RegisterView.class);
        });


    }

    /**
     * Authenticates a user by sending their username and password to the backend.
     * Upon successful authentication, navigates to the home view and stores the JWT in the session.
     *
     * @param username The user's username.
     * @param password The user's password.
     */
    private void authenticate(String username, String password) {

        loginOverlay.setEnabled(false);


        LoginResponse response = loginService.loginAccountant(new LoginRequest(username, password));

        if (response.message() != null && response.success()) {

            String token = response.token();

            VaadinSession.getCurrent().setAttribute("jwt", token);
            loginOverlay.setOpened(false);


            String clientId = jwtUtil.extractClientId(token);


            UI.getCurrent().navigate(ClientCaseView.class,
                    new RouteParameters("clientId", clientId));
            Notification
                    .show("ברוך הבא " + username)
                    .setPosition(Notification.Position.MIDDLE);
        } else {
            Notification.show("שם משתמש או סיסמא שגויים", 3000, Notification.Position.MIDDLE);
            loginOverlay.setError(true);
            loginOverlay.setEnabled(true);
        }


    }

}



