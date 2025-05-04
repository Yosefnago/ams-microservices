package com.ams.ui.layouts;

import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.accountantDto.AccountantDetailsResponse;
import com.ams.ui.views.DashboardView;
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
import org.springframework.web.client.RestTemplate;

/**
 * {@code AttendanceLayout} is a layout component used to wrap views related to attendance and working hours.
 * <p>
 * This layout displays authenticated accountant user details in the top navigation bar.
 * The details are fetched using a JWT stored in {@link VaadinSession} and retrieved via a secured API call
 * to the {@code /user/load-details} endpoint.
 * </p>
 *
 * <p><b>Lifecycle:</b></p>
 * <ul>
 *     <li>On navigation to any view using this layout, {@code beforeEnter()} is invoked</li>
 *     <li>User details are extracted from the JWT token and fetched from the user-service</li>
 *     <li>Header layout is dynamically populated with user info</li>
 * </ul>
 *
 * <p><b>Scope:</b> {@link VaadinSessionScope} – the layout persists within the session</p>
 *
 * @author Yosef Nago
 */
@VaadinSessionScope
public class AttendanceLayout extends AppLayout implements BeforeEnterObserver {

    private String username;
    private String email;
    private String phone;
    private String id;
    String msg;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    /**
     * Constructs the {@code AttendanceLayout} with required dependencies.
     *
     * @param restTemplate the REST client used to call backend services
     * @param jwtUtil      utility class for extracting information from JWT tokens
     */
    public AttendanceLayout(@Autowired RestTemplate restTemplate, JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Renders the top header layout with user details and a navigation link to the dashboard.
     */
    private void header(){

        RouterLink routerLink = new RouterLink("דף הבית", DashboardView.class);
        routerLink.getStyle().set("margin-left", "10px");
        routerLink.addClassNames(
                LumoUtility.FontWeight.SEMIBOLD

        );

        H6 accountantName = new H6("שם עובד: " + username);
        H6 accountantEmail = new H6("אימייל: " + email);
        H6 accountantPhone = new H6("כתובת: " + phone);

        VerticalLayout rightColumn = new VerticalLayout(accountantName, accountantEmail, accountantPhone);
        rightColumn.setSpacing(true);
        rightColumn.setPadding(false);
        rightColumn.setAlignItems(FlexComponent.Alignment.START);
        rightColumn.getStyle().set("direction", "rtl");

        HorizontalLayout accountantDetails = new HorizontalLayout(rightColumn);
        accountantDetails.setSpacing(true);
        accountantDetails.setPadding(true);
        accountantDetails.setAlignItems(FlexComponent.Alignment.START);

        HorizontalLayout headerLayout = new HorizontalLayout(accountantDetails, routerLink);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.getStyle().set("direction", "rtl");

        addToNavbar(headerLayout);

    }
    /**
     * Called before navigation to any view that uses this layout.
     * <p>
     * Extracts the JWT from {@link VaadinSession}, decodes it to retrieve the username,
     * and fetches the user details via a secured REST call. The details are rendered in the navbar.
     * </p>
     *
     * @param event the navigation event
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event){

        try{
            String token = (String)VaadinSession.getCurrent().getAttribute("jwt");
            String username1 = jwtUtil.extractUsername(token);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = "http://localhost:8080/user/load-details?username=" + username1;
            ResponseEntity<AccountantDetailsResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, AccountantDetailsResponse.class
            );
            msg = response.getBody().message();
            AccountantDetailsResponse accountantDetails = response.getBody();
            if (accountantDetails != null && accountantDetails.success()) {
                username = accountantDetails.username();
                email = accountantDetails.email();
                phone = accountantDetails.phone();
                id = accountantDetails.id().toString();

                header();
            }else {
                Notification.show(msg, 3000, Notification.Position.MIDDLE);
            }
        }catch (Exception e){

            Notification.show(msg,3000, Notification.Position.MIDDLE);
        }

    }
}
