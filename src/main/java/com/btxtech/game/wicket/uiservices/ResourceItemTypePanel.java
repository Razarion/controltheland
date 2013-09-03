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

import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class ResourceItemTypePanel extends Panel {
    @SpringBean
    private ServerItemTypeService serverItemTypeService;

    public ResourceItemTypePanel(String id) {
        super(id);
        add(new TextField<>("resourceItemType", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbItemType itemType = (DbItemType) getDefaultModelObject();
                // Using DbItemTypeI instead of DbResourceItemTypeI
                // java.lang.ClassCastException: com.btxtech.game.services.item.itemType.DbItemType_$$_javassist_1 cannot be cast to com.btxtech.game.services.item.itemType.DbBaseItemTypeI
                if (itemType != null) {
                    return itemType.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                if (integer != null) {
                    DbResourceItemType dbResourceItemType = serverItemTypeService.getDbResourceItemType(integer);
                    if (dbResourceItemType == null) {
                        error("Item type does not exist: " + integer);
                        return;
                    }
                    setDefaultModelObject(dbResourceItemType);
                } else {
                    setDefaultModelObject(null);
                }


            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new Label("resourceItemTypeName", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                DbResourceItemType resourceItemType = (DbResourceItemType) getDefaultModelObject();
                if (resourceItemType != null) {
                    return resourceItemType.getName();
                } else {
                    return null;
                }
            }
        }));

    }
}
