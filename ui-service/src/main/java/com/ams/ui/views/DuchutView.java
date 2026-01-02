package com.ams.ui.views;


import com.ams.ui.layouts.ClientCaseLayout;
import com.helger.commons.datetime.PDTFactory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Route(value = ":clientId/duchut",layout = ClientCaseLayout.class)
public class DuchutView extends VerticalLayout {


    public DuchutView() {
        setSizeFull();
        setSpacing(true);

        add(body());
    }

    public Component body() {
        VerticalLayout body = new VerticalLayout();

        Button open = new Button("דוח רווח והפסד");

        open.addClickListener(e -> {
            StreamResource resource = new StreamResource("report.pdf", () -> {
                return new ByteArrayInputStream(pdfGenerator());
            });
            Anchor download = new Anchor(resource, "");
            download.getElement().setAttribute("download", true);
            download.add(new Button("הורד דוח"));
            body.add(download);
        });

        body.add(open);
        return body;
    }
    private byte[] pdfGenerator() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);

            document.open();
            document.add(new com.lowagie.text.Paragraph("דו\"ח רווח והפסד"));
            document.add(new com.lowagie.text.Paragraph("הכנסות: 100,000 ₪"));
            document.add(new com.lowagie.text.Paragraph("הוצאות: 40,000 ₪"));
            document.add(new com.lowagie.text.Paragraph("רווח נקי: 60,000 ₪"));
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("שגיאה ביצירת PDF", e);
        }
    }


}
