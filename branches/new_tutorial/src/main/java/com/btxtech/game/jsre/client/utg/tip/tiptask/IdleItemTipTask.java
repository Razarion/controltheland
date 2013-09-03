package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.common.CommonJava;
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
    public void start() {
        ActionHandler.getInstance().setIdleListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return CommonJava.getFirst(ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), actorItemTypeId)).isIdle();
    }

    @Override
    public void cleanup() {
        ActionHandler.getInstance().setIdleListener(null);
    }

    @Override
    public void onIdle(SyncBaseItem syncBaseItem) {
        if (syncBaseItem.getBaseItemType().getId() == actorItemTypeId && ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
            onSucceed();
        }
    }

    public GameTipVisualization createInGameTip() throws NoSuchItemTypeException {
        return null;
    }
}
