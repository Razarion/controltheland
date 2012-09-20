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

import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.terrain.RegionService;
import com.btxtech.game.wicket.pages.mgmt.RegionEditor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 08.03.2011
 * Time: 18:44:15
 */
public abstract class RegionPanel extends Panel {
    @SpringBean
    private RegionService regionService;

    public RegionPanel(final String id, final TerrainLinkHelper terrainLinkHelper) {
        super(id);
        add(new Label("id", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                DbRegion dbRegion = (DbRegion) getDefaultModelObject();
                if (dbRegion != null) {
                    return "Id: " + dbRegion.getId();
                } else {
                    return "";
                }
            }
        }) {
            @Override
            public boolean isVisible() {
                return getDefaultModelObject() != null;
            }
        });
        add(new Button("edit") {
            @Override
            public void onSubmit() {
                setResponsePage(RegionEditor.class, terrainLinkHelper.createRegionEditorPageParameters((DbRegion) RegionPanel.this.getDefaultModelObject()));
            }

            @Override
            public boolean isVisible() {
                return RegionPanel.this.getDefaultModelObject() != null;
            }

            @Override
            public boolean isEnabled() {
                return terrainLinkHelper.hasAllParameters();
            }
        });
        add(new Button("create") {
            @Override
            public void onSubmit() {
                DbRegion dbRegion = regionService.getRegionCrud().createDbChild();
                RegionPanel.this.setDefaultModelObject(dbRegion);
                updateDependentModel();
            }

            @Override
            public boolean isVisible() {
                return RegionPanel.this.getDefaultModelObject() == null;
            }
        });
        add(new Button("delete") {
            @Override
            public void onSubmit() {
                DbRegion dbRegion = (DbRegion) RegionPanel.this.getDefaultModelObject();
                RegionPanel.this.setDefaultModelObject(null);
                updateDependentModel();
                regionService.getRegionCrud().deleteDbChild(dbRegion);
            }

            @Override
            public boolean isVisible() {
                return RegionPanel.this.getDefaultModelObject() != null;
            }
        });
    }

    protected abstract void updateDependentModel();
}
