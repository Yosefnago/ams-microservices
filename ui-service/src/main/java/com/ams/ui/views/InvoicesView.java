package com.ams.ui.views;

import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.dtos.documentDto.DocumentUploadRequest;
import com.ams.dtos.documentDto.DocumentUploadResponse;
import com.ams.dtos.documentDto.LoadDocumentsResponse;
import com.ams.dtos.invoiceDto.*;
import com.ams.ui.layouts.ClientCaseLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * InvoicesView is a Vaadin view for clients to upload, view, and manage their invoices.
 *
 * The view allows:
 * - Uploading invoices with OCR-based field extraction
 * - Displaying invoices in a grid
 * - Viewing the invoice PDF
 * - Reviewing extracted data
 * - Approving or rejecting invoices (if user is ACCOUNTANT)
 *
 * This view is mapped to the route ":clientId/invoices" and uses {@link ClientCaseLayout} as layout.
 */
@Route(value = ":clientId/invoices",layout = ClientCaseLayout.class)
public class InvoicesView extends VerticalLayout implements BeforeEnterObserver {


    private Upload upload;
    private MultiFileMemoryBuffer buffer;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private Grid<InvoiceGrid> grid = new Grid<>();
    private String clientId;
    private String fileNameSelected;
    Button viewButton;
    private InvoiceGridDto extractedData;

    public InvoicesView(RestTemplate restTemplate, JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        setSizeFull();
        head();
        add(body());
    }

    public Component head(){
        VerticalLayout layout = new VerticalLayout();

        layout.setWidthFull();
        layout.setHeight("40px");

        return layout;
    }
    /**
     * Constructs the main layout of the view including:
     * - Upload button
     * - Grid displaying uploaded invoices
     * - Action buttons for view/delete
     *
     * @return the main layout component
     */
    public Component body() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        HorizontalLayout headerBody = new HorizontalLayout();
        headerBody.setHeight("50px");
        headerBody.setWidthFull();
        headerBody.setJustifyContentMode(JustifyContentMode.END);


        Button uploadButton = new Button("העלאת חשבונית");
        uploadButton.addClickListener(e -> {
            uploadDialog();
        });
        headerBody.add(uploadButton);

        grid.addColumn(InvoiceGrid::fileName).setHeader("שם מסמך");
        grid.addColumn(InvoiceGrid::invoiceNumber).setHeader("מספר חשבונית");
        grid.addColumn(InvoiceGrid::sapakName).setHeader("שם ספק");
        grid.addColumn(InvoiceGrid::price).setHeader("סכום");
        grid.addColumn(InvoiceGrid::uploadedAt).setHeader("תאריך העלאה");
        grid.addColumn(InvoiceGrid::clientId).setHeader("ת.ז ח.פ");
        grid.addColumn(InvoiceGrid::status).setHeader("סטטוס");

        grid.setSizeFull();
        grid.getStyle().set("direction", "rtl");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.getColumns().get(0).setSortable(true);
        grid.getColumns().get(1).setSortable(true);
        grid.getColumns().get(2).setSortable(true);
        grid.getColumns().get(3).setSortable(true);

        grid.addComponentColumn(client -> {
            HorizontalLayout actions = new HorizontalLayout();


            viewButton = new Button(VaadinIcon.EYE.create(), e -> {
                Notification.show("צפייה" );
                String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

                String role = jwtUtil.extractRole(token);
                if (role.equals("CLIENT")){

                    ContextMenu contextMenu = new ContextMenu(viewButton);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth(token);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<Void> entity = new HttpEntity<>(headers);
                    String url = "http://localhost:8080/invoice/get-invoice-rejectReason/" + fileNameSelected;

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
                deleteInvoice(fileNameSelected);
            });
            editButton.getElement().setProperty("title", "מחיקה");

            viewButton.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.Margin.End.SMALL);
            editButton.addClassNames(LumoUtility.IconSize.SMALL);

            actions.add(viewButton, editButton);
            return actions;

        }).setHeader("פעולות").setAutoWidth(true).setFlexGrow(0);

        grid.addSelectionListener(event -> {
            fileNameSelected = event.getFirstSelectedItem().get().fileName();
        });


        layout.add(headerBody,grid);

