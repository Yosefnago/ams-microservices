package com.ams.ui.views;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.ams.dtos.loginDto.LoginRequest;
import com.ams.dtos.loginDto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * A Vaadin view for user login.
 * This view displays a login form and manages user authentication by sending login details to a server-side endpoint.
 *
 * @Route Marks this view as accessible at the 'login' URI.
 * @PageTitle Sets the browser tab title when this view is active.
 * @AnonymousAllowed Indicates that this view does not require the user to be authenticated.
 */
@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private LoginForm loginForm;
    private final RestTemplate restTemplate;

    /**
     * Constructs the login view, setting up the UI components for user interaction.
     */
    public LoginView(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        setSizeFull();
        loginForm = new LoginForm();
        Anchor registerLink = new Anchor("register", "Register");
        Image logo = new Image("logoAuth.png","לוגו");
        logo.setWidth("120px");
        logo.setHeight("110px");

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("justify-content", "center");
        loginForm.setForgotPasswordButtonVisible(true);

        loginForm.addLoginListener(a -> authenticate(a.getUsername(), a.getPassword()));

        add(logo,loginForm, registerLink);
    }

    /**
     * Authenticates a user by sending their username and password to the backend.
     * Upon successful authentication, navigates to the home view and stores the JWT in the session.
     *
     * @param username The user's username.
     * @param password The user's password.
     */
    private void authenticate(String username, String password) {
        loginForm.setEnabled(false);
        try {
            LoginRequest request = new LoginRequest(username, password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(request, headers);

            ResponseEntity<LoginResponse> response =
                    restTemplate.postForEntity("http://localhost:8080/auth/login", requestEntity, LoginResponse.class);

            if (response.getBody() != null && response.getBody().success()) {

                String token = response.getBody().token();
                VaadinSession.getCurrent().setAttribute("jwt", token);
                UI.getCurrent().navigate("dashboard");
                Notification
                        .show("Welcome " + username)
                        .setPosition(Notification.Position.MIDDLE);
            } else {
                Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
                loginForm.setError(true);
            }
        } catch (HttpClientErrorException e) {
            loginForm.setError(true);
        }finally {
            loginForm.setEnabled(true);
        }
    }
}



