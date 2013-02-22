package com.btxtech.game.jsre.client.action;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.TargetHasNoPositionException;

/**
 * User: beat
 * Date: 24.02.2012
 * Time: 12:37:35
 */
public abstract class GroupCommandHelperPosition {
    public void process(SyncBaseItem syncBaseItem, BaseItemType targetBaseItemType, Index targetPosition, boolean findPathIfNotInRange) {
        try {
            if (isCommandPossible(syncBaseItem, targetBaseItemType, targetPosition)) {
                if (isAllowedAllowedWithoutMoving(syncBaseItem, targetBaseItemType, targetPosition)) {
                    executeCommand(syncBaseItem, targetBaseItemType, targetPosition, syncBaseItem.getSyncItemArea().getPosition(), syncBaseItem.getSyncItemArea().getTurnToAngel(targetPosition));
                } else if (findPathIfNotInRange && syncBaseItem.hasSyncMovable()) {
                    AttackFormationItem format = ClientCollisionService.getInstance().getDestinationHint(syncBaseItem,
                            getRange(syncBaseItem),
                            targetBaseItemType.getBoundingBox().createSyntheticSyncItemArea(targetPosition),
                            targetBaseItemType.getTerrainType());
                    if (format.isInRange()) {
                        executeCommand(syncBaseItem, targetBaseItemType, targetPosition, format.getDestinationHint(), format.getDestinationAngel());
                    } else {
                        ActionHandler.getInstance().move(syncBaseItem, format.getDestinationHint());
                    }
                }
                Connection.getInstance().sendCommandQueue();
            }

        } catch (Exception e) {
            ClientExceptionHandler.handleException("GroupCommandHelperPosition.process()", e);
        }
    }

    private boolean isAllowedAllowedWithoutMoving(SyncBaseItem syncBaseItem, BaseItemType targetBaseItemType, Index targetPosition) throws TargetHasNoPositionException {
        return syncBaseItem.getSyncItemArea().isInRange(getRange(syncBaseItem), targetBaseItemType.getBoundingBox().createSyntheticSyncItemArea(targetPosition));
    }

    protected abstract boolean isCommandPossible(SyncBaseItem syncBaseItem, BaseItemType targetBaseItemType, Index targetPosition);

    protected abstract void executeCommand(SyncBaseItem syncBaseItem, BaseItemType targetType, Index targetPosition, Index destinationHint, double destinationAngel);

    protected abstract int getRange(SyncBaseItem syncBaseItem);
}
