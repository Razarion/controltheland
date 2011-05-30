package com.btxtech.game.services;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LoadContainCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UnloadContainerCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.HouseSpacePacket;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.bot.DbBotItemCount;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBuilderType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.item.itemType.DbItemContainerType;
import com.btxtech.game.services.item.itemType.DbMovableType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.market.XpSettings;
import com.btxtech.game.services.resource.DbRegionResource;
import com.btxtech.game.services.resource.ResourceService;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.DbTerritory;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.tutorial.DbItemTypeAndPosition;
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.utg.DbItemTypeLimitation;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.DbResurrection;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbComparisonItemCount;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbContainedInComparisonConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import com.btxtech.game.services.utg.condition.DbItemTypePositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import org.easymock.EasyMock;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * User: beat
 * Date: 21.02.2011
 * Time: 20:41:45
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:AbstractServiceTest-context.xml")
abstract public class AbstractServiceTest {
    protected static final String TEST_START_BUILDER_ITEM = "TestStartBuilderItem";
    protected static int TEST_START_BUILDER_ITEM_ID = -1;
    protected static final String TEST_FACTORY_ITEM = "TestFactoryItem";
    protected static int TEST_FACTORY_ITEM_ID = -1;
    protected static final String TEST_ATTACK_ITEM = "TestAttackItem";
    protected static int TEST_ATTACK_ITEM_ID = -1;
    protected static final String TEST_CONTAINER_ITEM = "TestContainerItem";
    protected static int TEST_CONTAINER_ITEM_ID = -1;
    protected static final String TEST_SIMPLE_BUILDING = "TEST_SIMPLE_BUILDING";
    protected static int TEST_SIMPLE_BUILDING_ID = -1;
    protected static final String TEST_RESOURCE_ITEM = "TestResourceItem";
    protected static int TEST_RESOURCE_ITEM_ID = -1;
    protected static final String TEST_HARVESTER_ITEM = "TEST_HARVESTER_ITEM";
    protected static int TEST_HARVESTER_ITEM_ID = -1;
    protected static final String TEST_LEVEL_1_SIMULATED = "TEST_LEVEL_1_SIMULATED";
    protected static final String TEST_LEVEL_2_REAL = "TEST_LEVEL_2_REAL";
    protected static int TEST_LEVEL_2_REAL_ID = -1;
    protected static final String TEST_LEVEL_3_REAL = "TEST_LEVEL_3_REAL";
    protected static int TEST_LEVEL_3_REAL_ID = -1;
    protected static final String TEST_LEVEL_4_SIMULATED = "TEST_LEVEL_4_SIMULATED";
    protected static int TEST_LEVEL_4_SIMULATED_ID = -1;
    protected static final String TEST_LEVEL_5_REAL = "TEST_LEVEL_5_REAL";
    protected static int TEST_LEVEL_5_REAL_ID = -1;
    protected static final String TEST_NOOB_TERRITORY = "TEST_NOOB_TERRITORY";
    protected static int TEST_NOOB_TERRITORY_ID = -1;
    protected static final String TEST_NOOB_RESURRECTION = "NOOB_RESURRECTION";
    protected static int TEST_NOOB_RESURRECTION_ID = -1;
    // Terrain
    protected static int TERRAIN_IMAGE_4x10 = -1;
    protected static int TERRAIN_IMAGE_10x4 = -1;
    protected static int TERRAIN_IMAGE_4x4 = -1;
    protected static int TERRAIN_IMAGE_10x10 = -1;
    protected static final Rectangle COMPLEX_TERRAIN_RECT_1 = new Rectangle(0, 1300, 1000, 400);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_2 = new Rectangle(1000, 0, 400, 1000);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_3 = new Rectangle(0, 2100, 1000, 400);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_4 = new Rectangle(1300, 2200, 1000, 400);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_5 = new Rectangle(2000, 1600, 1000, 400);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_6 = new Rectangle(2000, 700, 1000, 400);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_7 = new Rectangle(1000, 2900, 400, 1000);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_8 = new Rectangle(1500, 2600, 400, 1000);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_9 = new Rectangle(2100, 2900, 400, 1000);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_10 = new Rectangle(3500, 2000, 400, 1000);
    protected static final Rectangle COMPLEX_TERRAIN_RECT_11 = new Rectangle(3600, 500, 400, 1000);
    protected static final Collection<Rectangle> COMPLEX_TERRAIN_RECTS = Arrays.asList(COMPLEX_TERRAIN_RECT_1,
            COMPLEX_TERRAIN_RECT_2,
            COMPLEX_TERRAIN_RECT_3,
            COMPLEX_TERRAIN_RECT_4,
            COMPLEX_TERRAIN_RECT_5,
            COMPLEX_TERRAIN_RECT_6,
            COMPLEX_TERRAIN_RECT_7,
            COMPLEX_TERRAIN_RECT_8,
            COMPLEX_TERRAIN_RECT_9,
            COMPLEX_TERRAIN_RECT_10,
            COMPLEX_TERRAIN_RECT_11);

    // Territories
    protected static String COMPLEX_TERRITORY = "ComplexTerritory";
    protected static int COMPLEX_TERRITORY_ID = -1;

