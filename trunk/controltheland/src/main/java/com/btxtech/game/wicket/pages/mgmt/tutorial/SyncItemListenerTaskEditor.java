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

package com.btxtech.game.wicket.pages.mgmt.tutorial;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.tutorial.SyncItemListenerTaskConfig;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.tutorial.DbSyncItemListenerTaskConfig;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 22.12.2013
 * Time: 23:29:54
 */
public class SyncItemListenerTaskEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbSyncItemListenerTaskConfig> ruTaskServiceHelper;

    public SyncItemListenerTaskEditor(DbSyncItemListenerTaskConfig dbSyncItemListenerTaskConfig, final TerrainLinkHelper terrainLinkHelper) {
        add(new FeedbackPanel("msgs"));

        final Form<DbSyncItemListenerTaskConfig> form = new Form<>("taskForm", new CompoundPropertyModel<>(new RuModel<DbSyncItemListenerTaskConfig>(dbSyncItemListenerTaskConfig, DbSyncItemListenerTaskConfig.class) {
            @Override
            protected RuServiceHelper<DbSyncItemListenerTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }
        }));
        add(form);
        form.add(new CommonTaskEditorPanel("commonTaskEditorPanel", terrainLinkHelper));

        form.add(new BaseItemTypePanel("syncItemTypeToWatch"));
        form.add(new DropDownChoice<>("syncItemChange", Arrays.asList(SyncItemListener.Change.values())));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruTaskServiceHelper.updateDbEntity((DbSyncItemListenerTaskConfig) form.getDefaultModelObject());
            }
        });
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(TutorialTable.class);
            }
        });


    }

}