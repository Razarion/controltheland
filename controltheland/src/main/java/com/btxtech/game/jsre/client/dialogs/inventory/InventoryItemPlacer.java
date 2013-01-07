package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
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
    private BaseItemType baseItemType;
    private Logger log = Logger.getLogger(InventoryItemPlacer.class.getName());
    private String errorText;
    private int inventoryItemId;
    private InventoryItemPlacerChecker inventoryItemPlacerChecker;

    public InventoryItemPlacer(InventoryItemInfo inventoryItemInfo) {
        TerrainView.getInstance().setFocus();
        inventoryItemId = inventoryItemInfo.getInventoryItemId();
        try {
            baseItemType = (BaseItemType) ItemTypeContainer.getInstance().getItemType(inventoryItemInfo.getBaseItemTypeId());
        } catch (NoSuchItemTypeException e) {
            log.log(Level.SEVERE, "InventoryItemPlacer() ", e);
        }
        inventoryItemPlacerChecker = new InventoryItemPlacerChecker(baseItemType,
                inventoryItemInfo.getItemCount(),
                inventoryItemInfo.getItemFreeRange(),
                ClientBase.getInstance().getSimpleBase(),
                ClientPlanetServices.getInstance());
        calculateNormalizedPlacePositions(inventoryItemInfo.getItemCount());
        relativePositionsToPlace = Index.add(normalizedPositionsToPlace, relativeMiddlePos);
    }

    public void onMove(int relativeX, int relativeY, int absoluteX, int absoluteY) {
        relativeMiddlePos = new Index(relativeX, relativeY);
        relativePositionsToPlace = Index.add(normalizedPositionsToPlace, relativeMiddlePos);
        Index absoluteMiddlePosition = new Index(absoluteX, absoluteY);
        inventoryItemPlacerChecker.check(absoluteMiddlePosition, Index.add(normalizedPositionsToPlace, absoluteMiddlePosition));
        setupErrorText();
    }

    public boolean execute(int absoluteX, int absoluteY) {
        Index absoluteMiddlePosition = new Index(absoluteX, absoluteY);
        inventoryItemPlacerChecker.check(absoluteMiddlePosition, Index.add(normalizedPositionsToPlace, absoluteMiddlePosition));
        if (inventoryItemPlacerChecker.isPositionValid()) {
            Connection.getInstance().useInventoryItem(inventoryItemId, Index.add(normalizedPositionsToPlace, new Index(absoluteX, absoluteY)));
            return true;
        } else {
            return false;
        }
    }

    public int getItemFreeRadius() {
        return inventoryItemPlacerChecker.getItemFreeRadius();
    }

    public boolean isPositionValid() {
        return inventoryItemPlacerChecker.isPositionValid();
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

    private void setupErrorText() {
        if (!inventoryItemPlacerChecker.isEnemiesOk()) {
            errorText = ClientI18nHelper.CONSTANTS.enemyTooNear();
        } else if (!inventoryItemPlacerChecker.isItemsOk()) {
            errorText = ClientI18nHelper.CONSTANTS.notPlaceOver();
        } else if (!inventoryItemPlacerChecker.isTerrainOk()) {
            errorText = ClientI18nHelper.CONSTANTS.notPlaceHere();
        } else {
            errorText = null;
        }
    }

    private void calculateNormalizedPlacePositions(int itemCount) {
        normalizedPositionsToPlace = new ArrayList<Index>();
        int columns = inventoryItemPlacerChecker.getColumns();
        int rows = inventoryItemPlacerChecker.getRows();
        int offsetX = (columns - 1) * inventoryItemPlacerChecker.getSingleItemWidth() / 2;
        int offsetY = (rows - 1) * inventoryItemPlacerChecker.getSingleItemHeight() / 2;
        int count = 0;
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                int xPos = x * inventoryItemPlacerChecker.getSingleItemWidth() - offsetX;
                int yPos = y * inventoryItemPlacerChecker.getSingleItemHeight() - offsetY;
                count++;
                if (count <= itemCount) {
                    normalizedPositionsToPlace.add(new Index(xPos, yPos));
                }
            }
        }
    }
}
