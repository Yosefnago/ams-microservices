package com.ams.ui.views;



import com.ams.dtos.loginDto.LoginRequest;
import com.ams.dtos.registerDto.RegisterRequest;
import com.ams.dtos.registerDto.RegisterResponse;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;


/**
 * {@code RegisterView} is a Vaadin-based view for registering new users to the AMS system.
 * <p>
 * This view displays a form where users can provide personal information and account credentials
 * in order to create a new accountant user account. Upon successful registration, the user is redirected
 * to the login view.
 * </p>
 *
 * <p>
 * The registration form includes validation for required fields and basic input checks (e.g., email and phone).
 * Registration data is submitted via HTTP POST to the backend authentication controller.
 * </p>
 *
 * <p><b>Route:</b> {@code /register}</p>
 * <p><b>Access:</b> Anonymous (no authentication required)</p>
 *
 * @author Yosef Nago
 */
@Route("register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private Button saveButton;
    private Button cancelButton;
    private TextField firstName;
    private TextField lastName;
    private TextField username;
    private EmailField email;
    private PasswordField password;
    private PasswordField confirmPassword;
    private TextField phone;
    private Div div;
    String msg;

    /**
     * Constructs the registration view with an injected {@link RestTemplate} for HTTP communication.
     *
     * @param restTemplate the {@code RestTemplate} used for sending registration requests to the server
     */
    public RegisterView(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        createRegisterView();
    }

    /**
     * Initializes and builds the registration form UI components,
     * including fields for user details, password validation, and control buttons.
     * <p>
     * The form includes:
     * <ul>
     *     <li>First name</li>
     *     <li>Last name</li>
     *     <li>Username</li>
     *     <li>Email</li>
     *     <li>Phone</li>
     *     <li>Password</li>
     *     <li>Confirm password</li>
     * </ul>
     * </p>
     * <p>
     * Includes Save and Cancel buttons.
     * Save triggers the registration logic, while Cancel navigates back to the login screen.
     * </p>
     */
    public void createRegisterView(){

        Header header = new Header();
        header.setText("Register");
        header.getStyle().set("font-size", "24px");
        add(header);

        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("justify-content", "center");

        div = new Div();
        div.setHeight("600px");
        div.setWidth("700px");

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.setHeightFull();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        username = new TextField("Username");
        email = new EmailField("Email");
        phone = new TextField("Phone");
        password = new PasswordField("Password");
        confirmPassword = new PasswordField("Confirm Password");
        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        email.setAutocomplete(Autocomplete.ON);
        email.getDefaultValidator();
        phone.setManualValidation(true);
        phone.setMinLength(10);
        phone.setMaxLength(10);

        formLayout.add(firstName,1);
        formLayout.add(lastName,1);
        formLayout.add(username,1);
        formLayout.add(email,1);
        formLayout.add(phone,2);
        formLayout.add(password,1);
        formLayout.add(confirmPassword,1);
        formLayout.add(saveButton,2);
        formLayout.add(cancelButton,2);

        saveButton.addClickListener(e -> {
            save();
        });
        cancelButton.addClickListener(e -> {
            UI.getCurrent().navigate(LoginView.class);
        });

        div.add(formLayout);
        add(div);
    }

    /**
     * Handles the form submission to register a new user.
     * <p>
     * This method constructs a {@link RegisterRequest} object from form values
     * and sends it to the backend via a POST request.
     * On success, it shows a notification and navigates to the login page.
     * On error, it displays an error message from the response.
     * </p>
     */
    private void save()  {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RegisterRequest registerRequest = new RegisterRequest(
                    firstName.getValue(),
                    lastName.getValue(),
                    username.getValue(),
                    email.getValue(),
                    password.getValue(),
                    phone.getValue()
            );

            HttpEntity<?> requestEntity = new HttpEntity<>(registerRequest, headers);
            String url = "http://localhost:8080/auth/register";

            ResponseEntity<RegisterResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, requestEntity, RegisterResponse.class);
            msg = response.getBody().message();

            if (response.getBody() != null && response.getBody().success()) {
                Notification.show(msg, 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate(LoginView.class);
            }
        }catch (HttpClientErrorException e){
            Notification.show(msg, 3000, Notification.Position.MIDDLE);
        }
    }
}
