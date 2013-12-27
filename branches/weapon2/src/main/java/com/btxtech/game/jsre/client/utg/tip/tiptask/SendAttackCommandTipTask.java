package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemInGameTipVisualization;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class SendAttackCommandTipTask extends AbstractTipTask implements SelectionListener, ActionHandler.CommandListener {
    private int actorItemTypeId;

    public SendAttackCommandTipTask(int actorItemTypeId) {
        this.actorItemTypeId = actorItemTypeId;
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        SelectionHandler.getInstance().addSelectionListener(this);
        ActionHandler.getInstance().addCommandListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void internalCleanup() {
        ActionHandler.getInstance().removeCommandListener(this);
        SelectionHandler.getInstance().removeSelectionListener(this);
    }

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if (baseCommand instanceof AttackCommand) {
            onSucceed();
        }
    }

    @Override
    public String getTaskText() {
        return ClientI18nHelper.CONSTANTS.trainingTipClickItem();
    }

    public GameTipVisualization createInGameTip() throws NoSuchItemTypeException {
        SyncBaseItem attacker = CommonJava.getFirst(ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), actorItemTypeId));
        SyncItem target = ItemContainer.getInstance().getNearestEnemyItem(attacker.getSyncItemArea().getPosition(), null, ClientBase.getInstance().getSimpleBase());
        return new ItemInGameTipVisualization(target);
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
