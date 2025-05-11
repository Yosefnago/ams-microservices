package com.ams.ui.views;


import com.ams.dtos.loginDto.ClientLoginRequest;
import com.ams.dtos.loginDto.ClientLoginResponse;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.ams.dtos.loginDto.LoginRequest;
import com.ams.dtos.loginDto.LoginResponse;
import com.vaadin.flow.theme.lumo.LumoUtility;
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

    private final RestTemplate restTemplate;
    private LoginOverlay loginOverlay;
    private String selectedRole = "ACCOUNTANT";
    /**
     * Constructs the login view, setting up the UI components for user interaction.
     */
    public LoginView(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        setSizeFull();
        loginOverlay = new LoginOverlay();
        loginOverlay.setTitle("Ams");
        loginOverlay.setDescription("Accountant Management System");

        loginOverlay.addLoginListener(a -> authenticate(a.getUsername(), a.getPassword(),selectedRole));
        loginOverlay.setOpened(true);

        Button accountantButton = new Button("רואה חשבון");
        Button clientButton = new Button("לקוח");

        accountantButton.addClickListener(e -> {
            selectedRole = "ACCOUNTANT";
            highlightSelectedButton(accountantButton, clientButton);
            loginOverlay.setTitle("Ams - Accountant");
        });

        clientButton.addClickListener(e -> {
            selectedRole = "CLIENT";
            highlightSelectedButton(clientButton, accountantButton);
            loginOverlay.setTitle("Ams - Client");
        });

        accountantButton.getStyle().set("font-weight", "bold");
        clientButton.getStyle().set("font-weight", "bold");

        HorizontalLayout buttons = new HorizontalLayout(accountantButton, clientButton);
        buttons.setSpacing(true);

        buttons.getStyle()
                .set("position", "fixed")
                .set("top", "15px")
                .set("left", "50%")
                .set("transform", "translateX(-50%)")
                .set("z-index", "10000");

        Anchor registerLink = new Anchor("/register", "אין לך חשבון? הירשם כאן");
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

        add(buttons, registerLink, loginOverlay);

    }

    /**
     * Authenticates a user by sending their username and password to the backend.
     * Upon successful authentication, navigates to the home view and stores the JWT in the session.
     *
     * @param username The user's username.
     * @param password The user's password.
     */
    private void authenticate(String username, String password,String role) {

        loginOverlay.setEnabled(false);
        if (role.equals("ACCOUNTANT")){
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
                    loginOverlay.setOpened(false);
                    UI.getCurrent().navigate(DashboardView.class);
                    Notification
                            .show("Welcome " + username)
                            .setPosition(Notification.Position.MIDDLE);
                } else {
                    Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
                    loginOverlay.setError(true);
                }
            } catch (HttpClientErrorException e) {
                loginOverlay.setError(true);
            }finally {
                loginOverlay.setEnabled(true);
            }
        } else if (role.equals("CLIENT")) {
            try {
                ClientLoginRequest request = new ClientLoginRequest(username, password);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<ClientLoginRequest> requestEntity = new HttpEntity<>(request, headers);

                ResponseEntity<ClientLoginResponse> response =
                        restTemplate.postForEntity("http://localhost:8080/client/login", requestEntity, ClientLoginResponse.class);

                if (response.getBody() != null && response.getBody().success()) {

                    String token = response.getBody().token();
                    VaadinSession.getCurrent().setAttribute("jwt", token);
                    System.out.println("Saved "+token);

                    System.out.println(response.getBody());

                    loginOverlay.setOpened(false);
                    UI.getCurrent().navigate("case/" + response.getBody().clientId());
                    Notification
                            .show("Welcome " + username)
                            .setPosition(Notification.Position.MIDDLE);
                } else {
                    Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
                    loginOverlay.setError(true);
                }
            } catch (HttpClientErrorException e) {
                loginOverlay.setError(true);
            }finally {
                loginOverlay.setEnabled(true);
            }
        }

    }
    private void highlightSelectedButton(Button selected, Button other) {
        selected.getStyle().set("background-color", "#007bff").set("color", "white");
        other.getStyle().remove("background-color").remove("color");
    }
}



