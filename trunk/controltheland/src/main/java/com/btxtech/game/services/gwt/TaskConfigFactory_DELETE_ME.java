package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.client.utg.tip.PraiseSplashPopupInfo;
import com.btxtech.game.jsre.client.utg.tip.StorySplashPopupInfo;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.tutorial.AbstractTaskConfig;
import com.btxtech.game.jsre.common.tutorial.AutomatedBattleTaskConfig;
import com.btxtech.game.jsre.common.tutorial.AutomatedScrollTaskConfig;
import com.btxtech.game.jsre.common.tutorial.ConditionTaskConfig;
import com.btxtech.game.jsre.common.tutorial.GuiEventTaskConfig;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.SyncItemTypeComparisonConfig;
import com.btxtech.game.services.common.ExceptionHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 12.09.13
 * Time: 02:21
 */
public class TaskConfigFactory_DELETE_ME {
    public static TutorialConfig createTask(ItemTypeService itemTypeService) {
        try {
            List<AbstractTaskConfig> abstractTaskConfigs = new ArrayList<>();
            abstractTaskConfigs.add(setupScrollTask(itemTypeService));
            abstractTaskConfigs.add(new AutomatedScrollTaskConfig(new Index(0, 200), null));
            abstractTaskConfigs.add(setupBuildFactoryTask(itemTypeService));
            abstractTaskConfigs.add(setupBuildOilTruckTask(itemTypeService));
            abstractTaskConfigs.add(setupCollectMoneyTask());
            abstractTaskConfigs.add(setupBuildJeepTask(itemTypeService));
            abstractTaskConfigs.add(setupBotAttackTask(itemTypeService));
            // abstractTaskConfigs.add(setupBuildArmyTask(itemTypeService));
            abstractTaskConfigs.add(setupKillDatacenter(itemTypeService));
            // End
            // abstractTaskConfigs.add(setupEndTask());

            return new TutorialConfig(abstractTaskConfigs, "Your base", false, false, false);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            return null;
        }
    }

