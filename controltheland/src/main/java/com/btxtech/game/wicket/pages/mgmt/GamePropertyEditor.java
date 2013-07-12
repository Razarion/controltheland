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

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.common.db.DbProperty;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: Jun 11, 2013
 * Time: 10:35:35 PM
 */
public class GamePropertyEditor extends MgmtWebPage {
    @SpringBean
    private PropertyService propertyService;

    public GamePropertyEditor() {
        Form form = new Form("propertyForm");
        add(form);

        new CrudRootTableHelper<DbProperty>("properties", "save", "add", false, form, false) {

            @Override
            protected CrudRootServiceHelper<DbProperty> getCrudRootServiceHelperImpl() {
                return propertyService.getDPropertyCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbProperty> item) {
                displayId(item);
                item.add(new DropDownChoice<>("propertyServiceEnum", Arrays.asList(PropertyServiceEnum.values()), new ChoiceRenderer<PropertyServiceEnum>("displayName")));
                item.add(new TextField("valueAsString"));
            }
        };

        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                setResponsePage(MgmtPage.class);
            }
        }.setDefaultFormProcessing(false));
    }

}
