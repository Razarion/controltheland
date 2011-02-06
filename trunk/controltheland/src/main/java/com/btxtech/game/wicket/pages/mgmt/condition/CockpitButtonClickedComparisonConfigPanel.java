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

import com.btxtech.game.jsre.common.utg.config.CockpitWidgetEnum;
import com.btxtech.game.services.utg.condition.DbCockpitButtonClickedComparisonConfig;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 14.01.2011
 * Time: 23:13:41
 */
public class CockpitButtonClickedComparisonConfigPanel extends Panel {

    public CockpitButtonClickedComparisonConfigPanel(String id) {
        super(id);
        setDefaultModel(new CompoundPropertyModel<DbCockpitButtonClickedComparisonConfig>(new IModel<DbCockpitButtonClickedComparisonConfig>() {

            @Override
            public DbCockpitButtonClickedComparisonConfig getObject() {
                // TODO Why does not wicket do this?
                DbConditionConfig dbConditionConfig = (DbConditionConfig) getParent().getDefaultModelObject();
                return (DbCockpitButtonClickedComparisonConfig) dbConditionConfig.getDbAbstractComparisonConfig();
            }

            @Override
            public void setObject(DbCockpitButtonClickedComparisonConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
            }
        }));

        add(new DropDownChoice<CockpitWidgetEnum>("cockpitWidgetEnum", CockpitWidgetEnum.getButtons()));
    }
}
