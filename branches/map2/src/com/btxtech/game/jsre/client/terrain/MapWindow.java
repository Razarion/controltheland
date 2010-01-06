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

import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.InfoPanel;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.dialogs.SpeechBubble;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 12:30:16 PM
 */
public class MapWindow implements TerrainScrollListener, MouseMoveHandler, MouseOutHandler {
    public static final int AUTO_SCROLL_DETECTION_WIDTH = 40;
    public static final int SCROLL_SPEED = 50;
    public static final int SCROLL_DISTANCE = 50;
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
        SpeechBubble.closeAllBubbles();
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

        TerrainView.getInstance().move(scrollX, scrollY);
    }


    private static final MapWindow INSTANCE = new MapWindow();
    private ExtendedAbsolutePanel mapWindow;
    private ScrollDirection scrollDirectionX;
    private ScrollDirection scrollDirectionY;

    class ExtendedAbsolutePanel extends AbsolutePanel {
        public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
            return addDomHandler(handler, MouseMoveEvent.getType());
        }

        public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
            return addDomHandler(handler, MouseOutEvent.getType());
        }
    }

    /**
     * Singleton
     */
    private MapWindow() {
        mapWindow = new ExtendedAbsolutePanel();
        mapWindow.setSize("100%", "100%");
        mapWindow.addMouseMoveHandler(this);
        mapWindow.addMouseOutHandler(this);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        int x = event.getRelativeX(mapWindow.getElement());
        int y = event.getRelativeY(mapWindow.getElement());
        int height = mapWindow.getOffsetHeight();
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

        executeScrolling(tmpScrollDirectionX, tmpScrollDirectionY);

        if (Game.isDebug()) {
            InfoPanel.getInstance().setAbsoluteCureserPos(x + TerrainView.getInstance().getViewOriginLeft(), y + TerrainView.getInstance().getViewOriginTop());
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

    public static MapWindow getInstance() {
        return INSTANCE;
    }

    public static AbsolutePanel getAbsolutePanel() {
        return INSTANCE.mapWindow;
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        int count = mapWindow.getWidgetCount();
        for (int i = 0; i < count; i++) {
            Widget w = mapWindow.getWidget(i);
            if (w instanceof ClientSyncItemView) {
                ClientSyncItemView clientSyncItemView = (ClientSyncItemView) w;
                clientSyncItemView.setViewOrigin(left, top);
            } else if (w != TerrainView.getInstance().getCanvas() && !(w instanceof TopMapPanel)) {
                int newLeft = MapWindow.getAbsolutePanel().getWidgetLeft(w) - deltaLeft;
                int newtop = MapWindow.getAbsolutePanel().getWidgetTop(w) - deltaTop;
                MapWindow.getAbsolutePanel().setWidgetPosition(w, newLeft, newtop);
            }
        }
    }

}
