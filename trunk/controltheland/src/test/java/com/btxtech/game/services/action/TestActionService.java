package com.btxtech.game.services.action;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.collision.CircleFormation;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 04.04.2011
 * Time: 22:57:38
 */
public class TestActionService extends AbstractServiceTest {
    @Autowired
    private MovableService movableService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CollisionService collisionService;


    @Test
    @DirtiesContext
    public void testLogoutDuringBuild() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(target, new Index(500, 100), TEST_FACTORY_ITEM_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForActionServiceDone();
        Assert.assertEquals(2, movableService.getAllSyncInfo().size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLogoutDuringBuildRegUser() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(target, new Index(500, 100), TEST_FACTORY_ITEM_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForActionServiceDone();
        Assert.assertEquals(2, movableService.getAllSyncInfo().size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testActionServiceUtilAttackSingle() throws Exception {
        // Setup
        configureComplexGame();
        Id actor1Id = new Id(1, 1, 1);
        Id targetId = new Id(2, 2, 2);
        SyncBaseItem target = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(2000, 2000), targetId);
        SyncBaseItem actor1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), actor1Id);

        List<CircleFormation.CircleFormationItem> itemsIn = new ArrayList<CircleFormation.CircleFormationItem>();
        itemsIn.add(new CircleFormation.CircleFormationItem(actor1, 100));

        List<CircleFormation.CircleFormationItem> itemsOut = new ArrayList<CircleFormation.CircleFormationItem>();
        itemsOut.add(new CircleFormation.CircleFormationItem(actor1, 100, new Index(100, 100), true));

        ItemService itemServiceMock = EasyMock.createNiceMock(ItemService.class);
        EasyMock.expect(itemServiceMock.getItem(actor1Id)).andReturn(actor1);
        EasyMock.expect(itemServiceMock.getItem(targetId)).andReturn(target);
        EasyMock.replay(itemServiceMock);

        CollisionService collisionServiceMock = EasyMock.createNiceMock(CollisionService.class);
        EasyMock.expect(collisionServiceMock.setupDestinationHints(target, itemsIn)).andReturn(itemsOut);
        EasyMock.replay(collisionServiceMock);

        // Run test
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        baseCommands.add(createAttackCommand(actor1Id, targetId));
        ActionServiceUtil.addDestinationHintToCommands(baseCommands, collisionServiceMock, itemServiceMock);

        // Verify
        Assert.assertEquals(1, baseCommands.size());
        Assert.assertEquals(new Index(100, 100), ((AttackCommand) baseCommands.get(0)).getDestinationHint());
    }

    @Test
    @DirtiesContext
    public void testActionServiceUtilAttackSingle_Move() throws Exception {
        // Setup
        configureComplexGame();
        Id actor1Id = new Id(1, 1, 1);
        Id targetId = new Id(2, 2, 2);
        SyncBaseItem target = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(2000, 2000), targetId);
        SyncBaseItem actor1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), actor1Id);

        List<CircleFormation.CircleFormationItem> itemsIn = new ArrayList<CircleFormation.CircleFormationItem>();
        itemsIn.add(new CircleFormation.CircleFormationItem(actor1, 100));

        List<CircleFormation.CircleFormationItem> itemsOut = new ArrayList<CircleFormation.CircleFormationItem>();
        itemsOut.add(new CircleFormation.CircleFormationItem(actor1, 100, new Index(100, 100), false));

        ItemService itemServiceMock = EasyMock.createNiceMock(ItemService.class);
        EasyMock.expect(itemServiceMock.getItem(actor1Id)).andReturn(actor1);
        EasyMock.expect(itemServiceMock.getItem(targetId)).andReturn(target);
        EasyMock.replay(itemServiceMock);

        CollisionService collisionServiceMock = EasyMock.createNiceMock(CollisionService.class);
        EasyMock.expect(collisionServiceMock.setupDestinationHints(target, itemsIn)).andReturn(itemsOut);
        EasyMock.replay(collisionServiceMock);

        // Run test
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        baseCommands.add(createAttackCommand(actor1Id, targetId));
        ActionServiceUtil.addDestinationHintToCommands(baseCommands, collisionServiceMock, itemServiceMock);

        // Verify
        Assert.assertEquals(1, baseCommands.size());
        Assert.assertEquals(new Index(100, 100), ((MoveCommand) baseCommands.get(0)).getDestination());
    }

    @Test
    @DirtiesContext
    public void testActionServiceUtilCollectSingle() throws Exception {
        // Setup
        configureComplexGame();
        Id actor1Id = new Id(1, 1, 1);
        Id targetId = new Id(2, 2, 2);
        SyncResourceItem target = createSyncResourceItem(TEST_RESOURCE_ITEM_ID, new Index(2000, 2000), targetId);
        SyncBaseItem actor1 = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(1000, 1000), actor1Id);

        List<CircleFormation.CircleFormationItem> itemsIn = new ArrayList<CircleFormation.CircleFormationItem>();
        itemsIn.add(new CircleFormation.CircleFormationItem(actor1, 100));

        List<CircleFormation.CircleFormationItem> itemsOut = new ArrayList<CircleFormation.CircleFormationItem>();
        itemsOut.add(new CircleFormation.CircleFormationItem(actor1, 100, new Index(100, 100), true));

        ItemService itemServiceMock = EasyMock.createNiceMock(ItemService.class);
        EasyMock.expect(itemServiceMock.getItem(actor1Id)).andReturn(actor1);
        EasyMock.expect(itemServiceMock.getItem(targetId)).andReturn(target);
        EasyMock.replay(itemServiceMock);

        CollisionService collisionServiceMock = EasyMock.createNiceMock(CollisionService.class);
        EasyMock.expect(collisionServiceMock.setupDestinationHints(target, itemsIn)).andReturn(itemsOut);
        EasyMock.replay(collisionServiceMock);

        // Run test
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        baseCommands.add(createMoneyCollectCommand(actor1Id, targetId));
        ActionServiceUtil.addDestinationHintToCommands(baseCommands, collisionServiceMock, itemServiceMock);

        // Verify
        Assert.assertEquals(1, baseCommands.size());
        Assert.assertEquals(new Index(100, 100), ((MoneyCollectCommand) baseCommands.get(0)).getDestinationHint());
    }

    @Test
    @DirtiesContext
    public void testActionServiceUtilCollectSingle_Move() throws Exception {
        // Setup
        configureComplexGame();
        Id actor1Id = new Id(1, 1, 1);
        Id targetId = new Id(2, 2, 2);
        SyncResourceItem target = createSyncResourceItem(TEST_RESOURCE_ITEM_ID, new Index(2000, 2000), targetId);
        SyncBaseItem actor1 = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(1000, 1000), actor1Id);

        List<CircleFormation.CircleFormationItem> itemsIn = new ArrayList<CircleFormation.CircleFormationItem>();
        itemsIn.add(new CircleFormation.CircleFormationItem(actor1, 100));

        List<CircleFormation.CircleFormationItem> itemsOut = new ArrayList<CircleFormation.CircleFormationItem>();
        itemsOut.add(new CircleFormation.CircleFormationItem(actor1, 100, new Index(100, 100), false));

        ItemService itemServiceMock = EasyMock.createNiceMock(ItemService.class);
        EasyMock.expect(itemServiceMock.getItem(actor1Id)).andReturn(actor1);
        EasyMock.expect(itemServiceMock.getItem(targetId)).andReturn(target);
        EasyMock.replay(itemServiceMock);

        CollisionService collisionServiceMock = EasyMock.createNiceMock(CollisionService.class);
        EasyMock.expect(collisionServiceMock.setupDestinationHints(target, itemsIn)).andReturn(itemsOut);
        EasyMock.replay(collisionServiceMock);

        // Run test
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        baseCommands.add(createMoneyCollectCommand(actor1Id, targetId));
        ActionServiceUtil.addDestinationHintToCommands(baseCommands, collisionServiceMock, itemServiceMock);

        // Verify
        Assert.assertEquals(1, baseCommands.size());
        Assert.assertEquals(new Index(100, 100), ((MoveCommand) baseCommands.get(0)).getDestination());
    }

    @Test
    @DirtiesContext
    public void testAttackWithDestination() throws Exception {
        configureMinimalGame();

        // Target
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Attacker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommands(getAllSynItemId(TEST_ATTACK_ITEM_ID), target);
        waitForActionServiceDone();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
