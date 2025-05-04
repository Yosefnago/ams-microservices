package com.ams.ui.views;

import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.clientDto.ClientGridDto;
import com.ams.dtos.clientDto.CreateClientRequest;
import com.ams.dtos.clientDto.CreateClientResponse;
import com.ams.dtos.clientDto.LoadClientResponse;
import com.ams.ui.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * {@code ClientsView} is the main UI view for managing client records in the accounting management system.
 * <p>
 * This view allows the accountant to:
 * <ul>
 *   <li>View all clients in a sortable grid</li>
 *   <li>Create a new client via a tab-based dialog with form validation</li>
 *   <li>Delete an existing client after confirmation</li>
 *   <li>Navigate to a detailed client case view</li>
 * </ul>
 *
 * <p><b>Security:</b> JWT token validation is performed in {@code beforeEnter()} to restrict access to authenticated users.</p>
 *
 * <p><b>REST Integration:</b> Uses {@link RestTemplate} to communicate with the backend via {@code gateway-service}.</p>
 *
 * <p><b>UI Framework:</b> Built with Vaadin components including {@link Grid}, {@link Dialog}, {@link Tabs}, {@link ComboBox}, and {@link Notification}.</p>
 *
 * <p><b>Route:</b> {@code /clients} – Secured via {@link com.ams.ui.layouts.MainLayout}</p>
 *
 * @author Yosef
 */
@Route(value = "clients", layout = MainLayout.class)
@PageTitle("Clients")
public class ClientsView extends VerticalLayout implements BeforeEnterObserver {

    private Tabs tabs;
    private Dialog dialog;
    private EmailField emailField;
    private TextField phoneField,contactPhoneField;
    private TextField addressField;
    private TextField zipField;
    private TextField businessNameField;
    private TextField clientTypeField;
    private TextField taxIdField;
    private TextField bankOwnerNameField;
    private TextField bankBranchField;
    private TextField bankNumberField;
    private ComboBox<String> bankNameField;
    private Button saveButton,cancelButton;
    private Tab clientDetailsTab,financialDetailsTab,documentDetailsTab;
    private ComboBox<String> clientTypeComboBox;
    private RestTemplate restTemplate;
    private final Grid<ClientGridDto> grid = new Grid<>();
    private String clientIdSelected;
    private final JwtUtil jwtUtil;
    private String message;

    @Autowired
    public ClientsView(RestTemplate restTemplate, JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        setSizeFull();
        clientContent();
    }
    /**
     * Initializes the main content of the view, including:
     * <ul>
     *   <li>Buttons for creating and deleting clients</li>
     *   <li>A Vaadin Grid displaying basic client information</li>
     *   <li>Action buttons per row (view/edit)</li>
     * </ul>
     */
    private void clientContent() {
        Button createClientButton = new Button("לקוח חדש");
        createClientButton.addClickListener(e -> {
           newClientDialog();
        });

        Button deleteClientButton = new Button("מחק לקוח");
        deleteClientButton.addClickListener(event ->{
           deleteClient();
        });

        createClientButton.addClassNames(LumoUtility.BoxShadow.XSMALL,LumoUtility.Display.INLINE_FLEX,LumoUtility.TextColor.PRIMARY);
        deleteClientButton.getElement().getStyle().set("color", "var(--lumo-error-color)");

        HorizontalLayout buttonsLayout = new HorizontalLayout( deleteClientButton,createClientButton);
        buttonsLayout.addClassNames(LumoUtility.Gap.MEDIUM,LumoUtility.AlignSelf.END);

        deleteClientButton.setVisible(false);

        grid.addColumn(ClientGridDto::businessName).setHeader("שם לקוח");
        grid.addColumn(ClientGridDto::clientId).setHeader("ח.פ / ת.ז");
        grid.addColumn(ClientGridDto::phone).setHeader("טלפון");
        grid.addColumn(ClientGridDto::email).setHeader("אימייל");

        grid.getElement().setAttribute("dir", "rtl");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.getColumns().get(0).setSortable(true);
        grid.getColumns().get(1).setSortable(true);
        grid.getColumns().get(2).setSortable(true);
        grid.getColumns().get(3).setSortable(true);

        grid.addSelectionListener(event -> {
            boolean hasSelection = event.getFirstSelectedItem().isPresent();
            deleteClientButton.setVisible(hasSelection);
            clientIdSelected = event.getFirstSelectedItem().get().clientId();
        });
        grid.addComponentColumn(client -> {
            HorizontalLayout actions = new HorizontalLayout();

            // Client view icon
            Button viewButton = new Button(VaadinIcon.EYE.create(), e -> {
                Notification.show( client.businessName()+"צפה");
                UI.getCurrent().navigate("case/" + client.clientId());
            });
            viewButton.getElement().setProperty("title", "צפייה");

            // edit icon
            Button editButton = new Button(VaadinIcon.EDIT.create(), e -> {
                Notification.show("עריכה של: " + client.businessName());
            });
            editButton.getElement().setProperty("title", "עריכה");

            viewButton.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.Margin.End.SMALL);
            editButton.addClassNames(LumoUtility.IconSize.SMALL);

            actions.add(viewButton, editButton);
            return actions;
        }).setHeader("פעולות").setAutoWidth(true).setFlexGrow(0);


