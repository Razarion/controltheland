package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 30.07.12
 * Time: 23:36
 */
public class ToBeBuildPlacer {
    private Index relativeMiddlePos;
    private BaseItemType itemTypeToBuilt;
    private Group builders;
    private boolean isTerrainOk;
    private boolean isTerritoryOk;
    private boolean isTerritoryBuilderOk;
    private boolean isItemsOk;
    private String errorText;
    private Logger log = Logger.getLogger(ToBeBuildPlacer.class.getName());

    public ToBeBuildPlacer(BaseItemType itemTypeToBuilt, Group builders) {
        this.itemTypeToBuilt = itemTypeToBuilt;
        this.builders = builders;
        TerrainView.getInstance().setFocus();
        Index absolute = builders.getFirst().getSyncItemArea().getPosition();
        relativeMiddlePos = TerrainView.getInstance().toRelativeIndex(absolute);
        checkPlacingForAllAllowed(absolute);
    }

    public void onMove(int relativeX, int relativeY, int absoluteX, int absoluteY) {
        relativeMiddlePos = new Index(relativeX, relativeY);
        checkPlacingForAllAllowed(new Index(absoluteX, absoluteY));
        setupErrorText();
    }

    public boolean execute(int absoluteX, int absoluteY) {
        Index absolute = new Index(absoluteX, absoluteY);
        checkPlacingForAllAllowed(absolute);
        if (isValidPosition()) {
            try {
                ActionHandler.getInstance().build(builders.getItems(), absolute, itemTypeToBuilt);
            } catch (NoSuchItemTypeException e) {
                log.log(Level.SEVERE, "ToBeBuildPlacer.execute()", e);
            }
            return true;
        } else {
            return false;
        }
    }

    public String getErrorText() {
        return errorText;
    }

    public boolean isValidPosition() {
        return isTerrainOk & isTerritoryOk & isTerritoryBuilderOk & isItemsOk;
    }

    public Index getRelativeMiddlePos() {
        return relativeMiddlePos;
    }

    public BaseItemType getItemTypeToBuilt() {
        return itemTypeToBuilt;
    }

    private void checkPlacingForAllAllowed(Index absolute) {
        isTerrainOk = false;
        isTerritoryOk = false;
        isTerritoryBuilderOk = false;
        isItemsOk = false;

        isTerrainOk = ClientServices.getInstance().getTerrainService().isFree(absolute, itemTypeToBuilt);
        if (!isTerrainOk) {
            return;
        }
        isTerritoryOk = ClientTerritoryService.getInstance().isAllowed(absolute, itemTypeToBuilt);
        if (!isTerritoryOk) {
            return;
        }
        isTerritoryBuilderOk = ClientTerritoryService.getInstance().isAtLeastOneAllowed(absolute, builders.getSyncBaseItems());
        if (!isTerritoryBuilderOk) {
            return;
        }
        isItemsOk = !ItemContainer.getInstance().isUnmovableSyncItemOverlapping(itemTypeToBuilt.getBoundingBox(), absolute);
    }

    private void setupErrorText() {
        if (!isTerrainOk) {
            errorText = "You can not place here";
        } else if (!isTerritoryOk) {
            errorText = "Item not allowed on territory";
        } else if (!isTerritoryBuilderOk) {
            errorText = "Builders not allowed on territory";
        } else if (!isItemsOk) {
            errorText = "Not allowed to place on other items";
        } else {
            errorText = null;
        }
    }

}
