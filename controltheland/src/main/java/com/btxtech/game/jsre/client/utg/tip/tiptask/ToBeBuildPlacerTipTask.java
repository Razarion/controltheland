package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.cockpit.item.ToBeBuildPlacer;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ToBeBuiltItemCockpitGameOverlayTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.Collection;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:19
 */
public class ToBeBuildPlacerTipTask extends AbstractTipTask implements CockpitMode.ToBeBuildPlacerListener, SelectionListener {
    private int itemTypeToBePlaced;

    public ToBeBuildPlacerTipTask(int itemTypeToBePlaced) {
        this.itemTypeToBePlaced = itemTypeToBePlaced;
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        CockpitMode.getInstance().setToBeBuildPlacerListener(this);
        SelectionHandler.getInstance().addSelectionListener(this);
    }

    @Override
    public boolean isFulfilled() {
        Collection<SyncBaseItem> existingItems = ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), itemTypeToBePlaced);
        for (SyncBaseItem existingItem : existingItems) {
            if (!existingItem.isReady() && SelectionHandler.getInstance().atLeastOneItemTypeAllowed2FinalizeBuild(existingItem)) {
                return true;
            }
        }
        ToBeBuildPlacer toBeBuildPlacer = CockpitMode.getInstance().getToBeBuildPlacer();
        return toBeBuildPlacer != null && toBeBuildPlacer.getItemTypeToBuilt().getId() == itemTypeToBePlaced;
    }

    @Override
    public void internalCleanup() {
        CockpitMode.getInstance().setToBeBuildPlacerListener(null);
        SelectionHandler.getInstance().removeSelectionListener(this);
    }

    @Override
    public GameTipVisualization createInGameTip() {
        return new ToBeBuiltItemCockpitGameOverlayTipVisualization(itemTypeToBePlaced);
    }

    @Override
    public void onToBeBuildPlacerSet(ToBeBuildPlacer toBeBuildPlacer) {
        if (toBeBuildPlacer != null && toBeBuildPlacer.getItemTypeToBuilt().getId() == itemTypeToBePlaced) {
            onSucceed();
        }
    }

    @Override
    public String getTaskText() {
        return ClientI18nHelper.CONSTANTS.trainingTipToBeBuiltPlacer(getItemTypeName(itemTypeToBePlaced));
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
