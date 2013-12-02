package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.tutorial.AutomatedBattleTaskConfig;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.SyncItemTypeComparisonConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 11.09.13
 * Time: 16:01
 */
public class AutomatedBattleTask extends AbstractTask implements ConditionServiceListener<SimpleBase, Void> {
    private AutomatedBattleTaskConfig automatedBattleTaskConfig;

    public AutomatedBattleTask(AutomatedBattleTaskConfig automatedBattleTaskConfig) {
        super(automatedBattleTaskConfig);
        this.automatedBattleTaskConfig = automatedBattleTaskConfig;
    }

    public void internStart() {
        try {
            SimpleBase botBase = ClientBase.getInstance().createBotBase(automatedBattleTaskConfig.getBotName(), false);
            SyncBaseItem botAttacker = (SyncBaseItem) ItemContainer.getInstance().createSimulationSyncObject(automatedBattleTaskConfig.getBotAttacker(), botBase);
            botAttacker.setHealth(botAttacker.getHealth() * automatedBattleTaskConfig.getAttackerHealthFactor());
            Collection<SyncBaseItem> targets = ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), automatedBattleTaskConfig.getTargetItemType());
            if(targets.isEmpty()) {
                throw new IllegalStateException("At least on target must be available");
            }
            SyncBaseItem tower = CommonJava.getFirst(targets);
            ActionHandler.getInstance().attack(Collections.singletonList(botAttacker), tower);
            // Set condition
            Map<ItemType, Integer> itemTypeCount = new HashMap<ItemType, Integer>();
            itemTypeCount.put(ItemTypeContainer.getInstance().getItemType(automatedBattleTaskConfig.getBotAttacker().getItemTypeId()), 1);
            ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED,
                    new SyncItemTypeComparisonConfig(itemTypeCount),
                    null,
                    null,
                    false);
            SimulationConditionServiceImpl.getInstance().setConditionServiceListener(this);
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

            // TODO on fail detection. if target get killed
        } catch (NoSuchItemTypeException e) {
            ClientExceptionHandler.handleException("AutomatedBattleTask.internStart()", e);
        }
    }

    @Override
    public void internCleanup() {
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(null);
        SimulationConditionServiceImpl.getInstance().deactivateAll();
        SimulationConditionServiceImpl.getInstance().stopUpdateTimer();
    }

    @Override
    public void conditionPassed(SimpleBase actor, Void identifier) {
        onTaskSucceeded();
    }
}
