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

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemIdComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemIdPositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
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
public class SyncItemIdPositionComparisonConfigPanel extends Panel {

    public SyncItemIdPositionComparisonConfigPanel(String id) {
        super(id);
        setDefaultModel(new CompoundPropertyModel<DbSyncItemIdPositionComparisonConfig>(new IModel<DbSyncItemIdPositionComparisonConfig>() {

            @Override
            public DbSyncItemIdPositionComparisonConfig getObject() {
                // TODO Why does not wicket do this?
                DbConditionConfig dbConditionConfig = (DbConditionConfig) getParent().getDefaultModelObject();
                return (DbSyncItemIdPositionComparisonConfig) dbConditionConfig.getDbAbstractComparisonConfig();
            }

            @Override
            public void setObject(DbSyncItemIdPositionComparisonConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
            }
        }));
        add(new TextField("syncItemId"));
        add(new RectanglePanel("region"));
    }
}
