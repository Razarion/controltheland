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
import com.btxtech.game.services.utg.SessionOverviewDto;
import com.btxtech.game.services.utg.UserTrackingFilter;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class SessionTable extends MgmtWebPage {
    @SpringBean
    private UserTrackingService userTrackingService;
    private UserTrackingFilter userTrackingFilter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);

    public SessionTable() {
        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        filter();
        resultTable();
    }

    private void filter() {
        add(new FeedbackPanel("msgs"));

        Form<UserTrackingFilter> form = new Form<>("filterForm", new CompoundPropertyModel<>(userTrackingFilter));
        add(form);
        form.add(new RadioChoice<>("jsEnabled", UserTrackingFilter.JS_ENABLED_CHOICES));
        form.add(new TextField("days"));
        form.add(new TextField("hits"));
        form.add(new TextField("sessionId"));
        form.add(new TextField("cookieId"));
        form.add(new TextField("optionalFacebookAdValue"));
    }

    private void resultTable() {
        ListView<SessionOverviewDto> listView = new ListView<SessionOverviewDto>("visits", new IModel<List<SessionOverviewDto>>() {
            private List<SessionOverviewDto> visitorInfos;

            @Override
            public List<SessionOverviewDto> getObject() {
                if (visitorInfos == null) {
                    visitorInfos = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
                }
                return visitorInfos;
            }

            @Override
            public void setObject(List<SessionOverviewDto> baseInfos) {
                // Ignored
            }

            @Override
            public void detach() {
                visitorInfos = null;
            }
        }) {
            @Override
            protected void populateItem(ListItem<SessionOverviewDto> listItem) {
                listItem.add(new Label("date", simpleDateFormat.format(listItem.getModelObject().getDate())));
                listItem.add(new Label("pageHits", Integer.toString(listItem.getModelObject().getPageHits())));
                listItem.add(new Label("newUser", listItem.getModelObject().isNewUser() ? "Y" : "N"));
                listItem.add(new Label("enterGame", Integer.toString(listItem.getModelObject().getEnterGameHits())));
                listItem.add(new Label("startAttempts", Integer.toString(listItem.getModelObject().getStartAttempts())));
                listItem.add(new Label("successfulStarts", Integer.toString(listItem.getModelObject().getStartSucceeded())));
                listItem.add(new Label("startupFailure", listItem.getModelObject().isStartupFailure() ? "!" : ""));
                listItem.add(new Label("commands", Integer.toString(listItem.getModelObject().getCommands())));
                listItem.add(new Label("levelPromotions", Integer.toString(listItem.getModelObject().getLevelPromotions())));
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(SessionDetail.SESSION_KEY, listItem.getModelObject().getSessionId());
                BookmarkablePageLink<SessionDetail> link = new BookmarkablePageLink<>("visitorLink", SessionDetail.class, pageParameters);
                link.add(new Label("sessionId", listItem.getModelObject().getSessionId()));
                listItem.add(link);
                listItem.add(new Label("referer", listItem.getModelObject().getReferer()));
            }
        };
        add(listView);
    }

}