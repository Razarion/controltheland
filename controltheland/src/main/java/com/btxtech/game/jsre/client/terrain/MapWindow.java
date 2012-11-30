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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ExtendedAbsolutePanel;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.perfmon.Perfmon;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
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
                    ClientExceptionHandler.handleException(t);
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
        TerrainView.getInstance().getTerrainKeyHandler().handlePreviewNativeEvent(event);

        if ((event.getTypeInt() & Event.MOUSEEVENTS) != 0 && isTrackingEvents) {
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

    public static MapWindow getInstance() {
        return INSTANCE;
    }

    public static AbsolutePanel getAbsolutePanel() {
        return INSTANCE.mapWindow;
    }
}
