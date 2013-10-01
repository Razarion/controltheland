package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
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
    private final Index positionHint;
    private SyncBaseItem toBeFinalized;

    public SendBuildCommandTipTask(int toBeBuildId, Index positionHint) {
        this.toBeBuildId = toBeBuildId;
        this.positionHint = positionHint;
    }

    @Override
    public void start() {
        ActionHandler.getInstance().setCommandListener(this);
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
    public void cleanup() {
        ActionHandler.getInstance().setCommandListener(null);
        if (toBeFinalized != null) {
            SelectionHandler.getInstance().removeSelectionListener(this);
        } else {
            CockpitMode.getInstance().setToBeBuildPlacerListener(null);
        }
    }

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if(toBeFinalized != null) {
            if (baseCommand instanceof BuilderFinalizeCommand && ((BuilderFinalizeCommand) baseCommand).getToBeBuilt().equals(toBeFinalized.getId())) {
                onSucceed();
            }
        }   else {
            if (baseCommand instanceof BuilderCommand && ((BuilderCommand) baseCommand).getToBeBuilt() == toBeBuildId) {
                onSucceed();
            }
        }
    }

    public GameTipVisualization createInGameTip() {
        if(toBeFinalized != null) {
            return new ItemInGameTipVisualization(toBeFinalized);
        }  else {
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
