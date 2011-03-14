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
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbComparisonItemCount;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
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
    private UserGuidanceService userGuidanceService;

    public SyncItemTypeComparisonConfigPanel(String id) {
        super(id);
        setDefaultModel(new CompoundPropertyModel<DbSyncItemTypeComparisonConfig>(new IModel<DbSyncItemTypeComparisonConfig>() {

            @Override
            public DbSyncItemTypeComparisonConfig getObject() {
                // TODO Why does not wicket do this?
                DbConditionConfig dbConditionConfig = (DbConditionConfig) getParent().getDefaultModelObject();
                return (DbSyncItemTypeComparisonConfig) dbConditionConfig.getDbAbstractComparisonConfig();
            }

            @Override
            public void setObject(DbSyncItemTypeComparisonConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
            }
        }));

        new CrudRootTableHelper<DbComparisonItemCount>("itemCounts", null, "createItemCount", false, this, false) {

            @Override
            protected void extendedPopulateItem(Item<DbComparisonItemCount> dbComparisonItemCountItem) {
                dbComparisonItemCountItem.add(new BaseItemTypePanel("itemType"));
                dbComparisonItemCountItem.add(new TextField("count"));
            }

            @Override
            protected CrudServiceHelper<DbComparisonItemCount> getCrudRootServiceHelperImpl() {
                return ((DbSyncItemTypeComparisonConfig) getDefaultModelObject()).getCrudDbComparisonItemCount();
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new Button(createId) {

                    @Override
                    public void onSubmit() {
                         userGuidanceService.createDbComparisonItemCount((DbSyncItemTypeComparisonConfig)SyncItemTypeComparisonConfigPanel.this.getDefaultModelObject());
                    }
                });
            }
        };
    }
}
