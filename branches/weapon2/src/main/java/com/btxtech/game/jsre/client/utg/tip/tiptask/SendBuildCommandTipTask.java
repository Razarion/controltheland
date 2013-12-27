package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.cockpit.item.ToBeBuildPlacer;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemInGameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.TerrainInGameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderFinalizeCommand;

import java.util.Collection;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class SendBuildCommandTipTask extends AbstractTipTask implements ActionHandler.CommandListener, CockpitMode.ToBeBuildPlacerListener, SelectionListener {
    private final int toBeBuildId;
    private SyncBaseItem toBeFinalized;
    private final Index positionHint;

    public SendBuildCommandTipTask(int toBeBuildId, Index positionHint) {
        this.toBeBuildId = toBeBuildId;
        this.positionHint = positionHint;
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        ActionHandler.getInstance().addCommandListener(this);
        Collection<SyncBaseItem> existingItems = ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), toBeBuildId);
        for (SyncBaseItem existingItem : existingItems) {
            if (!existingItem.isReady()) {
                toBeFinalized = existingItem;
                break;
            }
        }
        if (toBeFinalized != null) {
            SelectionHandler.getInstance().addSelectionListener(this);
        } else {
            CockpitMode.getInstance().setToBeBuildPlacerListener(this);
        }
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void internalCleanup() {
        if (toBeFinalized != null) {
            SelectionHandler.getInstance().removeSelectionListener(this);
        } else {
            CockpitMode.getInstance().setToBeBuildPlacerListener(null);
        }
        ActionHandler.getInstance().removeCommandListener(this);
    }

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if (toBeFinalized != null) {
            if (baseCommand instanceof BuilderFinalizeCommand && ((BuilderFinalizeCommand) baseCommand).getToBeBuilt().equals(toBeFinalized.getId())) {
                onSucceed();
            }
        } else {
            if (baseCommand instanceof BuilderCommand && ((BuilderCommand) baseCommand).getToBeBuilt() == toBeBuildId) {
                onSucceed();
            }
        }
    }

    @Override
    public String getTaskText() {
        if (toBeFinalized != null) {
            return ClientI18nHelper.CONSTANTS.trainingTipSendBuildFinalizeCommand(getItemTypeName(toBeBuildId));
        } else {
            return ClientI18nHelper.CONSTANTS.trainingTipSendBuildCommand(getItemTypeName(toBeBuildId));
        }
    }

    public GameTipVisualization createInGameTip() {
        if (toBeFinalized != null) {
            return new ItemInGameTipVisualization(toBeFinalized);
        } else {
            return new TerrainInGameTipVisualization(positionHint);
        }
    }

    @Override
    public void onToBeBuildPlacerSet(ToBeBuildPlacer toBeBuildPlacer) {
        if (toBeBuildPlacer == null) {
            onFailed();
        }
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
