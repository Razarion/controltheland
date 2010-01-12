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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.terrain.TerrainService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: Jul 3, 2009
 * Time: 6:43:05 PM
 */
public class MapInfo extends WebPage {
    @SpringBean
    private CollisionService collisionService;
    @SpringBean
    private TerrainService terrainService;

    public MapInfo() {
        boolean[][] map = collisionService.getPassableTerrain();
        Index atomCount = terrainService.getTerrainFieldTileCount();
        List<String> rows = new ArrayList<String>();

        for (int y = 0; y < atomCount.getY(); y++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int x = 0; x < atomCount.getX(); x++) {
                if (map[x][y]) {
                    stringBuilder.append("__");
                } else {
                    stringBuilder.append("xx");
                }
            }
            rows.add(stringBuilder.toString());
        }


        ListView<String> listView = new ListView<String>("mapTable", rows) {


            @Override
            protected void populateItem(ListItem<String> listItem) {
                listItem.add(new Label("mapRow", listItem.getModelObject()));
            }
        };

        add(listView);
    }
}
