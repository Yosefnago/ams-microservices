package com.ams.ui.views;


import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.clientDto.LoadNumOfClientsResponse;
import com.ams.ui.layouts.MainLayout;
import com.vaadin.copilot.javarewriter.custom.DashboardComponentHandle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * {@code DashboardView} is the main dashboard UI view of the accounting management system.
 * <p>
 * This view provides the accountant with an at-a-glance summary of key data and quick access panels, including:
 * <ul>
 *   <li>Number of clients in the system</li>
 *   <li>Upcoming tax report deadlines</li>
 *   <li>Document queue status</li>
 *   <li>Time tracking (שעון נוכחות)</li>
 *   <li>Payment collection overview</li>
 * </ul>
 *
 * <p><b>Access Control:</b> Requires a valid JWT stored in the {@link VaadinSession}.</p>
 * <p><b>REST Integration:</b> Uses {@link RestTemplate} to call the backend via the gateway and fetch data.</p>
 * <p><b>Routing:</b> Mapped to the {@code /dashboard} path using {@link com.ams.ui.layouts.MainLayout}.</p>
 *
 * @author Yosef Nago
 */

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard")
public class DashboardView extends VerticalLayout {

    private final MessagesView messages;
    RouterLink link;
    String accountantUsername;
    private String message;
    private RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @Autowired
    public DashboardView(RestTemplate restTemplate, JwtUtil jwtUtil, MessagesView messages) {
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        setSizeFull();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();

        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        horizontalLayout.setAlignItems(Alignment.END);
        horizontalLayout.add(pays(),workingTime(),divuachKarov(),documentCare(),numOfClients());

        add(horizontalLayout);
        this.messages = messages;
    }
    /**
     * Creates a dashboard tile labeled "מסמכים לטיפול" (Documents to Handle).
     * Can be linked in the future to a view showing pending document verifications.
     *
     * @return a styled dashboard tile
     */
    private Component documentCare(){

        Div div = new Div();
        div.setHeight("180px");
        div.setWidth("210px");
        div.getStyle().set("margin-right","10px");
        div.addClassNames(
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.AlignContent.END,
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Transition.COLORS
        );
        Image image = new Image("doc.jpg","לוגו");
        image.addClassNames(LumoUtility.IconSize.SMALL);
        div.getStyle().set("background-image", "url('doc.jpg')")
                .set("background-size", "cover")
                .set("background-position", "center");

        div.getElement().setText("מסמכים לטיפול");

        return div;
    }
    /**
     * Creates a panel displaying the number of clients associated with the current accountant.
     * <p>
     * The value is fetched via a secured REST call to the backend and displayed over a styled image tile.
     * On click, navigates to {@link ClientsView}.
     *
     * @return a styled component with client count and click navigation
     */
    private Component numOfClients() {
        Div numOfClients = new Div();
        numOfClients.setHeight("180px");
        numOfClients.setWidth("210px");
        numOfClients.getStyle().set("margin-right","160px");
        numOfClients.addClassNames(
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.AlignContent.END,
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Transition.COLORS

        );
        Image image = new Image("customers.png","לוגו");
        image.addClassNames(LumoUtility.IconSize.SMALL);

        numOfClients.getStyle().set("background-image", "url('customers.png')")
                .set("background-size", "cover")
                .set("background-position", "center");


        numOfClients.getStyle().setCursor("pointer");
        numOfClients.addClickListener(event -> {
            UI.getCurrent().navigate(ClientsView.class);
        });
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

        if (token == null || token.trim().isEmpty() || !jwtUtil.validateToken(token)) {
            Notification.show("שגיאה: אין אישור גישה ממשתמש זה. אנא התחבר מחדש.", 5000, Notification.Position.MIDDLE);
            return numOfClients;
        }


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String str="";

        try {
            ResponseEntity<LoadNumOfClientsResponse> response = restTemplate.exchange(
                    "http://localhost:8080/client/load-numOfclients",
                    HttpMethod.GET,
                    entity,
                    LoadNumOfClientsResponse.class);
            message = response.getBody().message();

            if (response.hasBody() && response.getBody() != null) {
                numOfClients.getElement().setText("לקוחות במערכת: " + response.getBody().numOfClients());
            } else {
                numOfClients.getElement().setText(message);
            }

            str = "לקוחות במערכת: " + response.getBody().numOfClients();
        } catch (Exception e) {
            Notification.show(message, 4000, Notification.Position.MIDDLE);
        }


            numOfClients.getElement().setText(str);
        return numOfClients;
    }
    /**
     * Creates a dashboard tile labeled "דיווחים קרובים" (Upcoming Reports).
     * Used as a placeholder for future integration with report reminders or calendar events.
     *
     * @return a styled dashboard tile
     */
    private Component divuachKarov(){
        Div divuachKarov = new Div();
        divuachKarov.setHeight("180px");
        divuachKarov.setWidth("210px");
        divuachKarov.getStyle().set("margin-right","10px");
        divuachKarov.addClassNames(
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.AlignContent.END,
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Transition.COLORS
        );
        Image image = new Image("tax.jpg","לוגו");
        image.addClassNames(LumoUtility.IconSize.SMALL);

        divuachKarov.getStyle().set("background-image", "url('tax.jpg')")
                .set("background-size", "cover")
                .set("background-position", "center");

        divuachKarov.getElement().setText("דיווחים קרובים");

        return divuachKarov;
    }
    /**
     * Creates a dashboard tile labeled "שעון נוכחות" (Work Time).
     * Placeholder for attendance or time tracking module.
     *
     * @return a styled dashboard tile
     */
    private Component workingTime(){
        Div workTime = new Div();
        workTime.setHeight("180px");
        workTime.setWidth("210px");
        workTime.getStyle().set("margin-right","10px");
        workTime.addClassNames(
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.AlignContent.END,
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Transition.COLORS
        );
        Image image = new Image("time1.jpg","לוגו");
        image.addClassNames(LumoUtility.IconSize.SMALL);

        workTime.getStyle().set("background-image", "url('time1.jpg')")
                .set("background-size", "cover")
                .set("background-position", "center");

        workTime.getElement().setText("שעון נוכחות");
        workTime.getStyle().setCursor("pointer");
        workTime.addClickListener(event -> {
            
           UI.getCurrent().navigate(AttendanceView.class);
        });

        return workTime;
    }
    /**
     * Creates a dashboard tile labeled "גבייה" (Payments Collection).
     * Placeholder for displaying recent payments or unpaid invoices.
     *
     * @return a styled dashboard tile
     */
    public Component pays(){
        Div pays = new Div();
        pays.setHeight("180px");
        pays.setWidth("210px");
        pays.getStyle().set("margin-right","10px");
        pays.addClassNames(
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.AlignContent.END,
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Transition.COLORS
        );
        Image image = new Image("pays.jpg","לוגו");
        image.addClassNames(LumoUtility.IconSize.SMALL);

        pays.getStyle().set("background-image", "url('pays.jpg')")
                .set("background-size", "cover")
                .set("background-position", "center");

        pays.getElement().setText("גבייה");

        return pays;

    }
    private void charts(){

    }

}
