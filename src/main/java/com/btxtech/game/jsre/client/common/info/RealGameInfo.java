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

package com.btxtech.game.jsre.client.common.info;

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.jsre.common.packets.XpPacket;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 16.07.2010
 * Time: 23:41:47
 */
public class RealGameInfo extends GameInfo {
    private SimpleBase base;
    private double accountBalance;
    private int energyGenerating;
    private int energyConsuming;
    private Collection<BaseAttributes> allBases;
    private int houseSpace;
    private LevelTaskPacket levelTaskPacket;
    private XpPacket xpPacket;
    private LevelScope levelScope;
    private PlanetInfo planetInfo;
    private List<PlanetLiteInfo> allPlanets;
    private UnlockContainer unlockContainer;
    private StartPointInfo startPointInfo;
    private SimpleGuild mySimpleGuild;
    private Collection<StorablePacket> storablePackets;

    public SimpleBase getBase() {
        return base;
    }

    public void setBase(SimpleBase base) {
        this.base = base;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public int getEnergyGenerating() {
        return energyGenerating;
    }

    public void setEnergyGenerating(int energyGenerating) {
        this.energyGenerating = energyGenerating;
    }

    public int getEnergyConsuming() {
        return energyConsuming;
    }

    public void setEnergyConsuming(int energyConsuming) {
        this.energyConsuming = energyConsuming;
    }

    public Collection<BaseAttributes> getAllBase() {
        return allBases;
    }

    public void setAllBases(Collection<BaseAttributes> allBases) {
        this.allBases = allBases;
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    public LevelTaskPacket getLevelTaskPacket() {
        return levelTaskPacket;
    }

    public void setLevelTaskPacket(LevelTaskPacket levelTaskPacket) {
        this.levelTaskPacket = levelTaskPacket;
    }

    public XpPacket getXpPacket() {
        return xpPacket;
    }

    public void setXpPacket(XpPacket xpPacket) {
        this.xpPacket = xpPacket;
    }

    public void setLevel(LevelScope levelScope) {
        this.levelScope = levelScope;
    }

    public LevelScope getLevelScope() {
        return levelScope;
    }

    public PlanetInfo getPlanetInfo() {
        return planetInfo;
    }

    public void setPlanetInfo(PlanetInfo planetInfo) {
        this.planetInfo = planetInfo;
    }

    public List<PlanetLiteInfo> getAllPlanets() {
        return allPlanets;
    }

    public void setAllPlanets(List<PlanetLiteInfo> allPlanets) {
        this.allPlanets = allPlanets;
    }

    public UnlockContainer getUnlockContainer() {
        return unlockContainer;
    }

    public void setUnlockContainer(UnlockContainer unlockContainer) {
        this.unlockContainer = unlockContainer;
    }

    @Override
    public boolean isSellAllowed() {
        return true;
    }

    public StartPointInfo getStartPointInfo() {
        return startPointInfo;
    }

    public void setStartPointInfo(StartPointInfo startPointInfo) {
        this.startPointInfo = startPointInfo;
    }

    public SimpleGuild getMySimpleGuild() {
        return mySimpleGuild;
    }

    public void setMySimpleGuild(SimpleGuild mySimpleGuild) {
        this.mySimpleGuild = mySimpleGuild;
    }

    public void setStorablePackets(Collection<StorablePacket> storablePackets) {
        this.storablePackets = storablePackets;
    }

    public Collection<StorablePacket> getStorablePackets() {
        return storablePackets;
    }
}
