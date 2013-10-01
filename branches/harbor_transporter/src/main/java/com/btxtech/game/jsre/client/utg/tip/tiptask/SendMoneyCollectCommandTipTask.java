package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemInGameTipVisualization;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class SendMoneyCollectCommandTipTask extends AbstractTipTask implements SelectionListener, ActionHandler.CommandListener {
    private int toCollectFormId;

    public SendMoneyCollectCommandTipTask(int toCollectFormId) {
        this.toCollectFormId = toCollectFormId;
    }

    @Override
    public void start() {
        SelectionHandler.getInstance().addSelectionListener(this);
        ActionHandler.getInstance().setCommandListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void cleanup() {
        ActionHandler.getInstance().setCommandListener(null);
        SelectionHandler.getInstance().removeSelectionListener(this);
    }

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if (baseCommand instanceof MoneyCollectCommand) {
            onSucceed();
        }
    }

    public GameTipVisualization createInGameTip() throws NoSuchItemTypeException {
        ItemType resource = ItemTypeContainer.getInstance().getItemType(toCollectFormId);
        SyncItem syncItem = CommonJava.getFirst(ItemContainer.getInstance().getItems(resource, null));
        return new ItemInGameTipVisualization(syncItem);
    }

    @Override
    public void onTargetSelectionChanged(SyncItem selection) {
        // Ignore
    }

    @Override
    public void onSelectionCleared() {
        onFailed();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        // Ignore
    }
}
