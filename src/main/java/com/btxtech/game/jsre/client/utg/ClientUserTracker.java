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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientGlobalServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ParametrisedRunnable;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainScrollListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
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
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 13.01.2010
 * Time: 15:12:08
 */
public class ClientUserTracker implements SelectionListener, TerrainScrollListener, DialogManager.DialogListener {
    public static final String WINDOW_CLOSE = "Window closed -> move to DB";
    public static final String START_UUID = "uuid";
    private static final int SEND_TIMEOUT = 1000 * 5;
    private static final ClientUserTracker INSTANCE = new ClientUserTracker();
    private List<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
    private List<BrowserWindowTracking> browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
    private List<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
    private List<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
    private List<SyncItemInfo> syncItemInfos = new ArrayList<SyncItemInfo>();
    private List<DialogTracking> dialogTrackings = new ArrayList<DialogTracking>();
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
    }

    public void registerWindowsCloseHandler() {
        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> windowCloseEvent) {
                // Take care, the uuid parameter is added here. Would be better do add this parameter inside sendLogViaLoadScriptCommunication()
                GwtCommon.sendLogViaLoadScriptCommunication(WINDOW_CLOSE + "&" + START_UUID + "=" + ClientGlobalServices.getInstance().getClientRunner().getStartUuid());
                sendEventTrackerItems();
            }
        });
    }

    public void onTutorialFailed(int levelTaskId, long duration, long clientTimeStamp) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL_FAILED, levelTaskId, null, duration, clientTimeStamp, null);
    }

    public void onTutorialFinished(int levelTaskId, long duration, long clientTimeStamp, ParametrisedRunnable<GameFlow> runnable) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, levelTaskId, null, duration, clientTimeStamp, runnable);
    }

    public void onTaskFinished(int levelTaskId, AbstractTask abstractTask, long duration, long clientTimeStamp) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TASK, levelTaskId, abstractTask.getAbstractTaskConfig().getName(), duration, clientTimeStamp, null);
    }

    public void startEventTracking() {
        stopEventTracking();
        isCollecting = true;
        timer = new TimerPerfmon(PerfmonEnum.CLIENT_USER_TRACKING) {
            @Override
            public void runPerfmon() {
                try {
                    sendEventTrackerItems();
                } catch (Throwable t) {
                    ClientExceptionHandler.handleException(t);
                }
            }
        };
        timer.scheduleRepeating(SEND_TIMEOUT);
        SelectionHandler.getInstance().addSelectionListener(this);
        MapWindow.getInstance().setTrackingEvents(true);
        TerrainView.getInstance().addTerrainScrollListener(this);
        Connection.getInstance().sendEventTrackingStart(new EventTrackingStart(
                ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                GwtCommon.checkInt(Window.getClientWidth(), "startEventTracking Window.getClientWidth()"),
                GwtCommon.checkInt(Window.getClientHeight(), "startEventTracking Window.getClientHeight()"),
                GwtCommon.checkInt(Window.getScrollLeft(), "startEventTracking Window.getScrollLeft()"),
                GwtCommon.checkInt(Window.getScrollTop(), "startEventTracking Window.getScrollTop()"),
                GwtCommon.checkInt(MapWindow.getAbsolutePanel().getOffsetWidth(), "startEventTracking MapWindow.getAbsolutePanel().getOffsetWidth()"),
                GwtCommon.checkInt(MapWindow.getAbsolutePanel().getOffsetHeight(), "startEventTracking MapWindow.getAbsolutePanel().getOffsetHeight(")));
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
        DialogManager.getInstance().addDialogListener(this);
    }

    public void stopEventTracking() {
        isCollecting = false;
        DialogManager.getInstance().removeDialogListener(this);
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
            eventTrackingItems.add(new EventTrackingItem(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    GwtCommon.correctInt(xPos),
                    GwtCommon.correctInt(yPos),
                    GwtCommon.correctInt(eventType)));
        }
    }

    public void addBrowserWindowTracking() {
        if (isCollecting) {
            BrowserWindowTracking wind = new BrowserWindowTracking(
                    ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    GwtCommon.correctInt(Window.getClientWidth()),
                    GwtCommon.correctInt(Window.getClientHeight()),
                    GwtCommon.correctInt(Window.getScrollLeft()),
                    GwtCommon.correctInt(Window.getScrollTop()),
                    GwtCommon.correctInt(MapWindow.getAbsolutePanel().getOffsetWidth()),
                    GwtCommon.correctInt(MapWindow.getAbsolutePanel().getOffsetHeight()));
            browserWindowTrackings.add(wind);
        }
    }

    public void trackSyncInfo(SyncItem syncItem) {
        if (isCollecting) {
            SyncItemInfo syncItemInfo = syncItem.getSyncInfo();
            syncItemInfo.setStartUuid(ClientGlobalServices.getInstance().getClientRunner().getStartUuid());
            syncItemInfo.setClientTimeStamp();
            syncItemInfos.add(syncItemInfo);
        }
    }

    private void sendEventTrackerItems() {
        if (eventTrackingItems.isEmpty()
                && syncItemInfos.isEmpty()
                && selectionTrackingItems.isEmpty()
                && terrainScrollTrackings.isEmpty()
                && browserWindowTrackings.isEmpty()
                && dialogTrackings.isEmpty()) {
            return;
        }
        Connection.getInstance().sendEventTrackerItems(eventTrackingItems, syncItemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings, dialogTrackings);
        clearTracking();
    }

    private void clearTracking() {
        eventTrackingItems = new ArrayList<EventTrackingItem>();
        syncItemInfos = new ArrayList<SyncItemInfo>();
        selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
        terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
        browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
        dialogTrackings = new ArrayList<DialogTracking>();
    }

    @Override
    public void onTargetSelectionChanged(SyncItem selection) {
        if (isCollecting) {
            selectionTrackingItems.add(new SelectionTrackingItem(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(), selection));
        }
    }

    @Override
    public void onSelectionCleared() {
        if (isCollecting) {
            selectionTrackingItems.add(new SelectionTrackingItem(ClientGlobalServices.getInstance().getClientRunner().getStartUuid()));
        }
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        if (isCollecting) {
            selectionTrackingItems.add(new SelectionTrackingItem(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(), selectedGroup));
        }
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        if (isCollecting) {
            terrainScrollTrackings.add(new TerrainScrollTracking(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    GwtCommon.correctInt(left),
                    GwtCommon.correctInt(top)));
        }
    }

    public void onDialogAppears(Widget widget, String description) {
        if (isCollecting) {
            Integer zIndex = null;
            try {
                zIndex = Integer.parseInt(widget.getElement().getStyle().getZIndex());
            } catch (NumberFormatException e) {
                // Ignore
            }

            dialogTrackings.add(new DialogTracking(
                    ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    GwtCommon.correctInt(widget.getAbsoluteLeft()),
                    GwtCommon.correctInt(widget.getAbsoluteTop()),
                    GwtCommon.correctInt(widget.getOffsetWidth()),
                    GwtCommon.correctInt(widget.getOffsetHeight()),
                    GwtCommon.correctInt(zIndex),
                    description,
                    GwtCommon.correctInt(System.identityHashCode(widget))
            ));
        }
    }

    public void onDialogDisappears(Widget widget) {
        if (isCollecting) {
            dialogTrackings.add(new DialogTracking(ClientGlobalServices.getInstance().getClientRunner().getStartUuid(),
                    GwtCommon.checkInt(System.identityHashCode(widget), "onDialogDisappears System.identityHashCode(widget)")));
        }
    }

    @Override
    public void onDialogShown(Dialog dialog) {
        onDialogAppears(dialog, "Dialog: " + dialog.getTitle());
    }

    @Override
    public void onDialogHidden(Dialog dialog) {
        onDialogDisappears(dialog);
    }
}
