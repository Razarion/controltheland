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

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.OnlineBaseUpdate;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import java.util.Collection;

/**
 * User: beat
 * Date: 16.07.2010
 * Time: 23:41:47
 */
public class RealityInfo extends GameInfo {
    private SimpleBase base;
    private double accountBalance;
    private int xp;
    private Collection<Integer> allowedItemTypes;
    private int energyGenerating;
    private int energyConsuming;
    private OnlineBaseUpdate onlineBaseUpdate;
    private Level level;
    private Collection<BaseAttributes> allBases;

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

    public Collection<Integer> getAllowedItemTypes() {
        return allowedItemTypes;
    }

    public void setAllowedItemTypes(Collection<Integer> allowedItemTypes) {
        this.allowedItemTypes = allowedItemTypes;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
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

    public OnlineBaseUpdate getOnlineBaseUpdate() {
        return onlineBaseUpdate;
    }

    public void setOnlineBaseUpdate(OnlineBaseUpdate onlineBaseUpdate) {
        this.onlineBaseUpdate = onlineBaseUpdate;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Collection<BaseAttributes> getAllBase() {
        return allBases;
    }

    public void setAllBases(Collection<BaseAttributes> allBases) {
        this.allBases = allBases;
    }

    @Override
    public boolean showMissionTargetDialog() {
        return true;
    }

    @Override
    public boolean hasServerCommunication() {
        return true;
    }
}
