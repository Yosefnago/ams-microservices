package com.ams.ui.views;



import com.ams.dtos.registerDto.RegisterRequest;
import com.ams.dtos.registerDto.RegisterResponse;
import com.ams.ui.api.clientside.RegisterService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;


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

    private final RegisterService registerService;
    private Button saveButton;
    private Button cancelButton;
    private TextField firstName;
    private TextField lastName;
    private TextField username;
    private EmailField email;
    private PasswordField password;
    private PasswordField confirmPassword;
    private TextField phone;
    private TextField taxId;
    private TextField businessName;
    private Div div;
    private FormLayout formLayout;


    /**
     * Constructs the registration view with an injected {@link RestTemplate} for HTTP communication.
     *
     * @param restTemplate the {@code RestTemplate} used for sending registration requests to the server
     */
    public RegisterView(@Autowired RegisterService registerService) {
        this.registerService = registerService;
        createRegisterView();
    }

    /**
     * Initializes and builds the registration form UI components,
     * including fields for user details, password validation, and control buttons.
     * Includes Save and Cancel buttons.
     * Save triggers the registration logic, while Cancel navigates back to the login screen.
     *
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

        formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.setHeightFull();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        username = new TextField("Username");
        email = new EmailField("Email");
        phone = new TextField("Phone");
        taxId = new TextField("ID");
        password = new PasswordField("Password");
        confirmPassword = new PasswordField("Confirm Password");
        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        email.setAutocomplete(Autocomplete.ON);
        email.getDefaultValidator();
        phone.setManualValidation(true);
        phone.setMinLength(10);
        phone.setMaxLength(10);
        taxId.setMinLength(9);
        taxId.setMaxLength(9);
        businessName = new TextField("Business Name");

        formLayout.add(firstName,1);
        formLayout.add(lastName,1);
        formLayout.add(username,1);
        formLayout.add(email,1);
        formLayout.add(phone,2);
        formLayout.add(taxId,2);
        formLayout.add(businessName,2);
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

        RegisterRequest registerRequest = new RegisterRequest(
                firstName.getValue(),
                lastName.getValue(),
                username.getValue(),
                email.getValue(),
                password.getValue(),
                phone.getValue(),
                taxId.getValue(),
                businessName.getValue()
        );

        RegisterResponse response = registerService.register(registerRequest);

        if (response != null && response.success()) {
            Notification.show(response.message(), 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate(LoginView.class);
        } else {
            Notification.show(response.message(), 3000, Notification.Position.MIDDLE);
        }

    }

}
