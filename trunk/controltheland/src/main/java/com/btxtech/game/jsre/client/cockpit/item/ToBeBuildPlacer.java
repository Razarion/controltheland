package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.event.dom.client.MouseDownEvent;

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
    private boolean isItemsOk;
    private String errorText;
    private Logger log = Logger.getLogger(ToBeBuildPlacer.class.getName());

    public ToBeBuildPlacer(BaseItemType itemTypeToBuilt, Group builders, MouseDownEvent event) {
        this.itemTypeToBuilt = itemTypeToBuilt;
        this.builders = builders;
        relativeMiddlePos = new Index(event.getClientX(), event.getClientY());
        checkPlacingForAllAllowed(TerrainView.getInstance().toAbsoluteIndex(relativeMiddlePos));
        setupErrorText();
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
        return isTerrainOk && isItemsOk;
    }

    public Index getRelativeMiddlePos() {
        return relativeMiddlePos;
    }

    public BaseItemType getItemTypeToBuilt() {
        return itemTypeToBuilt;
    }

    private void checkPlacingForAllAllowed(Index absolute) {
        isTerrainOk = false;
        isItemsOk = false;

        isTerrainOk = ClientPlanetServices.getInstance().getTerrainService().isFree(absolute, itemTypeToBuilt);
        if (!isTerrainOk) {
            return;
        }
        Index relative = TerrainView.getInstance().toRelativeIndex(absolute);
        isTerrainOk = !ItemCockpit.getInstance().isInside(relative.getX(), relative.getY());
        if (!isTerrainOk) {
            return;
        }

        isItemsOk = !ItemContainer.getInstance().isUnmovableSyncItemOverlapping(itemTypeToBuilt.getBoundingBox(), absolute);
    }

    private void setupErrorText() {
        if (!isTerrainOk) {
            errorText = "You can not place here";
        } else if (!isItemsOk) {
            errorText = "Not allowed to place on other items";
        } else {
            errorText = null;
        }
    }

}
