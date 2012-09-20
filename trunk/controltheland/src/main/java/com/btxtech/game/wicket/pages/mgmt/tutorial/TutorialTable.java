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

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.level.LevelTable;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class TutorialTable extends MgmtWebPage {
    @SpringBean
    private TutorialService tutorialService;

    public TutorialTable() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("tutorialForm");
        add(form);

        new CrudRootTableHelper<DbTutorialConfig>("tutorialTable", "save", "create", true, form, false) {

            @Override
            protected CrudRootServiceHelper<DbTutorialConfig> getCrudRootServiceHelperImpl() {
                return tutorialService.getDbTutorialCrudRootServiceHelper();
            }

            @Override
            protected void onEditSubmit(DbTutorialConfig dbTutorialConfig) {
                setResponsePage(new TutorialEditor(dbTutorialConfig));
            }

            @Override
            protected void extendedPopulateItem(Item<DbTutorialConfig> dbTutorialConfigItem) {
                super.extendedPopulateItem(dbTutorialConfigItem);
                dbTutorialConfigItem.add(new Label("id"));
            }
        };

        form.add(new Button("activate") {

            @Override
            public void onSubmit() {
                setResponsePage(LevelTable.class);
            }
        });
    }

}