        return layout;
    }
    /**
     * Opens a dialog for uploading an invoice file using Vaadin Upload component.
     *
     * @return the dialog component
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
        cancel.addClickListener(event -> {
            dialog.close();
        });

        dialog.add(save,cancel);

        dialog.setOpened(true);
        return dialog;
    }
    /**
     * Sends the uploaded invoice to the backend using multipart/form-data.
     * After upload, the route is refreshed.
     *
     * @param fileName    the name of the uploaded file
     * @param fileContent the file contents as byte array
     * @param clientId    the ID of the client
     * @param status      the initial status of the invoice
     * @param date        the upload date
     */
    private void upload(String fileName, byte[] fileContent, String clientId,String status,LocalDate date){


        String token = (String)VaadinSession.getCurrent().getAttribute("jwt");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        InvoiceUploadRequest request = new InvoiceUploadRequest(fileName,fileContent,clientId,status,LocalDate.now());
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

        ResponseEntity<InvoiceUploadResponse> response = restTemplate.exchange(
                "http://localhost:8080/invoice/upload-invoice",
                HttpMethod.POST,
                requestEntity,
                InvoiceUploadResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
        } else {
            Notification.show(response.getBody().message(), 3000, Notification.Position.MIDDLE);
        }
        UI.getCurrent().refreshCurrentRoute(true);
    }
    /**
     * Displays a dialog with:
     * - The invoice PDF
     * - Extracted invoice data (number, supplier, price, VAT)
     * - Buttons for approving or rejecting the invoice
     *
     * Only available for ACCOUNTANT users.
     */
    private void viewDocument() {
        Dialog dialog = createDialog();
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

        String fixedFileName = fileNameSelected.replaceAll("\\.pdf\\.pdf$", ".pdf");

        try {
            this.extractedData = fetchInvoiceData(fixedFileName, token);
        } catch (Exception e) {
            Notification.show("שגיאה בשליפת נתונים", 3000, Notification.Position.MIDDLE);
            return;
        }

        StreamResource pdfResource;
        try {
            pdfResource = fetchInvoicePdf(fixedFileName, token);
        } catch (Exception e) {
            Notification.show("שגיאה בטעינת קובץ", 3000, Notification.Position.MIDDLE);
            return;
        }

        IFrame iframe = new IFrame(VaadinSession.getCurrent().getResourceRegistry().registerResource(pdfResource).getResourceUri().toString());
        iframe.setSizeFull();

        VerticalLayout invoiceDetails = new VerticalLayout(
                new TextField("מספר חשבונית: " + extractedData.invoiceNumber()),
                new TextField("ספק: " + extractedData.sapakName()),
                new TextField("סכום לפני מע\"מ: " + extractedData.priceBeforeVat()),
                new TextField("סכום כולל מע\"מ: " + extractedData.price())
        );

        TextArea rejectReason = new TextArea("סיבת דחייה");
        rejectReason.setPlaceholder("נא לציין מדוע נדחה");
        rejectReason.setWidthFull();

        Button approveButton = new Button("אשר", e -> {
            updateInvoiceStatus("אושר", null);
            dialog.close();
            UI.getCurrent().refreshCurrentRoute(true);
        });

        Button rejectButton = new Button("דחה", e -> {
            updateInvoiceStatus("נדחה", rejectReason.getValue());
            dialog.close();
            UI.getCurrent().refreshCurrentRoute(true);
        });

        HorizontalLayout actions = new HorizontalLayout(approveButton, rejectButton);
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout leftSide = new VerticalLayout(invoiceDetails, rejectReason, actions);
        leftSide.setWidth("40%");
        iframe.setWidth("60%");

        SplitLayout splitLayout = new SplitLayout(leftSide, iframe);
        splitLayout.setSizeFull();

        dialog.add(splitLayout);
        dialog.open();
    }
    /**
     * Calls the backend to analyze the invoice and extract fields (OCR).
     *
     * @param fileName the invoice file name
     * @param token    JWT token
     * @return the extracted invoice DTO
     */
    private InvoiceGridDto fetchInvoiceData(String fileName, String token) {
        String url = "http://localhost:8080/invoice/analyze-invoice/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<InvoiceGridDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, InvoiceGridDto.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("שגיאה בנתונים");
        }
        return response.getBody();
    }
    /**
     * Retrieves the PDF binary content of the invoice from backend and wraps it as a StreamResource.
     *
     * @param fileName the invoice file name
     * @param token    JWT token
     * @return StreamResource containing the invoice PDF
     */
    private StreamResource fetchInvoicePdf(String fileName, String token) {
        String url = "http://localhost:8080/invoice/get-invoice/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("קובץ לא נטען");
        }

        return new StreamResource(fileName, () -> new ByteArrayInputStream(response.getBody()));
    }
    /**
     * Helper method to create a styled, modal, resizable dialog.
     *
     * @return a new dialog instance
     */
    private Dialog createDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("1000px");
        dialog.setHeight("800px");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        return dialog;
    }
    /**
     * Updates the status of an invoice in the backend (approve/reject).
     * Includes relevant invoice details and rejection reason.
     *
     * @param newStatus the new status (e.g. "אושר", "נדחה")
     * @param reason    optional rejection reason (null if approved)
     */
    private void updateInvoiceStatus(String newStatus, String reason) {
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("fileName", fileNameSelected);
        body.put("status", newStatus);
        body.put("invoiceNumber", extractedData.invoiceNumber());
        body.put("sapakName", extractedData.sapakName());
        body.put("price", extractedData.price());
        if (reason != null) body.put("rejectionReason", reason);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    "http://localhost:8080/invoice/update-invoice-status",
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("החשבונית עודכנה", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("עדכון נכשל", 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("שגיאה בעדכון", 3000, Notification.Position.MIDDLE);
        }
    }
    /**
     * Sends a DELETE request to remove the selected invoice from the system.
     *
     * @param fileNameSelected the name of the invoice file to delete
     */
    private void deleteInvoice(String fileNameSelected){
        String token = (String)VaadinSession.getCurrent().getAttribute("jwt");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8080/invoice/delete-invoice/" + fileNameSelected;

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
     * Triggered before navigation to the view. Initializes the clientId from route parameters
     * and loads all invoices associated with this client from the backend.
     *
     * @param event navigation event
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
            ResponseEntity<LoadInvoicesResponse> response = restTemplate.exchange(
                    "http://localhost:8080/invoice/load-invoices" ,
                    HttpMethod.GET, entity, LoadInvoicesResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                grid.setItems(response.getBody().invoiceGrid());
            } else {
                grid.setItems(List.of());
            }

        } catch (Exception e) {
            Notification.show("שגיאה בטעינת המסמכים: ", 3000, Notification.Position.MIDDLE);

        }
    }

}
