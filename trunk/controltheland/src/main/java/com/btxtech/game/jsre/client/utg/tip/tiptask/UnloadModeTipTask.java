package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.UnloadModeCockpitGameOverlayTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 22.12.13
 * Time: 13:19
 */
public class UnloadModeTipTask extends AbstractTipTask implements CockpitMode.CockpitModeListener, SelectionListener {

    public UnloadModeTipTask() {
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        SelectionHandler.getInstance().addSelectionListener(this);
        CockpitMode.getInstance().addCockpitModeListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void internalCleanup() {
        CockpitMode.getInstance().removeCockpitModeListener(this);
        SelectionHandler.getInstance().removeSelectionListener(this);
    }

    @Override
    public GameTipVisualization createInGameTip() {
        return new UnloadModeCockpitGameOverlayTipVisualization();
    }

    @Override
    public String getTaskText() {
        return ClientI18nHelper.CONSTANTS.trainingTipClickUnloadMode();
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
            onSucceed();
        }
    }
}
