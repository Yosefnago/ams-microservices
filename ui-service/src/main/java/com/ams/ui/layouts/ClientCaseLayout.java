package com.ams.ui.layouts;

import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.clientDto.LoadClientDetailsCaseResponse;
import com.ams.ui.views.ClientsView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static io.netty.util.concurrent.FastThreadLocal.removeAll;

/**
 * {@code ClientCaseLayout} is a layout used in views related to a specific client case.
 * <p>
 * It dynamically fetches and displays client information before the view loads, based on
 * the {@code clientId} provided in the route. The layout uses a right-to-left display format
 * suitable for Hebrew-language interfaces.
 * </p>
 *
 * <p><b>Key Responsibilities:</b></p>
 * <ul>
 *     <li>Extract client ID from route parameters</li>
 *     <li>Call backend service using JWT-authenticated {@link RestTemplate}</li>
 *     <li>Render client details in a dual-column structured header</li>
 * </ul>
 *
 * <p><b>Example Route:</b> {@code /client-case/:clientId}</p>
 *
 * @see LoadClientDetailsCaseResponse
 * @see com.ams.ui.views.ClientsView
 * @author Yosef Nago
 */
public class ClientCaseLayout extends AppLayout implements BeforeEnterObserver {

    HorizontalLayout headerLayout;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    private String selectedClientId;
    private H6 bussName, email2, address2, id, phone2, type2;

    private Button backButton, grantAccsess;
    private RouterLink backLink;
    private HorizontalLayout buttonsLayout;
    private String msg;

