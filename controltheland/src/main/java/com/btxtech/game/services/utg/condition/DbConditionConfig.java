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

package com.btxtech.game.services.utg.condition;

import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.item.ItemService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * User: beat
 * Date: 29.12.2010
 * Time: 18:21:51
 */
@Entity(name = "GUIDANCE_CONDITION_CONFIG")
public class DbConditionConfig implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private DbAbstractComparisonConfig dbAbstractComparisonConfig; // TODO DELETE_ORPHAN does not work
    private ConditionTrigger conditionTrigger;
    @Transient
    private ConditionConfig conditionConfig;

    public DbConditionConfig() {
    }

    public DbConditionConfig(DbConditionConfig dbConditionConfig) {
        dbAbstractComparisonConfig = dbConditionConfig.dbAbstractComparisonConfig.copy();
        conditionTrigger = dbConditionConfig.conditionTrigger;
    }

    public Integer getId() {
        return id;
    }

    public DbAbstractComparisonConfig getDbAbstractComparisonConfig() {
        return dbAbstractComparisonConfig;
    }

    public void setDbAbstractComparisonConfig(DbAbstractComparisonConfig dbAbstractComparisonConfig) {
        this.dbAbstractComparisonConfig = dbAbstractComparisonConfig;
    }

    public ConditionTrigger getConditionTrigger() {
        return conditionTrigger;
    }

    public void setConditionTrigger(ConditionTrigger conditionTrigger) {
        this.conditionTrigger = conditionTrigger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbConditionConfig)) return false;

        DbConditionConfig that = (DbConditionConfig) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    public ConditionConfig createConditionConfig(ItemService itemService) {
        if (conditionTrigger == null) {
            throw new IllegalStateException("conditionTrigger is null");
        }
        if (conditionTrigger.isComparisonNeeded() && dbAbstractComparisonConfig == null) {
            throw new IllegalStateException("dbAbstractComparisonConfig is null");
        }
        AbstractComparisonConfig abstractComparisonConfig = null;
        if (conditionTrigger.isComparisonNeeded()) {
            abstractComparisonConfig = dbAbstractComparisonConfig.createComparisonConfig(itemService);
        }
        conditionConfig = new ConditionConfig(conditionTrigger, abstractComparisonConfig);
        return conditionConfig;
    }
}
