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

package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ParametrisedRunnable;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.simulation.Step;
import com.btxtech.game.jsre.client.simulation.Task;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainScrollListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 13.01.2010
 * Time: 15:12:08
 */
public class ClientUserTracker implements SelectionListener, TerrainScrollListener {
    private static final int SEND_TIMEOUT = 1000 * 5;
    private static final ClientUserTracker INSTANCE = new ClientUserTracker();
    private List<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
    private List<BrowserWindowTracking> browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
    private List<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
    private List<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
    private List<SyncItemInfo> syncItemInfos = new ArrayList<SyncItemInfo>();
    private Timer timer;
    private boolean isCollecting = false;
    private HandlerRegistration scrollHandlerRegistration;
    private HandlerRegistration resizeHandlerRegistration;

    public static ClientUserTracker getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ClientUserTracker() {
        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> windowCloseEvent) {
                sendEventTrackerItems();
            }
        });
    }

    public void onTutorialFinished(long duration, long clientTimeStamp, ParametrisedRunnable<Level> runnable) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, null, null, duration, clientTimeStamp, runnable);
    }

    public void onTaskFinished(Task task, long duration, long clientTimeStamp) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TASK, task.getTaskConfig().getName(), null, duration, clientTimeStamp, null);
    }

    public void onStepFinished(Step step, Task task, long duration, long clientTimeStamp) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.STEP, step.getStepConfig().getName(), task.getTaskConfig().getName(), duration, clientTimeStamp, null);
    }

    public void startEventTracking() {
        stopEventTracking();
        isCollecting = true;
        timer = new Timer() {
            @Override
            public void run() {
                try {
                    sendEventTrackerItems();
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                }
            }
        };
        timer.scheduleRepeating(SEND_TIMEOUT);
        SelectionHandler.getInstance().addSelectionListener(this);
        MapWindow.getInstance().setTrackingEvents(true);
        TerrainView.getInstance().addTerrainScrollListener(this);
        Connection.getInstance().sendEventTrackingStart(new EventTrackingStart(Window.getClientWidth(),
                Window.getClientHeight(),
                Window.getScrollLeft(),
                Window.getScrollTop(),
                MapWindow.getAbsolutePanel().getOffsetWidth(),
                MapWindow.getAbsolutePanel().getOffsetHeight()));
        scrollHandlerRegistration = Window.addWindowScrollHandler(new Window.ScrollHandler() {
            @Override
            public void onWindowScroll(Window.ScrollEvent event) {
                addBrowserWindowTracking();
            }
        });
        resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                addBrowserWindowTracking();
            }
        });
    }

    public void stopEventTracking() {
        isCollecting = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        SelectionHandler.getInstance().removeSelectionListener(this);
        MapWindow.getInstance().setTrackingEvents(false);
        TerrainView.getInstance().removeTerrainScrollListener(this);
        if (scrollHandlerRegistration != null) {
            scrollHandlerRegistration.removeHandler();
            scrollHandlerRegistration = null;
        }
        if (resizeHandlerRegistration != null) {
            resizeHandlerRegistration.removeHandler();
            resizeHandlerRegistration = null;
        }
        sendEventTrackerItems();
    }

    public void addEventTrackingItem(int xPos, int yPos, int eventType) {
        if (isCollecting) {
            eventTrackingItems.add(new EventTrackingItem(xPos, yPos, eventType));
        }
    }

    public void addBrowserWindowTracking() {
        if (isCollecting) {
            BrowserWindowTracking wind = new BrowserWindowTracking(Window.getClientWidth(),
                    Window.getClientHeight(),
                    Window.getScrollLeft(),
                    Window.getScrollTop(),
                    MapWindow.getAbsolutePanel().getOffsetWidth(),
                    MapWindow.getAbsolutePanel().getOffsetHeight());

            browserWindowTrackings.add(wind);
        }
    }

    public void trackSyncInfo(SyncItem syncItem) {
        if (isCollecting) {
            SyncItemInfo syncItemInfo = syncItem.getSyncInfo();
            syncItemInfo.setClientTimeStamp();
            syncItemInfos.add(syncItemInfo);
        }
    }

    private void sendEventTrackerItems() {
        if (eventTrackingItems.isEmpty() && syncItemInfos.isEmpty() && selectionTrackingItems.isEmpty() && terrainScrollTrackings.isEmpty() && browserWindowTrackings.isEmpty()) {
            return;
        }
        Connection.getInstance().sendEventTrackerItems(eventTrackingItems, syncItemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings);
        clearTracking();
    }

    private void clearTracking() {
        eventTrackingItems = new ArrayList<EventTrackingItem>();
        syncItemInfos = new ArrayList<SyncItemInfo>();
        selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
        terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
        browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
    }

    @Override
    public void onTargetSelectionChanged(ClientSyncItem selection) {
        if (isCollecting) {
            selectionTrackingItems.add(new SelectionTrackingItem(selection));
        }
    }

    @Override
    public void onSelectionCleared() {
        if (isCollecting) {
            selectionTrackingItems.add(new SelectionTrackingItem());
        }
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        if (isCollecting) {
            selectionTrackingItems.add(new SelectionTrackingItem(selectedGroup));
        }
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        if (isCollecting) {
            terrainScrollTrackings.add(new TerrainScrollTracking(left, top, width, height));
        }
    }
}
