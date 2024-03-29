package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class IdleItemTipTask extends AbstractTipTask implements ActionHandler.IdleListener {
    private int actorItemTypeId;

    public IdleItemTipTask(int actorItemTypeId) {
        this.actorItemTypeId = actorItemTypeId;
    }

    @Override
    public void internalStart() {
        ActionHandler.getInstance().setIdleListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return areAllItemsTypeIdle();
    }

    @Override
    public void internalCleanup() {
        ActionHandler.getInstance().setIdleListener(null);
    }

    @Override
    public String getTaskText() {
        return null;
    }

    @Override
    public void onIdle(SyncBaseItem syncBaseItem) {
        if (syncBaseItem.getBaseItemType().getId() == actorItemTypeId && ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
            if (areAllItemsTypeIdle()) {
                onSucceed();
            }
        }
    }

    public GameTipVisualization createInGameTip() throws NoSuchItemTypeException {
        return null;
    }

    private boolean areAllItemsTypeIdle() {
        for (SyncBaseItem syncBaseItem : ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), actorItemTypeId)) {
            if (!syncBaseItem.isIdle()) {
                return false;
            }
        }
        return true;
    }
}
