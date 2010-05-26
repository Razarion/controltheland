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

import com.btxtech.game.jsre.mapview.territory.TerritoryEditModel;
import com.btxtech.game.services.territory.DbTerritory;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.wicket.uiservices.ListProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.05.2010
 * Time: 16:28:44
 */
public class TerritoryEditor extends WebPage {
    @SpringBean
    private TerritoryService territoryService;

    public TerritoryEditor() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("territoryForm");
        add(form);

        final ListProvider<DbTerritory> provider = new ListProvider<DbTerritory>() {
            @Override
            protected List<DbTerritory> createList() {
                return new ArrayList<DbTerritory>(territoryService.getDbTerritories());
            }
        };
        form.add(new DataView<DbTerritory>("territories", provider) {
            @Override
            protected void populateItem(final Item<DbTerritory> territoryItem) {
                territoryItem.add(new TextField<String>("name"));
                territoryItem.add(new Button("edit") {

                    @Override
                    public void onSubmit() {
                        PageParameters pageParameters = new PageParameters();
                        pageParameters.put(TerritoryEditModel.TERRITORY_TO_EDIT, territoryItem.getModelObject().getName());
                        setResponsePage(TerritoryDesigner.class, pageParameters);
                    }
                });

                territoryItem.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        territoryService.removeDbTerritory(territoryItem.getModelObject());
                    }
                });

            }
        });

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                territoryService.saveDbTerritory(provider.getLastModifiedList());
            }
        });
        form.add(new Button("add") {

            @Override
            public void onSubmit() {
                territoryService.addDbTerritory();
            }
        });

    }
}
