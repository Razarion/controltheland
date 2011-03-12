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

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.territory.DbTerritory;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 28.05.2010
 * Time: 23:04:46
 */
public class TerritoryAssignment extends Panel {
    @SpringBean
    private ItemService itemService;

    public TerritoryAssignment(String id, final int dbBaseItemTypeId, final IModel<DbTerritory> model) {
        super(id);
        add(new CheckBox("check", new IModel<Boolean>() {

            @Override
            public Boolean getObject() {
                return model.getObject().isItemAllowed((DbBaseItemType) itemService.getDbItemType(dbBaseItemTypeId));
            }

            @Override
            public void setObject(Boolean allowed) {
                model.getObject().setItemAllowed((DbBaseItemType) itemService.getDbItemType(dbBaseItemTypeId), allowed);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));

    }

}
