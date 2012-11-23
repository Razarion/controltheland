package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ImageHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 23.11.12
 * Time: 16:21
 */
public class MinimizeButton extends Image {
    private static final String MINIMIZE_ICON = "application-sidebar-collapse.png";
    private static final String MAXIMIZE_ICON = "application-sidebar-expand.png";
    private Collection<Widget> widgetToHide = new ArrayList<Widget>();
    private boolean isMinimized;
    private boolean rightSide;

    public MinimizeButton(boolean rightSide) {
        this.rightSide = rightSide;
        setupIcon();
        addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                isMinimized = !isMinimized;
                handleState();
            }
        });
    }

    private void handleState() {
        for (Widget widget : widgetToHide) {
            widget.setVisible(!isMinimized);
        }
        setupIcon();
    }

    private void setupIcon() {
        if (isMinimized) {
            setTitle(ToolTips.TOOL_TIP_MINIMIZE);
            setUrl(ImageHandler.getCockpitImageUrl(rightSide ? MINIMIZE_ICON : MAXIMIZE_ICON));
        } else {
            setTitle(ToolTips.TOOL_TIP_MAXIMIZE);
            setUrl(ImageHandler.getCockpitImageUrl(rightSide ? MAXIMIZE_ICON : MINIMIZE_ICON));
        }
    }

    public void addWidgetToHide(Widget widget) {
        widgetToHide.add(widget);
    }

    public void setZIndex(int zIndex) {
        getElement().getStyle().setZIndex(zIndex);
    }

    public void maximize() {
       if(isMinimized) {
           isMinimized = false;
           handleState();
       }
    }
}
