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

import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 28.11.2010
 * Time: 14:50:22
 */
public class TerrainSettingsTable extends MgmtWebPage {
    @SpringBean
    private TerrainService terrainService;

    public TerrainSettingsTable() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("from");
        add(form);

        new CrudRootTableHelper<DbTerrainSetting>("terrainSettingTable", "saveTerrainSetting", "createTerrainSetting", false, form, false) {

            @Override
            protected CrudRootServiceHelper<DbTerrainSetting> getCrudRootServiceHelperImpl() {
                return terrainService.getDbTerrainSettingCrudServiceHelper();
            }

            @Override
            protected void setupSave(WebMarkupContainer markupContainer, String saveId) {
                markupContainer.add(new Button(saveId) {

                    @Override
                    public void onSubmit() {
                        terrainService.saveDbTerrainSetting(getList());
                    }
                });
            }

            @Override
            protected void extendedPopulateItem(final Item<DbTerrainSetting> item) {
                item.add(new Label("id"));
                super.extendedPopulateItem(item);
                item.add(new TextField("tileXCount"));
                item.add(new TextField("tileYCount"));
                item.add(new TextField("tileHeight"));
                item.add(new TextField("tileWidth"));
                item.add(new CheckBox("isRealGame"));
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(TerrainEditorAsync.TERRAIN_SETTING_ID, item.getModelObject().getId().toString());
                item.add(new BookmarkablePageLink<TerrainFieldEditor>("editorLink", TerrainFieldEditor.class, pageParameters));
                item.add(new BookmarkablePageLink<TerrainFieldEditor>("pathFindingLinkLink", Pathfinding.class, pageParameters));
            }
        };
        form.add(new Button("activate") {
            @Override
            public void onSubmit() {
                terrainService.activateTerrain();
            }
        });
    }
}
