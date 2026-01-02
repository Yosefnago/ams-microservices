package com.ams.ui.views;

import com.ams.dtos.invoiceDto.InvoiceIncomeNumber;
import com.ams.dtos.invoiceDto.InvoiceOutComeM;
import com.ams.ui.layouts.ClientCaseLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


/**
 * {@code ClientCaseView} is the main Vaadin view for displaying and managing a client's case file.
 *
 * <p>This view is associated with the route {@code /case/:clientId} and uses {@link ClientCaseLayout} as its layout.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *     <li>Loads client data from the backend using the provided {@code clientId} parameter</li>
 *     <li>Displays interactive components like an update form, documents, invoices, reports, and quick actions</li>
 *     <li>Supports secure updates via JWT-authenticated HTTP requests</li>
 * </ul>
 *
 * <p><b>Routing:</b> The {@code clientId} is extracted from the route to perform data operations</p>
 *
 * @see ClientCaseLayout
 * @see BeforeEnterObserver
 * @see com.ams.dtos.clientDto.LoadClientCaseDetailsRequest
 * @see com.ams.dtos.clientDto.UpdateClientResponse
 * @author Yosef Nago
 */
@Route(value = "client/:clientId", layout = ClientCaseLayout.class)
@PageTitle("Case")
public class ClientCaseView extends VerticalLayout implements BeforeEnterObserver {

    private String clientId;
    private final RestTemplate restTemplate;
    VerticalLayout contentLayout;

    Div outComes;
    Div incomes;
    /**
     * Default constructor. Initializes layout settings for the view.
     */
    public ClientCaseView(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.getStyle().set("background-color", "white");
        contentLayout.add(mainLayout());

        HorizontalLayout mainLayout = new HorizontalLayout();

        mainLayout.setSizeFull();
        mainLayout.add(contentLayout);
        mainLayout.setFlexGrow(1, contentLayout);

        add(mainLayout);
    }
    /**
     * Builds the main interactive area containing action Divs (documents, invoices, reports, etc.).
     * @return a layout with clickable Divs
     */
    private Component mainLayout(){

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setAlignItems(Alignment.END);

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Div documentsDiv = new Div("ארכיון מסמכים");
        documentsDiv.getStyle().setCursor("pointer");
        documentsDiv.getStyle().set("background-color", "#f0f0f0");
        documentsDiv.getStyle().setHeight("100px");
        documentsDiv.getStyle().setWidth("200px");
        documentsDiv.getStyle().set("display", "flex");
        documentsDiv.getStyle().set("flex-direction", "row-reverse");
        documentsDiv.getStyle().set("align-items", "center");
        documentsDiv.getStyle().set("justify-content", "center");
        documentsDiv.getStyle().set("direction", "rtl");
        documentsDiv.addClassNames(LumoUtility.BoxShadow.SMALL);
        documentsDiv.addClassNames(LumoUtility.TextColor.PRIMARY);
        documentsDiv.addClassNames(LumoUtility.FontSize.LARGE);
        documentsDiv.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
        documentsDiv.addClassNames(LumoUtility.BorderRadius.MEDIUM);

        documentsDiv.addClickListener(e -> UI.getCurrent().navigate(clientId + "/documents"));

        Div invoicesDiv = new Div("חשבוניות");
        invoicesDiv.getStyle().set("background-color", "#f0f0f0");
        invoicesDiv.getStyle().setHeight("100px");
        invoicesDiv.getStyle().setWidth("200px");
        invoicesDiv.getStyle().setCursor("pointer");
        invoicesDiv.getStyle().set("display", "flex");
        invoicesDiv.getStyle().set("flex-direction", "row-reverse");
        invoicesDiv.getStyle().set("align-items", "center");
        invoicesDiv.getStyle().set("justify-content", "center");
        invoicesDiv.getStyle().set("direction", "rtl");
        invoicesDiv.addClassNames(LumoUtility.BoxShadow.SMALL);
        invoicesDiv.addClassNames(LumoUtility.TextColor.PRIMARY);
        invoicesDiv.addClassNames(LumoUtility.FontSize.LARGE);
        invoicesDiv.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
        invoicesDiv.addClassNames(LumoUtility.BorderRadius.MEDIUM);
        invoicesDiv.addClickListener(e -> UI.getCurrent().navigate(clientId + "/invoices"));


        Div duchutDiv = new Div("דוחות");
        duchutDiv.getStyle().set("background-color", "#f0f0f0");
        duchutDiv.getStyle().setHeight("100px");
        duchutDiv.getStyle().setWidth("200px");
        duchutDiv.getStyle().set("display", "flex");
        duchutDiv.getStyle().set("flex-direction", "row-reverse");
        duchutDiv.getStyle().set("align-items", "center");
        duchutDiv.getStyle().set("justify-content", "center");
        duchutDiv.getStyle().set("direction", "rtl");
        duchutDiv.getStyle().setCursor("pointer");
        duchutDiv.addClassNames(LumoUtility.BoxShadow.SMALL);
        duchutDiv.addClassNames(LumoUtility.TextColor.PRIMARY);
        duchutDiv.addClassNames(LumoUtility.FontSize.LARGE);
        duchutDiv.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
        duchutDiv.addClassNames(LumoUtility.BorderRadius.MEDIUM);
        duchutDiv.addClickListener(e -> UI.getCurrent().navigate( clientId +"/duchut"));

        horizontalLayout.add(duchutDiv,invoicesDiv,documentsDiv);

        incomes = new Div();
        incomes.getStyle()
                .setWidth("415px")
                .setHeight("200px")
                .set("background-color", "#f0f0f0")
                .set("position", "relative")
                .set("direction", "rtl")
                .set("overflow", "hidden");

        incomes.addClassNames(LumoUtility.BoxShadow.SMALL);

        Span text = new Span("הכנסות ");
        text.addClassNames(
                LumoUtility.FontWeight.BOLD,
                LumoUtility.FontSize.LARGE,
                LumoUtility.TextColor.SUCCESS
        );
        text.getStyle()
                .set("margin", "10px")
                .set("position", "absolute")
                .set("top", "0")
                .set("right", "0");


        incomes.addClassNames(
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Gap.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM
        );

        incomes.add( text);


        outComes = new Div();
        outComes.getStyle()
                .setWidth("415px")
                .setHeight("200px")
                .set("background-color", "#f0f0f0")
                .set("position", "relative")
                .set("direction", "rtl")
                .set("overflow", "hidden");

        outComes.addClassNames(
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Gap.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM
        );


        Span text2 = new Span("הוצאות ");
        text2.addClassNames(
                LumoUtility.FontWeight.BOLD,
                LumoUtility.FontSize.LARGE,
                LumoUtility.TextColor.ERROR
        );
        text2.getStyle()
                .set("margin", "10px")
                .set("position", "absolute")
                .set("top", "0")
                .set("right", "0");




        outComes.add(text2);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setDefaultHorizontalComponentAlignment(Alignment.END);

        horizontalLayout1.add(outComes,incomes);

        layout.add(horizontalLayout1);

        mainLayout.add(horizontalLayout,horizontalLayout1);
        return mainLayout;
    }

