package com.btxtech.game.jsre.client.common.info;

import java.io.Serializable;

/**
 * User: beat
 * Date: 02.06.13
 * Time: 18:59
 */
public class CrystalCostInfo implements Serializable {
    private int cost;
    private int crystalAmount;

    /**
     * Used by GWT
     */
    CrystalCostInfo() {
    }

    public CrystalCostInfo(int cost, int crystalAmount) {
        this.cost = cost;
        this.crystalAmount = crystalAmount;
    }

    public int getCost() {
        return cost;
    }

    public int getCrystalAmount() {
        return crystalAmount;
    }

    public boolean isAfordable() {
        return crystalAmount >= cost;
    }
}
