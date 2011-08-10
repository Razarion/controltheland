package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.impl.BotItemContainer;
import com.btxtech.game.services.bot.impl.BotSyncBaseItem;
import com.btxtech.game.services.bot.impl.IntruderHandler;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

/**
 * User: beat
 * Date: 09.08.2011
 * Time: 20:40:47
 */
public class TestIntruderHandler extends AbstractServiceTest {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private BaseService baseService;

    @Test
    @DirtiesContext
    public void noIntruders() throws Exception {
        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region, true)).andReturn(Collections.<SyncBaseItem>emptyList());

        IntruderHandler intruderHandler = (IntruderHandler) applicationContext.getBean("intruderHandler");
        setPrivateField(IntruderHandler.class, intruderHandler, "itemService", mockItemService);
        intruderHandler.init(mockBotItemContainer, region);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
    }

    @Test
    @DirtiesContext
    public void oneIntrudersNoDefender() throws Exception {
        configureMinimalGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        EasyMock.expect(mockBotItemContainer.getFirstIdleAttacker(intruder)).andReturn(null).times(3);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region, true)).andReturn(Collections.<SyncBaseItem>singletonList(intruder)).times(3);

        IntruderHandler intruderHandler = (IntruderHandler) applicationContext.getBean("intruderHandler");
        setPrivateField(IntruderHandler.class, intruderHandler, "itemService", mockItemService);
        intruderHandler.init(mockBotItemContainer, region);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
    }

    @Test
    @DirtiesContext
    public void oneIntruders() throws Exception {
        configureMinimalGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-2, -2, 0));

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(defender, intruder, true);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, mockActionService);
        EasyMock.expect(mockBotItemContainer.getFirstIdleAttacker(intruder)).andReturn(defenderBotItem);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region, true)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        IntruderHandler intruderHandler = (IntruderHandler) applicationContext.getBean("intruderHandler");
        setPrivateField(IntruderHandler.class, intruderHandler, "itemService", mockItemService);
        intruderHandler.init(mockBotItemContainer, region);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndGone() throws Exception {
        configureMinimalGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-2, -2, 0));

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(defender, intruder, true);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, mockActionService);
        EasyMock.expect(mockBotItemContainer.getFirstIdleAttacker(intruder)).andReturn(defenderBotItem);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region, true)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region, true)).andReturn(Collections.<SyncBaseItem>emptyList());

        IntruderHandler intruderHandler = (IntruderHandler) applicationContext.getBean("intruderHandler");
        setPrivateField(IntruderHandler.class, intruderHandler, "itemService", mockItemService);
        intruderHandler.init(mockBotItemContainer, region);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndAttackerDies() throws Exception {
        configureMinimalGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-2, -2, 0));

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(defender, intruder, true);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, mockActionService);
        EasyMock.expect(mockBotItemContainer.getFirstIdleAttacker(intruder)).andReturn(defenderBotItem);
        EasyMock.expect(mockBotItemContainer.getFirstIdleAttacker(intruder)).andReturn(null);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region, true)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region, true)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        IntruderHandler intruderHandler = (IntruderHandler) applicationContext.getBean("intruderHandler");
        setPrivateField(IntruderHandler.class, intruderHandler, "itemService", mockItemService);
        intruderHandler.init(mockBotItemContainer, region);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        defender.setHealth(0);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }
}
