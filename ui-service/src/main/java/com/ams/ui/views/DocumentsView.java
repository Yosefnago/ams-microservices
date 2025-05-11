package com.ams.ui.views;


import com.ams.ui.layouts.ClientCaseLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import java.util.List;
import java.util.Map;

@Route(value = ":clientId/documents",layout = ClientCaseLayout.class)
public class DocumentsView extends VerticalLayout implements BeforeEnterObserver {

    private Upload upload;
    private MultiFileMemoryBuffer buffer;
    private String clientId;

    public DocumentsView() {
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
    private Component afterUpload(String fileName) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.getStyle().setAlignItems(Style.AlignItems.CENTER);
        FormLayout formLayout = new FormLayout();
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("550px");

        TextField fileNameField = new TextField("שם מסמך");
        fileNameField.setValue(fileName);
        fileNameField.setReadOnly(true);

        TextField invoiceNumber = new TextField("מספר חשבונית");
        DatePicker date = new DatePicker("תאריך");
        TextField supplier = new TextField("שם ספק");
        NumberField amount = new NumberField("סכום כולל");
        ComboBox<String> docType = new ComboBox<>("סוג מסמך");
        docType.setItems("חשבונית", "קבלה", "דו\"ח כספי", "הסכם שירות");

        Button approve = new Button("אשר", e -> {
            dialog.close();

        });

        Button cancel = new Button("בטל", e -> dialog.close());

        HorizontalLayout row1 = new HorizontalLayout();
        row1.setJustifyContentMode(JustifyContentMode.CENTER);
        HorizontalLayout row2 = new HorizontalLayout();
        row2.setJustifyContentMode(JustifyContentMode.CENTER);
        HorizontalLayout row3 = new HorizontalLayout();
        row3.setJustifyContentMode(JustifyContentMode.CENTER);
        HorizontalLayout row4 = new HorizontalLayout();
        row4.setJustifyContentMode(JustifyContentMode.CENTER);

        row1.add(fileNameField,invoiceNumber);
        row2.add(date,supplier);
        row3.add(amount,docType);
        row4.add(approve,cancel);

        formLayout.add(row1,1);
        formLayout.add(row2,1);
        formLayout.add(row3,1);
        formLayout.add(row4,1);

        layout.add(formLayout);

        dialog.add(layout);
        dialog.setOpened(true);
        return dialog;
    }
    private Component uploadDialog(){

        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("500px");

        buffer = new MultiFileMemoryBuffer();
        upload = new Upload(buffer);
        upload.setAutoUpload(true);


        dialog.add(upload);

        Checkbox aiDec = new Checkbox("חלץ מידע");

        Button save = new Button("החל");
        save.getStyle().setMarginRight("2px");
        save.addClickListener(event -> {
            dialog.close();
            for (String uploadedFileName : buffer.getFiles()) {
                afterUpload(uploadedFileName); // שולח את שם הקובץ לתיבה הבאה
                break; // נטפל בקובץ אחד לדוגמה
            }
        });

        Button cancel = new Button("בטל");


        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.add(aiDec);
        dialog.add(checkboxLayout);


        dialog.add(save,cancel);



        dialog.setOpened(true);
        return dialog;
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


        Grid<Map<String, Object>> grid = new Grid<>();

        grid.addColumn(doc -> doc.get("name")).setHeader("שם מסמך");
        grid.addColumn(doc -> doc.get("createdAt")).setHeader("תאריך");
        grid.addColumn(doc -> doc.get("createdBy")).setHeader("יוצר");

        List<Map<String, Object>> documents = List.of(
                Map.of(
                        "name", "דו\"ח כספי",
                        "createdAt", "08/05/2025",
                        "createdBy", "יוסי כהן"
                ),
                Map.of(
                        "name", "חשבונית מס",
                        "createdAt", "06/05/2025",
                        "createdBy", "אנה לוי"
                ),
                Map.of(
                        "name", "הסכם שירות",
                        "createdAt", "01/05/2025",
                        "createdBy", "דוד פרץ"
                )
        );

        grid.setItems(documents);
        grid.setSizeFull();
        grid.getStyle().set("direction", "rtl");

        layout.add(headerBody,grid);

        return layout;
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        clientId = event.getRouteParameters().get("clientId").orElse("");
    }
}