        add(buttonsLayout ,grid);
    }
    /**
     * Opens a modal dialog with two-step tabs to register a new client.
     * <p>
     * Tabs include:
     * <ul>
     *   <li>"פרטי לקוח" – for contact and business info</li>
     *   <li>"פרטים פיננסיים" – for bank account details</li>
     * </ul>
     *
     * @return the opened dialog component
     */
    private Dialog newClientDialog() {

        dialog = new Dialog();
        dialog.setWidth("55%");
        dialog.setHeight("70%");
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setHeaderTitle("תיק חדש");

        tabs = new Tabs();

        clientDetailsTab = new Tab(VaadinIcon.INFO.create(),new Span("פרטי לקוח"));
        financialDetailsTab = new Tab(VaadinIcon.CASH.create(),new Span("פרטים פיננסיים"));
        clientDetailsTab.getElement().setAttribute("dir", "rtl");
        financialDetailsTab.getElement().setAttribute("dir", "rtl");
        tabs.getElement().setAttribute("dir", "rtl");
        tabs.add(clientDetailsTab,financialDetailsTab);



        Component clientDetailsContent = dialogClientDetails();
        Component financialDetailsContent = dialogFinancialDetails();
        dialog.add(tabs);

        dialog.add(clientDetailsContent);
        tabs.addSelectedChangeListener(event -> {
            dialog.removeAll();
            dialog.add(tabs);
            if (event.getSelectedTab() == clientDetailsTab) {
                dialog.add(clientDetailsContent);
            } else if (event.getSelectedTab() == financialDetailsTab) {
                dialog.add(financialDetailsContent);
            }
        });
        dialog.getElement().setAttribute("dir", "rtl");
        dialog.open();

        return dialog;
    }
    /**
     * Constructs the first tab ("פרטי לקוח") in the new client dialog.
     * Includes fields like business name, email, phone, tax ID, and address.
     *
     * @return a component containing the layout of client details form
     */
    private Component dialogClientDetails(){
        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout row1 = new HorizontalLayout();
        HorizontalLayout row2 = new HorizontalLayout();
        HorizontalLayout row3 = new HorizontalLayout();
        HorizontalLayout row4 = new HorizontalLayout();

        // Fields
        emailField = new EmailField("אימייל");
        phoneField = new TextField("טלפון ראשי");
        contactPhoneField = new TextField("טלפון משני");
        addressField = new TextField("כתובת");
        businessNameField = new TextField("שם עסק");
        clientTypeField = new TextField("סוג עוסק");
        taxIdField = new TextField("ת.ז/ח.פ");
        zipField = new TextField("מיקוד");
        clientTypeComboBox = new ComboBox<>("סוג עוסק");
        clientTypeComboBox.setItems("עוסק פטור","עוסק מורשה");

        emailField.getElement().setAttribute("dir", "rtl");
        phoneField.getElement().setAttribute("dir", "rtl");
        contactPhoneField.getElement().setAttribute("dir", "rtl");
        addressField.getElement().setAttribute("dir", "rtl");
        businessNameField.getElement().setAttribute("dir", "rtl");
        clientTypeField.getElement().setAttribute("dir", "rtl");
        taxIdField.getElement().setAttribute("dir", "rtl");
        zipField.getElement().setAttribute("dir", "rtl");
        clientTypeComboBox.getElement().setAttribute("dir", "rtl");

        emailField.setRequired(true);
        phoneField.setRequired(true);
        contactPhoneField.setRequired(true);
        addressField.setRequired(true);
        businessNameField.setRequired(true);
        clientTypeField.setRequired(true);
        taxIdField.setRequired(true);
        zipField.setRequired(true);

        saveButton = new Button("שמור והמשך");
        cancelButton = new Button("ביטול");
        saveButton.getStyle().setOpacity("20px");

        // Adding fields to rows
        row1.add(businessNameField,clientTypeComboBox,taxIdField);
        row2.add(emailField,phoneField,contactPhoneField);
        row3.add(addressField,zipField);
        row4.add(cancelButton,saveButton);
        layout.add(row1,row2,row3,row4);

        cancelButton.addClickListener(event -> {
            dialog.close();
        });
        saveButton.addClickListener(event -> {
            tabs.setSelectedTab(financialDetailsTab);
        });
        saveButton.addClassNames(LumoUtility.TextColor.PRIMARY);

        cancelButton.addClassNames(LumoUtility.TextColor.ERROR);

        layout.setAlignItems(Alignment.END);

        return layout;
    }
    /**
     * Constructs the second tab ("פרטים פיננסיים") in the new client dialog.
     * Includes fields for bank name, branch, account number, and owner.
     * Also handles the "שמור" action, which submits the client to the backend.
     *
     * @return a component containing the financial details form
     */
    private Component dialogFinancialDetails() {
        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout row1 = new HorizontalLayout();
        HorizontalLayout row2 = new HorizontalLayout();
        HorizontalLayout row4 = new HorizontalLayout();

        bankOwnerNameField = new TextField("שם בעל החשבון");
        bankNameField = new ComboBox<>("שם בנק");
        bankNameField.setItems("מרכנתיל","דיסקונט","בנק הפועלים","הלאומי");
        bankBranchField = new TextField("מספר סניף");
        bankNumberField = new TextField("מספר חשבון");

        bankBranchField.getElement().setAttribute("dir", "rtl");
        bankNameField.getElement().setAttribute("dir", "rtl");
        bankNumberField.getElement().setAttribute("dir", "rtl");
        bankOwnerNameField.getElement().setAttribute("dir", "rtl");

        saveButton = new Button("שמור");
        cancelButton = new Button("ביטול");
        saveButton.getStyle().setOpacity("20px");

        layout.add(row1,row2,row4);
        saveButton.addClassNames(LumoUtility.TextColor.PRIMARY);
        cancelButton.addClassNames(LumoUtility.TextColor.ERROR);

        cancelButton.addClickListener(event -> {
            dialog.close();
        });
        saveButton.addClickListener(event -> {

            newClient();
            dialog.close();
            UI.getCurrent().refreshCurrentRoute(true);
        });

        row1.add(bankOwnerNameField,bankNameField);
        row2.add(bankBranchField,bankNumberField);
        row4.add(cancelButton,saveButton);

        layout.setAlignItems(Alignment.END);

        return layout;
    }
    /**
     * Sends a POST request with {@link CreateClientRequest} to create a new client.
     * Includes validation of fields (email format, phone length, tax ID).
     * Uses JWT token from session for authentication.
     */
    private void newClient() {

            String getToken = (String) VaadinSession.getCurrent().getAttribute("jwt");

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + getToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            CreateClientRequest clientRequest = new CreateClientRequest(
                    emailField.getValue(),
                    phoneField.getValue(),
                    addressField.getValue(),
                    zipField.getValue(),
                    businessNameField.getValue(),
                    clientTypeComboBox.getValue(),
                    taxIdField.getValue(),
                    bankOwnerNameField.getValue(),
                    bankNameField.getValue(),
                    bankBranchField.getValue(),
                    bankNumberField.getValue(),
                    getToken

            );
            Binder<CreateClientRequest> binder = new Binder<>(CreateClientRequest.class);
            binder.forField(emailField).withValidator(new EmailValidator("אימייל אינו תקין")).bind("email");
            binder.forField(phoneField).withValidator(new StringLengthValidator("מספר לא תקין", 8, 10)).bind("phone");
            binder.forField(taxIdField).withValidator(new StringLengthValidator("לא תקין", 9, 9)).bind("tax_id");


            if (binder.validate().isOk()) {
                try {
                    HttpEntity<?> requestEntity = new HttpEntity<>(clientRequest, headers);
                    ResponseEntity<CreateClientResponse> response = restTemplate.exchange(
                            "http://localhost:8080/client/create",
                            HttpMethod.POST,
                            requestEntity,
                            CreateClientResponse.class
                    );
                    message = response.getBody().message();
                    if (response.getStatusCode().is2xxSuccessful()) {
                        Notification.show(message, 3000, Notification.Position.MIDDLE);
                    } else {

                        Notification.show(message, 4000, Notification.Position.MIDDLE);
                    }

                } catch (HttpClientErrorException e) {
                    Notification.show(message);
                }
            }

    }


    /**
     * Opens a confirmation dialog to delete a selected client.
     * On confirmation, sends a DELETE request to the backend using the client's tax ID.
     * Shows success/failure message and refreshes the view upon completion.
     *
     * @return the opened {@link ConfirmDialog}
     */
    private ConfirmDialog deleteClient() {

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("אישור מחיקת לקוח");
        confirmDialog.setText("?פעולה זו תמחק את נתוני הלקוח לצמיתות. האם לאשר מחיקה");

        confirmDialog.setCancelable(true);
        confirmDialog.addCancelListener(event -> {confirmDialog.close();});

        confirmDialog.addClassNames(LumoUtility.AlignItems.CENTER);
        confirmDialog.setConfirmText("אשר");

        confirmDialog.addConfirmListener(event -> {try {
            String token = (String) VaadinSession.getCurrent().getAttribute("jwt");
            if (token == null || clientIdSelected == null) {
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String encodedName = URLEncoder.encode(clientIdSelected, StandardCharsets.UTF_8);
            String url = "http://localhost:8080/client/delete/" + clientIdSelected;

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("הלקוח נמחק בהצלחה", 3000, Notification.Position.MIDDLE);
                UI.getCurrent().refreshCurrentRoute(true);
                confirmDialog.close();
            } else {
                Notification.show("מחיקה נכשלה." , 3000, Notification.Position.MIDDLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("שגיאה במחיקה: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }});

        confirmDialog.open();

        return confirmDialog;
    }

    /**
     * Called before the view is entered.
     * <p>
     * - Validates the JWT token from the session.
     * - If invalid or missing, redirects to the login page.
     * - If valid, loads the list of clients for the logged-in accountant and displays them in the grid.
     *
     * @param event the route change event
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");
        if (token == null) {
            event.forwardTo("login");
            return;
        }


        String username = jwtUtil.extractUsername(token);
        System.out.println("username: " + username);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);


        try {
            ResponseEntity<LoadClientResponse> response = restTemplate.exchange(
                    "http://localhost:8080/client/load-clients",
                    HttpMethod.GET, entity, LoadClientResponse.class);
            grid.setItems(response.getBody().clients());
            message = response.getBody().message();

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                grid.setItems(response.getBody().clients());
            } else {
                Notification.show(message, 3000, Notification.Position.MIDDLE);
            }

        } catch (Exception e) {
            Notification.show(message, 3000, Notification.Position.MIDDLE);
        }

    }
}
