package com.ams.ui.views;


import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.clientDto.LoadClientResponse;
import com.ams.dtos.documentDto.DocumentGrid;
import com.ams.dtos.documentDto.DocumentUploadRequest;
import com.ams.dtos.documentDto.DocumentUploadResponse;
import com.ams.dtos.documentDto.LoadDocumentsResponse;
import com.ams.ui.layouts.ClientCaseLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DocumentsView is responsible for displaying,
 * uploading, deleting, viewing, and updating the status of documents
 * associated with a specific client.
 *
 * It handles both client-side and accountant-side interactions with documents.
 *
 * This view is mapped to the route ":clientId/documents".
 */
@Route(value = ":clientId/documents",layout = ClientCaseLayout.class)
public class DocumentsView extends VerticalLayout implements BeforeEnterObserver {

    private Upload upload;
    private MultiFileMemoryBuffer buffer;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    String clientId;
    private Grid<DocumentGrid> grid = new Grid<>();
    String documentNameSelected;
    Button viewButton;

    @Autowired
    public DocumentsView(RestTemplate restTemplate, JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;

        head();

        setSizeFull();
        add(body());
    }
    public Component head(){
        VerticalLayout layout = new VerticalLayout();

        layout.setWidthFull();
        layout.setHeight("40px");

        return layout;
    }
    /**
     * Opens a dialog that allows the user to upload a document.
     *
     * @return the upload dialog component
     */
    private Component uploadDialog(){

        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("500px");

        buffer = new MultiFileMemoryBuffer();
        upload = new Upload(buffer);
        upload.setAutoUpload(true);

        dialog.add(upload);


        Button save = new Button("שמור");
        save.getStyle().setMarginRight("2px");
        save.addClickListener(event -> {
            String uploadedFileName = buffer.getFiles().stream().findFirst().orElse(null);

            try {

                InputStream inputStream = buffer.getInputStream(uploadedFileName);
                byte[] fileData = inputStream.readAllBytes();


                String status = "ממתין לטיפול";
                LocalDate date = LocalDate.now();

                upload(uploadedFileName, fileData, clientId, status, date);

                dialog.close();
            } catch (IOException e) {

            }
        });

        Button cancel = new Button("בטל");


        dialog.add(save,cancel);

        dialog.setOpened(true);
        return dialog;
    }
    /**
     * Sends a multipart upload request to the backend with the document data.
     *
     * @param fileName    the name of the file
     * @param fileContent the file content in byte array
     * @param clientId    the client ID
     * @param status      the initial status of the document
     * @param date        the upload date
     */
    private void upload(String fileName, byte[] fileContent, String clientId,String status,LocalDate date){


        String token = (String)VaadinSession.getCurrent().getAttribute("jwt");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        DocumentUploadRequest request = new DocumentUploadRequest(fileName,fileContent,clientId,status,LocalDate.now());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource fileAsResource = new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        body.add("file", fileAsResource);
        body.add("clientId", clientId);
        body.add("status", status);
        body.add("uploadedAt", date.toString());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<DocumentUploadResponse> response = restTemplate.exchange(
                "http://localhost:8080/document/upload",
                HttpMethod.POST,
                requestEntity,
                DocumentUploadResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
        } else {
            Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
        }
        UI.getCurrent().refreshCurrentRoute(true);
    }
    /**
     * Constructs the main UI layout including the upload button and document grid.
     *
     * @return the layout component
     */
    public Component body() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        HorizontalLayout headerBody = new HorizontalLayout();
        headerBody.setHeight("50px");
        headerBody.setWidthFull();
        headerBody.setJustifyContentMode(JustifyContentMode.END);


        Button uploadButton = new Button("העלאת מסמך");
        uploadButton.addClickListener(e -> {
            uploadDialog();
        });
        headerBody.add(uploadButton);


        grid.addColumn(DocumentGrid::fileName).setHeader("שם מסמך");
        grid.addColumn(DocumentGrid::clientId).setHeader("ת.ז . ח.פ");
        grid.addColumn(DocumentGrid::uploadedAt).setHeader("תאריך העלאה");
        grid.addColumn(DocumentGrid::status).setHeader("סטטוס");

