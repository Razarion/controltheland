package com.btxtech.game.jsre.client.action;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 24.02.2012
 * Time: 12:37:35
 */
public abstract class GroupCommandHelperPosition {
    private Logger log = Logger.getLogger(GroupCommandHelperPosition.class.getName());

    public void process(ClientSyncItem clientSyncItem, BaseItemType targetBaseItemType, Index targetPosition, boolean findPathIfNotInRange) {
        try {
            if (isCommandPossible(clientSyncItem.getSyncBaseItem(), targetBaseItemType, targetPosition)) {
                if (isAllowedAllowedWithoutMoving(clientSyncItem.getSyncBaseItem(), targetBaseItemType, targetPosition)) {
                    executeCommand(clientSyncItem.getSyncBaseItem(), targetBaseItemType, targetPosition, clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(), clientSyncItem.getSyncBaseItem().getSyncItemArea().getTurnToAngel(targetPosition));
                } else if (findPathIfNotInRange && clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
                    AttackFormationItem format = ClientCollisionService.getInstance().getDestinationHint(clientSyncItem.getSyncBaseItem(),
                            getRange(clientSyncItem.getSyncBaseItem()),
                            targetBaseItemType.getBoundingBox().createSyntheticSyncItemArea(targetPosition),
                            targetBaseItemType.getTerrainType());
                    if (format.isInRange()) {
                        executeCommand(clientSyncItem.getSyncBaseItem(), targetBaseItemType, targetPosition, format.getDestinationHint(), format.getDestinationAngel());
                    } else {
                        ActionHandler.getInstance().move(clientSyncItem.getSyncBaseItem(), format.getDestinationHint());
                    }
                }
                Connection.getInstance().sendCommandQueue();
            }

        } catch (PathCanNotBeFoundException e) {
            log.warning("GroupCommandHelperItemType.process(): " + e.getMessage());
        }
    }

    private boolean isAllowedAllowedWithoutMoving(SyncBaseItem syncBaseItem, BaseItemType targetBaseItemType, Index targetPosition) {
        return syncBaseItem.getSyncItemArea().isInRange(getRange(syncBaseItem), targetBaseItemType.getBoundingBox().createSyntheticSyncItemArea(targetPosition));
    }

    protected abstract boolean isCommandPossible(SyncBaseItem syncBaseItem, BaseItemType targetBaseItemType, Index targetPosition);

    protected abstract void executeCommand(SyncBaseItem syncBaseItem, BaseItemType targetType, Index targetPosition, Index destinationHint, double destinationAngel);

    protected abstract int getRange(SyncBaseItem syncBaseItem);
}
