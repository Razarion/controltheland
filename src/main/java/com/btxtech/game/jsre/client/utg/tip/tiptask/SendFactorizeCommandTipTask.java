package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemCockpitGameOverlayTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:19
 */
public class SendFactorizeCommandTipTask extends AbstractTipTask implements SelectionListener, ActionHandler.CommandListener {
    private int itemTypeToFactorized;

    public SendFactorizeCommandTipTask(int itemTypeToFactorized) {
        this.itemTypeToFactorized = itemTypeToFactorized;
    }

    @Override
    public void internalStart() {
        ActionHandler.getInstance().setCommandListener(this);
        SelectionHandler.getInstance().addSelectionListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void internalCleanup() {
        SelectionHandler.getInstance().removeSelectionListener(this);
        ActionHandler.getInstance().setCommandListener(null);
    }

    @Override
    public GameTipVisualization createInGameTip() {
        return new ItemCockpitGameOverlayTipVisualization(itemTypeToFactorized);
    }

    @Override
    public String getTaskText() {
        // TODO use i18n
        // TODO dynamic create item name
        // TODO german flexion
        return "Klicke auf das " + getItemTypeName(itemTypeToFactorized) + " Menu. So kannst Du Einheiten Prodizieren";
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

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if (baseCommand instanceof FactoryCommand && ((FactoryCommand) baseCommand).getToBeBuilt() == itemTypeToFactorized) {
            onSucceed();
        }
    }
}
