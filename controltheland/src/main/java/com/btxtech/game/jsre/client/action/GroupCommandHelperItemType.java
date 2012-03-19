package com.btxtech.game.jsre.client.action;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

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

    public void process(Collection<ClientSyncItem> clientSyncItems, T target, boolean findPathIfNotInRange) {
        try {
            List<AttackFormationItem> attackFormationItemList = new ArrayList<AttackFormationItem>();
            for (ClientSyncItem clientSyncItem : clientSyncItems) {
                if (isCommandPossible(clientSyncItem.getSyncBaseItem(), target)) {
                    if (isAllowedAllowedWithoutMoving(clientSyncItem.getSyncBaseItem(), target)) {
                        executeCommand(clientSyncItem.getSyncBaseItem(), target, clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition(), clientSyncItem.getSyncBaseItem().getSyncItemArea().getTurnToAngel(target.getSyncItemArea()));
                    } else if (clientSyncItem.getSyncBaseItem().hasSyncMovable() && findPathIfNotInRange) {
                        attackFormationItemList.add(new AttackFormationItem(clientSyncItem.getSyncBaseItem(), getRange(clientSyncItem.getSyncBaseItem(), target)));
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
        } catch (PathCanNotBeFoundException e) {
            log.warning("GroupCommandHelperItemType.process(): " + e.getMessage());
        }
    }

    private boolean isAllowedAllowedWithoutMoving(SyncBaseItem syncBaseItem, T target) {
        return syncBaseItem.getSyncItemArea().isInRange(getRange(syncBaseItem, target), target);
    }

    protected abstract boolean isCommandPossible(SyncBaseItem syncBaseItem, T target);

    protected abstract void executeCommand(SyncBaseItem syncBaseItem, T target, Index destinationHint, double destinationAngel);

    protected abstract int getRange(SyncBaseItem syncBaseItem, T target);
}
