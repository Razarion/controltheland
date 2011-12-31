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

package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.tutorial.DbTaskAllowedItem;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 16.01.2011
 * Time: 21:12:06
 */
@Entity
@DiscriminatorValue("SIMULATION")
public class DbSimulationLevel extends DbAbstractLevel {
    @ManyToOne
    private DbTutorialConfig dbTutorialConfig;

    public DbTutorialConfig getDbTutorialConfig() {
        return dbTutorialConfig;
    }

    public void setDbTutorialConfig(DbTutorialConfig dbTutorialConfig) {
        this.dbTutorialConfig = dbTutorialConfig;
    }

    @Override
    protected ConditionConfig createConditionConfig(ItemService itemService) {
        return new ConditionConfig(ConditionTrigger.TUTORIAL, null);
    }

    @Override
    public String getDisplayType() {
        return "Simulation";
    }

    @Override
    protected Level createLevel() throws LevelActivationException {
        // Get all needed ItemTypes
        if (dbTutorialConfig == null) {
            throw new LevelActivationException("No tutorial set");
        }

        return new Level(getId(), getName(), getInGameHtml(), false, 0, createLimitationMap(), 0);
    }

    private Map<Integer, Integer> createLimitationMap() {
        Map<Integer, Integer> itemTypeLimitation = new HashMap<Integer, Integer>();
        for (DbTaskConfig dbTaskConfig : dbTutorialConfig.getDbTaskConfigs()) {
            for (DbTaskAllowedItem dbTaskAllowedItem : dbTaskConfig.getAllowedItemHelper().readDbChildren()) {
                Integer count = itemTypeLimitation.get(dbTaskAllowedItem.getDbBaseItemType().getId());
                if (count == null) {
                    count = 0;
                }
                Integer newCount = count + dbTaskAllowedItem.getCount();
                itemTypeLimitation.put(dbTaskAllowedItem.getDbBaseItemType().getId(), newCount);
            }
        }
        return itemTypeLimitation;
    }
}
