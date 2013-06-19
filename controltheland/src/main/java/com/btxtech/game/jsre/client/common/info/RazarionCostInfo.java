package com.btxtech.game.jsre.client.common.info;

import java.io.Serializable;

/**
 * User: beat
 * Date: 02.06.13
 * Time: 18:59
 */
public class RazarionCostInfo implements Serializable {
    private int cost;
    private int razarionAmount;

    /**
     * Used by GWT
     */
    RazarionCostInfo() {
    }

    public RazarionCostInfo(int cost, int razarionAmount) {
        this.cost = cost;
        this.razarionAmount = razarionAmount;
    }

    public int getCost() {
        return cost;
    }

    public int getRazarionAmount() {
        return razarionAmount;
    }

    public boolean isAfordable() {
        return razarionAmount >= cost;
    }
}
