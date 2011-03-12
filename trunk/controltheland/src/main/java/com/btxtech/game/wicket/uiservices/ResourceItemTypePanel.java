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

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class ResourceItemTypePanel extends Panel {
    @SpringBean
    private ItemService itemService;

    public ResourceItemTypePanel(String id) {
        super(id);
        add(new TextField<Integer>("resourceItemType", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbResourceItemType resourceItemType = (DbResourceItemType) getDefaultModelObject();
                if (resourceItemType != null) {
                    return resourceItemType.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                DbResourceItemType resourceItemType = (DbResourceItemType) itemService.getDbItemType(integer);
                setDefaultModelObject(resourceItemType);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
    }
}
