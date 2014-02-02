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
import com.btxtech.game.jsre.client.utg.tip.PraiseSplashPopupInfo;
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
    private int id;
    private Collection<BotConfig> botConfigs;
    private Collection<Integer> botIdsToStop;
    private Map<Integer, Integer> itemTypeLimitation;
    private RadarMode radarMode;
    private GameTipConfig gameTipConfig;
    private boolean clearGame;
    private StorySplashPopupInfo storySplashPopupInfo;
    private PraiseSplashPopupInfo praiseSplashPopupInfo;

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

    public int getId() {
        return id;
    }

    public Collection<BotConfig> getBotConfigs() {
        return botConfigs;
    }

    public boolean hasBots() {
        return botConfigs != null && !botConfigs.isEmpty();
    }

    public Collection<Integer> getBotIdsToStop() {
        return botIdsToStop;
    }

    public boolean hasBotIdsToStop() {
        return botIdsToStop != null && !botIdsToStop.isEmpty();
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

    public PraiseSplashPopupInfo getPraiseSplashPopupInfo() {
        return praiseSplashPopupInfo;
    }

    public void setBotConfigs(Collection<BotConfig> botConfigs) {
        this.botConfigs = botConfigs;
    }

    public void setBotIdsToStop(Collection<Integer> botIdsToStop) {
        this.botIdsToStop = botIdsToStop;
    }

    public void setClearGame(boolean clearGame) {
        this.clearGame = clearGame;
    }

    public void setGameTipConfig(GameTipConfig gameTipConfig) {
        this.gameTipConfig = gameTipConfig;
    }

    public void setHouseCount(int houseCount) {
        this.houseCount = houseCount;
    }

    public void setItemTypeLimitation(Map<Integer, Integer> itemTypeLimitation) {
        this.itemTypeLimitation = itemTypeLimitation;
    }

    public void setMaxMoney(int maxMoney) {
        this.maxMoney = maxMoney;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOwnItems(Collection<ItemTypeAndPosition> ownItems) {
        this.ownItems = ownItems;
    }

    public void setPraiseSplashPopupInfo(PraiseSplashPopupInfo praiseSplashPopupInfo) {
        this.praiseSplashPopupInfo = praiseSplashPopupInfo;
    }

    public void setRadarMode(RadarMode radarMode) {
        this.radarMode = radarMode;
    }

    public void setScroll(Index scroll) {
        this.scroll = scroll;
    }

    public void setStorySplashPopupInfo(StorySplashPopupInfo storySplashPopupInfo) {
        this.storySplashPopupInfo = storySplashPopupInfo;
    }
}
