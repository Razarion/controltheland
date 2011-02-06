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

import com.btxtech.game.services.utg.DbUserAction;
import com.btxtech.game.services.utg.GameTrackingInfo;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.services.utg.UserActionCommandMissions;
import com.btxtech.game.services.utg.UserCommand;
import com.btxtech.game.services.utg.UserTrackingService;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 13:11:15
 */
public class GameTracking extends Panel {
    @SpringBean
    UserTrackingService userTrackingService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);

    public GameTracking(String id, LifecycleTrackingInfo lifecycleTrackingInfo) {
        super(id);
        add(new LifecyclePanel("lifecycle", lifecycleTrackingInfo));
        GameTrackingInfo gameTrackingInfo = userTrackingService.getGameTracking(lifecycleTrackingInfo);
        overview(gameTrackingInfo);
        userActions(gameTrackingInfo.getUserActionCommand());
    }

    private void overview(GameTrackingInfo gameTrackingInfo) {
        add(new Label("moveCommands", Integer.toString(gameTrackingInfo.getMoveCommandCount())));
        add(new Label("builderCommands", Integer.toString(gameTrackingInfo.getBuilderCommandCount())));
        add(new Label("factoryCommands", Integer.toString(gameTrackingInfo.getFactoryCommandCount())));
        add(new Label("collectCommands", Integer.toString(gameTrackingInfo.getMoneyCollectCommandCount())));
        add(new Label("attackCommands", Integer.toString(gameTrackingInfo.getAttackCommandCount())));
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
                    throw new IllegalArgumentException();
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
}
