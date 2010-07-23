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

package com.btxtech.game.jsre.client.simulation.condition;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.common.tutorial.condition.HarvestConditionConfig;

/**
 * User: beat
 * Date: 21.07.2010
 * Time: 21:05:57
 */
public class HarvestCondition extends AbstractCondition {
    private HarvestConditionConfig harvestConditionConfig;
    private double accountBalance;

    public HarvestCondition(HarvestConditionConfig harvestConditionConfig) {
        this.harvestConditionConfig = harvestConditionConfig;
        accountBalance = ClientBase.getInstance().getAccountBalance();
    }

    @Override
    public boolean isFulfilledHarvest() {
        return ClientBase.getInstance().getAccountBalance() >= accountBalance + harvestConditionConfig.getAmount();
    }
}