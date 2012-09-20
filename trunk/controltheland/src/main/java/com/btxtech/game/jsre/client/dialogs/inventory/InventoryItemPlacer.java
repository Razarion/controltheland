package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 29.05.12
 * Time: 18:02
 */
public class InventoryItemPlacer {
    private Index relativeMiddlePos = new Index(200, 200);
    private Collection<Index> normalizedPositionsToPlace;
    private Collection<Index> relativePositionsToPlace;
    private int itemFreeRadius;
    private BaseItemType baseItemType;
    private boolean isTerrainOk;
    private boolean isItemsOk;
    private boolean isEnemiesOk;
    private Logger log = Logger.getLogger(InventoryItemPlacer.class.getName());
    private String errorText;
    private int inventoryItemId;
    private int placeWidth;
    private int placeHeight;

    public InventoryItemPlacer(InventoryItemInfo inventoryItemInfo) {
        TerrainView.getInstance().setFocus();
        inventoryItemId = inventoryItemInfo.getInventoryItemId();
        try {
            baseItemType = (BaseItemType) ItemTypeContainer.getInstance().getItemType(inventoryItemInfo.getBaseItemTypeId());
        } catch (NoSuchItemTypeException e) {
            log.log(Level.SEVERE, "InventoryItemPlacer() ", e);
        }
        calculateNormalizedPlacePositions(inventoryItemInfo.getItemCount(), inventoryItemInfo.getItemFreeRange());
        relativePositionsToPlace = Index.add(normalizedPositionsToPlace, relativeMiddlePos);
    }

    public void onMove(int relativeX, int relativeY, int absoluteX, int absoluteY) {
        relativeMiddlePos = new Index(relativeX, relativeY);
        relativePositionsToPlace = Index.add(normalizedPositionsToPlace, relativeMiddlePos);
        checkPlacingForAllAllowed(absoluteX, absoluteY);
        setupErrorText();
    }

    public boolean execute(int absoluteX, int absoluteY) {
        checkPlacingForAllAllowed(absoluteX, absoluteY);
        if (isTerrainOk && isItemsOk && isEnemiesOk) {
            Connection.getInstance().useInventoryItem(inventoryItemId, Index.add(normalizedPositionsToPlace, new Index(absoluteX, absoluteY)));
            return true;
        } else {
            return false;
        }
    }

    public int getItemFreeRadius() {
        return itemFreeRadius;
    }

    public boolean isPositionValid() {
        return isTerrainOk && isItemsOk && isEnemiesOk;
    }

    public Index getRelativeMiddlePos() {
        return relativeMiddlePos;
    }

    public String getErrorText() {
        return errorText;
    }

    public BaseItemType getBaseItemType() {
        return baseItemType;
    }

    public Collection<Index> getRelativePositionsToPlace() {
        return relativePositionsToPlace;
    }

    private void checkPlacingForAllAllowed(int absoluteX, int absoluteY) {
        isItemsOk = false;
        Index absolutePos = new Index(absoluteX, absoluteY);
        isEnemiesOk = !ItemContainer.getInstance().hasEnemyInRange(ClientBase.getInstance().getSimpleBase(), absolutePos, itemFreeRadius);
        if (isEnemiesOk) {
            isItemsOk = !ItemContainer.getInstance().hasItemsInRectangleFast(Rectangle.generateRectangleFromMiddlePoint(absolutePos, placeWidth, placeHeight));
        }
        if (isItemsOk) {
            for (Index normalizedPosition : normalizedPositionsToPlace) {
                if (!checkPlacingAllowed(normalizedPosition.add(absolutePos))) {
                    break;
                }
            }
        }
    }

    private void setupErrorText() {
        if (!isEnemiesOk) {
            errorText = "Enemy items are too near";
        } else if (!isItemsOk) {
            errorText = "Not allowed to place on other items";
        } else if (!isTerrainOk) {
            errorText = "You can not place here";
        } else {
            errorText = null;
        }
    }

    private void calculateNormalizedPlacePositions(int itemCount, int itemFreeRange) {
        normalizedPositionsToPlace = new ArrayList<Index>();
        double value = Math.sqrt(itemCount);
        int columns = (int) Math.ceil(value);
        ItemTypeSpriteMap itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        int offsetX = (columns - 1) * itemTypeSpriteMap.getImageWidth() / 2;
        int rows = (int) Math.round(value);
        int offsetY = (rows - 1) * itemTypeSpriteMap.getImageHeight() / 2;
        int count = 0;
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                int xPos = x * itemTypeSpriteMap.getImageWidth() - offsetX;
                int yPos = y * itemTypeSpriteMap.getImageHeight() - offsetY;
                count++;
                if (count <= itemCount) {
                    normalizedPositionsToPlace.add(new Index(xPos, yPos));
                }
            }
        }
        placeWidth = columns * itemTypeSpriteMap.getImageWidth();
        placeHeight = rows * itemTypeSpriteMap.getImageHeight();
        itemFreeRadius = itemFreeRange + (int) MathHelper.getPythagoras(placeWidth, placeHeight) / 2;
    }

    private boolean checkPlacingAllowed(Index absolutePos) {
        isTerrainOk = TerrainView.getInstance().getTerrainHandler().isFree(absolutePos, baseItemType);
        return isTerrainOk;
    }
}
