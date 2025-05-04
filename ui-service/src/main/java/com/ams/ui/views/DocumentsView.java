package com.ams.ui.views;


import com.ams.ui.layouts.ClientCaseLayout;
import com.vaadin.flow.component.Component;
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

@Route(value = ":clientId/documents",layout = ClientCaseLayout.class)
public class DocumentsView extends VerticalLayout implements BeforeEnterObserver {

    private String clientId;

    public DocumentsView() {
        head();

    }
    public Component head(){
        VerticalLayout layout = new VerticalLayout();

        layout.setWidthFull();
        layout.setHeight("40px");

        return layout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        clientId = event.getRouteParameters().get("clientId").orElse("");
    }
}
