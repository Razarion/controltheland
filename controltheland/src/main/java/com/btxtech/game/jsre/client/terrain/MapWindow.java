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

import com.btxtech.game.jsre.client.ExtendedAbsolutePanel;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.ChatCockpit;
import com.btxtech.game.jsre.client.cockpit.CockpitUtil;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.perfmon.Perfmon;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
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

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 12:30:16 PM
 */
public class MapWindow {
    private static final MapWindow INSTANCE = new MapWindow();
    private ExtendedAbsolutePanel mapWindow;
    private boolean isTrackingEvents = false;

    /**
     * Singleton
     */
    private MapWindow() {
        mapWindow = new ExtendedAbsolutePanel();
        mapWindow.setHeight("100%");
        mapWindow.setWidth("100%");

        Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                try {
                    Perfmon.getInstance().onEntered(PerfmonEnum.MAP_WINDOW_EVENT_PREVIEW);
                    handlePreviewNativeEvent(event);
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                } finally {
                    Perfmon.getInstance().onLeft(PerfmonEnum.MAP_WINDOW_EVENT_PREVIEW);
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
                        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollKey(TerrainMouseHandler.ScrollDirection.WEST, null);
                        event.cancel(); // Prevent from scrolling the browser window
                        break;
                    }
                }
                case 'D':
                case 'd':
                case KeyCodes.KEY_RIGHT: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollKey(TerrainMouseHandler.ScrollDirection.EAST, null);
                        event.cancel();
                        break;
                    }
                }
                case 'W':
                case 'w':
                case KeyCodes.KEY_UP: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollKey(null, TerrainMouseHandler.ScrollDirection.NORTH);
                        event.cancel();
                        break;
                    }
                }
                case 'S':
                case 's':
                case KeyCodes.KEY_DOWN: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollKey(null, TerrainMouseHandler.ScrollDirection.SOUTH);
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
                        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollKey(TerrainMouseHandler.ScrollDirection.STOP, null);
                        event.cancel(); // Prevent from scrolling the browser window
                        break;
                    }
                }
                case 'D':
                case 'd':
                case KeyCodes.KEY_RIGHT: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollKey(TerrainMouseHandler.ScrollDirection.STOP, null);
                        event.cancel();
                        break;
                    }
                }
                case 'W':
                case 'w':
                case KeyCodes.KEY_UP: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollKey(null, TerrainMouseHandler.ScrollDirection.STOP);
                        event.cancel();
                        break;
                    }
                }
                case 'S':
                case 's':
                case KeyCodes.KEY_DOWN: {
                    if (!ChatCockpit.getInstance().hasFocus()) {
                        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollKey(null, TerrainMouseHandler.ScrollDirection.STOP);
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
    //    if ((event.getTypeInt() & Event.ONMOUSEOUT) != 0) {
    //        TerrainView.getInstance().getTerrainMouseHandler().executeAutoScrollMouse(TerrainMouseHandler.ScrollDirection.STOP, TerrainMouseHandler.ScrollDirection.STOP);
    //    }
    }

    public void setTrackingEvents(boolean isTrackingEvents) {
        this.isTrackingEvents = isTrackingEvents;
    }

    public static MapWindow getInstance() {
        return INSTANCE;
    }

    public static AbsolutePanel getAbsolutePanel() {
        return INSTANCE.mapWindow;
    }
}
