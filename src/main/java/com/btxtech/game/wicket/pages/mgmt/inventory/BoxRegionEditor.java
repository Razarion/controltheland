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

package com.btxtech.game.wicket.pages.mgmt.inventory;

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.planet.db.DbBoxRegion;
import com.btxtech.game.services.planet.db.DbBoxRegionCount;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.planet.PlanetTable;
import com.btxtech.game.wicket.uiservices.BoxItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class BoxRegionEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbBoxRegion> ruTaskServiceHelper;

    public BoxRegionEditor(DbBoxRegion dbBoxRegion) {
        add(new FeedbackPanel("msgs"));

        final Form<DbBoxRegion> form = new Form<>("boxRegionForm", new CompoundPropertyModel<DbBoxRegion>(new RuModel<DbBoxRegion>(dbBoxRegion, DbBoxRegion.class) {
            @Override
            protected RuServiceHelper<DbBoxRegion> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }
        }));
        add(form);

        new CrudChildTableHelper<DbBoxRegion, DbBoxRegionCount>("counts", null, "createCount", false, form, false) {
            @Override
            protected RuServiceHelper<DbBoxRegion> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbBoxRegion getParent() {
                return (DbBoxRegion) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbBoxRegionCount> getCrudChildServiceHelperImpl() {
                return ((DbBoxRegion) form.getDefaultModelObject()).getBoxRegionCountCrud();
            }

            @Override
            protected void extendedPopulateItem(Item<DbBoxRegionCount> dbTaskAllowedItemItem) {
                dbTaskAllowedItemItem.add(new TextField("count"));
                dbTaskAllowedItemItem.add(new BoxItemTypePanel("dbBoxItemType"));
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruTaskServiceHelper.updateDbEntity((DbBoxRegion) form.getDefaultModelObject());
            }
        });
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(PlanetTable.class);
            }
        });
    }

}