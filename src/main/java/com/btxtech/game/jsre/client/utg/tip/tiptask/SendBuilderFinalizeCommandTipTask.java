package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemInGameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderFinalizeCommand;

import java.util.Collection;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class SendBuilderFinalizeCommandTipTask extends AbstractTipTask implements ActionHandler.CommandListener, SelectionListener {
    private final int toBeFinalizedId;
    private SyncBaseItem toBeFinalized;

    public SendBuilderFinalizeCommandTipTask(int toBeFinalizedId) {
        this.toBeFinalizedId = toBeFinalizedId;
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        ActionHandler.getInstance().setCommandListener(this);
        Collection<SyncBaseItem> existingItems = ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), toBeFinalizedId);
        for (SyncBaseItem existingItem : existingItems) {
            if (!existingItem.isReady()) {
                toBeFinalized = existingItem;
                break;
            }
        }
        SelectionHandler.getInstance().addSelectionListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void internalCleanup() {
        SelectionHandler.getInstance().removeSelectionListener(this);
    }

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if (baseCommand instanceof BuilderFinalizeCommand && ((BuilderFinalizeCommand) baseCommand).getToBeBuilt().equals(toBeFinalized.getId())) {
            onSucceed();
        }
    }

    @Override
    public String getTaskText() {
        // TODO use i18n
        // TODO dynamic create item name
        // TODO german flexion
        return "Baue die " + getItemTypeName(toBeFinalizedId) + " fertig";
    }

    public GameTipVisualization createInGameTip() {
        return new ItemInGameTipVisualization(toBeFinalized);
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
