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
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.NewUserDailyDto;
import com.btxtech.game.services.utg.NewUserDailyTrackingFilter;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class NewUserDailyTracking extends MgmtWebPage {
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private PlanetSystemService planetSystemService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_FORMAT_STRING);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);
    private NewUserDailyTrackingFilter newUserDailyTrackingFilter = NewUserDailyTrackingFilter.newDefaultFilter();

    public NewUserDailyTracking() {
        add(new FeedbackPanel("msgs"));
        filter();
        resultTable();
    }

    private void filter() {
        final Form<NewUserDailyTrackingFilter> form = new Form<>("filterForm", new CompoundPropertyModel<>(new IModel<NewUserDailyTrackingFilter>() {

            @Override
            public void detach() {
                //Ignored
            }

            @Override
            public NewUserDailyTrackingFilter getObject() {
                return newUserDailyTrackingFilter;
            }

            @Override
            public void setObject(NewUserDailyTrackingFilter object) {
                newUserDailyTrackingFilter = object;
            }
        }));
        add(form);
        List<String> availableTimeZones = new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs()));
        Collections.sort(availableTimeZones);
        form.add(new DropDownChoice<>("timeZone", new IModel<String>() {
            @Override
            public void detach() {
                // Ignore
            }

            @Override
            public String getObject() {
                if (newUserDailyTrackingFilter.getTimeZone() != null) {
                    return newUserDailyTrackingFilter.getTimeZone().getID();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(String timeZoneId) {
                newUserDailyTrackingFilter.setTimeZone(TimeZone.getTimeZone(timeZoneId));
            }
        }, availableTimeZones));
        form.add(new Label("timeZoneRo", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (newUserDailyTrackingFilter.getTimeZone() != null) {
                    return newUserDailyTrackingFilter.getTimeZone().getDisplayName() + " (" + newUserDailyTrackingFilter.getTimeZone().getDisplayName(false, TimeZone.SHORT) + ")";
                } else {
                    return "-";
                }
            }
        }));
        DateTextField formDate = new DateTextField("fromDate");
        form.add(formDate);
        formDate.add(new DatePicker().setShowOnFieldClick(true).setAutoHide(true));
        DateTextField toDate = new DateTextField("toDate");
        form.add(toDate);
        toDate.add(new DatePicker().setShowOnFieldClick(true).setAutoHide(true));

        form.add(new TextField<>("facebookAdId"));
        form.add(new Label("correctedFromDate", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (newUserDailyTrackingFilter.getCorrectedFromDate() != null) {
                    return simpleDateTimeFormat.format(newUserDailyTrackingFilter.getCorrectedFromDate());
                } else {
                    return null;
                }
            }
        }));
        form.add(new Label("correctedExclusiveToDate", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (newUserDailyTrackingFilter.getCorrectedExclusiveToDate() != null) {
                    return simpleDateTimeFormat.format(newUserDailyTrackingFilter.getCorrectedExclusiveToDate());
                } else {
                    return null;
                }
            }
        }));
        form.add(new Button("search") {
            @Override
            public void onSubmit() {
                // Nothing to do here
            }
        });
    }

    private void resultTable() {
        PropertyListView<NewUserDailyDto> listView = new PropertyListView<NewUserDailyDto>("dailyNewUsers", new LoadableDetachableModel<List<NewUserDailyDto>>() {
            @Override
            protected List<NewUserDailyDto> load() {
                return userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
            }
        }) {
            @Override
            protected void populateItem(ListItem<NewUserDailyDto> listItem) {
                listItem.add(new Label("date", simpleDateFormat.format(listItem.getModelObject().getDate())));
                listItem.add(new Label("sessions"));
                listItem.add(new Label("registered"));
                listItem.add(new Label("level1"));
                listItem.add(new Label("level1Percent"));
                listItem.add(new Label("level2"));
                listItem.add(new Label("level3"));
                listItem.add(new Label("level4"));
                listItem.add(new Label("level5"));
                listItem.add(new Label("level6"));
                if (listItem.getIndex() % 2 == 0) {
                    listItem.add(new AttributeModifier("style", "background-color:#f2f2f2"));
                }
            }
        };
        add(listView);
    }

}