    /**
     * Triggered before navigation into this view.
     * Extracts the {@code clientId} from the route parameters and stores it for later use.
     * @param event the navigation event containing route parameters
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        clientId = event.getRouteParameters().get("clientId").orElse("");



        fetchOutcomesFromBackend();
    }
    private void fetchOutcomesFromBackend() {
        Span outs = new Span();
        outs.addClassNames(
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.ERROR,
                LumoUtility.FontSize.XXLARGE
        );
        outs.getStyle()
                .set("margin", "10px")
                .set("position", "absolute")
                .set("bottom", "0")
                .set("left", "0");

        Span incs = new Span();
        incs.addClassNames(
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.SUCCESS,
                LumoUtility.FontSize.XXLARGE
        );
        incs.getStyle()
                .set("margin", "10px")
                .set("position", "absolute")
                .set("bottom", "0")
                .set("left", "0");


        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "http://localhost:8085/invoice/get-all-outcomes?clientId=" + clientId;
        String url2 = "http://localhost:8085/invoice/get-all-incomes?clientId=" + clientId;

        try {
            ResponseEntity<InvoiceOutComeM> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    InvoiceOutComeM.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                InvoiceOutComeM outcome = response.getBody();



                outs.setText("₪ " + outcome.price().toString());

            } else {
                outs.setText("");

            }

        } catch (Exception e) {
            outs.setText("שגיאה בעת טעינת סך ההוצאות");
            incs.setText("שגיאה בעת טעינת סך ההכנסות");
        }

        try {
            ResponseEntity<InvoiceIncomeNumber> responseIn = restTemplate.exchange(
                    url2,
                    HttpMethod.GET,
                    entity,
                    InvoiceIncomeNumber.class
            );

            if (responseIn.getStatusCode().is2xxSuccessful() && responseIn.getBody() != null) {
                incs.setText("₪ " + responseIn.getBody().price());
            } else {
                incs.setText("");
            }
        } catch (Exception e) {
            incs.setText("שגיאה בהכנסות");
        }

        outComes.add(outs);
        incomes.add(incs);

    }
}
