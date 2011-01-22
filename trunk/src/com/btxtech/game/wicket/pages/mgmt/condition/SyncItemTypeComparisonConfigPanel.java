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

import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbComparisonItemCount;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import com.btxtech.game.wicket.pages.mgmt.BaseItemTypeEditor;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 14.01.2011
 * Time: 23:13:41
 */
public class SyncItemTypeComparisonConfigPanel extends Panel {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public SyncItemTypeComparisonConfigPanel(String id, DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig) {
        super(id, new CompoundPropertyModel<DbSyncItemTypeComparisonConfig>(dbSyncItemTypeComparisonConfig));
        final int dbSyncItemTypeComparisonConfigId = dbSyncItemTypeComparisonConfig.getId();
        new CrudTableHelper<DbComparisonItemCount>("itemCounts", null, "createItemCount", false, this) {

            @Override
            protected void extendedPopulateItem(Item<DbComparisonItemCount> dbComparisonItemCountItem) {
                dbComparisonItemCountItem.add(new BaseItemTypePanel("itemType"));
                dbComparisonItemCountItem.add(new TextField("count"));
            }

            @Override
            protected CrudServiceHelper<DbComparisonItemCount> getCrudServiceHelper() {
                return ((DbSyncItemTypeComparisonConfig)getDefaultModelObject()).getCrudDbComparisonItemCount();
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new Button(createId) {

                    @Override
                    public void onSubmit() {
                        userGuidanceService.createDbComparisonItemCount(dbSyncItemTypeComparisonConfigId);
                    }
                });
            }
        };
    }
}
