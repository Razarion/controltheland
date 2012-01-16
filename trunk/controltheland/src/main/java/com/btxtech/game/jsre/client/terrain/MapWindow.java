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

package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.ExtendedAbsolutePanel;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemViewContainer;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 12:30:16 PM
 */
public class MapWindow implements TerrainScrollListener, MouseMoveHandler {
    public static final int AUTO_SCROLL_DETECTION_WIDTH = 40;
    public static final int SCROLL_SPEED = 50;
    public static final int SCROLL_DISTANCE = 50;
    public static final int SCROLL_DISTANCE_KEY = 205; // 5 is to avoid the effect that it seem not to moveDelta on key-down-repeat
    private static final MapWindow INSTANCE = new MapWindow();
    private ExtendedAbsolutePanel mapWindow;
    private TerrainMouseMoveListener terrainMouseMoveListener;
    private boolean isTrackingEvents = false;
    private Collection<Widget> scrollAbleWidget = new ArrayList<Widget>();

    /**
     * Singleton
     */
    private MapWindow() {
        mapWindow = new ExtendedAbsolutePanel();
        mapWindow.setHeight("100%");
        mapWindow.setWidth("100%");
        mapWindow.addMouseMoveHandler(this);
        mapWindow.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                TerrainView.getInstance().onMouseDown(event);
            }
        });
        mapWindow.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                TerrainView.getInstance().onMouseUp(event);
            }
        });

        Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                try {
                    handlePreviewNativeEvent(event);
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                }
            }
        });
    }

    public void setMinimalSize(int width, int height) {
        MapWindow.getAbsolutePanel().getElement().getStyle().setProperty("minWidth", width + "px");
        MapWindow.getAbsolutePanel().getElement().getStyle().setProperty("minHeight", height + "px");
        TerrainView.getInstance().updateSize();
    }

    private void handlePreviewNativeEvent(Event.NativePreviewEvent event) {
        if (event.getTypeInt() == Event.ONKEYDOWN) {
            switch (event.getNativeEvent().getKeyCode()) {
                case KeyCodes.KEY_LEFT: {
                    TerrainView.getInstance().moveDelta(-SCROLL_DISTANCE_KEY, 0);
                    event.cancel(); // Prevent from scrolling the browser window
                    break;
                }
                case KeyCodes.KEY_RIGHT: {
                    TerrainView.getInstance().moveDelta(SCROLL_DISTANCE_KEY, 0);
                    event.cancel();
                    break;
                }
                case KeyCodes.KEY_UP: {
                    TerrainView.getInstance().moveDelta(0, -SCROLL_DISTANCE_KEY);
                    event.cancel();
                    break;
                }
                case KeyCodes.KEY_DOWN: {
                    TerrainView.getInstance().moveDelta(0, SCROLL_DISTANCE_KEY);
                    event.cancel();
                    break;
                }
            }
        } else if ((event.getTypeInt() & Event.MOUSEEVENTS) != 0 && isTrackingEvents) {
            NativeEvent e = event.getNativeEvent();
            Document document = Document.get();
            ClientUserTracker.getInstance().addEventTrackingItem(e.getClientX() + document.getScrollLeft(),
                    e.getClientY() + document.getScrollTop(),
                    event.getTypeInt());
        }
    }

    public void setTrackingEvents(boolean isTrackingEvents) {
        this.isTrackingEvents = isTrackingEvents;
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        int x = event.getRelativeX(mapWindow.getElement());
        int y = event.getRelativeY(mapWindow.getElement());
        /* int height = mapWindow.getOffsetHeight();
        int width = mapWindow.getOffsetWidth();

        ScrollDirection tmpScrollDirectionX = null;
        ScrollDirection tmpScrollDirectionY = null;

        if (x < AUTO_SCROLL_DETECTION_WIDTH) {
            tmpScrollDirectionX = ScrollDirection.WEST;
        } else if (x > width - AUTO_SCROLL_DETECTION_WIDTH) {
            tmpScrollDirectionX = ScrollDirection.EAST;
        }

        if (y < AUTO_SCROLL_DETECTION_WIDTH) {
            tmpScrollDirectionY = ScrollDirection.NORTH;
        } else if (y > height - AUTO_SCROLL_DETECTION_WIDTH) {
            tmpScrollDirectionY = ScrollDirection.SOUTH;
        }
        scrollingAllowed
        executeScrolling(tmpScrollDirectionX, tmpScrollDirectionY);
        */
        if (Game.isDebug()) {
            SideCockpit.getInstance().debugAbsoluteCursorPos(x + TerrainView.getInstance().getViewOriginLeft(), y + TerrainView.getInstance().getViewOriginTop());
        }
        if (terrainMouseMoveListener != null) {
            terrainMouseMoveListener.onMove(x + TerrainView.getInstance().getViewOriginLeft(), y + TerrainView.getInstance().getViewOriginTop(), x, y);
        }
    }

    public void setTerrainMouseMoveListener(TerrainMouseMoveListener terrainMouseMoveListener) {
        this.terrainMouseMoveListener = terrainMouseMoveListener;
    }

    public static MapWindow getInstance() {
        return INSTANCE;
    }

    public static AbsolutePanel getAbsolutePanel() {
        return INSTANCE.mapWindow;
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        displayVisibleItems();
        scrollOtherElements(deltaLeft, deltaTop);
    }

    private void scrollOtherElements(int deltaLeft, int deltaTop) {
        for (Widget widget : scrollAbleWidget) {
            int newLeft = MapWindow.getAbsolutePanel().getWidgetLeft(widget) - deltaLeft;
            int newtop = MapWindow.getAbsolutePanel().getWidgetTop(widget) - deltaTop;
            MapWindow.getAbsolutePanel().setWidgetPosition(widget, newLeft, newtop);
        }
    }

    public void addToScrollElements(Widget widget) {
        scrollAbleWidget.add(widget);
    }

    public void removeToScrollElements(Widget widget) {
        scrollAbleWidget.remove(widget);
    }

    public void displayVisibleItems() {
        for (ClientSyncItem clientSyncItem : ItemContainer.getInstance().getItems()) {
            try {
                clientSyncItem.checkVisibility();
            } catch (Throwable t) {
                GwtCommon.handleException("Unable display sync item: " + clientSyncItem, t);
            }
        }
        for (ClientSyncItemView clientSyncItemView : ItemViewContainer.getInstance().getVisibleItems()) {
            clientSyncItemView.setPosition();
        }
    }

}
