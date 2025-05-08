package com.ams.ui.views;


import com.ams.ui.layouts.ClientCaseLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.Map;

@Route(value = ":clientId/documents",layout = ClientCaseLayout.class)
public class DocumentsView extends VerticalLayout implements BeforeEnterObserver {

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
    public Component body() {
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

        return grid;
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        clientId = event.getRouteParameters().get("clientId").orElse("");
    }
}
