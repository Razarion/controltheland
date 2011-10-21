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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.services.base.BaseService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class BasesTable extends MgmtWebPage {
    @SpringBean
    private BaseService baseService;

    public BasesTable() {
        ListView<SimpleBase> listView = new ListView<SimpleBase>("bases", new IModel<List<SimpleBase>>() {
            private List<SimpleBase> simpleBases;

            @Override
            public List<SimpleBase> getObject() {
                if (simpleBases == null) {
                    simpleBases = baseService.getSimpleBases();
                }
                return simpleBases;
            }

            @Override
            public void setObject(List<SimpleBase> baseInfos) {
                // Ignored
            }

            @Override
            public void detach() {
                simpleBases = null;
            }
        }) {
            @Override
            protected void populateItem(final ListItem<SimpleBase> listItem) {
                Link link = new Link("baseLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new BaseEditor(listItem.getModelObject()));
                    }
                };
                link.add(new Label("baseName", baseService.getBaseName(listItem.getModelObject()) + " (" + listItem.getModelObject().getId() + ")"));
                listItem.add(link);
            }
        };
        add(listView);
    }
}
