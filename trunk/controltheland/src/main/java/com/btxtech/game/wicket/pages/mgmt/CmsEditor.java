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

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsHomeLayout;
import com.btxtech.game.services.cms.DbCmsHomeText;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 07.07.2010
 * Time: 21:23:15
 */
public class CmsEditor extends MgmtWebPage {
    @SpringBean
    private CmsService cmsService;

    public CmsEditor() {
        add(new FeedbackPanel("msgs"));
        createTextTable();
        createLayoutTable();
        add(new Form("activateForm") {

            @Override
            protected void onSubmit() {
                cmsService.activateHome();
            }
        });
    }

    private void createTextTable() {
        Form form = new Form("textForm");
        add(form);

        new CrudRootTableHelper<DbCmsHomeText>("textTable", "save", "add", true, form, false) {
            @Override
            protected CrudRootServiceHelper<DbCmsHomeText> getCrudRootServiceHelperImpl() {
                return cmsService.getCmsHomeTextCrudRootServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(Item<DbCmsHomeText> dbCmsHomeTextItem) {
                super.extendedPopulateItem(dbCmsHomeTextItem);
                dbCmsHomeTextItem.add(new CheckBox("isActive"));
            }

            @Override
            protected void onEditSubmit(DbCmsHomeText dbCmsHomeText) {
                setResponsePage(new CmsHomeTextEditor(dbCmsHomeText));
            }
        };
    }

    private void createLayoutTable() {
        Form form = new Form("layoutForm");
        add(form);

        new CrudRootTableHelper<DbCmsHomeLayout>("layoutTable", "save", "add", true, form, false) {
            @Override
            protected CrudRootServiceHelper<DbCmsHomeLayout> getCrudRootServiceHelperImpl() {
                return cmsService.getCmsHomeLayoutCrudRootServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(Item<DbCmsHomeLayout> dbCmsHomeLayoutItem) {
                super.extendedPopulateItem(dbCmsHomeLayoutItem);
                dbCmsHomeLayoutItem.add(new CheckBox("isActive"));
            }

            @Override
            protected void onEditSubmit(DbCmsHomeLayout dbCmsHomeLayout) {
                setResponsePage(new CmsHomeLayoutEditor(dbCmsHomeLayout));
            }
        };
    }

}
