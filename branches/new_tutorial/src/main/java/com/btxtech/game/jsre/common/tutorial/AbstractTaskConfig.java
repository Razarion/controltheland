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
import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.client.utg.tip.StorySplashPopupInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:25:27
 */
public abstract class AbstractTaskConfig implements Serializable {
    private Collection<ItemTypeAndPosition> ownItems;
    private Index scroll;
    private int houseCount;
    private int money;
    private int maxMoney;
    private String name;
    private Collection<BotConfig> botConfigs;
    private Map<Integer, Integer> itemTypeLimitation;
    private RadarMode radarMode;
    private GameTipConfig gameTipConfig;
    private boolean clearGame;
    private StorySplashPopupInfo storySplashPopupInfo;
    private StorySplashPopupInfo praiseSplash;

    /**
     * Used by GWT
     */
    AbstractTaskConfig() {
    }

    public AbstractTaskConfig(List<ItemTypeAndPosition> ownItems, Index scroll, int houseCount, int money, int maxMoney, String name, Collection<BotConfig> botConfigs, Map<Integer, Integer> itemTypeLimitation, RadarMode radarMode, GameTipConfig gameTipConfig, boolean clearGame, StorySplashPopupInfo storySplashPopupInfo, StorySplashPopupInfo praiseSplash) {
        this.ownItems = ownItems;
        this.scroll = scroll;
        this.houseCount = houseCount;
        this.money = money;
        this.maxMoney = maxMoney;
        this.name = name;
        this.botConfigs = botConfigs;
        this.itemTypeLimitation = itemTypeLimitation;
        this.radarMode = radarMode;
        this.gameTipConfig = gameTipConfig;
        this.clearGame = clearGame;
        this.storySplashPopupInfo = storySplashPopupInfo;
        this.praiseSplash = praiseSplash;
    }

    public abstract AbstractTask createTask();

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

    public GameTipConfig getGameTipConfig() {
        return gameTipConfig;
    }


    public StorySplashPopupInfo getStorySplashPopupInfo() {
        return storySplashPopupInfo;
    }

    public StorySplashPopupInfo getPraiseSplash() {
        return praiseSplash;
    }
}
