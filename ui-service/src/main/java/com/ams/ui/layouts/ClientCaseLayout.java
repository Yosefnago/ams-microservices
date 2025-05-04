package com.ams.ui.layouts;

import com.ams.dtos.clientDto.LoadClientDetailsCaseResponse;
import com.ams.ui.views.ClientsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    private final RestTemplate restTemplate;
    private String selectedClientId;
    private H6 bussName;
    private H6 email2;
    private H6 address2;
    private H6 id;
    private H6 phone2;
    private H6 type2;
    String msg;

    /**
     * Constructs a {@code ClientCaseLayout} instance with the provided {@link RestTemplate}.
     *
     * @param restTemplate the HTTP client used to fetch client details from the backend
     */
    public ClientCaseLayout(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        header(); // basic layout structure (empty by default, filled later)
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
    private void header() {

        RouterLink backLink = new RouterLink("חיתוך חדש", ClientsView.class);
        backLink.getStyle().set("margin-left", "10px");
        backLink.addClassNames(
                LumoUtility.FontWeight.SEMIBOLD

        );

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
        leftColumn.getStyle().set("direction", "rtl")
                .setMarginRight("40px");

        HorizontalLayout clientDetailsLayout = new HorizontalLayout(rightColumn, leftColumn);
        clientDetailsLayout.setSpacing(true);
        clientDetailsLayout.setPadding(true);
        clientDetailsLayout.setAlignItems(FlexComponent.Alignment.START);

        HorizontalLayout headerLayout = new HorizontalLayout(clientDetailsLayout, backLink);
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
    public void beforeEnter(BeforeEnterEvent event){
        selectedClientId = event.getRouteParameters().get("clientId").orElse("");

        try{
            String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = "http://localhost:8080/client/load-case-details?clientId=" + selectedClientId;
            ResponseEntity<LoadClientDetailsCaseResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, LoadClientDetailsCaseResponse.class
            );
            msg = response.getBody().message();
            LoadClientDetailsCaseResponse clientData = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && clientData != null && clientData.success()) {
                updateHeader(clientData);
            }

        }catch (Exception e){
            Notification.show(msg,3000, Notification.Position.MIDDLE);
        }
    }

}
