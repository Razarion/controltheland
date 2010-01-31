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

package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.common.SimpleBase;
import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: Jun 5, 2009
 * Time: 8:19:05 PM
 */
public class GameInfo implements Serializable{
    private SimpleBase base;
    private int accountBalance;
    private int xp;
    private int[][] terrainField;
    private Collection<Integer> passableTerrainTileIds;
    private Collection<Integer> allowedItemTypes;
    private int energyGenerating;
    private int energyConsuming;

    public SimpleBase getBase() {
        return base;
    }

    public void setBase(SimpleBase base) {
        this.base = base;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }

    public int[][] getTerrainField() {
        return terrainField;
    }

    public void setTerrainField(int[][] terrainField) {
        this.terrainField = terrainField;
    }

    public Collection<Integer> getPassableTerrainTileIds() {
        return passableTerrainTileIds;
    }

    public void setPassableTerrainTileIds(Collection<Integer> passableTerrainTileIds) {
        this.passableTerrainTileIds = passableTerrainTileIds;
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
}