    private static AbstractTaskConfig setupScrollTask(ItemTypeService itemTypeService) throws Exception {
        GameTipConfig gameTipConfig1 = new GameTipConfig();
        gameTipConfig1.setTip(GameTipConfig.Tip.SCROLL);
        gameTipConfig1.setTerrainPositionHint(new Index(2900, 1300));
        StorySplashPopupInfo storySplashPopupInfo1 = new StorySplashPopupInfo();
        storySplashPopupInfo1.setTitle("Willkommen zum Training");
        storySplashPopupInfo1.setStoryText("Razaor Industrie, dein böser Ex-Arbeitgeber, hat auf diesem Planeten ein Datencenter aufgebaut. Finde und zerstöre dieses Datencenter, damit du auf weiteren Mehrspieler Planeten landen kannst.");
        PraiseSplashPopupInfo praiseSplashPopupInfo = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo.setTitle("Gut gemacht");
        praiseSplashPopupInfo.setPraiseText("Du hast die Basis von Razarion Industries gefunden. Das Datacenter muss zerstört werden. Dazu musst du zuerst eine Armee aufbauen");
        Collection<BotConfig> botConfigs = new ArrayList<>();
        // Scroll task bot
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemTypeService.getItemType(40), 1, true, null, true, null, true, null)); // Bot Factory
        botItems.add(new BotItemConfig((BaseItemType) itemTypeService.getItemType(39), 20, true, new Region(1, Collections.singletonList(new Rectangle(new Index(23, 9), new Index(32, 23)))), true, 0, false, null)); // Bot buggy
        botItems.add(new BotItemConfig((BaseItemType) itemTypeService.getItemType(64), 1, true, null, true, null, true, null)); // Missle launcher
        botEnragementStateConfigs.add(new BotEnragementStateConfig("Normal", botItems, null));
        botConfigs.add(new BotConfig(1, false, 1000, botEnragementStateConfigs, new Region(1, Collections.singletonList(new Rectangle(new Index(23, 13), new Index(32, 18)))), "Razarion Industries", null, null, null, null));
        // Environment bot 1
        botEnragementStateConfigs = new ArrayList<>();
        botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemTypeService.getItemType(29), 10, true, null, false, null, false, null));
        botEnragementStateConfigs.add(new BotEnragementStateConfig("Normal", botItems, null));
        botConfigs.add(new BotConfig(3, true, 1000, botEnragementStateConfigs, new Region(1, Collections.singletonList(new Rectangle(4, 0, 15, 10))), "Environment 1", null, null, null, null));
        // Environment bot 1
        botEnragementStateConfigs = new ArrayList<>();
        botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemTypeService.getItemType(52), 10, true, null, false, null, false, null));
        botEnragementStateConfigs.add(new BotEnragementStateConfig("Normal", botItems, null));
        botConfigs.add(new BotConfig(4, true, 1000, botEnragementStateConfigs, new Region(1, Collections.singletonList(new Rectangle(4, 0, 15, 10))), "Environment 2", null, null, null, null));
        return new GuiEventTaskConfig(Collections.<ItemTypeAndPosition>emptyList(),
                new Index(0, 200),
                0,
                0,
                0,
                "",
                botConfigs,
                null,
                Collections.<Integer, Integer>emptyMap(),
                RadarMode.MAP_AND_UNITS,
                gameTipConfig1,
                false,
                new Rectangle(2900, 1300, 10, 10),
                storySplashPopupInfo1,
                praiseSplashPopupInfo);
    }

    private static AbstractTaskConfig setupBuildFactoryTask(ItemTypeService itemTypeService) throws Exception {
        StorySplashPopupInfo storySplashPopupInfo2 = new StorySplashPopupInfo();
        storySplashPopupInfo2.setTitle("Starte eine Basis");
        storySplashPopupInfo2.setStoryText("Baue als erstes eine Fabrik");
        PraiseSplashPopupInfo praiseSplashPopupInfo2 = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo2.setTitle("Gut gemacht");
        praiseSplashPopupInfo2.setPraiseText("Mit der Fabrik kannst du weitere Einheiten produzieren.");
        List<ItemTypeAndPosition> ownItems = new ArrayList<>();
        ownItems.add(new ItemTypeAndPosition(4, new Index(646, 470), MathHelper.EIGHTH_RADIANT));
        Map<ItemType, Integer> toBeBuilt = new HashMap<>();
        toBeBuilt.put(itemTypeService.getItemType(3), 1);
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        itemTypeLimitation.put(3, 1);
        GameTipConfig gameTipConfig = new GameTipConfig();
        gameTipConfig.setActor(4);
        gameTipConfig.setTip(GameTipConfig.Tip.BUILD);
        gameTipConfig.setToBeBuiltId(3);
        gameTipConfig.setTerrainPositionHint(new Index(495, 481));
        return new ConditionTaskConfig(ownItems,
                new Index(0, 200),
                new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(toBeBuilt), null, "", false),
                10,
                1000,
                10000,
                "",
                null,
                null,
                itemTypeLimitation,
                RadarMode.MAP_AND_UNITS,
                gameTipConfig,
                false,
                storySplashPopupInfo2,
                praiseSplashPopupInfo2);
    }

    private static AbstractTaskConfig setupBuildOilTruckTask(ItemTypeService itemTypeService) throws Exception {
        StorySplashPopupInfo storySplashPopupInfo3 = new StorySplashPopupInfo();
        storySplashPopupInfo3.setTitle("Starte eine Basis");
        storySplashPopupInfo3.setStoryText("Beue einen Öllaster und verdiene Geld");
        PraiseSplashPopupInfo praiseSplashPopupInfo3 = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo3.setTitle("Gut gemacht");
        praiseSplashPopupInfo3.setPraiseText("Nun kannst du Geld abbauen.");
        List<ItemTypeAndPosition> ownItems = new ArrayList<>();
        ownItems.add(new ItemTypeAndPosition(5, new Index(660, 740), MathHelper.EIGHTH_RADIANT));
        Map<ItemType, Integer> toBeBuilt = new HashMap<>();
        toBeBuilt.put(itemTypeService.getItemType(2), 1);
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        itemTypeLimitation.put(3, 1);
        itemTypeLimitation.put(2, 2);
        GameTipConfig gameTipConfig3 = new GameTipConfig();
        gameTipConfig3.setActor(3);
        gameTipConfig3.setTip(GameTipConfig.Tip.FABRICATE);
        gameTipConfig3.setToBeBuiltId(2);
        return new ConditionTaskConfig(ownItems,
                null,
                new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(toBeBuilt), null, "", false),
                10,
                1000,
                10000,
                "",
                null,
                null,
                itemTypeLimitation,
                RadarMode.MAP_AND_UNITS,
                gameTipConfig3,
                false,
                storySplashPopupInfo3,
                praiseSplashPopupInfo3);
    }

    private static AbstractTaskConfig setupCollectMoneyTask() {
        StorySplashPopupInfo storySplashPopupInfo4 = new StorySplashPopupInfo();
        storySplashPopupInfo4.setTitle("Starte eine Basis");
        storySplashPopupInfo4.setStoryText("Baue mit deinem Öllaster Geld ab");
        PraiseSplashPopupInfo praiseSplashPopupInfo4 = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo4.setTitle("Gut gemacht");
        praiseSplashPopupInfo4.setPraiseText("Nun kannst Du deine Basis weiter aufbauen. Links oben im Munü siehst du deine Finanzen");
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        itemTypeLimitation.put(3, 1);
        itemTypeLimitation.put(2, 2);
        GameTipConfig gameTipConfig4 = new GameTipConfig();
        gameTipConfig4.setActor(2);
        gameTipConfig4.setTip(GameTipConfig.Tip.GET_RESOURCE);
        gameTipConfig4.setResourceId(5);
        return new ConditionTaskConfig(Collections.<ItemTypeAndPosition>emptyList(),
                null,
                new ConditionConfig(ConditionTrigger.MONEY_INCREASED, new CountComparisonConfig(20), null, "", false),
                10,
                1000,
                10000,
                "",
                null,
                null,
                itemTypeLimitation,
                RadarMode.MAP_AND_UNITS,
                gameTipConfig4,
                false,
                storySplashPopupInfo4,
                praiseSplashPopupInfo4);
    }

    private static AbstractTaskConfig setupBuildTowerTask(ItemTypeService itemTypeService) throws Exception {
        StorySplashPopupInfo storySplashPopupInfo = new StorySplashPopupInfo();
        storySplashPopupInfo.setTitle("Starte eine Basis");
        storySplashPopupInfo.setStoryText("Razar Industries hat dich auf der entdeckt, höchste Zeit dich zu verteidigen. Baue ein Abwehrturm.");
        PraiseSplashPopupInfo praiseSplashPopupInfo = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo.setTitle("Gut gemacht");
        praiseSplashPopupInfo.setPraiseText("Einen Angriff von Razar Industries kannst du nun abwehren.");
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        itemTypeLimitation.put(3, 1);
        itemTypeLimitation.put(2, 2);
        itemTypeLimitation.put(12, 2);
        GameTipConfig gameTipConfig = new GameTipConfig();
        gameTipConfig.setActor(4);
        gameTipConfig.setTip(GameTipConfig.Tip.BUILD);
        gameTipConfig.setToBeBuiltId(12);
        gameTipConfig.setTerrainPositionHint(new Index(695, 681));
        Map<ItemType, Integer> toBeBuilt = new HashMap<>();
        toBeBuilt.put(itemTypeService.getItemType(12), 1);
        return new ConditionTaskConfig(Collections.<ItemTypeAndPosition>emptyList(),
                null,
                new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(toBeBuilt), null, "", false),
                10,
                1000,
                10000,
                "",
                null,
                null,
                itemTypeLimitation,
                RadarMode.MAP_AND_UNITS,
                gameTipConfig,
                false,
                storySplashPopupInfo,
                praiseSplashPopupInfo);
    }

    private static AbstractTaskConfig setupBuildJeepTask(ItemTypeService itemTypeService) throws Exception {
        StorySplashPopupInfo storySplashPopupInfo = new StorySplashPopupInfo();
        storySplashPopupInfo.setTitle("Starte eine Basis");
        storySplashPopupInfo.setStoryText("Razar Industries hat dich entdeckt, höchste Zeit dich zu verteidigen. Baue einen Jeep.");
        PraiseSplashPopupInfo praiseSplashPopupInfo = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo.setTitle("Gut gemacht");
        praiseSplashPopupInfo.setPraiseText("Einen Angriff von Razar Industries kannst du nun abwehren.");
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        itemTypeLimitation.put(3, 1);
        itemTypeLimitation.put(2, 2);
        itemTypeLimitation.put(1, 1);
        GameTipConfig gameTipConfig = new GameTipConfig();
        gameTipConfig.setTip(GameTipConfig.Tip.WATCH_QUEST);
        gameTipConfig.setToBeBuiltId(1);
        gameTipConfig.setActor(3);
        Map<ItemType, Integer> toBeBuilt = new HashMap<>();
        toBeBuilt.put(itemTypeService.getItemType(1), 1);
        return new ConditionTaskConfig(Collections.<ItemTypeAndPosition>emptyList(),
                null,
                new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(toBeBuilt), null, "", false),
                10,
                1000,
                10000,
                "",
                null,
                null,
                itemTypeLimitation,
                RadarMode.MAP_AND_UNITS,
                gameTipConfig,
                false,
                storySplashPopupInfo,
                praiseSplashPopupInfo);
    }

    private static AbstractTaskConfig setupBotAttackTask(ItemTypeService itemTypeService) throws NoSuchItemTypeException {
        Collection<BotConfig> botConfigs = new ArrayList<>();
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemTypeService.getItemType(64), 1, true, null, true, null, true, null)); // Missle launcher
        botEnragementStateConfigs.add(new BotEnragementStateConfig("Normal", botItems, null));
        botConfigs.add(new BotConfig(2, false, 1000, botEnragementStateConfigs, new Region(1, Collections.singletonList(new Rectangle(new Index(23, 13), new Index(32, 18)))), "Razarion Industries", null, null, null, null));
        Collection<Integer> botToKill = new ArrayList<>();
        botToKill.add(1);
        PraiseSplashPopupInfo praiseSplashPopupInfo = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo.setTitle("Gut gemacht");
        praiseSplashPopupInfo.setPraiseText("Da hast du Razarion Industries aber kräftig eingeheizt.");
        return new AutomatedBattleTaskConfig(praiseSplashPopupInfo,
                new ItemTypeAndPosition(39, new Index(695, 981), MathHelper.EIGHTH_RADIANT),
                0.33,
                "Bot",
                1,
                botConfigs,
                botToKill);
    }

    private static AbstractTaskConfig setupBuildArmyTask(ItemTypeService itemTypeService) throws Exception {
        StorySplashPopupInfo storySplashPopupInfo = new StorySplashPopupInfo();
        storySplashPopupInfo.setTitle("Lösche Razar Industries aus");
        storySplashPopupInfo.setStoryText("Baue eine Armee.");
        PraiseSplashPopupInfo praiseSplashPopupInfo = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo.setTitle("Gut gemacht");
        praiseSplashPopupInfo.setPraiseText("Das sollte reichen um Raza Industries auszulöschen.");
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        itemTypeLimitation.put(3, 1);
        itemTypeLimitation.put(2, 2);
        itemTypeLimitation.put(1, 10);
        GameTipConfig gameTipConfig = new GameTipConfig();
        gameTipConfig.setTip(GameTipConfig.Tip.WATCH_QUEST);
        gameTipConfig.setToBeBuiltId(1);
        gameTipConfig.setActor(3);
        Map<ItemType, Integer> toBeBuilt = new HashMap<>();
        toBeBuilt.put(itemTypeService.getItemType(1), 4);
        return new ConditionTaskConfig(Collections.<ItemTypeAndPosition>emptyList(),
                null,
                new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(toBeBuilt), null, "", false),
                10,
                1000,
                10000,
                "",
                null,
                null,
                itemTypeLimitation,
                RadarMode.MAP_AND_UNITS,
                gameTipConfig,
                false,
                storySplashPopupInfo,
                praiseSplashPopupInfo);
    }

    private static AbstractTaskConfig setupKillDatacenter(ItemTypeService itemTypeService) throws Exception {
        StorySplashPopupInfo storySplashPopupInfo = new StorySplashPopupInfo();
        storySplashPopupInfo.setTitle("Lösche Razar Industries aus");
        storySplashPopupInfo.setStoryText("Du hast Razarion Industries in die Flucht geschlagen! Razarion Industries betreibt auf diesem Planeten nur noch eine Flakstellung, damit du den Planeten nicht verlassen kannst. Zerstöre die Flakstellung und gehe zum nächsten Planeten.");
        PraiseSplashPopupInfo praiseSplashPopupInfo = new PraiseSplashPopupInfo();
        praiseSplashPopupInfo.setTitle("Gut gemacht");
        praiseSplashPopupInfo.setPraiseText("Gehe zum nächsten Planeten. Dies ist ein Mehrspielerplanet.");
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        itemTypeLimitation.put(3, 1);
        itemTypeLimitation.put(2, 2);
        itemTypeLimitation.put(1, 10);
        GameTipConfig gameTipConfig = new GameTipConfig();
        gameTipConfig.setTip(GameTipConfig.Tip.ATTACK);
        gameTipConfig.setActor(1);
        Map<ItemType, Integer> toBeKilled = new HashMap<>();
        toBeKilled.put(itemTypeService.getItemType(64), 1);
        return new ConditionTaskConfig(Collections.<ItemTypeAndPosition>emptyList(),
                null,
                new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(toBeKilled), null, "", false),
                10,
                1000,
                10000,
                "",
                null,
                null,
                itemTypeLimitation,
                RadarMode.MAP_AND_UNITS,
                gameTipConfig,
                false,
                storySplashPopupInfo,
                praiseSplashPopupInfo);
    }

    private static AbstractTaskConfig setupEndTask() {
        StorySplashPopupInfo endStorySplashPopupInfo = new StorySplashPopupInfo();
        endStorySplashPopupInfo.setTitle("Fertig");
        endStorySplashPopupInfo.setStoryText("Ende");
        return new ConditionTaskConfig(Collections.<ItemTypeAndPosition>emptyList(),
                new Index(0, 200),
                new ConditionConfig(ConditionTrigger.CRYSTALS_INCREASED, new CountComparisonConfig(99999), null, "", false),
                10,
                1000,
                10000,
                "",
                null,
                null,
                Collections.<Integer, Integer>emptyMap(),
                RadarMode.MAP_AND_UNITS,
                null,
                false,
                endStorySplashPopupInfo,
                null);
    }

}
