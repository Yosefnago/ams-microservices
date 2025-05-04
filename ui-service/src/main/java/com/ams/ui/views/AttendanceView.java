package com.ams.ui.views;

import com.ams.ui.layouts.AttendanceLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * {@code AttendanceView} is the main view for displaying attendance or work tracking information
 * for a specific user within the accounting management system.
 *
 * <p>
 * This view is mapped to the dynamic route {@code /attendance/:username} and uses
 * {@link AttendanceLayout} as its parent layout. It is intended to show user-specific
 * attendance logs, work hours, or shift details.
 * </p>
 *
 * <p><b>Annotations:</b></p>
 * <ul>
 *     <li>{@code @Route} – binds the view to the route with dynamic username parameter</li>
 *     <li>{@code @PageTitle} – sets the browser tab title</li>
 * </ul>
 *
 * <p><b>Expected Future Enhancements:</b></p>
 * <ul>
 *     <li>Fetch attendance records based on {@code username}</li>
 *     <li>Display interactive grids, charts, or summaries</li>
 *     <li>Support filters by date range or project</li>
 * </ul>
 *
 * @author Yosef Nago
 * @see AttendanceLayout
 */
@Route(value = "attendance:/username", layout = AttendanceLayout.class)
@PageTitle("Attendance")
public class AttendanceView extends VerticalLayout {

    private String user;

    /**
     * Constructs the {@code AttendanceView} and initializes its layout configuration.
     * <p>
     * This constructor sets vertical spacing and prepares the container for future dynamic components.
     * </p>
     */
    public AttendanceView() {

        setSpacing(true);
    }

}
