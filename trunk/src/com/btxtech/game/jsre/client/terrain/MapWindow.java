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
import com.btxtech.game.jsre.client.InfoPanel;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemViewContainer;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 12:30:16 PM
 */
public class MapWindow implements TerrainScrollListener, MouseMoveHandler, MouseOutHandler {
    public static final int AUTO_SCROLL_DETECTION_WIDTH = 40;
    public static final int SCROLL_SPEED = 50;
    public static final int SCROLL_DISTANCE = 50;
    public static final int SCROLL_DISTANCE_KEY = 205; // 5 is to avoid the effect that it seem not to moveDelta on key-down-repeat
    private static final MapWindow INSTANCE = new MapWindow();
    private ExtendedAbsolutePanel mapWindow;
    private ScrollDirection scrollDirectionX;
    private ScrollDirection scrollDirectionY;
    private TerrainMouseMoveListener terrainMouseMoveListener;
    private boolean scrollingAllowed = true;
    private boolean isTrackingEvents = false;

    private enum ScrollDirection {
        NORTH,
        SOUTH,
        WEST,
        EAST;
    }

    private Timer timer = new Timer() {
        @Override
        public void run() {
            scroll();
        }
    };


    private void scroll() {
        int scrollX = 0;
        if (scrollDirectionX == ScrollDirection.WEST) {
            scrollX = -SCROLL_DISTANCE;
        } else if (scrollDirectionX == ScrollDirection.EAST) {
            scrollX = SCROLL_DISTANCE;
        }

        int scrollY = 0;
        if (scrollDirectionY == ScrollDirection.SOUTH) {
            scrollY = SCROLL_DISTANCE;
        } else if (scrollDirectionY == ScrollDirection.NORTH) {
            scrollY = -SCROLL_DISTANCE;
        }

        TerrainView.getInstance().moveDelta(scrollX, scrollY);
    }

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


        // TODO mapWindow.addMouseOutHandler(this);
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

    private void handlePreviewNativeEvent(Event.NativePreviewEvent event) {
        if (event.getTypeInt() == Event.ONKEYDOWN && scrollingAllowed) {
            switch (event.getNativeEvent().getKeyCode()) {
                case KeyCodes.KEY_LEFT: {
                    TerrainView.getInstance().moveDelta(-SCROLL_DISTANCE_KEY, 0);
                    break;
                }
                case KeyCodes.KEY_RIGHT: {
                    TerrainView.getInstance().moveDelta(SCROLL_DISTANCE_KEY, 0);
                    break;
                }
                case KeyCodes.KEY_UP: {
                    TerrainView.getInstance().moveDelta(0, -SCROLL_DISTANCE_KEY);
                    break;
                }
                case KeyCodes.KEY_DOWN: {
                    TerrainView.getInstance().moveDelta(0, SCROLL_DISTANCE_KEY);
                    break;
                }
            }
        } else if ((event.getTypeInt() & Event.MOUSEEVENTS) != 0 && isTrackingEvents) {
            ClientUserTracker.getInstance().addEventTrackingItem(event.getNativeEvent().getClientX(),
                    event.getNativeEvent().getClientY(),
                    event.getTypeInt());
        }
    }

    public void setTrackingEvents() {
        isTrackingEvents = true;
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
            InfoPanel.getInstance().setAbsoluteCureserPos(x + TerrainView.getInstance().getViewOriginLeft(), y + TerrainView.getInstance().getViewOriginTop());
        }
        if (terrainMouseMoveListener != null) {
            terrainMouseMoveListener.onMove(x + TerrainView.getInstance().getViewOriginLeft(), y + TerrainView.getInstance().getViewOriginTop(), x, y);
        }
    }

    private void executeScrolling(ScrollDirection tmpScrollDirectionX, ScrollDirection tmpScrollDirectionY) {
        if (tmpScrollDirectionX != scrollDirectionX || tmpScrollDirectionY != scrollDirectionY) {
            boolean isTimerRunningOld = scrollDirectionX != null || scrollDirectionY != null;
            boolean isTimerRunningNew = tmpScrollDirectionX != null || tmpScrollDirectionY != null;
            scrollDirectionX = tmpScrollDirectionX;
            scrollDirectionY = tmpScrollDirectionY;

            if (isTimerRunningOld != isTimerRunningNew) {
                if (isTimerRunningNew) {
                    scroll();
                    timer.scheduleRepeating(AUTO_SCROLL_DETECTION_WIDTH);
                } else {
                    timer.cancel();
                }
            }
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        executeScrolling(null, null);
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

    public void setScrollingAllowed(boolean scrollingAllowed) {
        this.scrollingAllowed = scrollingAllowed;
    }


}
