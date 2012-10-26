/*
 * Copyright (c) 2011.
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

package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collection;
import java.util.Iterator;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class ItemTypesReadonlyPanel extends Panel {
    @SpringBean
    private PlanetSystemService planetSystemService;

    public ItemTypesReadonlyPanel(String id) {
        super(id);
        add(new Label("itemTypeNames", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                Collection<DbItemType> dbItemTypes = (Collection<DbItemType>) getDefaultModelObject();
                if (dbItemTypes != null) {
                    StringBuilder builder = new StringBuilder();
                    for (Iterator<DbItemType> iterator = dbItemTypes.iterator(); iterator.hasNext(); ) {
                        DbItemType dbItemType = iterator.next();
                        builder.append(dbItemType.getName());
                        builder.append(" (");
                        builder.append(dbItemType.getId());
                        builder.append(")");
                        if (iterator.hasNext()) {
                            builder.append("<br>");
                        }
                    }
                    return builder.toString();
                } else {
                    return null;
                }
            }
        }).setEscapeModelStrings(false));

    }
}
