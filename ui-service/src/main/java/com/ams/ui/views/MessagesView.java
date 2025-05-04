package com.ams.ui.views;


import com.ams.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "messages",layout = MainLayout.class)
@PageTitle("Messages")
public class MessagesView extends VerticalLayout {

    public MessagesView() {}

}
