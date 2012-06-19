package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.item.ItemService;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 18.06.12
 * Time: 23:46
 */
public class TestPositionTaken extends AbstractServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ServerServices serverServices;

    @Test
    @DirtiesContext
    public void testMultipleAttackNoOverlapping() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(5000, 5000));
        waitForActionServiceDone();
        SyncBaseItem targetItem = (SyncBaseItem) itemService.getItem(target);
        targetItem.setHealth(100000);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(400, 400), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();

        for (int i = 0; i < 10; i++) {
            sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
            waitForActionServiceDone();
        }

        List<Id> allIds = getAllSynItemId(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(10, allIds.size());

        int range = ((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID)).getWeaponType().getRange();
        List<AttackFormationItem> attackFormationItemList = new ArrayList<>();
        for (Id id : allIds) {
            attackFormationItemList.add(new AttackFormationItem((SyncBaseItem) itemService.getItem(id), range));
        }
        attackFormationItemList = collisionService.setupDestinationHints(targetItem, attackFormationItemList);
        System.out.println("-------------------------------START-------------------------------");
        ConnectionService mockConnectionService = EasyMock.createStrictMock(ConnectionService.class);
        EasyMock.expect(mockConnectionService.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        EasyMock.replay(mockConnectionService);
        setPrivateField(ServerServices.class, serverServices, "connectionService", mockConnectionService);
        for (AttackFormationItem attackFormationItem : attackFormationItemList) {
            Assert.assertTrue(attackFormationItem.isInRange());
            actionService.attack(attackFormationItem.getSyncBaseItem(),
                    targetItem,
                    attackFormationItem.getDestinationHint(),
                    attackFormationItem.getDestinationAngel(),
                    true);
        }
        waitForActionServiceDone();
        // Verify position
        for (AttackFormationItem attackFormationItem : attackFormationItemList) {
            Assert.assertEquals("Position do not match " + attackFormationItem.getSyncBaseItem(), attackFormationItem.getDestinationHint(), attackFormationItem.getSyncBaseItem().getSyncItemArea().getPosition());
            Assert.assertEquals("Angel do not match " + attackFormationItem.getSyncBaseItem(), attackFormationItem.getDestinationAngel(), attackFormationItem.getSyncBaseItem().getSyncItemArea().getAngel(), 0.0001);
        }

        EasyMock.verify(mockConnectionService);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
