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

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.wicket.pages.mgmt.DbLevelEditor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class BaseItemTypePanel extends Panel {
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(DbLevelEditor.class);

    public BaseItemTypePanel(String id) {
        super(id);
        add(new TextField<Integer>("baseItemType", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbBaseItemType baseItemType = (DbBaseItemType) getDefaultModelObject();
                if (baseItemType != null) {
                    return baseItemType.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemType(integer);
                setDefaultModelObject(dbBaseItemType);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
    }
}