    private HibernateTemplate hibernateTemplate;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private MovableService movableService;
    @Autowired
    private BotService botService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private ServerMarketService serverMarketServic;
    @Autowired
    private ResourceService resourceService;
    private SessionHolder sessionHolder;
    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;
    private MockHttpSession mockHttpSession;
    private SecurityContext securityContext;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    protected HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    // ------------------- Sync Items --------------------

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id, Services services) throws Exception {
        SyncBaseItem syncBaseItem = new SyncBaseItem(id, null, (BaseItemType) itemService.getItemType(itemTypeId), services, new SimpleBase(1));
        syncBaseItem.setPosition(position);
        return syncBaseItem;
    }

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id) throws Exception {
        Services services = EasyMock.createNiceMock(Services.class);
        AbstractTerrainService terrainService = EasyMock.createNiceMock(AbstractTerrainService.class);
        EasyMock.expect(services.getTerrainService()).andReturn(terrainService);
        EasyMock.replay(services);
        return createSyncBaseItem(itemTypeId, position, id, services);
    }

    protected SyncResourceItem createSyncResourceItem(int itemTypeId, Index position, Id id) throws Exception {
        Services services = EasyMock.createNiceMock(Services.class);
        AbstractTerrainService terrainService = EasyMock.createNiceMock(AbstractTerrainService.class);
        EasyMock.expect(services.getTerrainService()).andReturn(terrainService);
        EasyMock.replay(services);
        SyncResourceItem syncResourceItem = new SyncResourceItem(id, null, (ResourceType) itemService.getItemType(itemTypeId), services);
        syncResourceItem.setPosition(position);
        return syncResourceItem;
    }

    /**
     * Attention: closes the current connection!!!
     *
     * @return Simple Base
     */
    protected SimpleBase getMyBase() {
        return ((RealityInfo) movableService.getGameInfo()).getBase();
    }

    protected Id getFirstSynItemId(int itemTypeId) {
        return getFirstSynItemId(itemTypeId, null);
    }

    protected Id getFirstSynItemId(int itemTypeId, Rectangle region) {
        return getFirstSynItemId(getMyBase(), itemTypeId, region);
    }

    protected Id getFirstSynItemId(SimpleBase simpleBase, int itemTypeId) {
        return getFirstSynItemId(simpleBase, itemTypeId, null);
    }

    protected Id getFirstSynItemId(SimpleBase simpleBase, int itemTypeId, Rectangle region) {
        for (SyncItemInfo syncItemInfo : movableService.getAllSyncInfo()) {
            if (syncItemInfo.getBase() == null && syncItemInfo.getItemTypeId() == itemTypeId) {
                if (region != null) {
                    if (region.contains(syncItemInfo.getPosition())) {
                        return syncItemInfo.getId();
                    }
                } else {
                    return syncItemInfo.getId();
                }
            } else if (syncItemInfo.getBase() != null && syncItemInfo.getBase().equals(simpleBase) && syncItemInfo.getItemTypeId() == itemTypeId) {
                if (region != null) {
                    if (region.contains(syncItemInfo.getPosition())) {
                        return syncItemInfo.getId();
                    }
                } else {
                    return syncItemInfo.getId();
                }
            }
        }
        throw new IllegalStateException("No such sync item: ItemTypeID=" + itemTypeId + " simpleBase=" + simpleBase);
    }

    protected Collection<Id> getAllSynItemId(int itemTypeId) {
        return getAllSynItemId(getMyBase(), itemTypeId, null);
    }

    protected Collection<Id> getAllSynItemId(SimpleBase simpleBase, int itemTypeId, Rectangle region) {
        Collection<Id> ids = new ArrayList<Id>();
        for (SyncItemInfo syncItemInfo : movableService.getAllSyncInfo()) {
            if (syncItemInfo.getBase() == null && syncItemInfo.getItemTypeId() == itemTypeId) {
                if (region != null) {
                    if (region.contains(syncItemInfo.getPosition())) {
                        ids.add(syncItemInfo.getId());
                    }
                } else {
                    ids.add(syncItemInfo.getId());
                }
            } else if (syncItemInfo.getBase() != null && syncItemInfo.getBase().equals(simpleBase) && syncItemInfo.getItemTypeId() == itemTypeId) {
                if (region != null) {
                    if (region.contains(syncItemInfo.getPosition())) {
                        ids.add(syncItemInfo.getId());
                    }
                } else {
                    ids.add(syncItemInfo.getId());
                }
            }
        }
        if (ids.isEmpty()) {
            throw new IllegalStateException("No such sync item: ItemTypeID=" + itemTypeId + " simpleBase=" + simpleBase);
        }
        return ids;
    }


    protected void assertWholeItemTypeCount(int count) {
        Assert.assertEquals(count, itemService.getItemsCopy().size());
    }

    // ------------------- Connection --------------------

    protected void clearPackets() throws Exception {
        movableService.getSyncInfo();
    }

    protected List<Packet> getPackagesIgnoreSyncItemInfoAndClear() throws Exception {
        List<Packet> receivedPackets = new ArrayList<Packet>(movableService.getSyncInfo());
        for (Iterator<Packet> iterator = receivedPackets.iterator(); iterator.hasNext();) {
            if (iterator.next() instanceof SyncItemInfo) {
                iterator.remove();
            }

        }
        return receivedPackets;
    }

    protected void assertPackagesIgnoreSyncItemInfoAndClear(Packet... expectedPackets) throws Exception {
        List<Packet> receivedPackets = getPackagesIgnoreSyncItemInfoAndClear();

        if (expectedPackets.length != receivedPackets.size()) {
            StringBuilder expectedBuilder = new StringBuilder();
            for (Packet expectedPacket : expectedPackets) {
                expectedBuilder.append("[");
                expectedBuilder.append(expectedPacket);
                expectedBuilder.append("] ");
            }
            System.out.println("Expected: " + expectedBuilder);
            System.out.println("Received: " + receivedPackets);
            Assert.assertEquals(expectedPackets.length, receivedPackets.size());
        }

        for (Packet expectedPacket : expectedPackets) {
            int index = receivedPackets.indexOf(expectedPacket);
            if (index < 0) {
                Assert.fail("Packet was not sent: " + expectedPacket);
            }
            comparePacket(expectedPacket, receivedPackets.get(index));
        }
    }

    protected void comparePacket(Packet expectedPacket, Packet receivedPacket) {
        if (expectedPacket instanceof AccountBalancePacket) {
            AccountBalancePacket expected = (AccountBalancePacket) expectedPacket;
            AccountBalancePacket received = (AccountBalancePacket) receivedPacket;
            Assert.assertEquals(expected.getAccountBalance(), received.getAccountBalance(), 0.1);
        } else if (expectedPacket instanceof XpBalancePacket) {
            XpBalancePacket expected = (XpBalancePacket) expectedPacket;
            XpBalancePacket received = (XpBalancePacket) receivedPacket;
            Assert.assertEquals(expected.getXp(), received.getXp());
        } else if (expectedPacket instanceof LevelPacket) {
            LevelPacket expected = (LevelPacket) expectedPacket;
            LevelPacket received = (LevelPacket) receivedPacket;
            Assert.assertEquals(expected.getLevel(), received.getLevel());
        } else if (expectedPacket instanceof HouseSpacePacket) {
            HouseSpacePacket expected = (HouseSpacePacket) expectedPacket;
            HouseSpacePacket received = (HouseSpacePacket) receivedPacket;
            Assert.assertEquals(expected.getHouseSpace(), received.getHouseSpace());
        } else if (expectedPacket instanceof Message) {
            Message expected = (Message) expectedPacket;
            Message received = (Message) receivedPacket;
            Assert.assertEquals(expected.getMessage(), received.getMessage());
        } else if (expectedPacket instanceof BaseChangedPacket) {
            BaseChangedPacket expected = (BaseChangedPacket) expectedPacket;
            BaseChangedPacket received = (BaseChangedPacket) receivedPacket;
            Assert.assertEquals(expected.getType(), received.getType());
            Assert.assertEquals(expected.getBaseAttributes().getSimpleBase(), received.getBaseAttributes().getSimpleBase());
            Assert.assertEquals(expected.getBaseAttributes().getSimpleBase(), received.getBaseAttributes().getSimpleBase());
            Assert.assertEquals(expected.getBaseAttributes().getName(), received.getBaseAttributes().getName());
            Assert.assertEquals(expected.getBaseAttributes().isBot(), received.getBaseAttributes().isBot());
            Assert.assertEquals(expected.getBaseAttributes().isAbandoned(), received.getBaseAttributes().isAbandoned());
        } else {
            Assert.fail("Unhandled packet: " + expectedPacket);
        }
    }

    // ------------------- Action Service --------------------

    protected void waitForActionServiceDone() throws TimeoutException, InterruptedException {
        long maxTime = System.currentTimeMillis() + 100000;
        while (actionService.isBusy()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    protected void sendMoveCommand(Id movable, Index destination) {
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setDestination(destination);
        moveCommand.setId(movable);
        moveCommand.setTimeStamp();
        baseCommands.add(moveCommand);
        movableService.sendCommands(baseCommands);
    }

    protected void sendBuildCommand(Id builderId, Index toBeBuiltPosition, int toBeBuiltId) {
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        BuilderCommand builderCommand = new BuilderCommand();
        builderCommand.setPositionToBeBuilt(toBeBuiltPosition);
        builderCommand.setId(builderId);
        builderCommand.setToBeBuilt(toBeBuiltId);
        builderCommand.setTimeStamp();
        baseCommands.add(builderCommand);
        movableService.sendCommands(baseCommands);
    }

    protected void sendFactoryCommand(Id factoryId, int toBeBuiltId) {
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        FactoryCommand factoryCommand = new FactoryCommand();
        factoryCommand.setId(factoryId);
        factoryCommand.setToBeBuilt(toBeBuiltId);
        factoryCommand.setTimeStamp();
        baseCommands.add(factoryCommand);
        movableService.sendCommands(baseCommands);
    }

    protected void sendAttackCommand(Id actorId, Id targetId) {
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setFollowTarget(true);
        attackCommand.setId(actorId);
        attackCommand.setTarget(targetId);
        attackCommand.setTimeStamp();
        baseCommands.add(attackCommand);
        movableService.sendCommands(baseCommands);
    }

    protected void sendAttackCommands(Collection<Id> attackers, Id targetId) {
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        for (Id attacker : attackers) {
            AttackCommand attackCommand = new AttackCommand();
            attackCommand.setFollowTarget(true);
            attackCommand.setId(attacker);
            attackCommand.setTarget(targetId);
            attackCommand.setTimeStamp();
            baseCommands.add(attackCommand);
        }
        movableService.sendCommands(baseCommands);
    }

    protected void sendCollectCommand(Id harvesterId, Id resourceId) {
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        MoneyCollectCommand moneyCollectCommand = new MoneyCollectCommand();
        moneyCollectCommand.setId(harvesterId);
        moneyCollectCommand.setTarget(resourceId);
        moneyCollectCommand.setTimeStamp();
        baseCommands.add(moneyCollectCommand);
        movableService.sendCommands(baseCommands);
    }

    protected void sendContainerLoadCommand(Id item, Id container) {
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        LoadContainCommand loadContainCommand = new LoadContainCommand();
        loadContainCommand.setId(item);
        loadContainCommand.setItemContainer(container);
        loadContainCommand.setTimeStamp();
        baseCommands.add(loadContainCommand);
        movableService.sendCommands(baseCommands);

    }

    protected void sendUnloadContainerCommand(Id container, Index position) {
        List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
        UnloadContainerCommand unloadContainerCommand = new UnloadContainerCommand();
        unloadContainerCommand.setId(container);
        unloadContainerCommand.setUnloadPos(position);
        unloadContainerCommand.setTimeStamp();
        baseCommands.add(unloadContainerCommand);
        movableService.sendCommands(baseCommands);
    }

    protected AttackCommand createAttackCommand(Id actor, Id target) {
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setFollowTarget(true);
        attackCommand.setId(actor);
        attackCommand.setTarget(target);
        attackCommand.setTimeStamp();
        return attackCommand;
    }

    protected MoneyCollectCommand createMoneyCollectCommand(Id actor, Id target) {
        MoneyCollectCommand moneyCollectCommand = new MoneyCollectCommand();
        moneyCollectCommand.setId(actor);
        moneyCollectCommand.setTarget(target);
        moneyCollectCommand.setTimeStamp();
        return moneyCollectCommand;
    }

    // -------------------  Game Config --------------------

    protected void configureMinimalGame() throws Exception {
        System.out.println("---- Configure minimal Game ---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Item Types
        createSimpleBuilding();
        createHarvesterItemType();
        createAttackBaseItemType();
        createContainerBaseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        createMoney();
        // Terrain
        setupMinimalTerrain();
        // Setup territory
        setupNoobTerritory();
        // Level
        setupLevels();
        // Market
        setupMinimalMarket();
        setupXpSettings();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    protected void configureComplexGame() throws Exception {
        System.out.println("---- Configure complex Game ---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Item Types
        createHarvesterItemType();
        createAttackBaseItemType();
        createContainerBaseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        createMoney();
        // Terrain
        setupComplexTerrain();
        // Setup territory
        setupComplexTerritory();
        // Level
        //setupLevels();
        // Market
        //setupMinimalMarket();
        //setupXpSettings();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // ------------------- Setup Item Types --------------------

    protected DbBaseItemType createBuilderBaseItemType() {
        DbBaseItemType dbBaseItemType = new DbBaseItemType();
        dbBaseItemType.setName(TEST_START_BUILDER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setWidth(100);
        dbBaseItemType.setHeight(100);
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(1);
        // DbBuilderType
        DbBuilderType dbBuilderType = new DbBuilderType();
        dbBuilderType.setProgress(1000);
        dbBuilderType.setRange(100);
        Set<DbBaseItemType> ableToBuild = new HashSet<DbBaseItemType>();
        ableToBuild.add((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        dbBuilderType.setAbleToBuild(ableToBuild);
        dbBaseItemType.setBuilderType(dbBuilderType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbMovableType.setTerrainType(SurfaceType.LAND);
        dbBaseItemType.setMovableType(dbMovableType);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_START_BUILDER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createFactoryBaseItemType() {
        DbBaseItemType dbBaseItemType = new DbBaseItemType();
        dbBaseItemType.setName(TEST_FACTORY_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setWidth(100);
        dbBaseItemType.setHeight(100);
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(2);
        // DbBuilderType
        DbFactoryType dbFactoryType = new DbFactoryType();
        dbFactoryType.setProgress(1000);
        Set<DbBaseItemType> ableToBuild = new HashSet<DbBaseItemType>();
        ableToBuild.add((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        ableToBuild.add((DbBaseItemType) itemService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        ableToBuild.add((DbBaseItemType) itemService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        dbFactoryType.setAbleToBuild(ableToBuild);
        dbBaseItemType.setFactoryType(dbFactoryType);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_FACTORY_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createAttackBaseItemType() {
        DbBaseItemType dbBaseItemType = new DbBaseItemType();
        dbBaseItemType.setName(TEST_ATTACK_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setWidth(100);
        dbBaseItemType.setHeight(100);
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(3);
        // DbWeaponType
        DbWeaponType dbWeaponType = new DbWeaponType();
        dbWeaponType.setRange(100);
        dbWeaponType.setReloadTime(1);
        dbWeaponType.setDamage(1000);
        dbBaseItemType.setWeaponType(dbWeaponType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbMovableType.setTerrainType(SurfaceType.LAND);
        dbBaseItemType.setMovableType(dbMovableType);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_ATTACK_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private void finishAttackBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID);
        // DbWeaponType
        dbBaseItemType.getWeaponType().setItemTypeAllowed((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID), true);
        dbBaseItemType.getWeaponType().setItemTypeAllowed((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID), true);
        dbBaseItemType.getWeaponType().setItemTypeAllowed((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID), true);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
    }

    protected DbBaseItemType createContainerBaseItemType() {
        DbBaseItemType dbBaseItemType = new DbBaseItemType();
        dbBaseItemType.setName(TEST_CONTAINER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setWidth(100);
        dbBaseItemType.setHeight(100);
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        // DbItemContainerType
        DbItemContainerType dbItemContainerType = new DbItemContainerType();
        dbItemContainerType.setMaxCount(1);
        dbItemContainerType.setRange(200);
        dbBaseItemType.setDbItemContainerType(dbItemContainerType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbMovableType.setTerrainType(SurfaceType.LAND);
        dbBaseItemType.setMovableType(dbMovableType);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_CONTAINER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createSimpleBuilding() {
        DbBaseItemType dbBaseItemType = new DbBaseItemType();
        dbBaseItemType.setName(TEST_SIMPLE_BUILDING);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setWidth(100);
        dbBaseItemType.setHeight(100);
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_SIMPLE_BUILDING_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private void finishContainerBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemType(TEST_CONTAINER_ITEM_ID);
        // DbItemContainerType
        Set<DbBaseItemType> ableToContain = new HashSet<DbBaseItemType>();
        ableToContain.add(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbBaseItemType.getDbItemContainerType().setAbleToContain(ableToContain);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
    }

    private DbResourceItemType createMoney() {
        DbResourceItemType dbResourceItemType = new DbResourceItemType();
        dbResourceItemType.setName(TEST_RESOURCE_ITEM);
        dbResourceItemType.setTerrainType(TerrainType.LAND);
        dbResourceItemType.setWidth(100);
        dbResourceItemType.setHeight(100);
        dbResourceItemType.setAmount(3);

        itemService.saveDbItemType(dbResourceItemType);
        itemService.activate();
        TEST_RESOURCE_ITEM_ID = dbResourceItemType.getId();
        return dbResourceItemType;
    }

    protected DbBaseItemType createHarvesterItemType() {
        DbBaseItemType dbBaseItemType = new DbBaseItemType();
        dbBaseItemType.setName(TEST_HARVESTER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setWidth(100);
        dbBaseItemType.setHeight(100);
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(4);
        // DbWeaponType
        DbHarvesterType dbHarvesterType = new DbHarvesterType();
        dbHarvesterType.setRange(100);
        dbHarvesterType.setProgress(1);
        dbBaseItemType.setHarvesterType(dbHarvesterType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbMovableType.setTerrainType(SurfaceType.LAND);
        dbBaseItemType.setMovableType(dbMovableType);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_HARVESTER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    // ------------------- Setup Market --------------------

    protected void setupMinimalMarket() {
        MarketEntry factory = serverMarketService.getCrudMarketEntryService().createDbChild();
        factory.setAlwaysAllowed(true);
        factory.setItemType(itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        serverMarketService.getCrudMarketEntryService().updateDbChild(factory);

        MarketEntry attacker = serverMarketService.getCrudMarketEntryService().createDbChild();
        attacker.setAlwaysAllowed(true);
        attacker.setItemType(itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        serverMarketService.getCrudMarketEntryService().updateDbChild(attacker);

        MarketEntry harvester = serverMarketService.getCrudMarketEntryService().createDbChild();
        harvester.setAlwaysAllowed(true);
        harvester.setItemType(itemService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        serverMarketService.getCrudMarketEntryService().updateDbChild(harvester);

        MarketEntry itemContainer = serverMarketService.getCrudMarketEntryService().createDbChild();
        itemContainer.setAlwaysAllowed(true);
        itemContainer.setItemType(itemService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        serverMarketService.getCrudMarketEntryService().updateDbChild(itemContainer);
    }

    // ------------------- Setup Terrain --------------------

    protected DbTerrainSetting setupComplexTerrain() {
        setupTerrainImages();
        DbTerrainSetting dbTerrainSetting = setupComplexRealGameTerrain(createDbSurfaceImage(SurfaceType.LAND));
        terrainService.activateTerrain();
        return dbTerrainSetting;
    }

    protected void setupTerrainImages() {
        DbTerrainImage dbTerrainImage = terrainService.getDbTerrainImageCrudServiceHelper().createDbChild();
        dbTerrainImage.setTiles(4, 10);
        terrainService.getDbTerrainImageCrudServiceHelper().updateDbChild(dbTerrainImage);
        TERRAIN_IMAGE_4x10 = dbTerrainImage.getId();

        dbTerrainImage = terrainService.getDbTerrainImageCrudServiceHelper().createDbChild();
        dbTerrainImage.setTiles(10, 4);
        terrainService.getDbTerrainImageCrudServiceHelper().updateDbChild(dbTerrainImage);
        TERRAIN_IMAGE_10x4 = dbTerrainImage.getId();

        dbTerrainImage = terrainService.getDbTerrainImageCrudServiceHelper().createDbChild();
        dbTerrainImage.setTiles(4, 4);
        terrainService.getDbTerrainImageCrudServiceHelper().updateDbChild(dbTerrainImage);
        TERRAIN_IMAGE_4x4 = dbTerrainImage.getId();

        dbTerrainImage = terrainService.getDbTerrainImageCrudServiceHelper().createDbChild();
        dbTerrainImage.setTiles(10, 10);
        terrainService.getDbTerrainImageCrudServiceHelper().updateDbChild(dbTerrainImage);
        TERRAIN_IMAGE_10x10 = dbTerrainImage.getId();
    }

    protected DbTerrainSetting setupMinimalTerrain() {
        DbTerrainSetting dbTerrainSetting = setupMinimalRealGameTerrain(createDbSurfaceImage(SurfaceType.LAND));
        terrainService.activateTerrain();
        return dbTerrainSetting;
    }

    protected DbSurfaceImage createDbSurfaceImage(SurfaceType surfaceType) {
        DbSurfaceImage dbSurfaceImage = terrainService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage.setSurfaceType(surfaceType);
        terrainService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage);
        terrainService.activateTerrain();
        return dbSurfaceImage;
    }

    protected DbTerrainImage createDbTerrainImage(int tileWidth, int tileHeight) {
        DbTerrainImage dbTerrainImage = terrainService.getDbTerrainImageCrudServiceHelper().createDbChild();
        dbTerrainImage.setTiles(tileWidth, tileHeight);
        terrainService.getDbTerrainImageCrudServiceHelper().updateDbChild(dbTerrainImage);
        terrainService.activateTerrain();
        return dbTerrainImage;
    }

    protected DbTerrainSetting setupMinimalRealGameTerrain(DbSurfaceImage dbSurfaceImage) {
        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().createDbChild();
        dbTerrainSetting.setRealGame(true);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        dbTerrainSetting.setTileWidth(100);
        dbTerrainSetting.setTileHeight(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), dbSurfaceImage);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect);
        terrainService.getDbTerrainSettingCrudServiceHelper().updateDbChild(dbTerrainSetting);
        return dbTerrainSetting;
    }

    protected DbTerrainSetting setupMinimalSimulatedTerrain() {
        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().createDbChild();
        dbTerrainSetting.setRealGame(false);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        dbTerrainSetting.setTileWidth(100);
        dbTerrainSetting.setTileHeight(100);
        terrainService.getDbTerrainSettingCrudServiceHelper().updateDbChild(dbTerrainSetting);
        terrainService.activateTerrain();
        return dbTerrainSetting;
    }

    protected DbTerrainSetting setupComplexRealGameTerrain(DbSurfaceImage dbSurfaceImage) {
        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().createDbChild();
        dbTerrainSetting.setRealGame(true);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        dbTerrainSetting.setTileWidth(100);
        dbTerrainSetting.setTileHeight(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), dbSurfaceImage);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect);
        // Setup Terrain Images
        Collection<DbTerrainImagePosition> dbTerrainImagePositions = new ArrayList<DbTerrainImagePosition>();
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(10, 0), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(0, 13), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(0, 21), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(13, 22), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(20, 16), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(20, 7), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(10, 29), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(15, 26), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(21, 29), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(35, 20), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(36, 5), terrainService.getDbTerrainImageCrudServiceHelper().readDbChild(TERRAIN_IMAGE_4x10)));


        dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().updateDbChildren(dbTerrainImagePositions);

        terrainService.getDbTerrainSettingCrudServiceHelper().updateDbChild(dbTerrainSetting);
        return dbTerrainSetting;
    }

    // ------------------- Setup Levels --------------------

    protected void setupLevels() throws LevelActivationException {
        setupResurrection();
        setupSimulationLevel();
        setupCreateBaseRealGameLevel();
        setupBuildFactoryRealGameLevel();
        setupSimulationLevel4();
        setupBuildRealGameLevel5();
        userGuidanceService.activateLevels();
    }

    private void setupResurrection() {
        DbResurrection dbResurrection = userGuidanceService.getCrudRootDbResurrection().createDbChild();
        dbResurrection.setDbTerritory(territoryService.getDbTerritoryCrudServiceHelper().readDbChild(TEST_NOOB_TERRITORY_ID));
        dbResurrection.setMoney(1000);
        dbResurrection.setName(TEST_NOOB_RESURRECTION);
        dbResurrection.setStartItemFreeRange(100);
        dbResurrection.setStartItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        userGuidanceService.getCrudRootDbResurrection().updateDbChild(dbResurrection);
        TEST_NOOB_RESURRECTION_ID = dbResurrection.getId();
    }

    private void setupCreateBaseRealGameLevel() {
        DbRealGameLevel dbRealGameLevel = (DbRealGameLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        dbRealGameLevel.setName(TEST_LEVEL_2_REAL);
        dbRealGameLevel.setDbResurrection(userGuidanceService.getCrudRootDbResurrection().readDbChild(TEST_NOOB_RESURRECTION_ID));
        // Create Base
        dbRealGameLevel.setCreateRealBase(true);
        dbRealGameLevel.setStartItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        dbRealGameLevel.setStartTerritory(territoryService.getDbTerritoryCrudServiceHelper().readDbChild(TEST_NOOB_TERRITORY_ID));
        dbRealGameLevel.setStartItemFreeRange(300);
        // Rewards
        dbRealGameLevel.setDeltaMoney(1000);
        // Scope
        dbRealGameLevel.setHouseSpace(20);
        dbRealGameLevel.setMaxMoney(10000);
        dbRealGameLevel.setItemSellFactor(0.5);
        dbRealGameLevel.setMaxXp(1000);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(100);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbRealGameLevel.setDbConditionConfig(dbConditionConfig);
        // Limitation
        DbItemTypeLimitation builder = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbItemTypeLimitation factory = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbItemTypeLimitation attacker = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbItemTypeLimitation harvester = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbItemTypeLimitation container = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);

        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbRealGameLevel);
        TEST_LEVEL_2_REAL_ID = dbRealGameLevel.getId();
    }

    private void setupBuildFactoryRealGameLevel() {
        DbRealGameLevel dbRealGameLevel = (DbRealGameLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        dbRealGameLevel.setName(TEST_LEVEL_3_REAL);
        // Scope
        dbRealGameLevel.setHouseSpace(40);
        dbRealGameLevel.setMaxMoney(2000);
        dbRealGameLevel.setItemSellFactor(1);
        dbRealGameLevel.setMaxXp(2000);
        // Rewards
        dbRealGameLevel.setDeltaMoney(500);
        dbRealGameLevel.setDeltaXp(500);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
        DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig = new DbSyncItemTypeComparisonConfig();
        DbComparisonItemCount dbComparisonItemCount = dbSyncItemTypeComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        dbComparisonItemCount.setCount(1);
        dbConditionConfig.setDbAbstractComparisonConfig(dbSyncItemTypeComparisonConfig);
        dbRealGameLevel.setDbConditionConfig(dbConditionConfig);
        // Limitation
        DbItemTypeLimitation builder = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(20);
        DbItemTypeLimitation factory = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(20);
        DbItemTypeLimitation attacker = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(20);

        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbRealGameLevel);
        TEST_LEVEL_3_REAL_ID = dbRealGameLevel.getId();
    }

    private void setupBuildRealGameLevel5() throws LevelActivationException {
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
        DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig = new DbSyncItemTypeComparisonConfig();
        DbComparisonItemCount dbComparisonItemCount = dbSyncItemTypeComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        dbComparisonItemCount.setCount(1);
        dbConditionConfig.setDbAbstractComparisonConfig(dbSyncItemTypeComparisonConfig);
        // Create
        DbRealGameLevel dbRealGameLevel = setupGameLevel(TEST_LEVEL_5_REAL, dbConditionConfig);
        dbRealGameLevel.setDbResurrection(userGuidanceService.getCrudRootDbResurrection().readDbChild(TEST_NOOB_RESURRECTION_ID));
        // Rewards
        dbRealGameLevel.setDeltaMoney(100);
        dbRealGameLevel.setDeltaXp(50);
        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbRealGameLevel);
        TEST_LEVEL_5_REAL_ID = dbRealGameLevel.getId();
    }

    protected DbRealGameLevel setupGameLevel(String name, DbConditionConfig dbConditionConfig) throws LevelActivationException {
        DbRealGameLevel dbRealGameLevel = (DbRealGameLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        dbRealGameLevel.setName(name);
        // Scope
        dbRealGameLevel.setHouseSpace(20);
        // Condition
        dbRealGameLevel.setDbConditionConfig(dbConditionConfig);
        // Limitation
        DbItemTypeLimitation builder = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbItemTypeLimitation factory = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbItemTypeLimitation attacker = dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);

        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbRealGameLevel);
        userGuidanceService.activateLevels();
        return dbRealGameLevel;
    }


    private void setupSimulationLevel() {
        DbSimulationLevel dbSimulationLevel = (DbSimulationLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        dbSimulationLevel.setName(TEST_LEVEL_1_SIMULATED);
        // Tutorial
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        dbSimulationLevel.setDbTutorialConfig(dbTutorialConfig);
        // Terrain
        DbTerrainSetting dbTerrainSetting = setupMinimalSimulatedTerrain();
        dbTutorialConfig.setDbTerrainSetting(dbTerrainSetting);
        // Task
        DbTaskConfig dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild();
        // Step
        DbStepConfig dbStepConfig = dbTaskConfig.getStepConfigCrudServiceHelper().createDbChild();
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(100);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbStepConfig.setConditionConfig(dbConditionConfig);

        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbSimulationLevel);
    }

    protected DbSimulationLevel setupContainedInSimulationLevel(String name, boolean containedIn) throws LevelActivationException {
        DbSimulationLevel dbSimulationLevel = (DbSimulationLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        dbSimulationLevel.setName(name);
        // Tutorial
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        dbTutorialConfig.setOwnBaseId(1);
        dbSimulationLevel.setDbTutorialConfig(dbTutorialConfig);
        // Terrain
        DbTerrainSetting dbTerrainSetting = setupMinimalSimulatedTerrain();
        dbTutorialConfig.setDbTerrainSetting(dbTerrainSetting);
        // Task
        DbTaskConfig dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild();
        DbItemTypeAndPosition bulldozer = dbTaskConfig.getItemCrudServiceHelper().createDbChild();
        bulldozer.setSyncItemId(1);
        bulldozer.setPosition(new Index(100, 100));
        bulldozer.setItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        bulldozer.setBaseId(1);
        DbItemTypeAndPosition container = dbTaskConfig.getItemCrudServiceHelper().createDbChild();
        container.setSyncItemId(2);
        container.setPosition(new Index(150, 150));
        container.setItemType(itemService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        container.setBaseId(1);
        // Step
        DbStepConfig dbStepConfig = dbTaskConfig.getStepConfigCrudServiceHelper().createDbChild();
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.CONTAINED_IN);
        DbContainedInComparisonConfig containedInComparisonConfig = new DbContainedInComparisonConfig();
        containedInComparisonConfig.setContainedIn(containedIn);
        dbConditionConfig.setDbAbstractComparisonConfig(containedInComparisonConfig);
        dbStepConfig.setConditionConfig(dbConditionConfig);
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);

        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbSimulationLevel);
        userGuidanceService.activateLevels();
        return dbSimulationLevel;
    }

    private void setupSimulationLevel4() {
        DbSimulationLevel dbSimulationLevel = (DbSimulationLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        dbSimulationLevel.setName(TEST_LEVEL_4_SIMULATED);
        // Tutorial
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        dbSimulationLevel.setDbTutorialConfig(dbTutorialConfig);
        // Terrain
        DbTerrainSetting dbTerrainSetting = setupMinimalSimulatedTerrain();
        dbTutorialConfig.setDbTerrainSetting(dbTerrainSetting);
        // Task
        DbTaskConfig dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild();
        // Step
        DbStepConfig dbStepConfig = dbTaskConfig.getStepConfigCrudServiceHelper().createDbChild();
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(100);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbStepConfig.setConditionConfig(dbConditionConfig);

        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbSimulationLevel);
        TEST_LEVEL_4_SIMULATED_ID = dbSimulationLevel.getId();
    }

    protected DbSimulationLevel setupItemTypePositionSimulationLevel(String name) throws LevelActivationException {
        DbSimulationLevel dbSimulationLevel = (DbSimulationLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        dbSimulationLevel.setName(name);
        // Tutorial
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        dbTutorialConfig.setOwnBaseId(1);
        dbSimulationLevel.setDbTutorialConfig(dbTutorialConfig);
        // Terrain
        DbTerrainSetting dbTerrainSetting = setupMinimalSimulatedTerrain();
        dbTutorialConfig.setDbTerrainSetting(dbTerrainSetting);
        // Task
        DbTaskConfig dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild();
        DbItemTypeAndPosition bulldozer = dbTaskConfig.getItemCrudServiceHelper().createDbChild();
        bulldozer.setSyncItemId(1);
        bulldozer.setPosition(new Index(100, 100));
        bulldozer.setItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        bulldozer.setBaseId(1);
        // Step
        DbStepConfig dbStepConfig = dbTaskConfig.getStepConfigCrudServiceHelper().createDbChild();
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_DEACTIVATE);
        DbItemTypePositionComparisonConfig itemTypePositionComparisonConfig = new DbItemTypePositionComparisonConfig();
        itemTypePositionComparisonConfig.setDbItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        itemTypePositionComparisonConfig.setRegion(new Rectangle(300, 300, 200, 200));
        dbConditionConfig.setDbAbstractComparisonConfig(itemTypePositionComparisonConfig);
        dbStepConfig.setConditionConfig(dbConditionConfig);
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);

        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbSimulationLevel);
        userGuidanceService.activateLevels();
        return dbSimulationLevel;
    }

    // ------------------- Setup minimal bot --------------------

    protected DbBotConfig setupMinimalBot(Rectangle realm, Rectangle core) {
        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(realm);
        dbBotConfig.setCore(core);
        dbBotConfig.setCoreSuperiority(2);
        dbBotConfig.setRealmSuperiority(1);
        DbBotItemCount fundamental = dbBotConfig.getBaseFundamentalCrudServiceHelper().createDbChild();
        fundamental.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        fundamental.setCount(1);
        DbBotItemCount baseBuildup = dbBotConfig.getBaseBuildupCrudServiceHelper().createDbChild();
        baseBuildup.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        baseBuildup.setCount(1);
        DbBotItemCount defence = dbBotConfig.getDefenceCrudServiceHelper().createDbChild();
        defence.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        defence.setCount(1);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);
        botService.activate();
        return dbBotConfig;
    }

    protected void waitForBotToBuildup(DbBotConfig dbBotConfig) throws InterruptedException, TimeoutException {
        long maxTime = System.currentTimeMillis() + 100000;
        while (!botService.getBotRunner(dbBotConfig).isBuildup()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    // ------------------- Territory Config --------------------

    protected void setupNoobTerritory() {
        DbTerritory dbTerritory = setupTerritory(TEST_NOOB_TERRITORY,
                new int[]{TEST_START_BUILDER_ITEM_ID, TEST_ATTACK_ITEM_ID, TEST_CONTAINER_ITEM_ID, TEST_FACTORY_ITEM_ID, TEST_HARVESTER_ITEM_ID},
                new Rectangle(50, 50, 50, 50));
        TEST_NOOB_TERRITORY_ID = dbTerritory.getId();
    }

    protected void setupComplexTerritory() {
        DbTerritory dbTerritory = setupTerritory(COMPLEX_TERRITORY,
                new int[]{TEST_START_BUILDER_ITEM_ID, TEST_ATTACK_ITEM_ID, TEST_CONTAINER_ITEM_ID, TEST_FACTORY_ITEM_ID, TEST_HARVESTER_ITEM_ID},
                new Rectangle(0, 0, 16, 6),
                new Rectangle(0, 6, 5, 6), new Rectangle(5, 6, 11, 7),
                new Rectangle(0, 12, 5, 6), new Rectangle(5, 13, 11, 5));
        COMPLEX_TERRITORY_ID = dbTerritory.getId();
    }

    protected DbTerritory setupTerritory(String name, int[] allowedItems, Rectangle... tileRegions) {
        DbTerritory dbTerritory = territoryService.getDbTerritoryCrudServiceHelper().createDbChild();
        dbTerritory.setName(name);
        if (allowedItems != null) {
            for (int allowedItem : allowedItems) {
                dbTerritory.setItemAllowed(itemService.getDbBaseItemType(allowedItem), true);
            }
        }
        territoryService.saveTerritory(dbTerritory.getId(), Arrays.asList(tileRegions));
        territoryService.activate();
        return dbTerritory;
    }

    // ------------------- XpSettings Config --------------------

    protected XpSettings setupXpSettings() {
        XpSettings xpSettings = serverMarketServic.getXpPointSettings();
        xpSettings.setPeriodMinutes(0);
        xpSettings.setPeriodItemFactor(0);
        xpSettings.setKillPriceFactor(1);
        serverMarketServic.saveXpPointSettings(xpSettings);
        return xpSettings;
    }

    protected XpSettings setupXpSettings(int msForPeriod, double periodItemFactor) {
        XpSettings xpSettings = serverMarketServic.getXpPointSettings();
        xpSettings.setPeriodMilliSeconds(msForPeriod);
        xpSettings.setPeriodItemFactor(periodItemFactor);
        xpSettings.setKillPriceFactor(1);
        serverMarketServic.saveXpPointSettings(xpSettings);
        return xpSettings;
    }

    // ------------------- Setup Resource --------------------

    protected DbRegionResource setupResource() {
        DbRegionResource dbRegionResource = resourceService.getDbRegionResourceCrudServiceHelper().createDbChild();
        dbRegionResource.setResourceItemType(itemService.getDbResourceItemType(TEST_RESOURCE_ITEM_ID));
        dbRegionResource.setCount(10);
        dbRegionResource.setMinDistanceToItems(100);
        dbRegionResource.setRegion(new Rectangle(5000, 5000, 10000, 10000));

        resourceService.getDbRegionResourceCrudServiceHelper().updateDbChild(dbRegionResource);
        resourceService.activate();
        return dbRegionResource;
    }

    // ------------------- Session Config --------------------

    private void beginOpenSessionInViewFilter() {
        if (sessionHolder != null) {
            throw new IllegalStateException("SessionHolder is NOT null. afterOpenSessionInViewFilter() was not called.");
        }

        Session session = SessionFactoryUtils.getSession(getHibernateTemplate().getSessionFactory(), true);
        session.setFlushMode(FlushMode.AUTO);
        sessionHolder = new SessionHolder(session);
        TransactionSynchronizationManager.bindResource(getHibernateTemplate().getSessionFactory(), sessionHolder);
    }

    private void endOpenSessionInViewFilter() {
        if (sessionHolder == null) {
            throw new IllegalStateException("SessionHolder is null. Call beforeOpenSessionInViewFilter() first.");
        }
        SessionFactoryUtils.closeSession(sessionHolder.getSession());
        TransactionSynchronizationManager.unbindResource(getHibernateTemplate().getSessionFactory());
        sessionHolder = null;
    }

    protected Session getSessionFromSessionInViewFilter() {
        if (sessionHolder == null) {
            throw new IllegalStateException("SessionHolder is null. Call beforeOpenSessionInViewFilter() first.");
        }
        Session session = sessionHolder.getSession();
        if (session == null) {
            throw new IllegalStateException("Session is null");
        }
        return session;
    }

    protected void beginHttpSession() {
        if (mockHttpSession != null) {
            throw new IllegalStateException("mockHttpSession is not null");
        }
        mockHttpSession = new MockHttpSession();
        securityContext = SecurityContextHolder.createEmptyContext();
    }

    protected void endHttpSession() {
        if (mockHttpSession == null) {
            throw new IllegalStateException("mockHttpSession is null");
        }
        mockHttpSession.invalidate();
        mockHttpSession = null;
        securityContext = null;
    }

    protected String getHttpSessionId() {
        if (mockHttpSession == null) {
            throw new IllegalStateException("mockHttpSession is null");
        }
        return mockHttpSession.getId();
    }

    protected MockHttpServletRequest getMockHttpServletRequest() {
        if (mockHttpServletRequest == null) {
            throw new IllegalStateException("mockHttpServletRequest is null");
        }
        return mockHttpServletRequest;
    }

    protected MockHttpServletResponse getMockHttpServletResponse() {
        if (mockHttpServletResponse == null) {
            throw new IllegalStateException("mockHttpServletResponse is null");
        }
        return mockHttpServletResponse;
    }

    private void beginHttpRequest() {
        if (mockHttpServletRequest != null) {
            throw new IllegalStateException("mockHttpServletRequest is not null");
        }
        if (mockHttpServletResponse != null) {
            throw new IllegalStateException("mockHttpServletResponse is not null");
        }
        if (mockHttpSession == null) {
            throw new IllegalStateException("mockHttpSession is null");
        }
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setSession(mockHttpSession);
        mockHttpServletResponse = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        SecurityContextHolder.setContext(securityContext);
    }

    private void endHttpRequest() {
        if (mockHttpServletRequest == null) {
            throw new IllegalStateException("mockHttpServletRequest not null");
        }
        if (mockHttpServletResponse == null) {
            throw new IllegalStateException("mockHttpServletResponse not null");
        }
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).requestCompleted();
        RequestContextHolder.resetRequestAttributes();
        mockHttpServletRequest = null;
        mockHttpServletResponse = null;
        securityContext = SecurityContextHolder.getContext();
    }

    protected void beginHttpRequestAndOpenSessionInViewFilter() {
        beginHttpRequest();
        beginOpenSessionInViewFilter();
    }

    protected void endHttpRequestAndOpenSessionInViewFilter() {
        endOpenSessionInViewFilter();
        endHttpRequest();
    }

    @Before
    public void setup() {
        configurableListableBeanFactory.registerResolvableDependency(ServletRequest.class, new ObjectFactory<ServletRequest>() {
            // This is used to inject HttpServletRequest into SessionImpl
            @Override
            public ServletRequest getObject() throws BeansException {
                return mockHttpServletRequest;
            }
        });
    }

    // ------------------- Div --------------------

    protected void setPrivateField(Class clazz, Object object, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }
}
