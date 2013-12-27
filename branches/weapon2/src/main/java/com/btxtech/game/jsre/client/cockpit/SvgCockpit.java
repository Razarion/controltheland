package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.SvgWidget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * User: beat
 * Date: 28.10.2011
 * Time: 12:02:47
 */
public class SvgCockpit {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 360;
    private static final SvgCockpit INSTANCE = new SvgCockpit();
    private boolean active = false;
    private SvgWidget svgWidget;
    private AbsolutePanel absolutePanel;

    public static SvgCockpit getInstance() {
        return INSTANCE;
    }

    public void activate(AbsolutePanel parent) {
        if (active) {
            return;
        }
        active = true;
        if (svgWidget == null) {
            svgWidget = new SvgWidget("/images/cockpit/gui.svg", WIDTH, HEIGHT);
            svgWidget.getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT);
            absolutePanel = new AbsolutePanel();
            absolutePanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT + 1);
            absolutePanel.setPixelSize(WIDTH, HEIGHT);
            preventEvents();
        }
        parent.add(svgWidget, 0, 0);
        parent.add(absolutePanel, 0, 0);
    }

    public void inactivate(AbsolutePanel parent) {
        if (!active) {
            return;
        }
        active = false;
        parent.remove(svgWidget);
        parent.remove(absolutePanel);
    }

    private void preventEvents() {
        absolutePanel.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        absolutePanel.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.stopPropagation();
            }
        }, MouseUpEvent.getType());

        absolutePanel.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        }, MouseDownEvent.getType());
        absolutePanel.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        }, MouseDownEvent.getType());
    }

}
