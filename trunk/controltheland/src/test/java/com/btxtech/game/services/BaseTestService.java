package com.btxtech.game.services;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBuilderType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.services.item.itemType.DbMovableType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.utg.DbItemTypeLimitation;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * User: beat
 * Date: 21.02.2011
 * Time: 20:41:45
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:BaseTestService-context.xml")
public class BaseTestService {
    protected static final String TEST_START_BUILDER_ITEM = "TestStartBuilderItem";
    protected static int TEST_START_BUILDER_ITEM_ID = -1;
    protected static final String TEST_FACTORY_ITEM = "TestFactoryItem";
    protected static int TEST_FACTORY_ITEM_ID = -1;
    protected static final String TEST_ATTACK_ITEM = "TestAttackItem";
    protected static int TEST_ATTACK_ITEM_ID = -1;
    protected static final String TEST_SIMULATED_LEVEL = "TestSimulatedLevel";
    protected static final String TEST_REAL_GAME_CREATE_BASE_LEVEL = "TestRealGameCreateBaseLevel";

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
    private SessionHolder sessionHolder;
    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpSession mockHttpSession;
    private SecurityContext securityContext;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    protected HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    // ------------------- Action Service --------------------

    protected void waitForActionServiceDone(long timeout) throws TimeoutException, InterruptedException {
        long maxTime = System.currentTimeMillis() + timeout;
        while (actionService.isBusy()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
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

    protected Id getFirstSynItemId(SimpleBase simpleBase, int itemTypeId) {
        for (SyncItemInfo syncItemInfo : movableService.getAllSyncInfo()) {
            if (syncItemInfo.getBase().equals(simpleBase) && syncItemInfo.getItemTypeId() == itemTypeId) {
                return syncItemInfo.getId();
            }
        }
        throw new IllegalStateException("No such sync item: ItemTypeID=" + itemTypeId + " simpleBase=" + simpleBase);
    }

    // ------------------- Setup Minimal Game Config --------------------            

    protected void configureMinimalGame() throws Exception {
        System.out.println("---- Configure minimal Game ---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Item Types
        createAttackBaseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        finishAttackBaseItemType();
        // Terrain
        setupMinimalTerrain();
        // Level
        setupLevels();
        // Market
        setupMinimalMarket();

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
        dbMovableType.setSpeed(1000);
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
        // DbBuilderType
        DbFactoryType dbFactoryType = new DbFactoryType();
        dbFactoryType.setProgress(1000);
        Set<DbBaseItemType> ableToBuild = new HashSet<DbBaseItemType>();
        ableToBuild.add((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
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
        // DbWeaponType
        DbWeaponType dbWeaponType = new DbWeaponType();
        dbWeaponType.setRange(100);
        dbWeaponType.setReloadTime(1);
        dbWeaponType.setDamage(1000);
        dbBaseItemType.setWeaponType(dbWeaponType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(1000);
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


    // ------------------- Setup Market --------------------

    protected void setupMinimalMarket() {
        MarketEntry factory = serverMarketService.getCrudMarketEntryService().createDbChild();
        factory.setAlwaysAllowed(true);
        factory.setItemType(itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        MarketEntry attacker = serverMarketService.getCrudMarketEntryService().createDbChild();
        attacker.setAlwaysAllowed(true);
        attacker.setItemType(itemService.getDbItemType(TEST_ATTACK_ITEM_ID));

        serverMarketService.getCrudMarketEntryService().updateDbChild(factory);
    }

    // ------------------- Setup Terrain --------------------

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

    // ------------------- Setup Levels --------------------

    protected void setupLevels() throws LevelActivationException {
        setupSimulationLevel(TEST_SIMULATED_LEVEL);
        setupRealGameCreateBaseLevel();
        userGuidanceService.activateLevels();
    }

    private void setupRealGameCreateBaseLevel() {
        DbRealGameLevel dbRealGameLevel = (DbRealGameLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbRealGameLevel.class);
        dbRealGameLevel.setName(TEST_REAL_GAME_CREATE_BASE_LEVEL);
        // Create Base
        dbRealGameLevel.setCreateRealBase(true);
        dbRealGameLevel.setStartItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        dbRealGameLevel.setStartRectangle(new Rectangle(0, 0, 100, 100));
        // Scope
        dbRealGameLevel.setHouseSpace(20);
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

        userGuidanceService.getDbLevelCrudServiceHelper().updateDbChild(dbRealGameLevel);
    }

    private void setupSimulationLevel(String name) {
        DbSimulationLevel dbSimulationLevel = (DbSimulationLevel) userGuidanceService.getDbLevelCrudServiceHelper().createDbChild(DbSimulationLevel.class);
        dbSimulationLevel.setName(name);
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

    protected void beginHttpSession() {
        if (mockHttpSession != null) {
            throw new IllegalStateException("mockHttpSession is not null");
        }
        mockHttpSession = new MockHttpSession();
        securityContext = SecurityContextHolder.createEmptyContext();;        
    }

    protected void endHttpSession() {
        if (mockHttpSession == null) {
            throw new IllegalStateException("mockHttpSession is null");
        }
        mockHttpSession.invalidate();
        mockHttpSession = null;
        securityContext = null;
    }

    private void beginHttpRequest() {
        if (mockHttpServletRequest != null) {
            throw new IllegalStateException("mockHttpServletRequest is not null");
        }
        if (mockHttpSession == null) {
            throw new IllegalStateException("mockHttpSession is null");
        }
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setSession(mockHttpSession);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        SecurityContextHolder.setContext(securityContext);
    }

    private void endHttpRequest() {
        if (mockHttpServletRequest == null) {
            throw new IllegalStateException("mockHttpServletRequest not null");
        }
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).requestCompleted();
        RequestContextHolder.resetRequestAttributes();
        mockHttpServletRequest = null;
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
}
