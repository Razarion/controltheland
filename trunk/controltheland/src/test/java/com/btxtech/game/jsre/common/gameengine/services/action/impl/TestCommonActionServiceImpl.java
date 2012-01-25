package com.btxtech.game.jsre.common.gameengine.services.action.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.territory.AbstractTerritoryService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestServices;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 22.10.2011
 * Time: 13:38:22
 */
public class TestCommonActionServiceImpl extends AbstractServiceTest {
    @Test
    @DirtiesContext
    public void defense() throws Exception {
        configureRealGame();

        final List<BaseCommand> commands = new ArrayList<BaseCommand>();
        final TestServices testServices = new TestServices();

        SimpleBase simpleBase1 = new SimpleBase(1);
        SyncBaseItem intruder = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(1000, 1000), new Id(1, 0, 0), simpleBase1);

        SimpleBase simpleBase2 = new SimpleBase(2);
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1100, 1100), new Id(2, 0, 0), simpleBase2);
        SyncBaseItem defender2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1100, 1100), new Id(3, 0, 0), simpleBase2);

        AbstractTerritoryService territoryServiceMock = EasyMock.createStrictMock(AbstractTerritoryService.class);
        EasyMock.expect(territoryServiceMock.isAllowed(EasyMock.<Index>anyObject(), EasyMock.eq(defender))).andReturn(true).times(3);
        testServices.setTerritoryService(territoryServiceMock);

        ItemService itemServiceMock = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(itemServiceMock.getFirstEnemyItemInRange(defender)).andReturn(intruder);
        EasyMock.expect(itemServiceMock.getFirstEnemyItemInRange(defender)).andReturn(null).times(2);
        testServices.setItemService(itemServiceMock);

        CommonActionServiceImpl actionService = new CommonActionServiceImpl() {
            @Override
            protected void executeCommand(SyncBaseItem syncItem, BaseCommand baseCommand) throws ItemLimitExceededException, HouseSpaceExceededException, ItemDoesNotExistException, NoSuchItemTypeException, InsufficientFundsException, NotYourBaseException {
                commands.add(baseCommand);
            }

            @Override
            protected Services getServices() {
                return testServices;
            }

            @Override
            public void syncItemActivated(SyncTickItem syncTickItem) {
            }
        };

        EasyMock.replay(territoryServiceMock, itemServiceMock);

        actionService.interactionGuardingItems(intruder);
        Assert.assertTrue(commands.isEmpty());

        actionService.addGuardingBaseItem(defender);
        assertAttackCommand(commands, intruder, defender);
        commands.clear();

        actionService.interactionGuardingItems(intruder);
        Assert.assertTrue(commands.isEmpty());        

        actionService.addGuardingBaseItem(defender);
        Assert.assertTrue(commands.isEmpty());

        actionService.interactionGuardingItems(intruder);
        assertAttackCommand(commands, intruder, defender);
        commands.clear();        

        actionService.removeGuardingBaseItem(defender);
        Assert.assertTrue(commands.isEmpty());
        
        actionService.interactionGuardingItems(intruder);
        Assert.assertTrue(commands.isEmpty());

        actionService.addGuardingBaseItem(intruder);
        Assert.assertTrue(commands.isEmpty());

        actionService.interactionGuardingItems(defender);
        Assert.assertTrue(commands.isEmpty());

        actionService.addGuardingBaseItem(defender);
        Assert.assertTrue(commands.isEmpty());

        actionService.interactionGuardingItems(defender);
        Assert.assertTrue(commands.isEmpty());

        actionService.interactionGuardingItems(defender2);
        Assert.assertTrue(commands.isEmpty());

        EasyMock.verify(territoryServiceMock, itemServiceMock);
    }

    private void assertAttackCommand(List<BaseCommand> commands, SyncBaseItem intruder, SyncBaseItem defender) {
        Assert.assertEquals(1, commands.size());
        Assert.assertTrue(commands.get(0) instanceof AttackCommand);
        AttackCommand attackCommand = (AttackCommand) commands.get(0);
        Assert.assertEquals(defender.getId(), attackCommand.getId());
        Assert.assertEquals(intruder.getId(), attackCommand.getTarget());
        Assert.assertFalse(attackCommand.isFollowTarget());
    }
}
