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

import com.btxtech.game.services.utg.QuestStatisticDto;
import com.btxtech.game.services.utg.QuestTrackingFilter;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class QuestStatistic extends MgmtWebPage {
    @SpringBean
    private UserTrackingService userTrackingService;
    private QuestTrackingFilter questTrackingFilter = QuestTrackingFilter.newDefaultFilter();
    private QuestStatisticDto questStatistic;

    public QuestStatistic() {
        setDefaultModel(new IModel<QuestStatisticDto>() {

            @Override
            public QuestStatisticDto getObject() {
                return questStatistic;
            }

            @Override
            public void detach() {
                questStatistic = null;
            }

            @Override
            public void setObject(QuestStatisticDto questStatisticDto) {

            }
        });
        add(new FeedbackPanel("msgs"));
        filter();
        resultTable();
    }

    private void filter() {
        final Form<QuestTrackingFilter> form = new Form<>("filterForm", new CompoundPropertyModel<>(new IModel<QuestTrackingFilter>() {

            @Override
            public void detach() {
                //Ignored
            }

            @Override
            public QuestTrackingFilter getObject() {
                return questTrackingFilter;
            }

            @Override
            public void setObject(QuestTrackingFilter object) {
                questTrackingFilter = object;
            }
        }));
        add(form);
        DateTextField formDate = new DateTextField("fromDate");
        form.add(formDate);
        formDate.add(new DatePicker().setShowOnFieldClick(true).setAutoHide(true));
        DateTextField toDate = new DateTextField("toDate");
        form.add(toDate);
        toDate.add(new DatePicker().setShowOnFieldClick(true).setAutoHide(true));

        form.add(new TextField<>("dbId"));
        form.add(new Button("search") {
            @Override
            public void onSubmit() {
                questStatistic = userTrackingService.getQuestStatistic(questTrackingFilter);
            }
        });
        add(new Label("levelNumber", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (questStatistic != null) {
                    return Integer.toString(questStatistic.getLevelNumber());
                } else {
                    return null;
                }
            }
        }));
    }

    private void resultTable() {
        PropertyListView<QuestStatisticDto.QuestEntry> listView = new PropertyListView<QuestStatisticDto.QuestEntry>("questTasks", new AbstractReadOnlyModel<List<QuestStatisticDto.QuestEntry>>() {
            @Override
            public List<QuestStatisticDto.QuestEntry> getObject() {
                if (questStatistic != null) {
                    return questStatistic.getQuestEntries();
                } else {
                    return null;
                }
            }
        }) {
            @Override
            protected void populateItem(ListItem<QuestStatisticDto.QuestEntry> listItem) {
                listItem.add(new Label("questName"));
                listItem.add(new Label("passed"));
                if (listItem.getIndex() % 2 == 0) {
                    listItem.add(new AttributeModifier("style", "background-color:#f2f2f2"));
                }
            }
        };
        add(listView);
    }

}
