package com.ams.ui.layouts;

import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.clientDto.GrantAccessRequestDto;
import com.ams.dtos.clientDto.GrantAccessResponse;
import com.ams.dtos.clientDto.LoadClientDetailsCaseResponse;
import com.ams.ui.api.clientside.ClientHttpService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

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
 * @author Yosef Nago
 */
public class ClientCaseLayout extends AppLayout implements BeforeEnterObserver {

    HorizontalLayout headerLayout;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final ClientHttpService clientHttpService;
    private String clientId;
    private H6 bussName, email2, address2, id, phone2, type2;

    private Button backButton;
    private HorizontalLayout buttonsLayout;
    private String msg;

    /**
     * Constructs a {@code ClientCaseLayout} instance with the provided {@link RestTemplate}.
     *
     * @param restTemplate the HTTP client used to fetch client details from the backend
     */
    public ClientCaseLayout(@Autowired RestTemplate restTemplate,JwtUtil jwtUtil,ClientHttpService clientHttpService) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        this.clientHttpService = clientHttpService;
    }

    /**
     * Populates the header with the loaded client data.
     *
     * @param clientData the DTO returned from the backend with client information
     */
    private void updateHeader(LoadClientDetailsCaseResponse clientData) {
        bussName.setText("שם העסק: " + clientData.businessName());
        email2.setText("אימייל: " + clientData.email());
        id.setText("ת.ז/ח.פ: " + clientData.clientId());
        phone2.setText("טלפון: " + clientData.phone());
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


        backButton = new Button("חזור", e ->
                UI.getCurrent().getPage().executeJs("window.history.back()")
        );


        buttonsLayout = new HorizontalLayout(backButton,logout);
        buttonsLayout.setSpacing(true);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);


        backButton.setVisible(true);
        logout.setVisible(true);


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
        Object tokenObj = VaadinSession.getCurrent().getAttribute("jwt");

        if (tokenObj == null) {
            UI.getCurrent().navigate("login");
            return;
        }

        String token = tokenObj.toString();

        if (!jwtUtil.validateToken(token)) {
            UI.getCurrent().navigate("login");
            return;
        }

        String role = jwtUtil.extractRole(token);
        header(role);
        loadClientData(token);
    }


    private void loadClientData(String token) {
        clientId = jwtUtil.extractClientId(token);

        LoadClientDetailsCaseResponse clientData = clientHttpService.loadClientDetails(token, clientId);

        if (clientData != null && clientData.success()) {
            updateHeader(clientData);
        } else {
            Notification.show(clientData != null ? clientData.message() : "שגיאה כללית", 3000, Notification.Position.MIDDLE);
        }
    }
}
