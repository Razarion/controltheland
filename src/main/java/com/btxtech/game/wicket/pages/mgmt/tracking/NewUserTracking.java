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
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.NewUserTrackingFilter;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class NewUserTracking extends MgmtWebPage {
    public static final String USER_ID = "userId";
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private PlanetSystemService planetSystemService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);
    private NewUserTrackingFilter newUserTrackingFilter = NewUserTrackingFilter.newDefaultFilter();

    public NewUserTracking() {
        add(new FeedbackPanel("msgs"));
        filter();
        resultTable();
    }

    private void filter() {
        Form<NewUserTrackingFilter> form = new Form<>("filterForm", new CompoundPropertyModel<NewUserTrackingFilter>(new IModel<NewUserTrackingFilter>() {

            @Override
            public void detach() {
                //Ignored
            }

            @Override
            public NewUserTrackingFilter getObject() {
                return newUserTrackingFilter;
            }

            @Override
            public void setObject(NewUserTrackingFilter object) {
                newUserTrackingFilter = object;
            }
        }));
        add(form);
        form.add(new DateTextField("fromDate"));
        form.add(new DateTextField("toDate"));
        form.add(new Button("search") {
            @Override
            public void onSubmit() {
                // Nothing to do here
            }
        });
    }

    private void resultTable() {
        ListView<User> listView = new ListView<User>("users", new LoadableDetachableModel<List<User>>() {
            @Override
            protected List<User> load() {
                return userTrackingService.getNewUsers(newUserTrackingFilter);
            }
        }) {
            @Override
            protected void populateItem(ListItem<User> listItem) {
                User user = listItem.getModelObject();
                listItem.add(new Label("registerDate", simpleDateFormat.format(user.getRegisterDate())));
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(UserTracking.USER_ID, Integer.toString(user.getId()));
                BookmarkablePageLink<UserTracking> link = new BookmarkablePageLink<>("userLink", UserTracking.class, pageParameters);
                link.add(new Label("userName", user.getUsername()));
                listItem.add(link);
                listItem.add(new Label("verified", user.isVerified()));
                listItem.add(new Label("socialNet", user.getSocialNet()));
                if (user.getDbFacebookSource() != null) {
                    listItem.add(new Label("fbSource", user.getDbFacebookSource().getFbSource()));
                    listItem.add(new Label("optionalAdValue", user.getDbFacebookSource().getOptionalAdValue()));
                } else {
                    listItem.add(new Label("fbSource", ""));
                    listItem.add(new Label("optionalAdValue", ""));
                }
                if(user.getDbInvitationInfo() != null) {
                    listItem.add(new Label("invited", user.getDbInvitationInfo().getHost().getUsername()));
                } else {
                    listItem.add(new Label("invited", ""));
                }
            }
        };
        add(listView);
    }

}