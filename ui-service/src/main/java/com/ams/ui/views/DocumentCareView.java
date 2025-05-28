package com.ams.ui.views;

import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.documentDto.DocumentCareGridDto;
import com.ams.dtos.documentDto.LoadDocumentsCareGridResponse;
import com.ams.ui.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "document-care", layout = MainLayout.class)
@RolesAllowed("ACCOUNTANT")
@PageTitle("Documents")
public class DocumentCareView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<DocumentCareGridDto> grid = new Grid<>();
    private final JwtUtil jwtUtil;
    private RestTemplate restTemplate;
    String documentNameSelected;


    @Autowired
    public DocumentCareView(JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        setSizeFull();
        gridContent();
    }
    private void gridContent(){

        grid.addColumn(DocumentCareGridDto::bussName).setHeader("שם לקוח");
        grid.addColumn(DocumentCareGridDto::fileName).setHeader("שם מסמך");
        grid.addColumn(DocumentCareGridDto::date).setHeader("תאריך העלאה");
        grid.addColumn(DocumentCareGridDto::status).setHeader("סטטוס");

        grid.getElement().setAttribute("dir", "rtl");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.getColumns().get(0).setSortable(true);
        grid.getColumns().get(1).setSortable(true);
        grid.getColumns().get(2).setSortable(true);
        grid.getColumns().get(3).setSortable(true);

        grid.addComponentColumn(documentCareGridDto -> {

            HorizontalLayout actionLayout = new HorizontalLayout();

            Button viewButton = new Button(VaadinIcon.EYE.create(),event -> {
                String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

                String role = jwtUtil.extractRole(token);
                if (role.equals("ACCOUNTANT")) {
                    viewDocument();
                }
            });
            viewButton.getElement().setProperty("title","הצג מסמך");

            actionLayout.add(viewButton);

           return actionLayout;
        }).setHeader("פעולות").setAutoWidth(true).setFlexGrow(0);

        grid.addSelectionListener(event -> {
           documentNameSelected = event.getFirstSelectedItem().get().fileName();
        });

        add(grid);
    }
    private void viewDocument() {
        Dialog dialog = new Dialog();
        dialog.setWidth("800px");
        dialog.setHeight("600px");

        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "http://localhost:8080/client/get-document/" + documentNameSelected;

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                StreamResource resource = new StreamResource(
                        documentNameSelected,
                        () -> new ByteArrayInputStream(response.getBody())
                );
                resource.setContentType("application/pdf");

                String resourceUrl = VaadinSession.getCurrent()
                        .getResourceRegistry()
                        .registerResource(resource)
                        .getResourceUri().toString();

                IFrame iframe = new IFrame();
                iframe.setSrc(resourceUrl);
                iframe.setSizeFull();

                Button approveButton = new Button("אשר", e -> {
                    updateDocumentStatus("אושר", null);
                    
                    dialog.close();
                    UI.getCurrent().refreshCurrentRoute(true);
                });

                TextArea rejectReason = new TextArea("סיבת דחייה");
                rejectReason.setPlaceholder("נא לציין מדוע נדחה");
                rejectReason.setWidthFull();

                Button rejectButton = new Button("דחה", e -> {
                    updateDocumentStatus("נדחה", rejectReason.getValue());
                    dialog.close();
                    UI.getCurrent().refreshCurrentRoute(true);
                });

                HorizontalLayout actions = new HorizontalLayout(approveButton, rejectButton);
                actions.setWidthFull();
                actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

                dialog.add(iframe, rejectReason, actions);
            } else {
                Notification.show("שגיאה בהבאת המסמך", 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("שגיאה בטעינה", 3000, Notification.Position.MIDDLE);
        }

        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.open();


    }
    private void updateDocumentStatus(String newStatus, String reason) {
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("documentName", documentNameSelected);
        body.put("status", newStatus);
        if (reason != null) body.put("rejectionReason", reason);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    "http://localhost:8080/client/update-document-status",
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("המסמך עודכן", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("עדכון נכשל", 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("שגיאה בעדכון", 3000, Notification.Position.MIDDLE);
        }
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");
        if (token == null) {
            event.forwardTo("login");
            return;
        }

        String message = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<LoadDocumentsCareGridResponse> response = restTemplate.exchange(
                    "http://localhost:8080/client/loadDocumentsCareList",
                    HttpMethod.GET,
                    entity,
                    LoadDocumentsCareGridResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                grid.setItems(response.getBody().documentGrids());
            } else {
                message = response.getBody() != null ? response.getBody().message() : "שגיאה בלתי צפויה";
                Notification.show(message, 3000, Notification.Position.MIDDLE);
            }

        } catch (Exception e) {
            Notification.show("שגיאה בטעינת מסמכים", 3000, Notification.Position.MIDDLE);
        }
    }
}
