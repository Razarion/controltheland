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

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.utg.DbXpSettings;
import com.btxtech.game.services.utg.XpService;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class XpSettingsEditor extends MgmtWebPage {
    @SpringBean
    private XpService xpService;
    @SpringBean
    private RuServiceHelper<DbXpSettings> serviceHelper;

    public XpSettingsEditor() {
        Form<DbXpSettings> form = new Form<DbXpSettings>("form", new CompoundPropertyModel<DbXpSettings>(new IModel<DbXpSettings>() {
            private DbXpSettings xpSettings;

            @Override
            public DbXpSettings getObject() {
                if (xpSettings == null) {
                    xpSettings = xpService.getXpPointSettings();
                }
                return xpSettings;
            }

            @Override
            public void setObject(DbXpSettings object) {
                // Ignore
            }

            @Override
            public void detach() {
                xpSettings = null;
            }
        })) {
            @Override
            protected void onSubmit() {
                xpService.saveXpPointSettings(getModelObject());
            }
        };
        form.add(new TextField<String>("killPriceFactor"));
        form.add(new TextField<String>("killQueuePeriod"));
        form.add(new TextField<String>("killQueueSize"));
        form.add(new TextField<String>("builtPriceFactor"));
        add(form);
    }
}