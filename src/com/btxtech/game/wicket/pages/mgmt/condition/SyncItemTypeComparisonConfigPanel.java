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

package com.btxtech.game.wicket.pages.mgmt.condition;

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 14.01.2011
 * Time: 23:13:41
 */
public class SyncItemTypeComparisonConfigPanel extends Panel {
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(SyncItemTypeComparisonConfigPanel.class);

    public SyncItemTypeComparisonConfigPanel(String id, DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig) {
        super(id, new CompoundPropertyModel<DbSyncItemTypeComparisonConfig>(dbSyncItemTypeComparisonConfig));
        add(new TextField<Integer>("dbItemType", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbItemType itemType = ((DbSyncItemTypeComparisonConfig) getDefaultModelObject()).getDbItemType();
                if (itemType != null) {
                    return itemType.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer id) {
                if (id != null) {
                    try {
                        ((DbSyncItemTypeComparisonConfig) getDefaultModelObject()).setDbItemType(itemService.getDbItemType(id));
                    } catch (Throwable t) {
                        log.error("", t);
                        error(t.getMessage());
                    }
                } else {
                    ((DbSyncItemTypeComparisonConfig) getDefaultModelObject()).setDbItemType(null);
                }
            }

            @Override
            public void detach() {
                //Ignore
            }
        }, Integer.class));
    }
}
