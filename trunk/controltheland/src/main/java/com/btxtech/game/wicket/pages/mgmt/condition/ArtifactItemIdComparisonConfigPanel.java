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
import com.btxtech.game.services.utg.condition.DbArtifactItemIdComparisonConfig;
import com.btxtech.game.services.utg.condition.DbComparisonArtifactItemCount;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.InventoryArtifactPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 10.09.2013
 * Time: 23:13:41
 */
public class ArtifactItemIdComparisonConfigPanel extends Panel {
    @SpringBean
    private RuServiceHelper<DbArtifactItemIdComparisonConfig> ruServiceHelper;

    public ArtifactItemIdComparisonConfigPanel(String id) {
        super(id);
        setDefaultModel(new CompoundPropertyModel<>(new IModel<DbArtifactItemIdComparisonConfig>() {

            @Override
            public DbArtifactItemIdComparisonConfig getObject() {
                // TODO Why does not wicket do this?
                DbConditionConfig dbConditionConfig = (DbConditionConfig) getParent().getDefaultModelObject();
                return (DbArtifactItemIdComparisonConfig) dbConditionConfig.getDbAbstractComparisonConfig();
            }

            @Override
            public void setObject(DbArtifactItemIdComparisonConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
            }
        }));

        new CrudChildTableHelper<DbArtifactItemIdComparisonConfig, DbComparisonArtifactItemCount>("artifactCounts", null, "createArtifactCount", false, this, false) {


            @Override
            protected void extendedPopulateItem(Item<DbComparisonArtifactItemCount> artifactItemCountItem) {
                artifactItemCountItem.add(new InventoryArtifactPanel("dbInventoryArtifact"));
                artifactItemCountItem.add(new TextField("count"));
            }

            @Override
            protected RuServiceHelper<DbArtifactItemIdComparisonConfig> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbArtifactItemIdComparisonConfig getParent() {
                return (DbArtifactItemIdComparisonConfig) getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbComparisonArtifactItemCount> getCrudChildServiceHelperImpl() {
                return getParent().getArtifactItemCountCrud();
            }
        };
    }
}
