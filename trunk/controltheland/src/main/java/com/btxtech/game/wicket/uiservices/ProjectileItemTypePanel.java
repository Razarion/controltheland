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
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeI;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
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
public class ProjectileItemTypePanel extends Panel {
    @SpringBean
    private ServerItemTypeService serverItemTypeService;

    public ProjectileItemTypePanel(String id) {
        super(id);
        add(new TextField<>("projectileItemType", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbItemTypeI itemType = (DbItemTypeI) getDefaultModelObject();
                // Using DbItemTypeI instead of DbBaseItemTypeI
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
                    DbProjectileItemType dbProjectileItemType = (DbProjectileItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(integer);
                    setDefaultModelObject(dbProjectileItemType);
                } else {
                    setDefaultModelObject(null);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new Label("projectileItemTypeName", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                DbItemTypeI itemType = (DbItemTypeI) getDefaultModelObject();
                // Using DbItemTypeI instead of DbBaseItemTypeI
                // java.lang.ClassCastException: com.btxtech.game.services.item.itemType.DbItemType_$$_javassist_1 cannot be cast to com.btxtech.game.services.item.itemType.DbBaseItemTypeI
                if (itemType != null) {
                    return itemType.getName();
                } else {
                    return null;
                }
            }
        }));
    }
}
