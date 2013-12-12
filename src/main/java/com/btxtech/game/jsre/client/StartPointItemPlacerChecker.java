package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;

/**
 * User: beat
 * Date: 01.05.13
 * Time: 12:55
 */
abstract public class StartPointItemPlacerChecker {
    private PlanetServices planetServices;
    private boolean isTerrainOk;
    private boolean isItemsOk;
    private boolean isEnemiesOk;
    private BaseItemType baseItemType;
    private int itemFreeRadius;

    public StartPointItemPlacerChecker(BaseItemType baseItemType, int itemFreeRadius, PlanetServices planetServices) {
        this.baseItemType = baseItemType;
        this.itemFreeRadius = itemFreeRadius + baseItemType.getBoundingBox().getRadius();
        this.planetServices = planetServices;
    }

    protected abstract boolean hasEnemyInRange(Index absoluteMiddlePosition, int itemFreeRadius);

    public void check(Index absoluteMiddlePosition) {
        isItemsOk = false;
        try {
            isEnemiesOk = !hasEnemyInRange(absoluteMiddlePosition, itemFreeRadius);
        } catch (Exception e) {
            isEnemiesOk = false;
            // The deleted may still exists
            // The remove package (SyncItemInfo) from the old item may be processed after the base lost package
        }
        if (isEnemiesOk) {
            isItemsOk = !planetServices.getItemService().hasItemsInRectangleFast(Rectangle.generateRectangleFromMiddlePoint(absoluteMiddlePosition, baseItemType.getBoundingBox().getDiameter(), baseItemType.getBoundingBox().getDiameter()));
        }
        if (isItemsOk) {
            isTerrainOk = planetServices.getTerrainService().isFree(absoluteMiddlePosition, baseItemType, null, null);
        }
    }

    public boolean isTerrainOk() {
        return isTerrainOk;
    }

    public boolean isItemsOk() {
        return isItemsOk;
    }

    public boolean isEnemiesOk() {
        return isEnemiesOk;
    }

    public boolean isPositionValid() {
        return isItemsOk && isEnemiesOk && isTerrainOk;
    }

    public int getItemFreeRadius() {
        return itemFreeRadius;
    }
}
