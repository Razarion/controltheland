package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.TerrainInGameTipVisualization;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UnloadContainerCommand;

/**
 * User: beat
 * Date: 22.12.13
 * Time: 12:53
 */
public class SendUnloadContainerCommandTipTask extends AbstractTipTask implements SelectionListener, ActionHandler.CommandListener, CockpitMode.CockpitModeListener {
    private int actorItemTypeId;
    private Index directionTo;

    public SendUnloadContainerCommandTipTask(int actorItemTypeId, Index directionTo) {
        this.actorItemTypeId = actorItemTypeId;
        this.directionTo = directionTo;
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        SelectionHandler.getInstance().addSelectionListener(this);
        ActionHandler.getInstance().addCommandListener(this);
        CockpitMode.getInstance().addCockpitModeListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void internalCleanup() {
        ActionHandler.getInstance().removeCommandListener(this);
        SelectionHandler.getInstance().removeSelectionListener(this);
        CockpitMode.getInstance().removeCockpitModeListener(this);
    }

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if (baseCommand instanceof UnloadContainerCommand) {
            onSucceed();
        }
    }

    @Override
    public String getTaskText() {
        return ClientI18nHelper.CONSTANTS.trainingTipClickUnload();
    }

    public GameTipVisualization createInGameTip() throws NoSuchItemTypeException {
        SyncBaseItem actor = CommonJava.getFirst(ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), actorItemTypeId));
        int distance = (int) ((double)actor.getBaseItemType().getBoundingBox().getRadius() + actor.getSyncItemContainer().getRange() * 0.9);
        return new TerrainInGameTipVisualization(actor.getSyncItemArea().getPosition().getPointWithDistance(distance, directionTo, false));
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
    public void onCockpitModChanged(CockpitMode.Mode mode) {
        if (mode == CockpitMode.Mode.UNLOAD) {
            onFailed();
        }
    }
}
