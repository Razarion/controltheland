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

import com.btxtech.game.services.mgmt.MgmtService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 03.11.2009
 * Time: 22:13:26
 */
public class QuerySelector extends MgmtWebPage {
    @SpringBean
    private MgmtService mgmtService;
    public QuerySelector() {
        Form form = new Form("forum");
        add(form);

        ListView<String> queries = new ListView<String>("queryRows", mgmtService.getSavedQueris()) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                Button select = new Button("select") {
                    @Override
                    public void onSubmit() {
                        setResponsePage(new DbView(item.getModelObject()));
                    }
                };
                item.add(select);
                item.add(new Label("queryString", item.getModelObject()));
                Button delete = new Button("delete") {
                    @Override
                    public void onSubmit() {
                        mgmtService.removeSavedQuery(item.getModelObject());
                        setResponsePage(new QuerySelector());
                    }
                };
                item.add(delete);
            }
        };
        form.add(queries);
        Button cancel = new Button("cancel") {
            @Override
            public void onSubmit() {
                setResponsePage(new DbView(null));
            }
        };
        form.add(cancel);

    }
}
