package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.PlanetSystemService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 22.04.13
 * Time: 15:23
 */
public class TestItemDeadEndProtection extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    private ItemTypeService itemTypeServiceMock;
    @Autowired
    private ServerGlobalServices serverGlobalServices;
    @Autowired
    private PlanetSystemService planetSystemService;
    private SyncBaseItem builder;
    private SyncBaseItem factory;
    private SyncBaseItem jeep;
    private SyncBaseItem harvester;
    private SimpleBase simpleBase;

    @Before
    public void init() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        simpleBase = getOrCreateBase();
        builder = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(1, 0), serverGlobalServices, planetSystemService.getServerPlanetServices(), simpleBase);
        factory = createFactorySyncBaseItem(TEST_FACTORY_ITEM_ID, new Index(100, 100), new Id(2, 0), serverGlobalServices, planetSystemService.getServerPlanetServices(), simpleBase);
        jeep = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(3, 0), serverGlobalServices, planetSystemService.getServerPlanetServices(), simpleBase);
        harvester = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(100, 100), new Id(4, 0), serverGlobalServices, planetSystemService.getServerPlanetServices(), simpleBase);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Make a specific order in the item-type-list. This makes testing easier
        List<ItemType> itemTypeList = new ArrayList<>();
        itemTypeList.add(builder.getBaseItemType());
        itemTypeList.add(factory.getBaseItemType());
        itemTypeList.add(jeep.getBaseItemType());
        itemTypeList.add(harvester.getBaseItemType());
        itemTypeList.add(serverItemTypeService.getItemType(TEST_RESOURCE_ITEM_ID));
        itemTypeServiceMock = EasyMock.createNiceMock(ItemTypeService.class);
        EasyMock.expect(itemTypeServiceMock.getItemTypes()).andReturn(itemTypeList).anyTimes();
        EasyMock.replay(itemTypeServiceMock);
    }

    public DeadEndProtection setupDeadEnd(final ItemService itemServiceMock, DeadEndListener deadEndListenerMock) {
        DeadEndProtection deadEndProtection = new DeadEndProtection() {
            @Override
            protected ItemService getItemService() {
                return itemServiceMock;
            }

            @Override
            protected ItemTypeService getItemTypeService() {
                return itemTypeServiceMock;
            }

            @Override
            protected SimpleBase getMyBase() {
                return simpleBase;
            }

            @Override
            protected boolean isBaseDead() {
                return false;
            }

            @Override
            protected int getMyMoney() {
                return 100;
            }

            @Override
            protected boolean isSuppressed() {
                return false;
            }
        };
        deadEndProtection.setDeadEndListener(deadEndListenerMock);
        deadEndProtection.start();
        return deadEndProtection;
    }

    @Test
    @DirtiesContext
    public void startOk1() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        setupDeadEnd(itemServiceMock, deadEndListenerMock);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void startOk2() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.singletonList(factory));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        setupDeadEnd(itemServiceMock, deadEndListenerMock);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void startActivation() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        setupDeadEnd(itemServiceMock, deadEndListenerMock);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void startActivation2() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(builder);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void stopNotActive() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.stop();

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void stopActive() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        deadEndListenerMock.revokeItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.stop();

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activation1() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(jeep);
        deadEndProtection.onSyncItemLost(builder);
        deadEndProtection.onSyncItemLost(builder);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activation2() throws Exception {
        AbstractItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(jeep);
        deadEndProtection.onSyncItemLost(factory);
        deadEndProtection.onSyncItemLost(factory);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activation3() throws Exception {
        AbstractItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(jeep);
        deadEndProtection.onSyncItemLost(factory);
        deadEndProtection.onSyncItemLost(factory);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void noActivationHasBuilder() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(jeep);
        deadEndProtection.onSyncItemLost(builder);
        deadEndProtection.onSyncItemLost(factory);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void noActivationHasFactory() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.singletonList(factory));

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.singletonList(factory));
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(jeep);
        deadEndProtection.onSyncItemLost(builder);
        deadEndProtection.onSyncItemLost(factory);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activateAndRevokeBuilder() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.singletonList(factory));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        deadEndListenerMock.revokeItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(jeep);
        deadEndProtection.onSyncItemLost(builder);
        deadEndProtection.onSyncItemLost(factory);
        deadEndProtection.onSyncItemCreated(builder);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activateAndRevokeFactory() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.singletonList(factory));

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        deadEndListenerMock.revokeItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(harvester);
        deadEndProtection.onSyncItemLost(factory);
        deadEndProtection.onSyncItemLost(builder);
        deadEndProtection.onSyncItemCreated(factory);

        EasyMock.verify(deadEndListenerMock);
    }


    @Test
    @DirtiesContext
    public void multiple() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_FACTORY_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateItemDeadEnd();
        deadEndListenerMock.revokeItemDeadEnd();
        deadEndListenerMock.activateItemDeadEnd();
        deadEndListenerMock.revokeItemDeadEnd();
        deadEndListenerMock.activateItemDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock);
        deadEndProtection.onSyncItemLost(harvester);
        deadEndProtection.onSyncItemCreated(jeep);
        deadEndProtection.onSyncItemLost(jeep);
        deadEndProtection.onSyncItemLost(factory);
        deadEndProtection.onSyncItemCreated(jeep);
        deadEndProtection.onSyncItemLost(builder);
        deadEndProtection.onSyncItemCreated(factory);
        deadEndProtection.onSyncItemCreated(jeep);
        deadEndProtection.onSyncItemCreated(jeep);
        deadEndProtection.onSyncItemLost(jeep);
        deadEndProtection.onSyncItemLost(harvester);
        deadEndProtection.onSyncItemCreated(jeep);
        deadEndProtection.onSyncItemLost(factory);
        deadEndProtection.onSyncItemCreated(builder);
        deadEndProtection.onSyncItemCreated(jeep);
        deadEndProtection.onSyncItemLost(builder);

        EasyMock.verify(deadEndListenerMock);
    }
}
