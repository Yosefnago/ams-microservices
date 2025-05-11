package com.ams.ui.views;


import com.vaadin.flow.component.accordion.Accordion;

import com.vaadin.flow.component.accordion.AccordionPanel;

import com.vaadin.flow.component.html.Div;

import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.vaadin.flow.component.avatar.Avatar;

import com.vaadin.flow.component.details.Details;

import com.vaadin.flow.component.html.Span;

import com.ams.ui.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "settings",layout = MainLayout.class)
@PageTitle("Settings")
public class SettingsView extends VerticalLayout  {

    public SettingsView() {

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.getStyle().setWidth("100%").setHeight("610px");
        mainLayout.getStyle().setAlignItems(Style.AlignItems.END);

        SideNav sideNav = new SideNav();
        sideNav.setWidth("200px");
        sideNav.getStyle().setBackgroundColor("#00706B");

        SideNavItem sideNavItem1 = new SideNavItem("רישום לקוח");
        sideNav.addItem(sideNavItem1);
        mainLayout.add(sideNav);

        add(mainLayout);
    }


}
