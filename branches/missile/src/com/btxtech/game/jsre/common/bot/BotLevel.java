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

package com.btxtech.game.jsre.common.bot;

import java.util.ArrayList;

/**
 * User: beat
 * Date: 19.03.2010
 * Time: 23:20:17
 */
public class BotLevel {
    private int attackPause;
    private int attackPauseMinDistance;
    private int attackPauseMaxDistance;
    private double attackHold;
    ArrayList<ItemTypeBalance> itemTypeBalances = new ArrayList<ItemTypeBalance>();

    public void addItemTypeBalance(String itemTypeName, int count) {
        itemTypeBalances.add(new ItemTypeBalance(itemTypeName, count));
    }

    public void setAttackPause(int attackPause) {
        this.attackPause = attackPause;
    }

    public void setAttackHold(double attackHold) {
        this.attackHold = attackHold;
    }

    public int getAttackPause() {
        return attackPause;
    }

    public double getAttackHold() {
        return attackHold;
    }

    public ArrayList<ItemTypeBalance> getItemTypeBalances() {
        return itemTypeBalances;
    }

    public int getAttackPauseMinDistance() {
        return attackPauseMinDistance;
    }

    public void setAttackPauseDistance(int min, int max) {
        attackPauseMinDistance = min;
        attackPauseMaxDistance = max;
    }

    public int getAttackPauseMaxDistance() {
        return attackPauseMaxDistance;
    }
}
