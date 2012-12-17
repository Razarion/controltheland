package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualtsationModel;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemInGameTipVisualization;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class SelectTipTask extends AbstractTipTask implements SelectionListener {
    private int itemTypeId;

    public SelectTipTask(int itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    @Override
    public void start() {
        SelectionHandler.getInstance().addSelectionListener(this);
        QuestVisualtsationModel.getInstance().setShowInGameVisualisation(false);
    }

    @Override
    public boolean isFulfilled() {
        Group ownSelection = SelectionHandler.getInstance().getOwnSelection();
        if (ownSelection == null) {
            return false;
        } else {
            for (SyncBaseItem syncBaseItem : ownSelection.getItems()) {
                if (syncBaseItem.getBaseItemType().getId() == itemTypeId) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void cleanup() {
        SelectionHandler.getInstance().removeSelectionListener(this);
        QuestVisualtsationModel.getInstance().setShowInGameVisualisation(true);
    }

    @Override
    public void onTargetSelectionChanged(SyncItem selection) {
        onFailed();
    }

    @Override
    public void onSelectionCleared() {
        onFailed();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        for (SyncBaseItem syncBaseItem : selectedGroup.getItems()) {
            if (syncBaseItem.getBaseItemType().getId() == itemTypeId) {
                onSucceed();
                return;
            }
        }
        onFailed();
    }

    public GameTipVisualization createInGameTip() {
        SyncBaseItem syncBaseItem = CommonJava.getFirst(ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), itemTypeId));
        return new ItemInGameTipVisualization(syncBaseItem);
    }
}
