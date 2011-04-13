package com.btxtech.game.services.utg;

import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.utg.condition.DbAbstractComparisonConfig;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: 07.03.2011
 * Time: 17:15:43
 */
public class TestLevel extends BaseTestService {
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void simple() {
        CrudRootServiceHelper<DbAbstractLevel> crudServiceHelper = userGuidanceService.getDbLevelCrudServiceHelper();
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);

        Assert.assertEquals(2, crudServiceHelper.readDbChildren().size());
    }

    @Test
    @DirtiesContext
    public void simpleMoveLevels() {
        CrudRootServiceHelper<DbAbstractLevel> crudServiceHelper = userGuidanceService.getDbLevelCrudServiceHelper();
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        Assert.assertEquals(2, crudServiceHelper.readDbChildren().size());

        List<DbAbstractLevel> dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelper.readDbChildren();
        Assert.assertTrue(dbAbstractLevels.get(0) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(1) instanceof DbRealGameLevel);
        Collections.swap(dbAbstractLevels, 0, 1);
        crudServiceHelper.updateDbChildren(dbAbstractLevels);

        dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelper.readDbChildren();
        Assert.assertTrue(dbAbstractLevels.get(0) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(1) instanceof DbSimulationLevel);

    }

    @Test
    @DirtiesContext
    public void simpleMoveLevels2() {
        CrudRootServiceHelper<DbAbstractLevel> crudServiceHelper = userGuidanceService.getDbLevelCrudServiceHelper();
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        Assert.assertEquals(8, crudServiceHelper.readDbChildren().size());

        List<DbAbstractLevel> dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelper.readDbChildren();
        Assert.assertTrue(dbAbstractLevels.get(0) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(1) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(2) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(3) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(4) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(5) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(6) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(7) instanceof DbRealGameLevel);
        Collections.swap(dbAbstractLevels, 3, 4);
        crudServiceHelper.updateDbChildren(dbAbstractLevels);

        dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelper.readDbChildren();
        Assert.assertTrue(dbAbstractLevels.get(0) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(1) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(2) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(3) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(4) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(5) instanceof DbRealGameLevel);
        Assert.assertTrue(dbAbstractLevels.get(6) instanceof DbSimulationLevel);
        Assert.assertTrue(dbAbstractLevels.get(7) instanceof DbRealGameLevel);
    }

    @Test
    @DirtiesContext
    public void copyAbstractLevel() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbRealGameLevel copy = (DbRealGameLevel) userGuidanceService.copyDbAbstractLevel(TEST_LEVEL_2_REAL_ID);
        userGuidanceService.activateLevels();

        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();

        DbRealGameLevel original = (DbRealGameLevel) userGuidanceService.getDbLevelCrudServiceHelper().readDbChild(TEST_LEVEL_2_REAL_ID);
        copy = (DbRealGameLevel) userGuidanceService.getDbLevelCrudServiceHelper().readDbChild(copy.getId());

        // Assert level attributes
        Assert.assertEquals(original.getDeltaMoney(), copy.getDeltaMoney());
        Assert.assertEquals(original.getDeltaXp(), copy.getDeltaXp());
        Assert.assertEquals(original.getDisplayType(), copy.getDisplayType());
        Assert.assertEquals(original.getHouseSpace(), copy.getHouseSpace());
        Assert.assertEquals(original.getHtml(), copy.getHtml());
        Assert.assertEquals(original.getHouseSpace(), copy.getHouseSpace());
        Assert.assertEquals(original.getInternalDescription(), copy.getInternalDescription());
        Assert.assertEquals(original.getItemSellFactor(), copy.getItemSellFactor(), 0.001);
        Assert.assertEquals(original.getMaxMoney(), copy.getMaxMoney());
        Assert.assertEquals(original.getMaxXp(), copy.getMaxXp());
        Assert.assertFalse(original.getName().equals(copy.getName()));
        Assert.assertEquals(original.getDeltaMoney(), copy.getDeltaMoney());
        Assert.assertEquals(original.getStartItemFreeRange(), copy.getStartItemFreeRange());
        Assert.assertEquals(original.getStartItemType(), copy.getStartItemType());
        Assert.assertEquals(original.getStartRectangle(), copy.getStartRectangle());

        Assert.assertFalse(original.getId().intValue() == copy.getId().intValue());
        Assert.assertTrue(copy.getOrderIndex() == userGuidanceService.getDbLevels().get(userGuidanceService.getDbLevels().size() - 1).getOrderIndex());

        // Assert condition
        DbConditionConfig originalDbConditionConfig = original.getDbConditionConfig();
        DbConditionConfig copyDbConditionConfig = copy.getDbConditionConfig();
        Assert.assertFalse(originalDbConditionConfig.getId().intValue() == copyDbConditionConfig.getId().intValue());
        Assert.assertEquals(originalDbConditionConfig.getConditionTrigger(), copyDbConditionConfig.getConditionTrigger());
        DbAbstractComparisonConfig originalDbAbstractComparisonConfig = originalDbConditionConfig.getDbAbstractComparisonConfig();
        DbAbstractComparisonConfig copyDbAbstractComparisonConfig = copyDbConditionConfig.getDbAbstractComparisonConfig();
        Assert.assertFalse(originalDbAbstractComparisonConfig.getId().intValue() == copyDbAbstractComparisonConfig.getId().intValue());
        Assert.assertEquals(originalDbAbstractComparisonConfig.getExcludedDbTerritory(), copyDbAbstractComparisonConfig.getExcludedDbTerritory());
        Assert.assertEquals(originalDbAbstractComparisonConfig.getClass(), copyDbAbstractComparisonConfig.getClass());
        // TODO check subclasses of DbAbstractComparisonConfig

        // Assert
        Set<DbItemTypeLimitation> originalDbItemTypeLimitations = original.getItemTypeLimitation();
        Collection<DbItemTypeLimitation> copyDbItemTypeLimitations = new ArrayList<DbItemTypeLimitation>(copy.getItemTypeLimitation());
        Assert.assertEquals(originalDbItemTypeLimitations.size(), copyDbItemTypeLimitations.size());
        for (DbItemTypeLimitation originalDbItemTypeLimitation : originalDbItemTypeLimitations) {
            findAndRemoveDbItemTypeLimitation(copyDbItemTypeLimitations, originalDbItemTypeLimitation);
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void findAndRemoveDbItemTypeLimitation(Collection<DbItemTypeLimitation> toBeFoundIn, DbItemTypeLimitation dbItemTypeLimitation) {
        for (Iterator<DbItemTypeLimitation> iterator = toBeFoundIn.iterator(); iterator.hasNext();) {
            DbItemTypeLimitation toBeFound = iterator.next();
            if (toBeFound.getCount() != dbItemTypeLimitation.getCount()) {
                continue;
            }
            if (toBeFound.getDbBaseItemType() == null && dbItemTypeLimitation.getDbBaseItemType() != null) {
                continue;
            }
            if (toBeFound.getDbBaseItemType() != null && dbItemTypeLimitation.getDbBaseItemType() == null) {
                continue;
            }
            if (toBeFound.getDbBaseItemType() != null && !(toBeFound.getDbBaseItemType().equals(dbItemTypeLimitation.getDbBaseItemType()))) {
                continue;
            }
            // Found but Id is not allowed to be the same
            Assert.assertFalse(toBeFound.getId().equals(dbItemTypeLimitation.getId()));
            iterator.remove();
            return;
        }
        Assert.fail("DbItemTypeLimitation can not be found: " + dbItemTypeLimitation);
    }

}
