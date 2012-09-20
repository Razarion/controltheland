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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.mgmt.DbViewDTO;
import com.btxtech.game.services.mgmt.MgmtService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:15:59 PM
 */
public class DbView extends MgmtWebPage {
    @SpringBean
    private MgmtService mgmtService;
    private String sqlField;
    private RepeatingView rowRepeatingView;
    private RepeatingView heading;

    public DbView() {
        this(null);
    }
    public DbView(String query) {
        sqlField = query;
        FeedbackPanel feedbackPanel = new FeedbackPanel("msgs");
        add(feedbackPanel);

        Form<DbView> selectForm = new Form<DbView>("selectForm") {
            @Override
            public void onSubmit() {
                setResponsePage(new QuerySelector());
            }
        };
        add(selectForm);

        Form<DbView> form = new Form<DbView>("sqlForm", new CompoundPropertyModel<DbView>(this));
        form.add(new Button("execute") {
            @Override
            public void onSubmit() {
                try {
                    DbViewDTO dbViewDTO = mgmtService.queryDb(sqlField);
                    display(dbViewDTO);
                } catch (Throwable t) {
                    info(t.getMessage());
                }
            }
        });
        form.add(new Button("saveQuery") {
            @Override
            public void onSubmit() {
                mgmtService.saveQuery(sqlField);
            }
        });

        add(form);
        TextArea<String> textField = new TextArea<String>("sqlField");
        textField.setRequired(true);
        form.add(textField);
        rowRepeatingView = new RepeatingView("dbRows");
        add(rowRepeatingView);
        heading = new RepeatingView("dbHeader");
        add(heading);
    }

    private void display(DbViewDTO dbViewDTO) {
        heading.removeAll();
        for (String header : dbViewDTO.getHeader()) {
            heading.add(new Label(heading.newChildId(), header));
        }

        rowRepeatingView.removeAll();
        for (List<String> row : dbViewDTO.getRows()) {
            WebMarkupContainer container = new WebMarkupContainer(rowRepeatingView.newChildId());
            RepeatingView cell = new RepeatingView("dbCell");
            rowRepeatingView.add(container);
            container.add(cell);
            for (String cellString : row) {
                cell.add(new Label(cell.newChildId(), cellString));
            }
        }
    }
}