        grid.setSizeFull();
        grid.getStyle().set("direction", "rtl");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.getColumns().get(0).setSortable(true);
        grid.getColumns().get(1).setSortable(true);
        grid.getColumns().get(2).setSortable(true);
        grid.getColumns().get(3).setSortable(true);

        grid.addComponentColumn(client -> {
            HorizontalLayout actions = new HorizontalLayout();

            // Document view
            viewButton = new Button(VaadinIcon.EYE.create(), e -> {
                Notification.show("צפייה" );
                String token = (String)VaadinSession.getCurrent().getAttribute("jwt");

                String role = jwtUtil.extractRole(token);
                if (role.equals("CLIENT")){

                    ContextMenu contextMenu = new ContextMenu(viewButton);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth(token);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<Void> entity = new HttpEntity<>(headers);
                    String url = "http://localhost:8080/document/get-document-rejectReason/" + documentNameSelected;

                    ResponseEntity<String> response = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            String.class
                    );
                    contextMenu.setOpenOnClick(true);
                    contextMenu.removeAll();
                    contextMenu.add(new Paragraph(response.getBody()));

                }
                if (role.equals("ACCOUNTANT")) {
                    viewDocument();
                }

            });
            viewButton.getElement().setProperty("title", "צפייה");

            // Delete icon
            Button editButton = new Button(VaadinIcon.TRASH.create(), e -> {
                deleteDocument(documentNameSelected);
            });
            editButton.getElement().setProperty("title", "מחיקה");

            viewButton.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.Margin.End.SMALL);
            editButton.addClassNames(LumoUtility.IconSize.SMALL);

            actions.add(viewButton, editButton);
            return actions;

        }).setHeader("פעולות").setAutoWidth(true).setFlexGrow(0);

        grid.addSelectionListener(event -> {
           documentNameSelected = event.getFirstSelectedItem().get().fileName();
        });


        layout.add(headerBody,grid);

        return layout;
    }
    /**
     * Triggered before entering the view. Loads the documents for the client.
     *
     * @param event the navigation event
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        clientId = event.getRouteParameters().get("clientId").orElse("");
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("clientId", clientId);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<LoadDocumentsResponse> response = restTemplate.exchange(
                    "http://localhost:8080/document/load-documents",
                    HttpMethod.GET, entity, LoadDocumentsResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                grid.setItems(response.getBody().documentGrids());
            } else {
                grid.setItems(List.of());
            }

        } catch (Exception e) {
            Notification.show("שגיאה בטעינת המסמכים: ", 3000, Notification.Position.MIDDLE);

        }
    }
    /**
     * Sends a DELETE request to remove a document by its name.
     *
     * @param documentNameSelected the name of the document to delete
     */
    private void deleteDocument(String documentNameSelected){
        String token = (String)VaadinSession.getCurrent().getAttribute("jwt");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8080/document/delete-document/" + documentNameSelected;

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE, entity, Void.class);

            if(response.getStatusCode().is2xxSuccessful()){
                Notification.show("נמחק בהצלחה");
                UI.getCurrent().refreshCurrentRoute(true);
            }

        }catch (HttpClientErrorException e){
            Notification.show("מחיקה נכשלה." , 3000, Notification.Position.MIDDLE);
        }

    }
    /**
     * Opens a dialog to display a PDF document in an iframe with action buttons
     * for approve or reject, available to accountants only.
     */
    private void viewDocument() {
        Dialog dialog = new Dialog();
        dialog.setWidth("800px");
        dialog.setHeight("600px");

        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "http://localhost:8080/document/get-document/" + documentNameSelected;

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
                actions.setJustifyContentMode(JustifyContentMode.END);

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
    /**
     * Sends a PUT request to update the document's status (approved/rejected).
     *
     * @param newStatus the new status to update
     * @param reason    the reason for rejection (nullable)
     */
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
                    "http://localhost:8080/document/update-document-status",
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
}
