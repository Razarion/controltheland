package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.item.ToBeBuildPlacer;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.TerrainInGameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class SendBuildCommandTipTask extends AbstractTipTask implements ActionHandler.CommandListener, CockpitMode.ToBeBuildPlacerListener {
    private final int toBeBuildId;
    private final Index positionHint;

    public SendBuildCommandTipTask(int toBeBuildId, Index positionHint) {
        this.toBeBuildId = toBeBuildId;
        this.positionHint = positionHint;
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        ActionHandler.getInstance().setCommandListener(this);
        CockpitMode.getInstance().setToBeBuildPlacerListener(this);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public void internalCleanup() {
        ActionHandler.getInstance().setCommandListener(null);
        CockpitMode.getInstance().setToBeBuildPlacerListener(null);
    }

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if (baseCommand instanceof BuilderCommand && ((BuilderCommand) baseCommand).getToBeBuilt() == toBeBuildId) {
            onSucceed();
        }
    }

    @Override
    public String getTaskText() {
        // TODO use i18n
        // TODO dynamic create item name
        // TODO German flexion
        return "Platziere die " + getItemTypeName(toBeBuildId) + " auf dem vorgschalgenen Platz im Terrain";
    }

    public GameTipVisualization createInGameTip() {
        return new TerrainInGameTipVisualization(positionHint);
    }

    @Override
    public void onToBeBuildPlacerSet(ToBeBuildPlacer toBeBuildPlacer) {
        if (toBeBuildPlacer == null) {
            onFailed();
        }
    }
}