    /**
     * Constructs a {@code ClientCaseLayout} instance with the provided {@link RestTemplate}.
     *
     * @param restTemplate the HTTP client used to fetch client details from the backend
     */
    public ClientCaseLayout(@Autowired RestTemplate restTemplate,JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Populates the header with the loaded client data.
     *
     * @param clientData the DTO returned from the backend with client information
     */
    private void updateHeader(LoadClientDetailsCaseResponse clientData) {
        bussName.setText("תיק לקוח: " + clientData.businessName());
        email2.setText("אימייל: " + clientData.email());
        address2.setText("כתובת: " + clientData.address());
        id.setText("ת.ז/ח.פ: " + clientData.clientId());
        phone2.setText("טלפון: " + clientData.phone());
        type2.setText("סוג עוסק: " + clientData.businessType());
    }
    /**
     * Builds the initial header layout structure.
     * This includes labels for client info and a navigation link back to the client list.
     */
    private void header(String role) {
        if (headerLayout != null) {
            remove(headerLayout);
        }
        Button logout = new Button("התנתק");
        logout.setIcon(VaadinIcon.SIGN_OUT.create());
        logout.getElement().getStyle().set("color", "var(--lumo-error-color)");

        logout.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute("jwt", null);
            VaadinSession.getCurrent().close();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        bussName = new H6();
        email2 = new H6();
        address2 = new H6();
        id = new H6();
        phone2 = new H6();
        type2 = new H6();

        VerticalLayout rightColumn = new VerticalLayout(bussName, email2, address2);
        rightColumn.setSpacing(true);
        rightColumn.setPadding(false);
        rightColumn.setAlignItems(FlexComponent.Alignment.START);
        rightColumn.getStyle().set("direction", "rtl");

        VerticalLayout leftColumn = new VerticalLayout(id, phone2, type2);
        leftColumn.setSpacing(true);
        leftColumn.setPadding(false);
        leftColumn.setAlignItems(FlexComponent.Alignment.START);
        leftColumn.getStyle().set("direction", "rtl").setMarginRight("40px");

        HorizontalLayout clientDetailsLayout = new HorizontalLayout(rightColumn, leftColumn);
        clientDetailsLayout.setSpacing(true);
        clientDetailsLayout.setPadding(true);
        clientDetailsLayout.setAlignItems(FlexComponent.Alignment.START);

        // כפתורים
        backButton = new Button("חזור", e ->
                UI.getCurrent().getPage().executeJs("window.history.back()")
        );

        backLink = new RouterLink("חיתוך חדש", ClientsView.class);
        backLink.getStyle().set("margin-left", "10px");
        backLink.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

        grantAccsess = new Button("הענק גישה ללקוח");
        grantAccsess.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        grantAccsess.addClickListener(e -> grantAccessDialog(selectedClientId));

        buttonsLayout = new HorizontalLayout(backButton, backLink, grantAccsess,logout);
        buttonsLayout.setSpacing(true);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        if (role.equals("CLIENT")) {
            backButton.setVisible(true);
            backLink.setVisible(false);
            grantAccsess.setVisible(false);
            logout.setVisible(true);
        } else if (role.equals("ACCOUNTANT")) {
            backButton.setVisible(true);
            backLink.setVisible(true);
            grantAccsess.setVisible(true);
            logout.setVisible(false);
        }

        headerLayout = new HorizontalLayout(clientDetailsLayout, buttonsLayout);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.getStyle().set("direction", "rtl");


        addToNavbar(headerLayout);
    }
    /**
     * Triggered before navigating to a view that uses this layout.
     * <p>
     * Extracts the {@code clientId} from route parameters, performs an authenticated request
     * to fetch client data, and updates the header layout accordingly.
     * </p>
     *
     * @param event navigation event containing route parameters
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        selectedClientId = event.getRouteParameters().get("clientId").orElse("");

        String token = VaadinSession.getCurrent().getAttribute("jwt").toString();

        if (token == null || !jwtUtil.validateToken(token)) {
            UI.getCurrent().navigate("login");
            return;
        }

        String role = jwtUtil.extractRole(token);


        header(role);
        loadClientData(token);
    }
    private void grantAccessDialog(String clientId) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("הרשאת גישה ללקוח");

        TextField clientName = new TextField("שם לקוח");
        clientName.setReadOnly(true);
        clientName.setValue(bussName.getText());

        TextField clientUsername = new TextField("שם משתמש");
        PasswordField clientPassword = new PasswordField("סיסמה ראשונית");

        clientName.getStyle().set("direction", "rtl");
        clientUsername.getStyle().set("direction", "rtl");
        clientPassword.getStyle().set("direction", "rtl");

        VerticalLayout layout = new VerticalLayout(clientName, clientUsername, clientPassword);
        layout.getStyle().setAlignItems(Style.AlignItems.END);
        dialog.add(layout);

        Button save = new Button("אשר", e -> {
            grantAccess(clientId, bussName.getText(), clientUsername.getValue(), clientPassword.getValue());
            dialog.close();
        });

        Button cancel = new Button("בטל", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(save, cancel);
        buttons.getStyle().setJustifyContent(Style.JustifyContent.END);
        buttons.setSpacing(true);

        dialog.add(buttons);
        dialog.open();
    }
    private void grantAccess(String clientId, String clientName, String clientUsername, String clientPassword) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth((String) VaadinSession.getCurrent().getAttribute("jwt"));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8080/client/grant-access" +
                "?clientId=" + clientId +
                "&clientUsername=" + clientUsername +
                "&clientPassword=" + clientPassword;

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Notification.show("גישה ניתנה ללקוח בהצלחה", 3000, Notification.Position.TOP_CENTER);
            } else {
                Notification.show("שגיאה במתן גישה ללקוח: " + response.getBody(), 4000, Notification.Position.TOP_CENTER);
            }
        } catch (Exception e) {
            Notification.show("שגיאה כללית: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            e.printStackTrace();
        }
    }
    private void loadClientData(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = "http://localhost:8080/client/load-case-details?clientId=" + selectedClientId;

            ResponseEntity<LoadClientDetailsCaseResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, LoadClientDetailsCaseResponse.class
            );

            LoadClientDetailsCaseResponse clientData = response.getBody();
            msg = clientData.message();

            if (response.getStatusCode().is2xxSuccessful() && clientData != null && clientData.success()) {
                updateHeader(clientData);
            } else {
                Notification.show("שגיאה בטעינת פרטי הלקוח", 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show(msg != null ? msg : "שגיאה", 3000, Notification.Position.MIDDLE);
        }
    }
}
