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

package com.btxtech.game.wicket.pages.mgmt.level;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.mgmt.MgmtPage;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 14.05.2010
 * Time: 14:53:19
 */
public class LevelTable extends MgmtWebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public LevelTable() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("form");
        add(form);

        new CrudRootTableHelper<DbLevel>("levels", "save", "create", true, form, true) {
            @Override
            protected CrudRootServiceHelper<DbLevel> getCrudRootServiceHelperImpl() {
                return userGuidanceService.getDbLevelCrud();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbLevel> item) {
                displayId(item);
                item.add(new TextField("number"));
            }

            @Override
            protected void onEditSubmit(DbLevel dbLevel) {
                setResponsePage(new DbLevelEditor(dbLevel));
            }
        };

        form.add(new Button("activate") {
            @Override
            public void onSubmit() {
                try {
                    userGuidanceService.activateLevels();
                } catch (LevelActivationException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                setResponsePage(MgmtPage.class);
            }
        });
    }
}
