package com.ams.ui.views;



import com.ams.dtos.clientDto.LoadClientCaseDetailsRequest;
import com.ams.dtos.clientDto.UpdateClientResponse;
import com.ams.ui.layouts.ClientCaseLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;


/**
 * {@code ClientCaseView} is the main Vaadin view for displaying and managing a client's case file.
 *
 * <p>This view is associated with the route {@code /case/:clientId} and uses {@link ClientCaseLayout} as its layout.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *     <li>Loads client data from the backend using the provided {@code clientId} parameter</li>
 *     <li>Displays interactive components like update form, documents, invoices, reports, and quick actions</li>
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
@Route(value = "case/:clientId", layout = ClientCaseLayout.class)
@PageTitle("Case")
public class ClientCaseView extends VerticalLayout implements BeforeEnterObserver {

    private List<TextField> allFields;
    private String clientId;
    private TextField businessNameFiled;
    private TextField clientIdFiled;
    private TextField emailFiled;
    private TextField phoneFiled;
    private TextField addressFiled;
    private TextField zipCodeFiled;
    private TextField businessTypeFiled;
    private TextField bankNameFiled;
    private TextField bankBranchFiled;
    private TextField bankAccountNumberFiled;
    private final RestTemplate restTemplate;
    VerticalLayout contentLayout;
    Button updateButton,saveButton;
    String msg;
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

        Div documentsDiv = new Div("מסמכים");
        documentsDiv.getStyle().set("background-color", "#f0f0f0");
        documentsDiv.getStyle().setHeight("100px");
        documentsDiv.getStyle().setWidth("200px");
        documentsDiv.addClickListener(e -> {
           UI.getCurrent().navigate(clientId + "/documents");
        });

        Div invoicesDiv = new Div("חשבוניות");
        invoicesDiv.getStyle().set("background-color", "#f0f0f0");
        invoicesDiv.getStyle().setHeight("100px");
        invoicesDiv.getStyle().setWidth("200px");

        Div reportsDiv = new Div("דיווחים");
        reportsDiv.getStyle().set("background-color", "#f0f0f0");
        reportsDiv.getStyle().setHeight("100px");
        reportsDiv.getStyle().setWidth("200px");

        Div duchutDiv = new Div("דוחות");
        duchutDiv.getStyle().set("background-color", "#f0f0f0");
        duchutDiv.getStyle().setHeight("100px");
        duchutDiv.getStyle().setWidth("200px");
        

        horizontalLayout.add(duchutDiv,reportsDiv,invoicesDiv,documentsDiv);

        mainLayout.add(horizontalLayout);
        return mainLayout;
    }


    /**
     * Builds the form for updating client case details with REST data fetching and JWT authorization.
     * @return a vertical layout containing form fields and control buttons
     */
    private Component updateCaseDetails(){

        updateButton = new Button("ערוך");
        updateButton.getStyle().setAlignSelf(Style.AlignSelf.END);

        saveButton = new Button("שמור");
        saveButton.getStyle().setAlignSelf(Style.AlignSelf.END);
        saveButton.setVisible(false);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.getStyle().set("background-color", "white");


        businessNameFiled = new TextField("שם עסק");
        clientIdFiled = new TextField("ח.פ/ת.ז");
        emailFiled = new TextField("אימייל");
        phoneFiled = new TextField("מספר טלפון");
        addressFiled = new TextField("כתובת");
        zipCodeFiled = new TextField("מיקוד");
        businessTypeFiled = new TextField("סוג עוסק");
        bankNameFiled = new TextField("שם בנק");
        bankBranchFiled = new TextField("מספר סניף");
        bankAccountNumberFiled = new TextField("מספר חשבון");
        allFields = List.of(
                businessNameFiled,
                clientIdFiled,
                emailFiled,
                phoneFiled,
                addressFiled,
                zipCodeFiled,
                businessTypeFiled,
                bankNameFiled,
                bankBranchFiled,
                bankAccountNumberFiled
        );
        blockFields();

        String token = (String) getUI().get().getSession().getAttribute("jwt");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<LoadClientCaseDetailsRequest> requestEntity =
                    restTemplate.exchange("http://localhost:8080/client/load-client-case?clientId=" + clientId,
                            HttpMethod.GET, entity, LoadClientCaseDetailsRequest.class);

            if (requestEntity.getStatusCode().is2xxSuccessful() && requestEntity.getBody() != null) {
                businessNameFiled.setValue(requestEntity.getBody().businessName().toString());
                clientIdFiled.setValue(requestEntity.getBody().clientId().toString());
                emailFiled.setValue(requestEntity.getBody().email().toString());
                phoneFiled.setValue(requestEntity.getBody().phone().toString());
                addressFiled.setValue(requestEntity.getBody().address().toString());
                zipCodeFiled.setValue(requestEntity.getBody().zip().toString());
                businessTypeFiled.setValue(requestEntity.getBody().businessType().toString());
                bankNameFiled.setValue(requestEntity.getBody().bankName().toString());
                bankBranchFiled.setValue(requestEntity.getBody().bankBranch().toString());
                bankAccountNumberFiled.setValue(requestEntity.getBody().bankAccountNumber().toString());
            } else {
                Notification.show("לקוח לא נמצא", 3000, Notification.Position.MIDDLE);
            }

        }catch (Exception e){
            Notification.show("שגיאה בטעינת לקוח", 3000, Notification.Position.MIDDLE);

        }

        updateButton.addClickListener(e -> {
            blockFields();
            saveButton.setVisible(true);
            saveButton.addClickListener(ee -> {
                update();
                blockFields();
            });
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(businessNameFiled,clientIdFiled,emailFiled,phoneFiled);
        formLayout.add(addressFiled,zipCodeFiled,businessTypeFiled);
        formLayout.add(bankNameFiled,bankBranchFiled,bankAccountNumberFiled);

        formLayout.getElement().setAttribute("dir", "rtl");
        HorizontalLayout buttonLayout = new HorizontalLayout();

        buttonLayout.add(saveButton,updateButton);
        mainLayout.setDefaultHorizontalComponentAlignment(Alignment.END);
        mainLayout.add(buttonLayout,formLayout);
        return mainLayout;
    }
    /**
     * Updates the client case by sending a PUT request to the backend using JWT authentication.
     */
    private void update(){
        LoadClientCaseDetailsRequest request = new LoadClientCaseDetailsRequest(
                clientIdFiled.getValue(),
                businessNameFiled.getValue(),
                emailFiled.getValue(),
                phoneFiled.getValue(),
                addressFiled.getValue(),
                zipCodeFiled.getValue(),
                businessTypeFiled.getValue(),
                bankNameFiled.getValue(),
                bankBranchFiled.getValue(),
                bankAccountNumberFiled.getValue()
        );
        String token = (String) getUI().get().getSession().getAttribute("jwt");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoadClientCaseDetailsRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<UpdateClientResponse> response = restTemplate.exchange(
                    "http://localhost:8080/client/update",
                    HttpMethod.PUT,
                    entity,
                    UpdateClientResponse.class
            );
            msg = response.getBody().message();
            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show(msg, 3000, Notification.Position.MIDDLE);
                saveButton.setVisible(false);
            } else {
                Notification.show(msg, 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show(msg, 3000, Notification.Position.MIDDLE);
        }
    }
    /**
     * Enables or disables editing for all text fields based on current read-only state.
     */
    private void blockFields() {
        boolean shouldUnlock = allFields.get(0).isReadOnly();

        allFields.forEach(field -> field.setReadOnly(!shouldUnlock));
    }
    /**
     * Triggered before navigation into this view.
     * Extracts the {@code clientId} from the route parameters and stores it for later use.
     * @param event the navigation event containing route parameters
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        clientId = event.getRouteParameters().get("clientId").orElse("לא ידוע");

    }
}
