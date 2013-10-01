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

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.utg.condition.DbComparisonItemCount;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbItemTypePositionComparisonConfig;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.RegionPanel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import org.apache.wicket.markup.html.form.CheckBox;
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
public class ItemTypePositionComparisonConfigPanel extends Panel {
    @SpringBean
    private RuServiceHelper<DbItemTypePositionComparisonConfig> ruServiceHelper;

    public ItemTypePositionComparisonConfigPanel(String id, TerrainLinkHelper terrainLinkHelper) {
        super(id);
        setDefaultModel(new CompoundPropertyModel<>(new IModel<DbItemTypePositionComparisonConfig>() {

            @Override
            public DbItemTypePositionComparisonConfig getObject() {
                // TODO Why does not wicket do this?
                DbConditionConfig dbConditionConfig = (DbConditionConfig) getParent().getDefaultModelObject();
                return (DbItemTypePositionComparisonConfig) dbConditionConfig.getDbAbstractComparisonConfig();
            }

            @Override
            public void setObject(DbItemTypePositionComparisonConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
            }
        }));
        new CrudChildTableHelper<DbItemTypePositionComparisonConfig, DbComparisonItemCount>("itemCounts", null, "createItemCount", false, this, false) {


            @Override
            protected void extendedPopulateItem(Item<DbComparisonItemCount> dbComparisonItemCountItem) {
                dbComparisonItemCountItem.add(new BaseItemTypePanel("itemType"));
                dbComparisonItemCountItem.add(new TextField("count"));
            }

            @Override
            protected RuServiceHelper<DbItemTypePositionComparisonConfig> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbItemTypePositionComparisonConfig getParent() {
                return (DbItemTypePositionComparisonConfig) getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbComparisonItemCount> getCrudChildServiceHelperImpl() {
                return getParent().getCrudDbComparisonItemCount();
            }
        };

        add(new RegionPanel("region", terrainLinkHelper){

            @Override
            protected void updateDependentModel() {
                ruServiceHelper.updateDbEntity((DbItemTypePositionComparisonConfig) ItemTypePositionComparisonConfigPanel.this.getDefaultModelObject());
            }
        });
        add(new TextField("timeInMinutes"));
        add(new CheckBox("addExistingItems"));
    }
}
