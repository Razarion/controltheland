package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.item.itemType.DbMovableType;
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
public class TestMoneyDeadEndProtection extends AbstractServiceTest {
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

    public DeadEndProtection setupDeadEnd(final ItemService itemServiceMock, DeadEndListener deadEndListenerMock, final int startMoney) {
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
                return startMoney;
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
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester));
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void startOk2() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        setupDeadEnd(itemServiceMock, deadEndListenerMock, 5);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void startActivation() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void stopNotActive() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester));
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);
        deadEndProtection.stop();

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void stopActive() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        deadEndListenerMock.revokeMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);
        deadEndProtection.stop();

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activation1() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester));

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);
        deadEndProtection.onSyncItemLost(harvester);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activation2() throws Exception {
        AbstractItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 5);
        deadEndProtection.onMoneyChanged(3);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void noActivationHasHarvester() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester));

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 5);
        deadEndProtection.onMoneyChanged(3);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void noActivationHasHarvester2() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester));

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester));

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 5);
        deadEndProtection.onSyncItemLost(harvester);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void noActivationHasMoney() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester));

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 6);
        deadEndProtection.onMoneyChanged(4);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void noActivationHasMoney2() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester));

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 6);
        deadEndProtection.onSyncItemLost(harvester);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activateAndRevokeMoney() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        deadEndListenerMock.revokeMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);
        deadEndProtection.onMoneyChanged(3);
        deadEndProtection.onMoneyChanged(4);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void activateAndRevokeHarvester() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        deadEndListenerMock.revokeMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);
        deadEndProtection.onSyncItemCreated(harvester);
        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void multiple() throws Exception {
        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        deadEndListenerMock.revokeMoneyDeadEnd();
        deadEndListenerMock.activateMoneyDeadEnd();
        deadEndListenerMock.revokeMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 10);
        deadEndProtection.onSyncItemCreated(harvester);
        deadEndProtection.onMoneyChanged(0);
        deadEndProtection.onSyncItemLost(harvester);
        deadEndProtection.onMoneyChanged(10);
        deadEndProtection.onMoneyChanged(0);
        deadEndProtection.onSyncItemCreated(harvester);
        deadEndProtection.onSyncItemCreated(harvester);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void twoHarvesterDifferentPrice() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName("Harvester 2");
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(3);
        // DbHarvesterType
        DbHarvesterType dbHarvesterType = new DbHarvesterType();
        dbHarvesterType.setRange(100);
        dbHarvesterType.setProgress(1);
        dbBaseItemType.setDbHarvesterType(dbHarvesterType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbBaseItemType.setDbMovableType(dbMovableType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        SyncBaseItem harvester2 = createSyncBaseItem( dbBaseItemType.getId(), new Index(100, 100), new Id(4, 0), serverGlobalServices, planetSystemService.getServerPlanetServices(), simpleBase);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        deadEndListenerMock.revokeMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);
        deadEndProtection.onMoneyChanged(2);
        deadEndProtection.onMoneyChanged(3);
        deadEndProtection.onSyncItemCreated(harvester2);

        EasyMock.verify(deadEndListenerMock);
    }

    @Test
    @DirtiesContext
    public void harvesterWithPrice0() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName("Harvester 2");
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(0);
        // DbHarvesterType
        DbHarvesterType dbHarvesterType = new DbHarvesterType();
        dbHarvesterType.setRange(100);
        dbHarvesterType.setProgress(1);
        dbBaseItemType.setDbHarvesterType(dbHarvesterType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbBaseItemType.setDbMovableType(dbMovableType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        SyncBaseItem harvester2 = createSyncBaseItem( dbBaseItemType.getId(), new Index(100, 100), new Id(4, 0), serverGlobalServices, planetSystemService.getServerPlanetServices(), simpleBase);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ItemService itemServiceMock = EasyMock.createStrictMock(AbstractItemService.class);

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_START_BUILDER_ITEM_ID)).andReturn(Collections.singletonList(builder));
        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.<SyncBaseItem>emptyList());

        EasyMock.expect(itemServiceMock.getItems4BaseAndType(simpleBase, TEST_HARVESTER_ITEM_ID)).andReturn(Collections.singletonList(harvester2));

        EasyMock.replay(itemServiceMock);
        DeadEndListener deadEndListenerMock = EasyMock.createStrictMock(DeadEndListener.class);
        deadEndListenerMock.activateMoneyDeadEnd();
        deadEndListenerMock.revokeMoneyDeadEnd();
        deadEndListenerMock.activateMoneyDeadEnd();
        deadEndListenerMock.revokeMoneyDeadEnd();
        EasyMock.replay(deadEndListenerMock);

        DeadEndProtection deadEndProtection = setupDeadEnd(itemServiceMock, deadEndListenerMock, 0);
        deadEndProtection.onMoneyChanged(4);
        deadEndProtection.onMoneyChanged(0);
        deadEndProtection.onSyncItemCreated(harvester2);

        EasyMock.verify(deadEndListenerMock);
    }

}
