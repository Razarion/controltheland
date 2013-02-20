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
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:25:27
 */
public class TaskConfig implements Serializable {
    private Collection<ItemTypeAndPosition> ownItems;
    private Index scroll;
    private int houseCount;
    private int money;
    private int maxMoney;
    private String name;
    private Collection<BotConfig> botConfigs;
    private Map<Integer, Integer> itemTypeLimitation;
    private RadarMode radarMode;
    private ConditionConfig conditionConfig;
    private GameTipConfig gameTipConfig;
    private boolean clearGame;

    /**
     * Used by GWT
     */
    public TaskConfig() {
    }

    public TaskConfig(List<ItemTypeAndPosition> ownItems, Index scroll, ConditionConfig conditionConfig, int houseCount, int money, int maxMoney, String name, Collection<BotConfig> botConfigs, Map<Integer, Integer> itemTypeLimitation, RadarMode radarMode, GameTipConfig gameTipConfig, boolean clearGame) {
        this.ownItems = ownItems;
        this.scroll = scroll;
        this.conditionConfig = conditionConfig;
        this.houseCount = houseCount;
        this.money = money;
        this.maxMoney = maxMoney;
        this.name = name;
        this.botConfigs = botConfigs;
        this.itemTypeLimitation = itemTypeLimitation;
        this.radarMode = radarMode;
        this.gameTipConfig = gameTipConfig;
        this.clearGame = clearGame;
    }

    public Collection<ItemTypeAndPosition> getOwnItems() {
        return ownItems;
    }

    public Index getScroll() {
        return scroll;
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

    public LevelScope createLevelScope(int levelNumber) {
        return new LevelScope(PlanetInfo.MISSION_PLANET_ID, -1, levelNumber, itemTypeLimitation, 0);
    }

    public boolean isClearGame() {
        return clearGame;
    }

    public PlanetInfo createPlanetInfo() {
        PlanetInfo planetInfo = new PlanetInfo();
        planetInfo.setPlanetIdAndName(PlanetInfo.MISSION_PLANET_ID.getPlanetId(), null, null);
        planetInfo.setHouseSpace(houseCount);
        planetInfo.setMaxMoney(maxMoney);
        planetInfo.setItemTypeLimitation(itemTypeLimitation);
        planetInfo.setRadarMode(radarMode);
        return planetInfo;
    }

    public ConditionConfig getConditionConfig() {
        return conditionConfig;
    }

    public GameTipConfig getGameTipConfig() {
        return gameTipConfig;
    }
}
