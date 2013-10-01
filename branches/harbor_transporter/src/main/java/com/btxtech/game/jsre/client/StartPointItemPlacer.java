package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.CursorHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.StartPointInfo;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.user.client.Window;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 02.05.2013
 * Time: 18:02
 */
public class StartPointItemPlacer {
    private Index relativeMiddlePos;
    private BaseItemType baseItemType;
    private String errorText;
    private StartPointItemPlacerChecker startPointItemPlacerChecker;

    public StartPointItemPlacer(StartPointInfo startPointInfo) {
        CursorHandler.getInstance().noCursor();
        if (startPointInfo.getSuggestedPosition() != null) {
            relativeMiddlePos = TerrainView.getInstance().toRelativeIndex(startPointInfo.getSuggestedPosition());
        } else {
            relativeMiddlePos = new Index(Window.getClientWidth() / 2, Window.getClientHeight() / 2);
        }
        TerrainView.getInstance().setFocus();
        try {
            baseItemType = (BaseItemType) ItemTypeContainer.getInstance().getItemType(startPointInfo.getBaseItemTypeId());
        } catch (NoSuchItemTypeException e) {
            Logger.getLogger(StartPointItemPlacer.class.getName()).log(Level.SEVERE, "StartPointItemPlacer() ", e);
        }
        startPointItemPlacerChecker = new StartPointItemPlacerChecker(baseItemType,
                startPointInfo.getItemFreeRange(),
                ClientPlanetServices.getInstance()) {

            @Override
            protected boolean hasEnemyInRange(Index absoluteMiddlePosition, int itemFreeRadius) {
                return ClientPlanetServices.getInstance().getItemService().hasEnemyInRange(ClientBase.getInstance().getSimpleBase(), absoluteMiddlePosition, itemFreeRadius);
            }
        };
        Index absolute = TerrainView.getInstance().toAbsoluteIndex(relativeMiddlePos);
        onMove(relativeMiddlePos.getX(), relativeMiddlePos.getY(), absolute.getX(), absolute.getY());
    }

    public void onMove(int relativeX, int relativeY, int absoluteX, int absoluteY) {
        relativeMiddlePos = new Index(relativeX, relativeY);
        Index absoluteMiddlePosition = new Index(absoluteX, absoluteY);
        startPointItemPlacerChecker.check(absoluteMiddlePosition);
        setupErrorText();
    }

    public int getItemFreeRadius() {
        return startPointItemPlacerChecker.getItemFreeRadius();
    }

    public boolean isPositionValid() {
        return startPointItemPlacerChecker.isPositionValid();
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

    private void setupErrorText() {
        if (!startPointItemPlacerChecker.isEnemiesOk()) {
            errorText = ClientI18nHelper.CONSTANTS.enemyTooNear();
        } else if (!startPointItemPlacerChecker.isItemsOk()) {
            errorText = ClientI18nHelper.CONSTANTS.notPlaceOver();
        } else if (!startPointItemPlacerChecker.isTerrainOk()) {
            errorText = ClientI18nHelper.CONSTANTS.notPlaceHere();
        } else {
            errorText = null;
        }
    }
}
