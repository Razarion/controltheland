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

import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import com.btxtech.game.services.itemTypeAccess.XpSettings;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class XpSettingsEditor extends WebPage {
    @SpringBean
    private ServerItemTypeAccessService serverItemTypeAccessService;

    public XpSettingsEditor() {
        Form<XpSettings> form = new Form<XpSettings>("form", new CompoundPropertyModel<XpSettings>(serverItemTypeAccessService.getXpPointSettings())) {
            @Override
            protected void onSubmit() {
                serverItemTypeAccessService.saveXpPointSettings(getModelObject());
            }
        };
        form.add(new TextField<String>("killPriceFactor"));
        form.add(new TextField<String>("periodItemFactor"));
        form.add(new TextField<String>("periodMinutes"));
        add(form);
    }
}