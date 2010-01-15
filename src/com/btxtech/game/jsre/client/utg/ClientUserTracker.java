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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Date;

/**
 * User: beat
 * Date: 13.01.2010
 * Time: 15:12:08
 */
public class ClientUserTracker {
    public static final int SEND_TIMEOUT = 1000 * 60;
    private static final ClientUserTracker INSTANCE = new ClientUserTracker();
    private ArrayList<UserAction> userActions = new ArrayList<UserAction>();

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
                closeWindow();
            }
        });
        Timer timer = new Timer() {
            @Override
            public void run() {
                sendUserActionsToServer();
            }
        };
        timer.scheduleRepeating(SEND_TIMEOUT);
    }

    public void sandGameStartupState(GameStartupState state, Date timeStamp) {
        if (Connection.isConnected()) {
            Connection.getMovableServiceAsync().gameStartupState(state, timeStamp, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }

                @Override
                public void onSuccess(Void aVoid) {
                    // Ignore
                }
            });
        }
    }

    public void clickOnTerrain(int absoluteX, int absoluteY, MouseDownEvent mouseDownEvent) {
        UserAction userAction = new UserAction(UserAction.TERRAIN_CLICKED, absoluteX + ":" + absoluteY);
        userActions.add(userAction);
    }

    public void clickOwnItem(SyncBaseItem syncBaseItem) {
        UserAction userAction = new UserAction(UserAction.OWN_ITEM_CLICKED, syncBaseItem.getBaseItemType().getName() + ":" + syncBaseItem.getId());
        userActions.add(userAction);
    }

    public void clickEnemyItem(SyncBaseItem syncBaseItem) {
        UserAction userAction = new UserAction(UserAction.ENEMY_ITEM_CLICKED, syncBaseItem.getBaseItemType().getName() + ":" + syncBaseItem.getId());
        userActions.add(userAction);
    }

    public void clickResourceItem(SyncResourceItem syncResourceItem) {
        UserAction userAction = new UserAction(UserAction.RESOURCE_CLICKED, syncResourceItem.getId().toString());
        userActions.add(userAction);
    }

    public void scroll(int left, int top, int width, int height) {
        if (userActions.isEmpty()) {
            UserAction userAction = new UserAction(UserAction.SCROLL, "Origin " + left + ":" + top + " width:" + width + " height:" + height);
            userActions.add(userAction);
        } else {
            UserAction prevAction = userActions.get(userActions.size() - 1);
            if (prevAction.getType() == UserAction.SCROLL) {
                prevAction.repeat("Origin " + left + ":" + top + " width:" + width + " height:" + height);
            } else {
                UserAction userAction = new UserAction(UserAction.SCROLL, "Origin " + left + ":" + top + " width:" + width + " height:" + height);
                userActions.add(userAction);
            }
        }
    }

    public void closeWindow() {
        UserAction userAction = new UserAction(UserAction.CLOSE_WINDOW, null);
        userActions.add(userAction);
        sendUserActionsToServer();
    }

    private void sendUserActionsToServer() {
        if (Connection.isConnected() && !userActions.isEmpty()) {
            Connection.getMovableServiceAsync().sendUserActions(userActions, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }

                @Override
                public void onSuccess(Void aVoid) {
                    // Ignore
                }
            });
            userActions = new ArrayList<UserAction>();
        }

    }

}
