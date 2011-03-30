package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 29.03.2011
 * Time: 13:55:41
 */
public class TestTutorialConditions extends BaseTestService {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MovableService movableService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testContainedIn() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSimulationLevel dbSimulationLevel = setupContainedInSimulationLevel(true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        userGuidanceService.promote(userService.getUserState(), dbSimulationLevel.getId());
        SimulationInfo simulationInfo = (SimulationInfo) movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Start simulation
        ConditionServiceListener<Object> conditionServiceListener = EasyMock.createStrictMock(ConditionServiceListener.class);
        conditionServiceListener.conditionPassed(null);
        EasyMock.replay(conditionServiceListener);
        SimulationConditionServiceImpl.getInstance().activateCondition(simulationInfo.getTutorialConfig().getTasks().get(0).getStepConfigs().get(0).getConditionConfig(), null);
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(conditionServiceListener);
        SimulationConditionServiceImpl.getInstance().onContainedInChanged(true);
        EasyMock.verify(conditionServiceListener);
    }

    @Test
    @DirtiesContext
    public void testContainedOut() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSimulationLevel dbSimulationLevel = setupContainedInSimulationLevel(false);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        userGuidanceService.promote(userService.getUserState(), dbSimulationLevel.getId());
        SimulationInfo simulationInfo = (SimulationInfo) movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Start simulation
        ConditionServiceListener<Object> conditionServiceListener = EasyMock.createStrictMock(ConditionServiceListener.class);
        conditionServiceListener.conditionPassed(null);
        EasyMock.replay(conditionServiceListener);
        SimulationConditionServiceImpl.getInstance().activateCondition(simulationInfo.getTutorialConfig().getTasks().get(0).getStepConfigs().get(0).getConditionConfig(), null);
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(conditionServiceListener);
        SimulationConditionServiceImpl.getInstance().onContainedInChanged(false);
        EasyMock.verify(conditionServiceListener);
    }

    @Test
    @DirtiesContext
    public void testContainedIn_neg() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSimulationLevel dbSimulationLevel = setupContainedInSimulationLevel(true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        userGuidanceService.promote(userService.getUserState(), dbSimulationLevel.getId());
        SimulationInfo simulationInfo = (SimulationInfo) movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Start simulation
        ConditionServiceListener<Object> conditionServiceListener = EasyMock.createStrictMock(ConditionServiceListener.class);
        EasyMock.replay(conditionServiceListener);
        SimulationConditionServiceImpl.getInstance().activateCondition(simulationInfo.getTutorialConfig().getTasks().get(0).getStepConfigs().get(0).getConditionConfig(), null);
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(conditionServiceListener);
        SimulationConditionServiceImpl.getInstance().onContainedInChanged(false);
        EasyMock.verify(conditionServiceListener);
    }

    @Test
    @DirtiesContext
    public void testItemTypePositionComparison() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSimulationLevel dbSimulationLevel = setupItemTypePositionLevel();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        userGuidanceService.promote(userService.getUserState(), dbSimulationLevel.getId());
        SimulationInfo simulationInfo = (SimulationInfo) movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Start simulation
        ConditionServiceListener<Object> conditionServiceListener = EasyMock.createStrictMock(ConditionServiceListener.class);
        conditionServiceListener.conditionPassed(null);
        EasyMock.replay(conditionServiceListener);
        //
        SyncBaseItem syncItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID);
        //
        SimulationConditionServiceImpl.getInstance().activateCondition(simulationInfo.getTutorialConfig().getTasks().get(0).getStepConfigs().get(0).getConditionConfig(), null);
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(conditionServiceListener);        
        syncItem.setPosition(new Index(400, 400));
        SimulationConditionServiceImpl.getInstance().onSyncItemDeactivated(syncItem);
        EasyMock.verify(conditionServiceListener);
    }

    @Test
    @DirtiesContext
    public void testItemTypePositionComparison_NegPos() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSimulationLevel dbSimulationLevel = setupItemTypePositionLevel();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        userGuidanceService.promote(userService.getUserState(), dbSimulationLevel.getId());
        SimulationInfo simulationInfo = (SimulationInfo) movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Start simulation
        ConditionServiceListener<Object> conditionServiceListener = EasyMock.createStrictMock(ConditionServiceListener.class);
        EasyMock.replay(conditionServiceListener);
        //
        SyncBaseItem syncItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID);
        //
        SimulationConditionServiceImpl.getInstance().activateCondition(simulationInfo.getTutorialConfig().getTasks().get(0).getStepConfigs().get(0).getConditionConfig(), null);
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(conditionServiceListener);
        syncItem.setPosition(new Index(1000, 1000));
        SimulationConditionServiceImpl.getInstance().onSyncItemDeactivated(syncItem);
        EasyMock.verify(conditionServiceListener);
    }

    @Test
    @DirtiesContext
    public void testItemTypePositionComparison_NegItemType() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSimulationLevel dbSimulationLevel = setupItemTypePositionLevel();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        userGuidanceService.promote(userService.getUserState(), dbSimulationLevel.getId());
        SimulationInfo simulationInfo = (SimulationInfo) movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Start simulation
        ConditionServiceListener<Object> conditionServiceListener = EasyMock.createStrictMock(ConditionServiceListener.class);
        EasyMock.replay(conditionServiceListener);
        //
        SyncBaseItem syncItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID);
        //
        SimulationConditionServiceImpl.getInstance().activateCondition(simulationInfo.getTutorialConfig().getTasks().get(0).getStepConfigs().get(0).getConditionConfig(), null);
        SimulationConditionServiceImpl.getInstance().setConditionServiceListener(conditionServiceListener);
        syncItem.setPosition(new Index(400, 400));
        SimulationConditionServiceImpl.getInstance().onSyncItemDeactivated(syncItem);
        EasyMock.verify(conditionServiceListener);
    }

}
