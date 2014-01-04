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
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.services.planet.PlanetSystemService;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class PlanetBasesTable extends MgmtWebPage {
    @SpringBean
    private PlanetSystemService planetSystemService;

    public PlanetBasesTable() {
        ListView<Map.Entry<PlanetInfo, List<SimpleBase>>> listView = new ListView<Map.Entry<PlanetInfo, List<SimpleBase>>>("planets", new IModel<List<Map.Entry<PlanetInfo, List<SimpleBase>>>>() {
            private List<Map.Entry<PlanetInfo, List<SimpleBase>>> entries;

            @Override
            public List<Map.Entry<PlanetInfo, List<SimpleBase>>> getObject() {
                if (entries == null) {
                    entries = planetSystemService.getAllSimpleBases();
                }
                return entries;
            }

            @Override
            public void setObject(List<Map.Entry<PlanetInfo, List<SimpleBase>>> entries) {
                // Ignored
            }

            @Override
            public void detach() {
                entries = null;
            }
        }) {
            @Override
            protected void populateItem(ListItem<Map.Entry<PlanetInfo, List<SimpleBase>>> item) {
                item.add(new BasesTable("planet", item.getModelObject().getKey(), item.getModelObject().getValue()));
            }
        };
        add(listView);
    }
}
