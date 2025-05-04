package com.ams.ui.views;


import com.ams.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "settings",layout = MainLayout.class)
@PageTitle("Settings")
public class SettingsView extends VerticalLayout  {

    public SettingsView() {}

}
