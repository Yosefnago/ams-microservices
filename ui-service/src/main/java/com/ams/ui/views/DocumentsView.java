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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
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

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;


@Route(value = ":clientId/documents",layout = ClientCaseLayout.class)
public class DocumentsView extends VerticalLayout implements BeforeEnterObserver {

    private Upload upload;
    private MultiFileMemoryBuffer buffer;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    String clientId;
    private Grid<DocumentGrid> grid = new Grid<>();
    String documentNameSelected;

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


                String status = "PENDING";
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
                "http://localhost:8080/client/upload",
                HttpMethod.POST,
                requestEntity,
                DocumentUploadResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
        } else {
            Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
        }

    }
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

            // Client view icon
            Button viewButton = new Button(VaadinIcon.EYE.create(), e -> {
                Notification.show("צפייה" );
            });
            viewButton.getElement().setProperty("title", "צפייה");

            // edit icon
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
                    "http://localhost:8080/client/load-documents",
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
    private void deleteDocument(String documentNameSelected){
        String token = (String)VaadinSession.getCurrent().getAttribute("jwt");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8080/client/delete-document/" + documentNameSelected;

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE, entity, Void.class);

            if(response.getStatusCode().is2xxSuccessful()){
                Notification.show("נמחק בהצלחה");
            }

        }catch (HttpClientErrorException e){
            Notification.show("מחיקה נכשלה." , 3000, Notification.Position.MIDDLE);
        }
    }
}
