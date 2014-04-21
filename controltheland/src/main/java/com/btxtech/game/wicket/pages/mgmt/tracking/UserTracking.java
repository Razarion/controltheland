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

import com.btxtech.game.jsre.client.common.info.Suggestion;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserNameSuggestionFilter;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.SessionOverviewDto;
import com.btxtech.game.services.utg.UserTrackingFilter;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.mgmt.BaseEditor;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.usermgmt.UserStateEditor;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class UserTracking extends MgmtWebPage {
    public static final String USER_ID = "userId";
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private PlanetSystemService planetSystemService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);
    private Integer userId;

    public UserTracking(PageParameters pageParameters) {
        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<User>() {
            @Override
            protected User load() {
                return userService.getUser(userId);
            }
        }));
        filter(pageParameters);
        userDetails();
        resultTable();
    }

    private void userDetails() {
        add(new Label("username"));
        add(new Label("id"));
        add(new Label("accountNonLocked"));
        add(new Label("registerDate"));
        add(new Label("lastLoginDate"));
        add(new Label("email"));
        add(new Label("loginCount", new LoadableDetachableModel<Integer>() {
            @Override
            protected Integer load() {
                User user = (User) UserTracking.this.getDefaultModelObject();
                if (user != null) {
                    return userTrackingService.getLoginCount(user);
                } else {
                    return null;
                }
            }
        }));
        add(new Label("socialNet"));
        ExternalLink socialNetUserLink = new ExternalLink("socialNetUserPage", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                User user = (User) UserTracking.this.getDefaultModelObject();
                if (user != null) {
                    return user.getSocialNetUserLink();
                } else {
                    return null;
                }
            }
        });
        socialNetUserLink.add(new Label("socialNetUserLink"));
        add(socialNetUserLink);
        add(new Label("dbFacebookSource.fbSource"));
        add(new Label("dbFacebookSource.optionalAdValue"));
        add(new Label("dbFacebookSource.wholeString"));
        add(new Label("dbInvitationInfo.host.username"));
        add(new Label("dbInvitationInfo.source"));
        add(new Label("inGameTime", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                User user = (User) UserTracking.this.getDefaultModelObject();
                if (user != null) {
                    return DateUtil.formatDuration(userTrackingService.calculateInGameTime(user));
                } else {
                    return null;
                }
            }
        }));
        add(new Form("userStateForm") {
            @Override
            protected void onSubmit() {
                User user = (User) UserTracking.this.getDefaultModelObject();
                if (user != null) {
                    PageParameters userStatePageParameters = new PageParameters();
                    userStatePageParameters.add(UserStateEditor.USER_STATE_HASH, System.identityHashCode(userService.getUserState(user)));
                    setResponsePage(UserStateEditor.class, userStatePageParameters);
                }
            }
        });
        add(new Form("baseForm") {
            @Override
            protected void onSubmit() {
                User user = (User) UserTracking.this.getDefaultModelObject();
                if (user != null) {
                    UserState userState = userService.getUserState(user);
                    if (planetSystemService.hasPlanet(userState)) {
                        SimpleBase simpleBase = planetSystemService.getServerPlanetServices(userState).getBaseService().getSimpleBase(user);
                        if (simpleBase != null) {
                            setResponsePage(new BaseEditor(simpleBase));
                        }
                    }
                }
            }
        });
    }

    private void filter(PageParameters pageParameters) {
        add(new FeedbackPanel("msgs"));
        Form<UserTrackingFilter> form = new Form<>("filterForm");
        add(form);
        final Model<String> userNameModel = new Model<>();
        final Model<Integer> userIdModel = new Model<>();
        final AutoCompleteTextField<String> field = new AutoCompleteTextField<String>("userName", userNameModel) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }
                return Suggestion.createStringList(userService.getSuggestedUserName(input, UserNameSuggestionFilter.USER_TRACKING_SEARCH, 0)).iterator();
            }
        };
        form.add(field);
        form.add(new TextField<>("userId", userIdModel, Integer.class));
        form.add(new Button("search") {
            @Override
            public void onSubmit() {
                User user = null;
                if (!Strings.isEmpty(userNameModel.getObject())) {
                    user = userService.getUser(userNameModel.getObject());
                } else if (userIdModel.getObject() != null) {
                    user = userService.getUser(userIdModel.getObject());
                }
                if (user != null) {
                    userId = user.getId();
                } else {
                    userId = null;
                    info("User not found");
                }
            }
        });
        if (!pageParameters.get(USER_ID).isNull()) {
            this.userId = pageParameters.get(USER_ID).toInteger();
        }
    }

    private void resultTable() {
        ListView<SessionOverviewDto> listView = new ListView<SessionOverviewDto>("visits", new LoadableDetachableModel<List<? extends SessionOverviewDto>>() {
            @Override
            protected List<? extends SessionOverviewDto> load() {
                User user = (User) UserTracking.this.getDefaultModelObject();
                if (user != null) {
                    return userTrackingService.getSessionOverviewDtos(user);
                } else {
                    return null;
                }
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