package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.QuestVisualisationCockpitInGameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;

/**
 * User: beat
 * Date: 09.11.12
 * Time: 12:53
 */
public class WatchQuestVisualisationCockpitTipTask extends AbstractTipTask implements ActionHandler.CommandListener {
    private int toBeBuildId;

    public WatchQuestVisualisationCockpitTipTask(int toBeBuildId) {
        this.toBeBuildId = toBeBuildId;
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        ActionHandler.getInstance().setCommandListener(this);
    }

    @Override
    public void internalCleanup() {
        ActionHandler.getInstance().setCommandListener(null);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }

    @Override
    public String getTaskText() {
        return ClientI18nHelper.CONSTANTS.trainingTipWatchQuestDialog();
    }

    public GameTipVisualization createInGameTip() {
        return new QuestVisualisationCockpitInGameTipVisualization();
    }

    @Override
    public void onCommand(BaseCommand baseCommand) {
        if (baseCommand instanceof FactoryCommand && ((FactoryCommand) baseCommand).getToBeBuilt() == toBeBuildId) {
            onSucceed();
        } else if (baseCommand instanceof BuilderCommand && ((BuilderCommand) baseCommand).getToBeBuilt() == toBeBuildId) {
            onSucceed();
        }
    }
}
