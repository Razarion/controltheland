package com.btxtech.game.jsre.client.common.info;

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;

/**
 * User: beat
 * Date: 01.05.13
 * Time: 13:00
 */
public class StartPointInfo implements Serializable {
    private Index suggestedPosition;
    private int baseItemTypeId;
    private int itemFreeRange;


    /**
     * Used by GWT
     */
    StartPointInfo() {
    }

    public StartPointInfo(int baseItemTypeId, int itemFreeRange) {
        this.baseItemTypeId = baseItemTypeId;
        this.itemFreeRange = itemFreeRange;
    }

    public int getBaseItemTypeId() {
        return baseItemTypeId;
    }

    public int getItemFreeRange() {
        return itemFreeRange;
    }

    public Index getSuggestedPosition() {
        return suggestedPosition;
    }

    public void setSuggestedPosition(Index suggestedPosition) {
        this.suggestedPosition = suggestedPosition;
    }
}
