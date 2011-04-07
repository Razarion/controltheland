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

import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.market.XpSettings;
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
    private ServerMarketService serverMarketService;

    public XpSettingsEditor() {
        Form<XpSettings> form = new Form<XpSettings>("form", new CompoundPropertyModel<XpSettings>(new IModel<XpSettings>() {
            private XpSettings xpSettings;

            @Override
            public XpSettings getObject() {
                if (xpSettings == null) {
                    xpSettings = serverMarketService.getXpPointSettings();
                }
                return xpSettings;
            }

            @Override
            public void setObject(XpSettings object) {
              // Ignore
            }

            @Override
            public void detach() {
                xpSettings = null;
            }
        })) {
            @Override
            protected void onSubmit() {
                serverMarketService.saveXpPointSettings(getModelObject());
            }
        };
        form.add(new TextField<String>("killPriceFactor"));
        form.add(new TextField<String>("periodItemFactor"));
        form.add(new TextField<String>("periodMinutes"));
        add(form);
    }
}