package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

/**
 * User: beat
 * Date: 16.10.2011
 * Time: 15:25:58
 */
public class PlaceCanNotBeFoundException extends RuntimeException {
    public PlaceCanNotBeFoundException(ItemType itemType, Rectangle region, int itemFreeRange) {
        super("Can not find free position. itemType: " + itemType + " region: " + region + " itemFreeRange: " + itemFreeRange);
    }

    public PlaceCanNotBeFoundException(ItemType itemType, Region region, int itemFreeRange) {
        super("Can not find free position. itemType: " + itemType + " region Id: " + region.getId() + " itemFreeRange: " + itemFreeRange);
    }
}
