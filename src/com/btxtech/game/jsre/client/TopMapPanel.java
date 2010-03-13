/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: Oct 18, 2009
 * Time: 1:55:03 AM
 */
public abstract class TopMapPanel extends DecoratorPanel {
    public enum Direction {
        LEFT_TOP,
        RIGHT_TOP,
        LEFT_BOTTOM,
        RIGHT_BOTTOM
    }

    private boolean isExpanded = true;
    private Image expandImage;

    public TopMapPanel() {
        setStyleName("topMapPanel");

        Widget content = createBody();
        content.getElement().getStyle().setBackgroundImage("url(/images/transparentimg.png)");

        setWidget(content);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_TOP_MAP_PANEL);

    }

    private void addCollapseButton(Direction direction) {
        Image image;
        switch (direction) {
            case LEFT_TOP:
                image = new Image("/images/topmappanel1.png");
                DOM.appendChild(getCellElement(1, 0), image.getElement());
                break;
            case LEFT_BOTTOM:
                image = new Image("/images/topmappanel1.png");
                DOM.appendChild(getCellElement(1, 0), image.getElement());
                break;
            case RIGHT_TOP:
                image = new Image("/images/topmappanel2.png");
                DOM.appendChild(getCellElement(1, 2), image.getElement());
                break;
            case RIGHT_BOTTOM:
                image = new Image("/images/topmappanel2.png");
                DOM.appendChild(getCellElement(1, 2), image.getElement());
                break;
            default:
                throw new IllegalArgumentException(this + " unknwo direction: " + direction);
        }
        DOM.sinkEvents(image.getElement(), Event.ONMOUSEDOWN);
        DOM.setEventListener(image.getElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                collapse();
            }
        });
    }

    public void addToParent(AbsolutePanel absolutePanel, Direction direction, int distance) {
        switch (direction) {
            case LEFT_TOP:
                addCollapseButton(direction);
                absolutePanel.add(this, distance, distance);
                expandImage = new Image("/images/topmappanel2.png");
                absolutePanel.add(expandImage, distance, distance);
                break;
            case LEFT_BOTTOM:
                addCollapseButton(direction);
                absolutePanel.add(this, distance, 1);
                getElement().getStyle().setProperty("top", "");
                getElement().getStyle().setProperty("bottom", distance + "px");
                expandImage = new Image("/images/topmappanel2.png");
                absolutePanel.add(expandImage, distance, 1);
                expandImage.getElement().getStyle().setProperty("top", "");
                expandImage.getElement().getStyle().setProperty("bottom", distance + "px");
                break;
            case RIGHT_TOP:
                addCollapseButton(direction);
                absolutePanel.add(this, 1, distance);
                getElement().getStyle().setProperty("left", "");
                getElement().getStyle().setProperty("right", distance + "px");
                expandImage = new Image("/images/topmappanel1.png");
                absolutePanel.add(expandImage, 1, distance);
                expandImage.getElement().getStyle().setProperty("left", "");
                expandImage.getElement().getStyle().setProperty("right", distance + "px");
                break;
            case RIGHT_BOTTOM:
                addCollapseButton(direction);
                absolutePanel.add(this, 1, 1);
                getElement().getStyle().setProperty("left", "");
                getElement().getStyle().setProperty("top", "");
                getElement().getStyle().setProperty("right", distance + "px");
                getElement().getStyle().setProperty("bottom", distance + "px");
                expandImage = new Image("/images/topmappanel1.png");
                absolutePanel.add(expandImage, 1, distance);
                expandImage.getElement().getStyle().setProperty("left", "");
                expandImage.getElement().getStyle().setProperty("top", "");
                expandImage.getElement().getStyle().setProperty("right", distance + "px");
                expandImage.getElement().getStyle().setProperty("bottom", distance + "px");
                break;
            default:
                throw new IllegalArgumentException(this + " unknwo direction: " + direction);
        }
        expandImage.setVisible(!isExpanded);
        expandImage.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                expand();
            }
        });
        expandImage.getElement().getStyle().setZIndex(Constants.Z_INDEX_TOP_MAP_PANEL);
    }

    protected abstract Widget createBody();

    public void expand() {
        if (isExpanded) {
            return;
        }
        isExpanded = true;
        setVisible(true);
        expandImage.setVisible(false);
    }

    public void collapse() {
        if (!isExpanded) {
            return;
        }
        isExpanded = false;
        setVisible(false);
        expandImage.setVisible(true);
    }
}
