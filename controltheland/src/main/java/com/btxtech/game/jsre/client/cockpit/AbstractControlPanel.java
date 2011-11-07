package com.btxtech.game.jsre.client.cockpit;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 07.11.2011
 * Time: 15:50:01
 */
public abstract class AbstractControlPanel extends Grid {
    private static final String URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG = "url(/images/cockpit/controlPanel.png)";
    private static final String URL_IMAGES_COCKPIT_CONTROL_PANEL_V_PNG = "url(/images/cockpit/controlPanelV.png)";
    private static final String URL_IMAGES_COCKPIT_CONTROL_PANEL_H_PNG = "url(/images/cockpit/controlPanelH.png)";
    protected static final int OFFSET_LEFT = 15;
    protected static final int OFFSET_TOP = 15;
    protected static final int OFFSET_RIGHT = 34;
    protected static final int OFFSET_BOTTOM = 34;
    private Integer width;
    private Integer height;

    public AbstractControlPanel(int width, int height) {
        super(3, 3);
        this.width = width;
        this.height = height;
        setPixelSize(width, height);
        setCellSpacing(0);
        // Top Left
        Element cell = getCellFormatter().getElement(0, 0);
        cell.getStyle().setWidth(OFFSET_LEFT, Style.Unit.PX);
        cell.getStyle().setHeight(OFFSET_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG);
        // TOP
        cell = getCellFormatter().getElement(0, 1);
        cell.getStyle().setHeight(OFFSET_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_V_PNG);
        // Top right
        cell = getCellFormatter().getElement(0, 2);
        cell.getStyle().setWidth(OFFSET_LEFT, Style.Unit.PX);
        cell.getStyle().setHeight(OFFSET_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG);
        cell.getStyle().setProperty("backgroundPosition", "-26px 0");
        // Bottom Left
        cell = getCellFormatter().getElement(2, 0);
        cell.getStyle().setWidth(OFFSET_LEFT, Style.Unit.PX);
        cell.getStyle().setHeight(OFFSET_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG);
        cell.getStyle().setProperty("backgroundPosition", "0 -26px");
        // Bottom
        cell = getCellFormatter().getElement(2, 1);
        cell.getStyle().setHeight(OFFSET_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_V_PNG);
        cell.getStyle().setProperty("backgroundPosition", "0 -26px");
        // Bottom right
        cell = getCellFormatter().getElement(2, 2);
        cell.getStyle().setWidth(OFFSET_LEFT, Style.Unit.PX);
        cell.getStyle().setHeight(OFFSET_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG);
        cell.getStyle().setProperty("backgroundPosition", "-26px -26px");
        // Left
        cell = getCellFormatter().getElement(1, 0);
        cell.getStyle().setWidth(OFFSET_LEFT, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_H_PNG);
        // Right
        cell = getCellFormatter().getElement(1, 2);
        cell.getStyle().setWidth(OFFSET_LEFT, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_H_PNG);
        cell.getStyle().setProperty("backgroundPosition", "-26px 0");
        // Middle
        cell = getCellFormatter().getElement(1, 1);
        cell.getStyle().setBackgroundColor("rgba(15,92,133,0.9)");

        setWidget(1, 1, createBody());
    }

    protected abstract Widget createBody();

    public int getContentWidth() {
        return width - OFFSET_LEFT - OFFSET_RIGHT;
    }

    public int getContentHeight() {
        return height - OFFSET_TOP - OFFSET_BOTTOM;
    }
}
