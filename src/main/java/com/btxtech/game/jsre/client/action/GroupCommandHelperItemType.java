package com.btxtech.game.jsre.client.action;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.TargetHasNoPositionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 24.02.2012
 * Time: 12:37:35
 */
public abstract class GroupCommandHelperItemType<T extends SyncItem> {
    private Logger log = Logger.getLogger(GroupCommandHelperItemType.class.getName());

    public void process(Collection<SyncBaseItem> syncBaseItems, T target, boolean findPathIfNotInRange) {
        try {
            List<AttackFormationItem> attackFormationItemList = new ArrayList<AttackFormationItem>();
            for (SyncBaseItem syncBaseItem : syncBaseItems) {
                if (isCommandPossible(syncBaseItem, target)) {
                    if (isAllowedAllowedWithoutMoving(syncBaseItem, target)) {
                        executeCommand(syncBaseItem, target, syncBaseItem.getSyncItemArea().getPosition(), syncBaseItem.getSyncItemArea().getTurnToAngel(target.getSyncItemArea()));
                    } else if (syncBaseItem.hasSyncMovable() && findPathIfNotInRange) {
                        attackFormationItemList.add(new AttackFormationItem(syncBaseItem, getRange(syncBaseItem, target)));
                    }
                }
            }
            attackFormationItemList = ClientCollisionService.getInstance().setupDestinationHints(target, attackFormationItemList);
            for (AttackFormationItem item : attackFormationItemList) {
                if (item.isInRange()) {
                    executeCommand(item.getSyncBaseItem(), target, item.getDestinationHint(), item.getDestinationAngel());
                } else {
                    ActionHandler.getInstance().move(item.getSyncBaseItem(), item.getDestinationHint());
                }

            }

            Connection.getInstance().sendCommandQueue();
        } catch (Exception e) {
            ClientExceptionHandler.handleException("GroupCommandHelperItemType.process()", e);
        }
    }

    private boolean isAllowedAllowedWithoutMoving(SyncBaseItem syncBaseItem, T target) throws TargetHasNoPositionException {
        return syncBaseItem.getSyncItemArea().isInRange(getRange(syncBaseItem, target), target);
    }

    protected abstract boolean isCommandPossible(SyncBaseItem syncBaseItem, T target);

    protected abstract void executeCommand(SyncBaseItem syncBaseItem, T target, Index destinationHint, double destinationAngel);

    protected abstract int getRange(SyncBaseItem syncBaseItem, T target);
}
