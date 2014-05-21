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

import com.btxtech.game.services.utg.QuestTrackingFilter;
import com.btxtech.game.services.utg.TutorialStatisticDto;
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
public class TutorialStatistic extends MgmtWebPage {
    @SpringBean
    private UserTrackingService userTrackingService;
    private QuestTrackingFilter questTrackingFilter = QuestTrackingFilter.newDefaultFilter();
    private TutorialStatisticDto tutorialStatistic;

    public TutorialStatistic() {
        setDefaultModel(new IModel<TutorialStatisticDto>() {

            @Override
            public TutorialStatisticDto getObject() {
                return tutorialStatistic;
            }

            @Override
            public void detach() {
                tutorialStatistic = null;
            }

            @Override
            public void setObject(TutorialStatisticDto tutorialStatisticDto) {

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
                tutorialStatistic = userTrackingService.getTutorialStatistic(questTrackingFilter);
            }
        });
        add(new Label("tutorialName", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (tutorialStatistic != null) {
                    return tutorialStatistic.getTutorialName();
                } else {
                    return null;
                }
            }
        }));
        add(new Label("tutorialStarted", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (tutorialStatistic != null) {
                    return Integer.toString(tutorialStatistic.getTutorialStarted());
                } else {
                    return null;
                }
            }
        }));
    }

    private void resultTable() {
        PropertyListView<TutorialStatisticDto.TutorialQuestEntry> listView = new PropertyListView<TutorialStatisticDto.TutorialQuestEntry>("tutorialTasks", new AbstractReadOnlyModel<List<TutorialStatisticDto.TutorialQuestEntry>>() {
            @Override
            public List<TutorialStatisticDto.TutorialQuestEntry> getObject() {
                if (tutorialStatistic != null) {
                    return tutorialStatistic.getTutorialQuestEntries();
                } else {
                    return null;
                }
            }
        }) {
            @Override
            protected void populateItem(ListItem<TutorialStatisticDto.TutorialQuestEntry> listItem) {
                listItem.add(new Label("questName"));
                listItem.add(new Label("passed"));
                listItem.add(new Label("percentage"));
                if (listItem.getIndex() % 2 == 0) {
                    listItem.add(new AttributeModifier("style", "background-color:#f2f2f2"));
                }
            }
        };
        add(listView);
    }

}
