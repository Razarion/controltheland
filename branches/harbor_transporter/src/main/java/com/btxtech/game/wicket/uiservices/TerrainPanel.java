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

package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.wicket.pages.mgmt.Pathfinding;
import com.btxtech.game.wicket.pages.mgmt.planet.TerrainFieldEditor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 14.09.2012
 * Time: 18:44:15
 */
public class TerrainPanel extends Panel {
    public TerrainPanel(final String id, final TerrainLinkHelper terrainLinkHelper) {
        super(id);
        add(new Label("id", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return Integer.toString(((DbTerrainSetting) getDefaultModelObject()).getId());
            }
        }));
        add(new TextField<>("tileXCount", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                return ((DbTerrainSetting) getDefaultModelObject()).getTileXCount();
            }

            @Override
            public void setObject(Integer integer) {
                ((DbTerrainSetting) getDefaultModelObject()).setTileXCount(integer);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new TextField<>("tileYCount", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                return ((DbTerrainSetting) getDefaultModelObject()).getTileYCount();
            }

            @Override
            public void setObject(Integer integer) {
                ((DbTerrainSetting) getDefaultModelObject()).setTileYCount(integer);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new Button("edit") {
            @Override
            public void onSubmit() {
                setResponsePage(TerrainFieldEditor.class, terrainLinkHelper.createTerrainEditorPageParameters());
            }
        });
        add(new Button("pathfinding") {
            @Override
            public void onSubmit() {
                setResponsePage(Pathfinding.class, terrainLinkHelper.createTerrainEditorPageParameters());
            }
        });
    }
}
