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

package com.btxtech.game.wicket.pages.mgmt.planet;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.planet.ResourceService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.db.DbRegionResource;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
import com.btxtech.game.wicket.uiservices.ResourceItemTypePanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 09.05.2010
 * Time: 15:49:16
 */
public class ResourceEditor extends MgmtWebPage {
    @SpringBean
    private ResourceService resourceService;
    @SpringBean
    private ServerItemService serverItemService;

    public ResourceEditor() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("regionResourceForm");
        add(form);

        new CrudRootTableHelper<DbRegionResource>("regionResources", "save", "create", false, form, false) {

            @Override
            protected CrudRootServiceHelper<DbRegionResource> getCrudRootServiceHelperImpl() {
                throw new UnsupportedOperationException();
                // TODO return resourceService.getDbRegionResourceCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbRegionResource> item) {
                super.extendedPopulateItem(item);
                item.add(new TextField<String>("count"));
                item.add(new ResourceItemTypePanel("resourceItemType"));
                item.add(new RectanglePanel("region"));
                item.add(new TextField<String>("minDistanceToItems"));
            }
        };

        form.add(new Button("activate") {

            @Override
            public void onSubmit() {
                throw new UnsupportedOperationException();
                // TODO resourceService.activate();
            }
        });

    }
}
