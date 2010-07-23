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

import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.services.utg.DbMissionAction;
import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.services.utg.GameStartup;
import com.btxtech.game.services.utg.GameTrackingInfo;
import com.btxtech.game.services.utg.UserActionCommandMissions;
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
        if (gameTrackingInfo.hasDuration()) {
            add(new Label("timeInGame", WebCommon.formatDuration(gameTrackingInfo.getDuration())));
        } else {
            add(new Label("timeInGame", "???"));
        }
        add(new Label("userName", gameTrackingInfo.getUserName()));
        add(new Label("baseName", gameTrackingInfo.getBaseName()));
        add(new Label("moveCommands", Integer.toString(gameTrackingInfo.getMoveCommandCount())));
        add(new Label("builderCommands", Integer.toString(gameTrackingInfo.getBuilderCommandCount())));
        add(new Label("factoryCommands", Integer.toString(gameTrackingInfo.getFactoryCommandCount())));
        add(new Label("collectCommands", Integer.toString(gameTrackingInfo.getMoneyCollectCommandCount())));
        add(new Label("attackCommands", Integer.toString(gameTrackingInfo.getAttackCommandCount())));
        add(new Label("completedMissions", Integer.toString(gameTrackingInfo.getCompletedMissionCount())));
    }

    private void gameStartup(GameTrackingInfo gameTrackingInfo) {
        // Startup
        add(new ListView<GameStartup>("gameStartup", gameTrackingInfo.getGameStartups()) {
            @Override
            protected void populateItem(ListItem<GameStartup> gameStartupListItem) {
                gameStartupListItem.add(new Label("task", gameStartupListItem.getModelObject().getState().getNiceText()));
                gameStartupListItem.add(new Label("time",WebCommon.formatDurationMilis(gameStartupListItem.getModelObject().getDuration())));
            }
        });
        add(new Label("totalGameStartup", WebCommon.formatDurationMilis(gameTrackingInfo.getTotalGameStartup())));
    }

    private void userActions(List<UserActionCommandMissions> userActions) {
        ListView<UserActionCommandMissions> userActionList = new ListView<UserActionCommandMissions>("userActions", userActions) {
            private Date previous;

            @Override
            protected void populateItem(ListItem<UserActionCommandMissions> listItem) {
                listItem.add(new Label("clientTime", simpleDateFormat.format(listItem.getModelObject().getClientTimeStamp())));
                if (previous != null) {
                    listItem.add(new Label("clientTimeDelta", WebCommon.getTimeDiff(previous, listItem.getModelObject().getClientTimeStamp())));
                } else {
                    listItem.add(new Label("clientTimeDelta", ""));
                }
                previous = listItem.getModelObject().getClientTimeStamp();


                if (listItem.getModelObject().getUserAction() != null) {
                    populateClinetAction(listItem, listItem.getModelObject().getUserAction());
                } else if (listItem.getModelObject().getUserCommand() != null) {
                    populateCommand(listItem, listItem.getModelObject().getUserCommand());
                } else {
                    populateMission(listItem, listItem.getModelObject().getDbMissionAction());
                }
            }
        };
        add(userActionList);
    }

    private void populateClinetAction(ListItem<UserActionCommandMissions> listItem, DbUserAction userAction) {
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

    private void populateCommand(ListItem<UserActionCommandMissions> listItem, UserCommand userCommand) {
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

    private void populateMission(ListItem<UserActionCommandMissions> listItem, DbMissionAction dbMissionAction) {
        if (dbMissionAction.getAction().equals(MissionAction.MISSION_COMPLETED) || dbMissionAction.getAction().equals(MissionAction.MISSION_SKIPPED)) {
            listItem.add(new AttributeModifier("class", true, new Model<String>("missionFinished")));
        } else if (dbMissionAction.getAction().equals(MissionAction.MISSION_USER_STOPPED) || dbMissionAction.getAction().equals(MissionAction.MISSION_TIMED_OUT)) {
            listItem.add(new AttributeModifier("class", true, new Model<String>("missionStopped")));
        } else {
            listItem.add(new AttributeModifier("class", true, new Model<String>("mission")));
        }

        Label label = new Label("type", dbMissionAction.getAction());
        listItem.add(label);

        label = new Label("additional", dbMissionAction.getMission());
        listItem.add(label);

        label = new Label("repeat", dbMissionAction.getTask());
        label.add(new AttributeAppender("colspan", new Model<String>("3"), " "));
        listItem.add(label);

        // Blank out the other colums
        label = new Label("lastTime", "");
        label.setVisible(false);
        listItem.add(label);

        label = new Label("lastAdditional", "");
        label.setVisible(false);
        listItem.add(label);
    }
}
