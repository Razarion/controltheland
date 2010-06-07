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

import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.territory.DbTerritory;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 28.05.2010
 * Time: 23:04:46
 */
public class TerritoryAssignment extends Panel {
    public TerritoryAssignment
            (String id, final DbBaseItemType dbBaseItemType, final DbTerritory dbTerritory) {
        super(id);
        add(new CheckBox("check", new IModel<Boolean>() {

            @Override
            public Boolean getObject() {
                return dbTerritory.isItemAllowed(dbBaseItemType);
            }

            @Override
            public void setObject(Boolean allowed) {
                dbTerritory.setItemAllowed(dbBaseItemType, allowed);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));

    }

}
