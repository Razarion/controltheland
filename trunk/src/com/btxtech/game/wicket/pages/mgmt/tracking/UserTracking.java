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

import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.VisitorInfo;
import com.btxtech.game.wicket.WebCommon;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class UserTracking extends WebPage {
    @SpringBean
    private UserTrackingService userTrackingService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);

    public UserTracking() {
        ListView<VisitorInfo> listView = new ListView<VisitorInfo>("visits", new IModel<List<VisitorInfo>>() {
            private List<VisitorInfo> visitorInfos;

            @Override
            public List<VisitorInfo> getObject() {
                if (visitorInfos == null) {
                    visitorInfos = userTrackingService.getVisitorInfos();
                }
                return visitorInfos;
            }

            @Override
            public void setObject(List<VisitorInfo> baseInfos) {
                // Ignored
            }

            @Override
            public void detach() {
                visitorInfos = null;
            }
        }) {
            @Override
            protected void populateItem(final ListItem<VisitorInfo> listItem) {
                listItem.add(new Label("date", simpleDateFormat.format(listItem.getModelObject().getDate())));
                listItem.add(new Label("pageHits", Integer.toString(listItem.getModelObject().getPageHits())));
                listItem.add(new Label("enterGame", Integer.toString(listItem.getModelObject().getEnterGameHits())));
                listItem.add(new Label("commands", Integer.toString(listItem.getModelObject().getCommands())));
                listItem.add(new Label("missions", Integer.toString(listItem.getModelObject().getMissions())));
                Link link = new Link("visitorLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new VisitorDetails(listItem.getModelObject().getSessionId()));
                    }
                };
                link.add(new Label("sessionId", listItem.getModelObject().getSessionId()));
                listItem.add(link);
            }
        };
        add(listView);
    }

}