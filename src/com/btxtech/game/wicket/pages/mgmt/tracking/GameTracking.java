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

package com.btxtech.game.wicket.pages.mgmt.tracking;

import com.btxtech.game.services.utg.GameStartup;
import com.btxtech.game.services.utg.GameTrackingInfo;
import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.wicket.WebCommon;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 13:11:15
 */
public class GameTracking extends Panel {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);

    public GameTracking(String id, GameTrackingInfo clientRunningTimeDelta) {
        super(id);
        gameStartup(clientRunningTimeDelta);
        userActions(clientRunningTimeDelta.getUserActions());
    }

    private void gameStartup(GameTrackingInfo clientRunningTimeDelta) {
        // Startup
        GameStartup server = clientRunningTimeDelta.getServerGameStartup();
        GameStartup clientStart = clientRunningTimeDelta.getClientStartGameStartup();
        GameStartup clientRunning = clientRunningTimeDelta.getClientRunningGameStartup();

        add(new Label("serverTimeServer", simpleDateFormat.format(server.getTimeStamp())));
        if (clientStart != null) {
            add(new Label("clientStartTimeServer", simpleDateFormat.format(clientStart.getTimeStamp())));
            add(new Label("clientStartDelteServer", WebCommon.getTimeDiff(server.getTimeStamp(), clientStart.getTimeStamp())));
            add(new Label("clientStartTimeClient", simpleDateFormat.format(clientStart.getClientTimeStamp())));
        } else {
            add(new Label("clientStartTimeServer", "?"));
            add(new Label("clientStartDelteServer", "?"));
            add(new Label("clientStartTimeClient", "?"));
        }

        if (clientRunning != null) {
            add(new Label("clientRunningTimeServer", simpleDateFormat.format(clientRunning.getTimeStamp())));
            if (clientStart != null) {
                add(new Label("clientRunningDelteServer", WebCommon.getTimeDiff(clientStart.getTimeStamp(), clientRunning.getTimeStamp())));
                add(new Label("clientRunningTimeDelta", WebCommon.getTimeDiff(clientStart.getClientTimeStamp(), clientRunning.getClientTimeStamp())));
            } else {
                add(new Label("clientRunningDelteServer", "?"));
                add(new Label("clientRunningTimeDelta", "?"));
            }
            add(new Label("clientRunningTimeClient", simpleDateFormat.format(clientRunning.getClientTimeStamp())));
        } else {
            add(new Label("clientRunningTimeServer", "?"));
            add(new Label("clientRunningDelteServer", "?"));
            add(new Label("clientRunningTimeClient", "?"));
            add(new Label("clientRunningTimeDelta", "?"));
        }
    }

    private void userActions(List<DbUserAction> userActions) {
        ListView<DbUserAction> userActionList = new ListView<DbUserAction>("userActions", userActions) {
            private Date previous;

            @Override
            protected void populateItem(ListItem<DbUserAction> listItem) {
                listItem.add(new Label("clientTime", simpleDateFormat.format(listItem.getModelObject().getClientTimeStamp())));
                if (previous != null) {
                    listItem.add(new Label("clientTimeDelta", WebCommon.getTimeDiff(previous, listItem.getModelObject().getClientTimeStamp())));
                } else {
                    listItem.add(new Label("clientTimeDelta", ""));
                }
                previous = listItem.getModelObject().getClientTimeStamp();
                listItem.add(new Label("type", listItem.getModelObject().getType()));
                listItem.add(new Label("additional", listItem.getModelObject().getAdditionalString()));
                listItem.add(new Label("repeat", Integer.toString(listItem.getModelObject().getRepeatingCount())));
                if (listItem.getModelObject().getRepeatingCount() > 0) {
                    listItem.add(new Label("lastTime", simpleDateFormat.format(listItem.getModelObject().getClientTimeStampLast())));
                    listItem.add(new Label("lastAdditional", listItem.getModelObject().getAdditionalString()));
                } else {
                    listItem.add(new Label("lastTime", ""));
                    listItem.add(new Label("lastAdditional", ""));
                }
            }
        };
        add(userActionList);
    }

}
