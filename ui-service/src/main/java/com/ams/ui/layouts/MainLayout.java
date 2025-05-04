package com.ams.ui.layouts;

import com.ams.commonsecurity.utils.JwtUtil;
import com.ams.ui.views.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@code MainLayout} defines the primary application layout for all main views in the AMS system.
 * <p>
 * It includes a fixed top header with branding, user session options, and a horizontal navigation bar.
 * JWT authentication is validated on each route entry to ensure authorized access.
 * </p>
 *
 * <p><b>Main Components:</b></p>
 * <ul>
 *     <li>Header with logo, avatar, and context menu</li>
 *     <li>Navigation bar with tabs for major views (Dashboard, Clients, Messages, Settings)</li>
 *     <li>Route protection based on JWT token</li>
 * </ul>
 *
 * <p><b>Usage:</b> Applied automatically to any {@code @Route} view using this layout</p>
 *
 * @see com.vaadin.flow.component.applayout.AppLayout
 * @see com.vaadin.flow.router.BeforeEnterObserver
 * @see com.vaadin.flow.component.tabs.Tabs
 * @see com.vaadin.flow.server.VaadinSession
 * @see com.ams.commonsecurity.utils.JwtUtil
 *
 * @author Yosef Nago
 */
@VaadinSessionScope
public class MainLayout extends AppLayout implements BeforeEnterObserver {


    private final Tabs tabs = new Tabs();
    private final JwtUtil jwtUtil;

    /**
     * Constructs the main layout with header and navigation components.
     */
    public MainLayout(@Autowired  JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        createHeader();
        createNavigation();
    }
    /**
     * Builds the top header layout, including logo, avatar, dark mode toggle, and user menu.
     */
    private void createHeader() {
        Image logo = new Image("logo.png", "לוגו");
        logo.setWidth("110px");
        logo.setHeight("80px");

        Avatar avatar = new Avatar();
        avatar.getStyle().setCursor("pointer");
        avatar.addClassNames(
                LumoUtility.IconSize.LARGE,
                LumoUtility.AlignSelf.CENTER,
                LumoUtility.JustifyContent.START,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.TextColor.PRIMARY
        );

        Button logout = new Button();
        logout.setIcon(VaadinIcon.SIGN_OUT.create());
        logout.getElement().getStyle().set("color", "var(--lumo-error-color)");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        headerLayout.add(logo,avatar);

        ContextMenu contextMenu = new ContextMenu(avatar);
        contextMenu.setOpenOnClick(true);
        String username = jwtUtil.extractUsername((String) VaadinSession.getCurrent().getAttribute("jwt"));

        contextMenu.addItem(username + " :משתמש מחובר ").setEnabled(false);
        contextMenu.addItem("הפרופיל שלי", e -> {
            getUI().ifPresent(ui -> ui.navigate("settings"));
        });


        contextMenu.addItem("התנתק", e -> {
            VaadinSession.getCurrent().setAttribute("jwt", null);
            VaadinSession.getCurrent().close();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        contextMenu.getItems().get(0).addClassName(LumoUtility.TextColor.HEADER);
        contextMenu.getItems().get(1).addClassName(LumoUtility.TextColor.PRIMARY);
        contextMenu.getItems().get(2).addClassName(LumoUtility.TextColor.ERROR);


        addToNavbar(headerLayout);
    }
    /**
     * Builds the top navigation tabs for the main sections of the app.
     */
    private void createNavigation() {

        tabs.add(
                createTab("הגדרות", SettingsView.class, VaadinIcon.TOOLS),
                createTab("הודעות", MessagesView.class, VaadinIcon.MAILBOX),
                createTab("לקוחות", ClientsView.class, VaadinIcon.USERS),
                createTab("דשבורד", DashboardView.class, VaadinIcon.DASHBOARD)
                );
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.setSelectedIndex(1);

        tabs.getStyle().setFlexDirection(Style.FlexDirection.ROW_REVERSE);
        addToNavbar(tabs);
    }
    /**
     * Creates an individual tab with a label and icon, linking to a specific view.
     *
     * @param label     the label to display
     * @param viewClass the view to link to
     * @param icon      the icon to show beside the label
     * @return a {@link Tab} component
     */
    private Tab createTab(String label, Class<?> viewClass, VaadinIcon icon) {
        RouterLink link = new RouterLink(label, (Class<? extends Component>) viewClass);
        link.getElement().getStyle().set("display", "flex").set("align-items", "center").set("gap", "0.5em");

        Span labelSpan = new Span(label);
        labelSpan.getElement().getStyle().set("font-weight", "500");

        Tab tab = new Tab(icon.create(), link);
        tab.setId(viewClass.getSimpleName());
        return tab;
    }
    /**
     * Updates the selected tab after navigation based on the current view.
     */
    @Override
    public void afterNavigation() {
        super.afterNavigation();

        String currentViewName = getContent().getClass().getSimpleName();
        tabs.getChildren().forEach(tab -> {
            if (tab instanceof Tab) {
                Tab t = (Tab) tab;
                if (t.getId().isPresent() && t.getId().get().equals(currentViewName)) {
                    tabs.setSelectedTab(t);
                }
            }
        });
    }
    /**
     * Intercepts route entry to verify the JWT token.
     * Redirects to the login view if the token is missing or invalid.
     *
     * @param beforeEnterEvent the navigation event
     */
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt");
        if (token == null || !jwtUtil.validateToken(token)) {
            beforeEnterEvent.forwardTo("login");
        }

    }

}
