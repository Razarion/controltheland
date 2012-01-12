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
    protected static final int WIDTH_IMAGE_LEFT = 15;
    protected static final int HEIGHT_IMAGE_TOP = 15;
    protected static final int WIDTH_IMAGE_RIGHT = 34;
    protected static final int HEIGHT_IMAGE_BOTTOM = 34;
    private Integer width;
    private Integer height;

    public AbstractControlPanel(int width, int height) {
        super(3, 3);
        this.width = width;
        this.height = height;
        setPixelSize(width, height);
    }

    public AbstractControlPanel(int width) {
        super(3, 3);
        this.width = width;
        setHeight("100%");
        setWidth(Integer.toString(width) + "px");
    }

    /**
     * Width and height is auto
     */
    public AbstractControlPanel() {
        super(3, 3);
    }

    protected void setup() {
        setCellSpacing(0);
        setCellPadding(0);
        setBorderWidth(0);
        // Top Left
        Element cell = getCellFormatter().getElement(0, 0);
        cell.getStyle().setWidth(WIDTH_IMAGE_LEFT, Style.Unit.PX);
        cell.getStyle().setHeight(HEIGHT_IMAGE_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG);
        // TOP
        cell = getCellFormatter().getElement(0, 1);
        cell.getStyle().setHeight(HEIGHT_IMAGE_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_V_PNG);
        // Top right
        cell = getCellFormatter().getElement(0, 2);
        cell.getStyle().setWidth(WIDTH_IMAGE_RIGHT, Style.Unit.PX);
        cell.getStyle().setHeight(HEIGHT_IMAGE_TOP, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG);
        cell.getStyle().setProperty("backgroundPosition", "-26px 0");
        // Bottom Left
        cell = getCellFormatter().getElement(2, 0);
        cell.getStyle().setWidth(WIDTH_IMAGE_LEFT, Style.Unit.PX);
        cell.getStyle().setHeight(HEIGHT_IMAGE_BOTTOM, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG);
        cell.getStyle().setProperty("backgroundPosition", "0 -26px");
        // Bottom
        cell = getCellFormatter().getElement(2, 1);
        cell.getStyle().setHeight(HEIGHT_IMAGE_BOTTOM, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_V_PNG);
        cell.getStyle().setProperty("backgroundPosition", "0 -26px");
        // Bottom right
        cell = getCellFormatter().getElement(2, 2);
        cell.getStyle().setWidth(WIDTH_IMAGE_RIGHT, Style.Unit.PX);
        cell.getStyle().setHeight(HEIGHT_IMAGE_BOTTOM, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_PNG);
        cell.getStyle().setProperty("backgroundPosition", "-26px -26px");
        // Left
        cell = getCellFormatter().getElement(1, 0);
        cell.getStyle().setWidth(WIDTH_IMAGE_LEFT, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_H_PNG);
        // Right
        cell = getCellFormatter().getElement(1, 2);
        cell.getStyle().setWidth(WIDTH_IMAGE_RIGHT, Style.Unit.PX);
        cell.getStyle().setBackgroundImage(URL_IMAGES_COCKPIT_CONTROL_PANEL_H_PNG);
        cell.getStyle().setProperty("backgroundPosition", "-26px 0");
        // Middle
        cell = getCellFormatter().getElement(1, 1);
        cell.getStyle().setBackgroundColor("rgba(15,92,133,0.9)");

        setWidget(1, 1, createBody());
    }

    protected abstract Widget createBody();

    public int getContentWidth() {
        if (width != null) {
            return width - WIDTH_IMAGE_LEFT - WIDTH_IMAGE_RIGHT;
        } else {
            return getOffsetWidth() - WIDTH_IMAGE_LEFT - WIDTH_IMAGE_RIGHT;
        }
    }

    public int getContentHeight() {
        if (height != null) {
            return height - HEIGHT_IMAGE_TOP - HEIGHT_IMAGE_BOTTOM;
        } else {
            return getOffsetHeight() - HEIGHT_IMAGE_TOP - HEIGHT_IMAGE_BOTTOM;
        }
    }
}
