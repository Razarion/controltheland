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
import com.btxtech.game.services.utg.UserActionCommand;
import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.services.utg.UserCommand;
import com.btxtech.game.wicket.WebCommon;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 13:11:15
 */
public class GameTracking extends Panel {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);

    public GameTracking(String id, GameTrackingInfo gameTrackingInfo) {
        super(id);
        overview(gameTrackingInfo);
        gameStartup(gameTrackingInfo);
        userActions(gameTrackingInfo.getUserActionCommand());
    }

    private void overview(GameTrackingInfo gameTrackingInfo) {
        if (gameTrackingInfo.getInGameMilliS() < 0) {
            add(new Label("timeInGame", "???"));
        } else {
            add(new Label("timeInGame", WebCommon.formatDuration(gameTrackingInfo.getInGameMilliS())));
        }
        add(new Label("moveCommands", Integer.toString(gameTrackingInfo.getMoveCommands())));
        add(new Label("builderCommands", Integer.toString(gameTrackingInfo.getBuilderCommands())));
        add(new Label("factoryCommands", Integer.toString(gameTrackingInfo.getFactoryCommands())));
        add(new Label("collectCommands", Integer.toString(gameTrackingInfo.getMoneyCollectCommands())));
        add(new Label("attackCommands", Integer.toString(gameTrackingInfo.getAttackCommands())));
    }

    private void gameStartup(GameTrackingInfo gameTrackingInfo) {
        // Startup
        GameStartup server = gameTrackingInfo.getServerGameStartup();
        GameStartup clientStart = gameTrackingInfo.getClientStartGameStartup();
        GameStartup clientRunning = gameTrackingInfo.getClientRunningGameStartup();

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

    private void userActions(List<UserActionCommand> userActions) {
        ListView<UserActionCommand> userActionList = new ListView<UserActionCommand>("userActions", userActions) {
            private Date previous;

            @Override
            protected void populateItem(ListItem<UserActionCommand> listItem) {
                listItem.add(new Label("clientTime", simpleDateFormat.format(listItem.getModelObject().getClientTimeStamp())));
                if (previous != null) {
                    listItem.add(new Label("clientTimeDelta", WebCommon.getTimeDiff(previous, listItem.getModelObject().getClientTimeStamp())));
                } else {
                    listItem.add(new Label("clientTimeDelta", ""));
                }
                previous = listItem.getModelObject().getClientTimeStamp();


                if (listItem.getModelObject().getUserAction() != null) {
                    populateClinetAction(listItem, listItem.getModelObject().getUserAction());
                } else {
                    populateCommand(listItem, listItem.getModelObject().getUserCommand());
                }
            }
        };
        add(userActionList);
    }

    private void populateClinetAction(ListItem<UserActionCommand> listItem, DbUserAction userAction) {
        listItem.add(new Label("type", userAction.getType()));
        listItem.add(new Label("additional", userAction.getAdditionalString()));
        listItem.add(new Label("repeat", Integer.toString(userAction.getRepeatingCount())));
        if (userAction.getRepeatingCount() > 0) {
            listItem.add(new Label("lastTime", simpleDateFormat.format(userAction.getClientTimeStampLast())));
            listItem.add(new Label("lastAdditional", userAction.getAdditionalString()));
        } else {
            listItem.add(new Label("lastTime", ""));
            listItem.add(new Label("lastAdditional", ""));
        }
    }

    private void populateCommand(ListItem<UserActionCommand> listItem, UserCommand userCommand) {
        listItem.add(new AttributeModifier("class", true, new Model<String>("command")));

        Label label = new Label("type", userCommand.getInteraction());
        label.add(new AttributeAppender("colspan", new Model<String>("5"), " "));
        listItem.add(label);

        // Blank out the other colums
        label = new Label("additional", "");
        label.setVisible(false);
        listItem.add(label);

        label = new Label("repeat", "");
        label.setVisible(false);
        listItem.add(label);

        label = new Label("lastTime", "");
        label.setVisible(false);
        listItem.add(label);

        label = new Label("lastAdditional", "");
        label.setVisible(false);
        listItem.add(label);
    }


}
