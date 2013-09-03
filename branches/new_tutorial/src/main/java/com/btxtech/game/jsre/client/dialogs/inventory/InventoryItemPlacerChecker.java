package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;

import java.util.Collection;

/**
 * User: beat
 * Date: 26.09.12
 * Time: 12:55
 */
public class InventoryItemPlacerChecker {
    private SimpleBase simpleBase;
    private PlanetServices planetServices;
    private boolean isTerrainOk;
    private boolean isItemsOk;
    private boolean isEnemiesOk;
    private BaseItemType baseItemType;
    private int placeWidth;
    private int placeHeight;
    private int itemFreeRadius;
    private int columns;
    private int rows;
    private int singleItemWidth;
    private int singleItemHeight;

    public InventoryItemPlacerChecker(BaseItemType baseItemType, int itemCount, int itemFreeRange, SimpleBase simpleBase, PlanetServices planetServices) {
        this.baseItemType = baseItemType;
        this.simpleBase = simpleBase;
        this.planetServices = planetServices;
        double value = Math.sqrt(itemCount);
        columns = (int) Math.ceil(value);
        rows = (int) Math.round(value);
        ItemTypeSpriteMap itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        singleItemWidth = itemTypeSpriteMap.getImageWidth();
        singleItemHeight = itemTypeSpriteMap.getImageHeight();
        placeWidth = columns * singleItemWidth;
        placeHeight = rows * singleItemHeight;
        itemFreeRadius = itemFreeRange + (int) MathHelper.getPythagoras(placeWidth, placeHeight) / 2;
    }

    public void check(Collection<Index> absoluteItemPositions) {
        check(Index.calculateMiddle(absoluteItemPositions), absoluteItemPositions);
    }

    public void check(Index absoluteMiddlePosition, Collection<Index> absoluteItemPositions) {
        isItemsOk = false;
        isEnemiesOk = !planetServices.getItemService().hasEnemyInRange(simpleBase, absoluteMiddlePosition, itemFreeRadius);
        if (isEnemiesOk) {
            isItemsOk = !planetServices.getItemService().hasItemsInRectangleFast(Rectangle.generateRectangleFromMiddlePoint(absoluteMiddlePosition, placeWidth, placeHeight));
        }
        if (isItemsOk) {
            for (Index absoluteItemPosition : absoluteItemPositions) {
                if (!checkPlacingAllowed(absoluteItemPosition)) {
                    break;
                }
            }
        }
    }

    private boolean checkPlacingAllowed(Index absolutePos) {
        isTerrainOk = planetServices.getTerrainService().isFree(absolutePos, baseItemType);
        return isTerrainOk;
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

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getSingleItemWidth() {
        return singleItemWidth;
    }

    public int getSingleItemHeight() {
        return singleItemHeight;
    }
}
