/*
 * Copyright (c) 2010.
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

package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.common.tutorial.AbstractTaskConfig;
import com.btxtech.game.jsre.common.tutorial.ConditionTaskConfig;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.util.Locale;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 14:11:15
 */
@Entity
@DiscriminatorValue("CONDITION_CONFIG")
public class DbConditionTaskConfig extends DbAbstractTaskConfig {
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbConditionConfig conditionConfig;

    public DbConditionConfig getConditionConfig() {
        return conditionConfig;
    }

    public void setConditionConfig(DbConditionConfig conditionConfig) {
        this.conditionConfig = conditionConfig;
    }

    @Override
    protected AbstractTaskConfig createTaskConfig(ServerItemTypeService serverItemTypeService, Locale locale) {
        ConditionTaskConfig conditionTaskConfig = new ConditionTaskConfig();
        conditionTaskConfig.setConditionConfig(conditionConfig != null ? conditionConfig.createConditionConfig(serverItemTypeService, locale) : null);
        return conditionTaskConfig;
    }
}
