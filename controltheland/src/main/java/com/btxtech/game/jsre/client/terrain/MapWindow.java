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
import com.btxtech.game.jsre.client.cockpit.ChatCockpit;
import com.btxtech.game.jsre.client.cockpit.CockpitUtil;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
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
import com.google.gwt.user.client.Timer;
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
    private static final int SCROLL_AUTO_MOUSE_DETECTION_WIDTH = 40;
    private static final int SCROLL_AUTO_SPEED = 25;
    private static final int SCROLL_AUTO_DISTANCE = 30;
    private static final MapWindow INSTANCE = new MapWindow();
    private ExtendedAbsolutePanel mapWindow;
    private TerrainMouseMoveListener terrainMouseMoveListener;
    private boolean isTrackingEvents = false;
    private Collection<Widget> scrollAbleWidget = new ArrayList<Widget>();
    private ScrollDirection scrollDirectionXKey = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionYKey = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionXMouse = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionYMouse = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionX = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionY = ScrollDirection.STOP;

    private enum ScrollDirection {
        NORTH,
        SOUTH,
        WEST,
        EAST,
        STOP
    }

    private Timer timer = new Timer() {
        @Override
        public void run() {
            autoScroll();
        }
    };

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
        GwtCommon.preventNativeSelection(mapWindow);
    }

    public void setMinimalSize(int width, int height) {
        MapWindow.getAbsolutePanel().getElement().getStyle().setProperty("minWidth", width + "px");
        MapWindow.getAbsolutePanel().getElement().getStyle().setProperty("minHeight", height + "px");
        TerrainView.getInstance().updateSize();
    }

    private void handlePreviewNativeEvent(Event.NativePreviewEvent event) {
        if (event.getTypeInt() == Event.ONKEYDOWN) {
            switch (event.getNativeEvent().getKeyCode()) {
                case 'A':
                case 'a':
                case KeyCodes.KEY_LEFT: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        executeAutoScrollKey(ScrollDirection.WEST, null);
                        event.cancel(); // Prevent from scrolling the browser window
                        break;
                    }
                }
                case 'D':
                case 'd':
                case KeyCodes.KEY_RIGHT: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        executeAutoScrollKey(ScrollDirection.EAST, null);
                        event.cancel();
                        break;
                    }
                }
                case 'W':
                case 'w':
                case KeyCodes.KEY_UP: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        executeAutoScrollKey(null, ScrollDirection.NORTH);
                        event.cancel();
                        break;
                    }
                }
                case 'S':
                case 's':
                case KeyCodes.KEY_DOWN: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        executeAutoScrollKey(null, ScrollDirection.SOUTH);
                        event.cancel();
                        break;
                    }
                }
            }
        } else if (event.getTypeInt() == Event.ONKEYUP) {
            switch (event.getNativeEvent().getKeyCode()) {
                case 'A':
                case 'a':
                case KeyCodes.KEY_LEFT: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        executeAutoScrollKey(ScrollDirection.STOP, null);
                        event.cancel(); // Prevent from scrolling the browser window
                        break;
                    }
                }
                case 'D':
                case 'd':
                case KeyCodes.KEY_RIGHT: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        executeAutoScrollKey(ScrollDirection.STOP, null);
                        event.cancel();
                        break;
                    }
                }
                case 'W':
                case 'w':
                case KeyCodes.KEY_UP: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        executeAutoScrollKey(null, ScrollDirection.STOP);
                        event.cancel();
                        break;
                    }
                }
                case 'S':
                case 's':
                case KeyCodes.KEY_DOWN: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        executeAutoScrollKey(null, ScrollDirection.STOP);
                        event.cancel();
                        break;
                    }
                }
            }
        }
        if ((event.getTypeInt() & Event.MOUSEEVENTS) != 0 && isTrackingEvents) {
            NativeEvent e = event.getNativeEvent();
            Document document = Document.get();
            ClientUserTracker.getInstance().addEventTrackingItem(e.getClientX() + document.getScrollLeft(),
                    e.getClientY() + document.getScrollTop(),
                    event.getTypeInt());
        }
        if ((event.getTypeInt() & Event.ONMOUSEOUT) != 0) {
            executeAutoScrollMouse(ScrollDirection.STOP, ScrollDirection.STOP);
        }
    }

    public void setTrackingEvents(boolean isTrackingEvents) {
        this.isTrackingEvents = isTrackingEvents;
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        int x = event.getRelativeX(mapWindow.getElement());
        int y = event.getRelativeY(mapWindow.getElement());
        int height = mapWindow.getOffsetHeight();
        int width = mapWindow.getOffsetWidth();

        if (CockpitUtil.isInsideCockpit(new Index(x, y))) {
            executeAutoScrollMouse(ScrollDirection.STOP, ScrollDirection.STOP);
        } else {
            ScrollDirection tmpScrollDirectionX = ScrollDirection.STOP;
            ScrollDirection tmpScrollDirectionY = ScrollDirection.STOP;
            if (x < SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
                tmpScrollDirectionX = ScrollDirection.WEST;
            } else if (x > width - SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
                tmpScrollDirectionX = ScrollDirection.EAST;
            }

            if (y < SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
                tmpScrollDirectionY = ScrollDirection.NORTH;
            } else if (y > height - SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
                tmpScrollDirectionY = ScrollDirection.SOUTH;
            }
            executeAutoScrollMouse(tmpScrollDirectionX, tmpScrollDirectionY);
        }

        if (Game.isDebug()) {
            SideCockpit.getInstance().debugAbsoluteCursorPos(x + TerrainView.getInstance().getViewOriginLeft(), y + TerrainView.getInstance().getViewOriginTop());
        }

        if (terrainMouseMoveListener != null) {
            terrainMouseMoveListener.onMove(x + TerrainView.getInstance().getViewOriginLeft(), y + TerrainView.getInstance().getViewOriginTop(), x, y);
        }
    }

    private void executeAutoScrollKey(ScrollDirection tmpScrollDirectionX, ScrollDirection tmpScrollDirectionY) {
        if (tmpScrollDirectionX != scrollDirectionXKey || tmpScrollDirectionY != scrollDirectionYKey) {
            if (tmpScrollDirectionX != null) {
                scrollDirectionXKey = tmpScrollDirectionX;
            }
            if (tmpScrollDirectionY != null) {
                scrollDirectionYKey = tmpScrollDirectionY;
            }
            executeAutoScroll();
        }
    }

    private void executeAutoScrollMouse(ScrollDirection tmpScrollDirectionX, ScrollDirection tmpScrollDirectionY) {
        if (tmpScrollDirectionX != scrollDirectionXMouse || tmpScrollDirectionY != scrollDirectionYMouse) {
            scrollDirectionXMouse = tmpScrollDirectionX;
            scrollDirectionYMouse = tmpScrollDirectionY;
            executeAutoScroll();
        }
    }

    private void executeAutoScroll() {
        ScrollDirection newScrollDirectionX = ScrollDirection.STOP;
        if (scrollDirectionXKey != ScrollDirection.STOP) {
            newScrollDirectionX = scrollDirectionXKey;
        } else if (scrollDirectionXMouse != ScrollDirection.STOP) {
            newScrollDirectionX = scrollDirectionXMouse;
        }

        ScrollDirection newScrollDirectionY = ScrollDirection.STOP;
        if (scrollDirectionYKey != ScrollDirection.STOP) {
            newScrollDirectionY = scrollDirectionYKey;
        } else if (scrollDirectionYMouse != ScrollDirection.STOP) {
            newScrollDirectionY = scrollDirectionYMouse;
        }

        if (newScrollDirectionX != scrollDirectionX || newScrollDirectionY != scrollDirectionY) {
            boolean isTimerRunningOld = scrollDirectionX != ScrollDirection.STOP || scrollDirectionY != ScrollDirection.STOP;
            boolean isTimerRunningNew = newScrollDirectionX != ScrollDirection.STOP || newScrollDirectionY != ScrollDirection.STOP;
            scrollDirectionX = newScrollDirectionX;
            scrollDirectionY = newScrollDirectionY;
            if (isTimerRunningOld != isTimerRunningNew) {
                if (isTimerRunningNew) {
                    autoScroll();
                    timer.scheduleRepeating(SCROLL_AUTO_SPEED);
                } else {
                    timer.cancel();
                }
            }
        }
    }

    private void autoScroll() {
        int scrollX = 0;
        if (scrollDirectionX == ScrollDirection.WEST) {
            scrollX = -SCROLL_AUTO_DISTANCE;
        } else if (scrollDirectionX == ScrollDirection.EAST) {
            scrollX = SCROLL_AUTO_DISTANCE;
        }

        int scrollY = 0;
        if (scrollDirectionY == ScrollDirection.SOUTH) {
            scrollY = SCROLL_AUTO_DISTANCE;
        } else if (scrollDirectionY == ScrollDirection.NORTH) {
            scrollY = -SCROLL_AUTO_DISTANCE;
        }

        TerrainView.getInstance().moveDelta(scrollX, scrollY);
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
        Rectangle viewRect = TerrainView.getInstance().getViewRect();
        for (ClientSyncItem clientSyncItem : ItemContainer.getInstance().getItems()) {
            try {
                clientSyncItem.checkVisibility(viewRect);
            } catch (Throwable t) {
                GwtCommon.handleException("Unable display sync item: " + clientSyncItem, t);
            }
        }
        for (ClientSyncItemView clientSyncItemView : ItemViewContainer.getInstance().getVisibleItems()) {
            clientSyncItemView.setPosition();
        }
    }

}
