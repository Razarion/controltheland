package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.tutorial.ConditionTaskConfig;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 11.09.13
 * Time: 15:54
 */
public class ConditionTask extends AbstractTask implements ConditionServiceListener<SimpleBase, Void> {
    private ConditionConfig conditionConfig;

    public ConditionTask(ConditionTaskConfig conditionTaskConfig) {
        super(conditionTaskConfig);

        conditionConfig = conditionTaskConfig.getConditionConfig();
    }

    @Override
    public void internStart() {
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(this);
        new Timer() {
            @Override
            public void run() {
                // TODO: timer wegmachen
                // TODO: Activate the condition after the items have been created and deactivated in the ActionHandler
                // TODO: Prevent condition trigger due to deactivation of items

                // TODO make special scroll task (oder so)
                SimulationConditionServiceImpl.getInstance().activateCondition(conditionConfig, ClientBase.getInstance().getSimpleBase(), null);
               LevelTaskPacket levelTaskPacket = new LevelTaskPacket();
                    levelTaskPacket.setQuestInfo(new QuestInfo(getAbstractTaskConfig().getName(),
                            null,
                            conditionConfig.getAdditionalDescription(),
                            null,
                            0,
                            0,
                            0,
                            QuestInfo.Type.MISSION,
                            conditionConfig.getRadarPositionHint(),
                            conditionConfig.isHideQuestProgress(),
                            null));
                    levelTaskPacket.setQuestProgressInfo(SimulationConditionServiceImpl.getInstance().getQuestProgressInfo(ClientBase.getInstance().getSimpleBase(), null));
                    QuestVisualisationModel.getInstance().setLevelTask(levelTaskPacket);
            }
        }.schedule(200);
    }

    @Override
    public void internCleanup() {
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(null);
        SimulationConditionServiceImpl.getInstance().deactivateAll();
        SimulationConditionServiceImpl.getInstance().stopUpdateTimer();
    }

    @Override
    public void conditionPassed(SimpleBase actor, Void identifier) {
        if (!ClientBase.getInstance().isMyOwnBase(actor)) {
            throw new IllegalStateException("Received conditionPassed for unexpected base: " + actor);
        }
        onTaskSucceeded();
    }


}
