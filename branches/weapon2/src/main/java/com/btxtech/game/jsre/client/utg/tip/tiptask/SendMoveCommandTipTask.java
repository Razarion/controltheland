package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.TerrainInGameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class SendMoveCommandTipTask extends AbstractTipTask implements SelectionListener, ActionHandler.CommandListener {
    private Index position;

    public SendMoveCommandTipTask(Index position) {
        this.position = position;
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
        if (baseCommand instanceof MoveCommand) {
            onSucceed();
        }
    }

    @Override
    public String getTaskText() {
        return ClientI18nHelper.CONSTANTS.trainingTipClickMove();
    }

    public GameTipVisualization createInGameTip() throws NoSuchItemTypeException {
        return new TerrainInGameTipVisualization(position);
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
