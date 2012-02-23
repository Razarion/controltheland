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

import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.services.utg.RealGameTrackingInfo;
import com.btxtech.game.services.utg.UserCommandHistoryElement;
import com.btxtech.game.services.utg.UserTrackingService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 13:11:15
 */
public class RealGameTracking extends Panel {
    @SpringBean
    UserTrackingService userTrackingService;

    public RealGameTracking(String id, LifecycleTrackingInfo lifecycleTrackingInfo) {
        super(id);
        add(new LifecyclePanel("lifecycle", lifecycleTrackingInfo));
        RealGameTrackingInfo realGameTrackingInfo = userTrackingService.getGameTracking(lifecycleTrackingInfo);
        add(new Label("baseName", lifecycleTrackingInfo.getBaseName()));
        userActions(realGameTrackingInfo.getUserCommandHistoryElements());
    }

    private void userActions(List<UserCommandHistoryElement> userCommandHistoryElements) {
        ListView<UserCommandHistoryElement> userActionList = new ListView<UserCommandHistoryElement>("userActions", userCommandHistoryElements) {
            private Long previous;

            @Override
            protected void populateItem(ListItem<UserCommandHistoryElement> listItem) {
                listItem.add(new Label("timeStamp", DateUtil.formatTime(listItem.getModelObject().getTimeStamp())));

                if (previous != null) {
                    listItem.add(new Label("timeDelta", DateUtil.getTimeDiff(previous, listItem.getModelObject().getTimeStamp())));
                } else {
                    listItem.add(new Label("timeDelta", ""));
                }
                previous = listItem.getModelObject().getTimeStamp();
                if (listItem.getModelObject().getClientTimeStamp() != null) {
                    listItem.add(new Label("clientTimeStamp", DateUtil.formatTime(listItem.getModelObject().getClientTimeStamp())));
                } else {
                    listItem.add(new Label("clientTimeStamp", ""));
                }
                listItem.add(new Label("info1", listItem.getModelObject().getInfo1()));
                listItem.add(new Label("info2", listItem.getModelObject().getInfo2()));
            }
        };
        add(userActionList);
    }
}
