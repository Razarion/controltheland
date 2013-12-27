package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestTypeEnum;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.SyncItemTypeComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.utg.condition.DbAbstractComparisonConfig;
import com.btxtech.game.services.utg.condition.DbComparisonItemCount;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbItemTypePositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User: beat
 * Date: 07.03.2011
 * Time: 17:15:43
 */
public class TestLevelCrud extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @DirtiesContext
    public void createModifyDelete() {
        // Setup environment
        DbTutorialConfig tut1 = createTutorial1();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbLevel> dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Assert.assertEquals(0, dbLevels.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // --------------------- CREATE ---------------------

        // Setup QuestionHub
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel1.setNumber(1);
        dbLevel1.setXp(599);
        DbLevelTask dbLevelTask11 = dbLevel1.getLevelTaskCrud().createDbChild();
        dbLevelTask11.setMoney(16);
        dbLevelTask11.setXp(34);
        dbLevelTask11.getI18nTitle().putString("dbLevelTask11");
        dbLevelTask11.setDbTutorialConfig(tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(tut1.getId()));
        DbLevelTask dbLevelTask12 = dbLevel1.getLevelTaskCrud().createDbChild();
        dbLevelTask12.setMoney(17);
        dbLevelTask12.setXp(35);
        dbLevelTask12.getI18nTitle().putString("dbLevelTask12");
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Assert.assertEquals(1, dbLevels.size());
        DbLevel dbLevel = dbLevels.get(0);
        Assert.assertEquals("1", dbLevel.getName());
        Assert.assertEquals(1, dbLevel1.getNumber());
        Assert.assertEquals(2, dbLevel1.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(599, dbLevel1.getXp());
        dbLevelTask11 = dbLevel1.getLevelTaskCrud().readDbChild(dbLevelTask11.getId());
        Assert.assertEquals(16, dbLevelTask11.getMoney());
        Assert.assertEquals(34, dbLevelTask11.getXp());
        Assert.assertEquals("dbLevelTask11", dbLevelTask11.getI18nTitle().getString());
        Assert.assertEquals(tut1.getId(), dbLevelTask11.getDbTutorialConfig().getId());
        dbLevelTask12 = dbLevel1.getLevelTaskCrud().readDbChild(dbLevelTask12.getId());
        Assert.assertEquals(17, dbLevelTask12.getMoney());
        Assert.assertEquals(35, dbLevelTask12.getXp());
        Assert.assertEquals("dbLevelTask12", dbLevelTask12.getI18nTitle().getString());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Setup new Level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel2 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel2.setNumber(2);
        dbLevel2.setXp(333);
        DbLevelTask dbLevelTask21 = dbLevel2.getLevelTaskCrud().createDbChild();
        dbLevelTask21.setMoney(17);
        dbLevelTask21.setXp(39);
        dbLevelTask21.getI18nTitle().putString("dbLevelTask21");
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Assert.assertEquals(2, dbLevels.size());
        dbLevel = dbLevels.get(0);
        Assert.assertEquals("1", dbLevel.getName());
        Assert.assertEquals(1, dbLevel1.getNumber());
        Assert.assertEquals(2, dbLevel1.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(599, dbLevel1.getXp());
        dbLevelTask11 = dbLevel1.getLevelTaskCrud().readDbChild(dbLevelTask11.getId());
        Assert.assertEquals(16, dbLevelTask11.getMoney());
        Assert.assertEquals(34, dbLevelTask11.getXp());
        Assert.assertEquals("dbLevelTask11", dbLevelTask11.getI18nTitle().getString());
        Assert.assertEquals(tut1.getId(), dbLevelTask11.getDbTutorialConfig().getId());
        dbLevelTask12 = dbLevel1.getLevelTaskCrud().readDbChild(dbLevelTask12.getId());
        Assert.assertEquals(17, dbLevelTask12.getMoney());
        Assert.assertEquals(35, dbLevelTask12.getXp());
        Assert.assertEquals("dbLevelTask12", dbLevelTask12.getI18nTitle().getString());
        dbLevel2 = dbLevels.get(1);
        Assert.assertEquals(2, dbLevel2.getNumber());
        Assert.assertEquals(1, dbLevel2.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(333, dbLevel2.getXp());
        dbLevelTask21 = dbLevel2.getLevelTaskCrud().readDbChild(dbLevelTask21.getId());
        Assert.assertEquals(17, dbLevelTask21.getMoney());
        Assert.assertEquals(39, dbLevelTask21.getXp());
        Assert.assertEquals("dbLevelTask21", dbLevelTask21.getI18nTitle().getString());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Setup new QuestionHub
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel3 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel3.setNumber(3);
        dbLevel3.setXp(74);
        DbLevelTask dbLevelTask31 = dbLevel3.getLevelTaskCrud().createDbChild();
        dbLevelTask31.setMoney(36);
        dbLevelTask31.setXp(87);
        dbLevelTask31.getI18nTitle().putString("dbLevelTask31");
        dbLevelTask31.setDbTutorialConfig(tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(tut1.getId()));
        DbLevelTask dbLevelTask32 = dbLevel3.getLevelTaskCrud().createDbChild();
        dbLevelTask32.setMoney(18);
        dbLevelTask32.setXp(90);
        dbLevelTask32.getI18nTitle().putString("dbLevelTask32");
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Assert.assertEquals(3, dbLevels.size());
        dbLevel = dbLevels.get(0);
        Assert.assertEquals("1", dbLevel.getName());
        Assert.assertEquals(1, dbLevel1.getNumber());
        Assert.assertEquals(2, dbLevel1.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(599, dbLevel1.getXp());
        dbLevelTask11 = dbLevel1.getLevelTaskCrud().readDbChild(dbLevelTask11.getId());
        Assert.assertEquals(16, dbLevelTask11.getMoney());
        Assert.assertEquals(34, dbLevelTask11.getXp());
        Assert.assertEquals("dbLevelTask11", dbLevelTask11.getI18nTitle().getString());
        Assert.assertEquals(tut1.getId(), dbLevelTask11.getDbTutorialConfig().getId());
        dbLevelTask12 = dbLevel1.getLevelTaskCrud().readDbChild(dbLevelTask12.getId());
        Assert.assertEquals(17, dbLevelTask12.getMoney());
        Assert.assertEquals(35, dbLevelTask12.getXp());
        Assert.assertEquals("dbLevelTask12", dbLevelTask12.getI18nTitle().getString());
        dbLevel2 = dbLevels.get(1);
        Assert.assertEquals(2, dbLevel2.getNumber());
        Assert.assertEquals(1, dbLevel2.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(333, dbLevel2.getXp());
        dbLevelTask21 = dbLevel2.getLevelTaskCrud().readDbChild(dbLevelTask21.getId());
        Assert.assertEquals(17, dbLevelTask21.getMoney());
        Assert.assertEquals(39, dbLevelTask21.getXp());
        Assert.assertEquals("dbLevelTask21", dbLevelTask21.getI18nTitle().getString());
        dbLevel3 = dbLevels.get(2);
        Assert.assertEquals(3, dbLevel3.getNumber());
        Assert.assertEquals(2, dbLevel3.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(74, dbLevel3.getXp());
        dbLevelTask31 = dbLevel3.getLevelTaskCrud().readDbChild(dbLevelTask31.getId());
        Assert.assertEquals(36, dbLevelTask31.getMoney());
        Assert.assertEquals(87, dbLevelTask31.getXp());
        Assert.assertEquals("dbLevelTask31", dbLevelTask31.getI18nTitle().getString());
        Assert.assertEquals(tut1.getId(), dbLevelTask31.getDbTutorialConfig().getId());
        dbLevelTask32 = dbLevel3.getLevelTaskCrud().readDbChild(dbLevelTask32.getId());
        Assert.assertEquals(18, dbLevelTask32.getMoney());
        Assert.assertEquals(90, dbLevelTask32.getXp());
        Assert.assertEquals("dbLevelTask32", dbLevelTask32.getI18nTitle().getString());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // --------------------- MODIFY ---------------------
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevels.get(0));
        dbLevels.get(1).setXp(191);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevels.get(1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Assert.assertEquals(3, dbLevels.size());
        dbLevel1 = dbLevels.get(0);
        Assert.assertEquals(1, dbLevel1.getNumber());
        Assert.assertEquals(2, dbLevel1.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(599, dbLevel1.getXp());
        dbLevelTask11 = dbLevel1.getLevelTaskCrud().readDbChild(dbLevelTask11.getId());
        Assert.assertEquals(16, dbLevelTask11.getMoney());
        Assert.assertEquals(34, dbLevelTask11.getXp());
        Assert.assertEquals("dbLevelTask11", dbLevelTask11.getI18nTitle().getString());
        Assert.assertEquals(tut1.getId(), dbLevelTask11.getDbTutorialConfig().getId());
        dbLevelTask12 = dbLevel1.getLevelTaskCrud().readDbChild(dbLevelTask12.getId());
        Assert.assertEquals(17, dbLevelTask12.getMoney());
        Assert.assertEquals(35, dbLevelTask12.getXp());
        Assert.assertEquals("dbLevelTask12", dbLevelTask12.getI18nTitle().getString());
        dbLevel2 = dbLevels.get(1);
        Assert.assertEquals(2, dbLevel2.getNumber());
        Assert.assertEquals(1, dbLevel2.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(191, dbLevel2.getXp());
        dbLevelTask21 = dbLevel2.getLevelTaskCrud().readDbChild(dbLevelTask21.getId());
        Assert.assertEquals(17, dbLevelTask21.getMoney());
        Assert.assertEquals(39, dbLevelTask21.getXp());
        Assert.assertEquals("dbLevelTask21", dbLevelTask21.getI18nTitle().getString());
        dbLevel3 = dbLevels.get(2);
        Assert.assertEquals(3, dbLevel3.getNumber());
        Assert.assertEquals(2, dbLevel3.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(74, dbLevel3.getXp());
        dbLevelTask31 = dbLevel3.getLevelTaskCrud().readDbChild(dbLevelTask31.getId());
        Assert.assertEquals(36, dbLevelTask31.getMoney());
        Assert.assertEquals(87, dbLevelTask31.getXp());
        Assert.assertEquals("dbLevelTask31", dbLevelTask31.getI18nTitle().getString());
        Assert.assertEquals(tut1.getId(), dbLevelTask31.getDbTutorialConfig().getId());
        dbLevelTask32 = dbLevel3.getLevelTaskCrud().readDbChild(dbLevelTask32.getId());
        Assert.assertEquals(18, dbLevelTask32.getMoney());
        Assert.assertEquals(90, dbLevelTask32.getXp());
        Assert.assertEquals("dbLevelTask32", dbLevelTask32.getI18nTitle().getString());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // --------------------- DELETE ---------------------

        // Delete level 0
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        userGuidanceService.getDbLevelCrud().deleteDbChild(dbLevels.get(0));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Assert.assertEquals(2, dbLevels.size());
        dbLevel3 = dbLevels.get(1);
        Assert.assertEquals(3, dbLevel3.getNumber());
        Assert.assertEquals(2, dbLevel3.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(74, dbLevel3.getXp());
        dbLevelTask31 = dbLevel3.getLevelTaskCrud().readDbChild(dbLevelTask31.getId());
        Assert.assertEquals(36, dbLevelTask31.getMoney());
        Assert.assertEquals(87, dbLevelTask31.getXp());
        Assert.assertEquals("dbLevelTask31", dbLevelTask31.getI18nTitle().getString());
        Assert.assertEquals(tut1.getId(), dbLevelTask31.getDbTutorialConfig().getId());
        dbLevelTask32 = dbLevel3.getLevelTaskCrud().readDbChild(dbLevelTask32.getId());
        Assert.assertEquals(18, dbLevelTask32.getMoney());
        Assert.assertEquals(90, dbLevelTask32.getXp());
        Assert.assertEquals("dbLevelTask32", dbLevelTask32.getI18nTitle().getString());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete dbLevelTask32
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        dbLevel3 = dbLevels.get(1);
        dbLevel3.getLevelTaskCrud().deleteDbChild(dbLevel3.getLevelTaskCrud().readDbChild(dbLevelTask32.getId()));
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevels.get(0));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Assert.assertEquals(2, dbLevels.size());
        dbLevel3 = dbLevels.get(1);
        Assert.assertEquals(3, dbLevel3.getNumber());
        Assert.assertEquals(1, dbLevel3.getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(74, dbLevel3.getXp());
        dbLevelTask31 = dbLevel3.getLevelTaskCrud().readDbChild(dbLevelTask31.getId());
        Assert.assertEquals(36, dbLevelTask31.getMoney());
        Assert.assertEquals(87, dbLevelTask31.getXp());
        Assert.assertEquals("dbLevelTask31", dbLevelTask31.getI18nTitle().getString());
        Assert.assertEquals(tut1.getId(), dbLevelTask31.getDbTutorialConfig().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete dbLevel3
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        userGuidanceService.getDbLevelCrud().deleteDbChild(dbLevels.get(1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Assert.assertEquals(1, dbLevels.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void orderingDbLevel() {
        // Setup Levels
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        DbLevel dbLevel2 = userGuidanceService.getDbLevelCrud().createDbChild();
        DbLevel dbLevel3 = userGuidanceService.getDbLevelCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbLevel> dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        // Size
        Assert.assertEquals(3, dbLevels.size());
        // Ordering
        Assert.assertEquals(dbLevel1.getId(), dbLevels.get(0).getId());
        Assert.assertEquals(dbLevel2.getId(), dbLevels.get(1).getId());
        Assert.assertEquals(dbLevel3.getId(), dbLevels.get(2).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Modify ordering
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        Collections.swap(dbLevels, 0, 1);
        userGuidanceService.getDbLevelCrud().updateDbChildren(dbLevels);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        // Size
        Assert.assertEquals(3, dbLevels.size());
        // Ordering
        Assert.assertEquals(dbLevel1.getId(), dbLevels.get(1).getId());
        Assert.assertEquals(dbLevel2.getId(), dbLevels.get(0).getId());
        Assert.assertEquals(dbLevel3.getId(), dbLevels.get(2).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    @SuppressWarnings("unchecked")
    public void dbItemTypePositionComparisonConfig() throws Exception {
        configureItemTypes();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevelCrud().readDbChild(dbLevel1.getId());
        DbConditionConfig dbConditionConfig1 = new DbConditionConfig();
        DbLevelTask dbLevelTask = dbLevel1.getLevelTaskCrud().createDbChild();
        dbLevelTask.setDbConditionConfig(dbConditionConfig1);
        dbConditionConfig1.setConditionTrigger(ConditionTrigger.SYNC_ITEM_POSITION);
        dbConditionConfig1.getI18nAdditionalDescription().putString("setAdditionalDescription");
        DbItemTypePositionComparisonConfig comparisonConfig = new DbItemTypePositionComparisonConfig();
        DbComparisonItemCount dbComparisonItemCount = comparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setCount(1);
        dbComparisonItemCount.setItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbComparisonItemCount = comparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setCount(2);
        dbComparisonItemCount.setItemType(serverItemTypeService.getDbBaseItemType(TEST_HARVESTER_ITEM_ID));
        DbRegion dbRegion = createDbRegion(new Rectangle(101, 202, 3003, 4004));
        comparisonConfig.setRegion(dbRegion);
        comparisonConfig.setTimeInMinutes(10);
        dbConditionConfig1.setDbAbstractComparisonConfig(comparisonConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevel(dbLevel1.getId());
        dbLevelTask = CommonJava.getFirst(dbLevel1.getLevelTaskCrud().readDbChildren());
        dbConditionConfig1 = dbLevelTask.getDbConditionConfig();
        ConditionConfig conditionConfig = dbConditionConfig1.createConditionConfig(serverItemTypeService, Locale.ENGLISH);
        Assert.assertEquals(ConditionTrigger.SYNC_ITEM_POSITION, conditionConfig.getConditionTrigger());
        Assert.assertEquals("setAdditionalDescription", conditionConfig.getAdditionalDescription());
        ItemTypePositionComparisonConfig itemTypePositionComparisonConfig = (ItemTypePositionComparisonConfig) conditionConfig.getAbstractComparisonConfig();
        Region region = (Region) getPrivateField(ItemTypePositionComparisonConfig.class, itemTypePositionComparisonConfig, "region");
        Assert.assertEquals((int) dbRegion.getId(), region.getId());
        Integer time = (Integer) getPrivateField(ItemTypePositionComparisonConfig.class, itemTypePositionComparisonConfig, "time");
        Assert.assertEquals((int) time, 10 * 60 * 1000);
        Map<ItemType, Integer> itemTypes = (Map<ItemType, Integer>) getPrivateField(ItemTypePositionComparisonConfig.class, itemTypePositionComparisonConfig, "itemTypes");
        Assert.assertEquals(2, itemTypes.size());
        Assert.assertEquals(1, (int) itemTypes.get(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID)));
        Assert.assertEquals(2, (int) itemTypes.get(serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Remove dbComparisonItemCount
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevel(dbLevel1.getId());
        dbLevelTask = CommonJava.getFirst(dbLevel1.getLevelTaskCrud().readDbChildren());
        comparisonConfig = (DbItemTypePositionComparisonConfig) dbLevelTask.getDbConditionConfig().getDbAbstractComparisonConfig();
        dbComparisonItemCount = CommonJava.getFirst(comparisonConfig.getCrudDbComparisonItemCount().readDbChildren());
        comparisonConfig.getCrudDbComparisonItemCount().deleteDbChild(dbComparisonItemCount);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, HibernateUtil.loadAll(sessionFactory, DbComparisonItemCount.class).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Remove whole comparision
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevel(dbLevel1.getId());
        dbLevelTask = CommonJava.getFirst(dbLevel1.getLevelTaskCrud().readDbChildren());
        dbLevelTask.setDbConditionConfig(null);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbConditionConfig.class).size());
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbAbstractComparisonConfig.class).size());
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbComparisonItemCount.class).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void dbSyncItemTypeComparisonConfig() throws Exception {
        configureItemTypes();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevelCrud().readDbChild(dbLevel1.getId());
        DbLevelTask dbLevelTask = dbLevel1.getLevelTaskCrud().createDbChild();
        DbConditionConfig dbConditionConfig1 = new DbConditionConfig();
        dbLevelTask.setDbConditionConfig(dbConditionConfig1);
        dbConditionConfig1.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
        DbSyncItemTypeComparisonConfig comparisonConfig = new DbSyncItemTypeComparisonConfig();
        DbComparisonItemCount dbComparisonItemCount = comparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setCount(1);
        dbComparisonItemCount.setItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbComparisonItemCount = comparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setCount(2);
        dbComparisonItemCount.setItemType(serverItemTypeService.getDbBaseItemType(TEST_HARVESTER_ITEM_ID));
        dbConditionConfig1.setDbAbstractComparisonConfig(comparisonConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevel(dbLevel1.getId());
        dbLevelTask = CommonJava.getFirst(dbLevel1.getLevelTaskCrud().readDbChildren());
        dbConditionConfig1 = dbLevelTask.getDbConditionConfig();
        ConditionConfig conditionConfig = dbConditionConfig1.createConditionConfig(serverItemTypeService, Locale.ENGLISH);
        Assert.assertEquals(ConditionTrigger.SYNC_ITEM_BUILT, conditionConfig.getConditionTrigger());
        SyncItemTypeComparisonConfig syncItemTypeComparisonConfig = (SyncItemTypeComparisonConfig) conditionConfig.getAbstractComparisonConfig();
        Map<ItemType, Integer> itemTypes = (Map<ItemType, Integer>) getPrivateField(SyncItemTypeComparisonConfig.class, syncItemTypeComparisonConfig, "itemTypeCount");
        Assert.assertEquals(2, itemTypes.size());
        Assert.assertEquals(1, (int) itemTypes.get(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID)));
        Assert.assertEquals(2, (int) itemTypes.get(serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Remove dbComparisonItemCount
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevelCrud().readDbChild(dbLevel1.getId());
        dbLevelTask = CommonJava.getFirst(dbLevel1.getLevelTaskCrud().readDbChildren());
        comparisonConfig = (DbSyncItemTypeComparisonConfig) dbLevelTask.getDbConditionConfig().getDbAbstractComparisonConfig();
        dbComparisonItemCount = CommonJava.getFirst(comparisonConfig.getCrudDbComparisonItemCount().readDbChildren());
        comparisonConfig.getCrudDbComparisonItemCount().deleteDbChild(dbComparisonItemCount);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, HibernateUtil.loadAll(sessionFactory, DbComparisonItemCount.class).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Remove whole comparision
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevelCrud().readDbChild(dbLevel1.getId());
        dbLevelTask = CommonJava.getFirst(dbLevel1.getLevelTaskCrud().readDbChildren());
        dbLevelTask.setDbConditionConfig(null);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbConditionConfig.class).size());
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbAbstractComparisonConfig.class).size());
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbComparisonItemCount.class).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void orderingDbLevelTask() {
        // Setup QuestHubs
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        DbLevelTask dbLevelTask11 = dbLevel1.getLevelTaskCrud().createDbChild();
        DbLevelTask dbLevelTask12 = dbLevel1.getLevelTaskCrud().createDbChild();
        DbLevelTask dbLevelTask13 = dbLevel1.getLevelTaskCrud().createDbChild();
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        DbLevel dbLevel2 = userGuidanceService.getDbLevelCrud().createDbChild();
        DbLevelTask dbLevelTask21 = dbLevel2.getLevelTaskCrud().createDbChild();
        DbLevelTask dbLevelTask22 = dbLevel2.getLevelTaskCrud().createDbChild();
        DbLevelTask dbLevelTask23 = dbLevel2.getLevelTaskCrud().createDbChild();
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbLevel> dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        // Size
        Assert.assertEquals(3, dbLevels.get(0).getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(3, dbLevels.get(1).getLevelTaskCrud().readDbChildren().size());
        // Ordering
        List<DbLevelTask> dbLevelTasks11 = dbLevels.get(0).getLevelTaskCrud().readDbChildren();
        List<DbLevelTask> dbLevelTasks12 = dbLevels.get(1).getLevelTaskCrud().readDbChildren();
        Assert.assertEquals(dbLevelTask11.getId(), dbLevelTasks11.get(0).getId());
        Assert.assertEquals(dbLevelTask12.getId(), dbLevelTasks11.get(1).getId());
        Assert.assertEquals(dbLevelTask13.getId(), dbLevelTasks11.get(2).getId());
        Assert.assertEquals(dbLevelTask21.getId(), dbLevelTasks12.get(0).getId());
        Assert.assertEquals(dbLevelTask22.getId(), dbLevelTasks12.get(1).getId());
        Assert.assertEquals(dbLevelTask23.getId(), dbLevelTasks12.get(2).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Modify ordering
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevel(dbLevel1.getId());
        dbLevelTasks11 = dbLevel1.getLevelTaskCrud().readDbChildren();
        Collections.swap(dbLevelTasks11, 0, 1);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        // Size
        Assert.assertEquals(3, dbLevels.get(0).getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(3, dbLevels.get(1).getLevelTaskCrud().readDbChildren().size());
        // Ordering
        dbLevelTasks11 = dbLevels.get(0).getLevelTaskCrud().readDbChildren();
        dbLevelTasks12 = dbLevels.get(1).getLevelTaskCrud().readDbChildren();
        Assert.assertEquals(dbLevelTask11.getId(), dbLevelTasks11.get(1).getId());
        Assert.assertEquals(dbLevelTask12.getId(), dbLevelTasks11.get(0).getId());
        Assert.assertEquals(dbLevelTask13.getId(), dbLevelTasks11.get(2).getId());
        Assert.assertEquals(dbLevelTask21.getId(), dbLevelTasks12.get(0).getId());
        Assert.assertEquals(dbLevelTask22.getId(), dbLevelTasks12.get(1).getId());
        Assert.assertEquals(dbLevelTask23.getId(), dbLevelTasks12.get(2).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Modify ordering
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel2 = userGuidanceService.getDbLevel(dbLevel2.getId());
        dbLevelTasks12 = dbLevel2.getLevelTaskCrud().readDbChildren();
        Collections.swap(dbLevelTasks12, 1, 2);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        // Size
        Assert.assertEquals(3, dbLevels.get(0).getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(3, dbLevels.get(1).getLevelTaskCrud().readDbChildren().size());
        // Ordering
        dbLevelTasks11 = dbLevels.get(0).getLevelTaskCrud().readDbChildren();
        dbLevelTasks12 = dbLevels.get(1).getLevelTaskCrud().readDbChildren();
        Assert.assertEquals(dbLevelTask11.getId(), dbLevelTasks11.get(1).getId());
        Assert.assertEquals(dbLevelTask12.getId(), dbLevelTasks11.get(0).getId());
        Assert.assertEquals(dbLevelTask13.getId(), dbLevelTasks11.get(2).getId());
        Assert.assertEquals(dbLevelTask21.getId(), dbLevelTasks12.get(0).getId());
        Assert.assertEquals(dbLevelTask22.getId(), dbLevelTasks12.get(2).getId());
        Assert.assertEquals(dbLevelTask23.getId(), dbLevelTasks12.get(1).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Modify delete
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel2 = userGuidanceService.getDbLevel(dbLevel2.getId());
        dbLevelTask23 = dbLevel2.getLevelTaskCrud().readDbChild(dbLevelTask23.getId());
        dbLevel2.getLevelTaskCrud().deleteDbChild(dbLevelTask23);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        // Size
        Assert.assertEquals(3, dbLevels.get(0).getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(2, dbLevels.get(1).getLevelTaskCrud().readDbChildren().size());
        // Ordering
        dbLevelTasks11 = dbLevels.get(0).getLevelTaskCrud().readDbChildren();
        dbLevelTasks12 = dbLevels.get(1).getLevelTaskCrud().readDbChildren();
        Assert.assertEquals(dbLevelTask11.getId(), dbLevelTasks11.get(1).getId());
        Assert.assertEquals(dbLevelTask12.getId(), dbLevelTasks11.get(0).getId());
        Assert.assertEquals(dbLevelTask13.getId(), dbLevelTasks11.get(2).getId());
        Assert.assertEquals(dbLevelTask21.getId(), dbLevelTasks12.get(0).getId());
        Assert.assertEquals(dbLevelTask22.getId(), dbLevelTasks12.get(1).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Modify add
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel1 = userGuidanceService.getDbLevel(dbLevel1.getId());
        DbLevelTask dbLevelTask114 = dbLevel1.getLevelTaskCrud().createDbChild();
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevels = new ArrayList<>(userGuidanceService.getDbLevelCrud().readDbChildren());
        // Size
        Assert.assertEquals(4, dbLevels.get(0).getLevelTaskCrud().readDbChildren().size());
        Assert.assertEquals(2, dbLevels.get(1).getLevelTaskCrud().readDbChildren().size());
        // Ordering
        dbLevelTasks11 = dbLevels.get(0).getLevelTaskCrud().readDbChildren();
        dbLevelTasks12 = dbLevels.get(1).getLevelTaskCrud().readDbChildren();
        Assert.assertEquals(dbLevelTask11.getId(), dbLevelTasks11.get(1).getId());
        Assert.assertEquals(dbLevelTask12.getId(), dbLevelTasks11.get(0).getId());
        Assert.assertEquals(dbLevelTask13.getId(), dbLevelTasks11.get(2).getId());
        Assert.assertEquals(dbLevelTask114.getId(), dbLevelTasks11.get(3).getId());
        Assert.assertEquals(dbLevelTask21.getId(), dbLevelTasks12.get(0).getId());
        Assert.assertEquals(dbLevelTask22.getId(), dbLevelTasks12.get(1).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createQuestInfo() {
        DbTutorialConfig tut1 = createTutorial1();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        DbLevelTask dbLevelTask11 = dbLevel1.getLevelTaskCrud().createDbChild();
        dbLevelTask11.getI18nTitle().putString("name1");
        dbLevelTask11.getI18nDescription().putString("html1");
        dbLevelTask11.setQuestTypeEnum(QuestTypeEnum.BOSS_PVE);
        dbLevelTask11.setXp(11);
        dbLevelTask11.setMoney(12);
        dbLevelTask11.setDbTutorialConfig(tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(tut1.getId()));
        DbLevelTask dbLevelTask12 = dbLevel1.getLevelTaskCrud().createDbChild();
        dbLevelTask12.getI18nTitle().putString("name2");
        dbLevelTask12.getI18nDescription().putString("html2");
        dbLevelTask12.setXp(21);
        dbLevelTask12.setMoney(22);
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setRadarPositionHint(new Index(100, 200));
        dbConditionConfig.getI18nAdditionalDescription().putString("dbConditionConfig2");
        dbConditionConfig.setHideQuestProgress(true);
        dbLevelTask12.setDbConditionConfig(dbConditionConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        QuestInfo questInfo1 = userGuidanceService.getDbLevelCrud().readDbChild(dbLevel1.getId()).getLevelTaskCrud().readDbChildren().get(0).createQuestInfo(Locale.ENGLISH);
        QuestInfo questInfo2 = userGuidanceService.getDbLevelCrud().readDbChild(dbLevel1.getId()).getLevelTaskCrud().readDbChildren().get(1).createQuestInfo(Locale.ENGLISH);
        Assert.assertEquals(new QuestInfo("name1", "html1", null, QuestTypeEnum.BOSS_PVE, 11, 12, dbLevelTask11.getId(), QuestInfo.Type.MISSION, null, false, null), questInfo1);
        Assert.assertEquals(new QuestInfo("name2", "html2", "dbConditionConfig2", null, 21, 22, dbLevelTask12.getId(), QuestInfo.Type.QUEST, new Index(100, 200), true, null), questInfo2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }
}
