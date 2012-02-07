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

package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:25:27
 */
public class TaskConfig implements Serializable {
    private boolean clearGame;
    private Collection<ItemTypeAndPosition> ownItems;
    private Index scroll;
    private List<StepConfig> stepConfigs;
    private int houseCount;
    private int money;
    private int maxMoney;
    private double itemSellFactor;
    private String name;
    private Collection<BotConfig> botConfigs;
    private Map<Integer, Integer> itemTypeLimitation;

    /**
     * Used by GWT
     */
    public TaskConfig() {
    }

    public TaskConfig(boolean clearGame, ArrayList<ItemTypeAndPosition> ownItems, Index scroll, ArrayList<StepConfig> stepConfigs, int houseCount, int money, int maxMoney, double itemSellFactor, String name, Collection<BotConfig> botConfigs, Map<Integer, Integer> itemTypeLimitation) {
        this.clearGame = clearGame;
        this.ownItems = ownItems;
        this.scroll = scroll;
        this.stepConfigs = stepConfigs;
        this.houseCount = houseCount;
        this.money = money;
        this.maxMoney = maxMoney;
        this.itemSellFactor = itemSellFactor;
        this.name = name;
        this.botConfigs = botConfigs;
        this.itemTypeLimitation = itemTypeLimitation;
    }

    public boolean isClearGame() {
        return clearGame;
    }

    public Collection<ItemTypeAndPosition> getOwnItems() {
        return ownItems;
    }

    public Index getScroll() {
        return scroll;
    }

    public List<StepConfig> getStepConfigs() {
        return stepConfigs;
    }

    public String getName() {
        return name;
    }

    public Collection<BotConfig> getBotConfigs() {
        return botConfigs;
    }

    public boolean hasBots() {
        return botConfigs != null && !botConfigs.isEmpty();
    }

    public int getMoney() {
        return money;
    }

    public LevelScope createLevelScope(String levelName) {
        return new LevelScope(levelName, maxMoney, itemTypeLimitation, houseCount, itemSellFactor);
    }
}
