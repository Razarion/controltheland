package com.btxtech.game.services;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.HouseSpacePacket;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.bot.DbBotEnragementStateConfig;
import com.btxtech.game.services.bot.DbBotItemConfig;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.gwt.MovableServiceImpl;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBuilderType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.item.itemType.DbHouseType;
import com.btxtech.game.services.item.itemType.DbItemContainerType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbMovableType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ResourceService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.db.DbPlanetItemTypeLimitation;
import com.btxtech.game.services.planet.db.DbRegionResource;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.playback.impl.PlaybackServiceImpl;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.RegionService;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelItemTypeLimitation;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.DbComparisonItemCount;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import com.btxtech.game.services.utg.condition.DbItemTypePositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.LocalizedImageResource;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.value.ValueMap;
import org.easymock.EasyMock;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
    public static final String START_UID_1 = "startuid1";
    public static final String START_UID_2 = "startuid2";
    public static final String START_UID_3 = "startuid3";
    public static final double[] ANGELS_24 = {0, 0.261799387799149, 0.523598775598299, 0.785398163397448, 1.0471975511966, 1.30899693899575, 1.5707963267949, 1.83259571459405, 2.0943951023932, 2.35619449019234, 2.61799387799149, 2.87979326579064, 3.14159265358979, 3.40339204138894, 3.66519142918809, 3.92699081698724, 4.18879020478639, 4.45058959258554, 4.71238898038469, 4.97418836818384, 5.23598775598299, 5.49778714378214, 5.75958653158129, 6.02138591938044};
    public static final double[] ANGELS_24_2 = {0, 0.436332312998582, 0.837758040957278, 1.09955742875643, 1.2915436464758, 1.44862327915529, 1.5707963267949, 1.74532925199433, 1.86750229963393, 2.05948851735331, 2.32128790515246, 2.68780704807127, 3.14159265358979, 3.64773813666815, 4.01425727958696, 4.25860337486616, 4.4331363000656, 4.59021593274509, 4.71238898038469, 4.85201532054424, 5.00909495322373, 5.14872129338327, 5.42797397370236, 5.79449311662117};
    public static final double[] ANGELS_JEEP = {0, 0.331612557878923, 0.785398163397448, 1.08210413623648, 1.2915436464758, 1.43116998663535, 1.5707963267949, 1.71042266695444, 1.85004900711399, 2.07694180987325, 2.30383461263251, 2.7401669256311, 3.14159265358979, 3.59537825910832, 3.76991118430775, 4.1538836197465, 4.36332312998582, 4.5553093477052, 4.71238898038469, 4.81710873550435, 5.06145483078356, 5.32325421858271, 5.55014702134197, 5.846852994181};
    public static final double[] ANGELS_TANK = {0, 0.453785605518526, 0.872664625997165, 1.11701072127637, 1.30899693899575, 1.46607657167524, 1.5707963267949, 1.72787595947439, 1.91986217719376, 2.07694180987325, 2.39110107523223, 2.75762021815104, 3.14159265358979, 3.5081117965086, 3.94444410950718, 4.25860337486616, 4.4331363000656, 4.57276264022514, 4.71238898038469, 4.86946861306418, 5.02654824574367, 5.18362787842316, 5.44542726622231, 5.86430628670095};
    public static final double[] ANGELS_1 = {0};
    protected static final String TEST_START_BUILDER_ITEM = "TestStartBuilderItem";
    protected static int TEST_START_BUILDER_ITEM_ID = -1;
    protected static final String TEST_FACTORY_ITEM = "TestFactoryItem";
    protected static int TEST_FACTORY_ITEM_ID = -1;
    protected static final String TEST_ATTACK_ITEM = "TestAttackItem";
    protected static int TEST_ATTACK_ITEM_ID = -1;
    protected static final String TEST_ATTACK_ITEM_2 = "TestAttackItem2";
    protected static int TEST_ATTACK_ITEM_ID_2 = -1;
    protected static final String TEST_CONTAINER_ITEM = "TestContainerItem";
    protected static int TEST_CONTAINER_ITEM_ID = -1;
    protected static final String TEST_SIMPLE_BUILDING = "TEST_SIMPLE_BUILDING";
    protected static int TEST_SIMPLE_BUILDING_ID = -1;
    protected static final String TEST_HOUSE = "TEST_HOUSE";
    protected static int TEST_HOUSE_ID = -1;
    protected static final String TEST_RESOURCE_ITEM = "TestResourceItem";
    protected static int TEST_RESOURCE_ITEM_ID = -1;
    protected static final String TEST_HARVESTER_ITEM = "TEST_HARVESTER_ITEM";
    protected static int TEST_HARVESTER_ITEM_ID = -1;
    protected static final String TEST_BOX_ITEM_1 = "TEST_BOX_ITEM_1";
    protected static int TEST_BOX_ITEM_1_ID = -1;
    protected static final String TEST_BOX_ITEM_2 = "TEST_BOX_ITEM_2";
    protected static int TEST_BOX_ITEM_2_ID = -1;
    // Planets
    protected static final String TEST_PLANET_1 = "TEST_PLANET_1";
    protected static int TEST_PLANET_1_ID = -1;
    protected static final String TEST_PLANET_2 = "TEST_PLANET_2";
    protected static int TEST_PLANET_2_ID = -1;
    protected static final String TEST_PLANET_3 = "TEST_PLANET_3";
    protected static int TEST_PLANET_3_ID = -1;
    // Level ID
    protected static int TEST_LEVEL_1_SIMULATED_ID = -1;
    protected static int TEST_LEVEL_2_REAL_ID = -1;
    protected static int TEST_LEVEL_3_REAL_ID = -1;
    protected static int TEST_LEVEL_4_REAL_ID = -1;
    protected static int TEST_LEVEL_5_REAL_ID = -1;
    // Level numbers
    protected static final int TEST_LEVEL_1_SIMULATED = 1;
    protected static final int TEST_LEVEL_2_REAL = 2;
    protected static final int TEST_LEVEL_3_REAL = 3;
    protected static final int TEST_LEVEL_4_REAL = 4;
    protected static final int TEST_LEVEL_5_REAL = 5;
    // Level Task ID
    protected static int TEST_LEVEL_TASK_1_1_SIMULATED_ID = -1;
    protected static int TEST_LEVEL_TASK_1_2_REAL_ID = -1;
    protected static int TEST_LEVEL_TASK_2_2_REAL_ID = -1;
    protected static int TEST_LEVEL_TASK_1_3_REAL_ID = -1;
    protected static int TEST_LEVEL_TASK_2_3_REAL_ID = -1;
    protected static int TEST_LEVEL_TASK_3_3_SIMULATED_ID = -1;
    protected static int TEST_LEVEL_TASK_4_3_SIMULATED_ID = -1;
    protected static int TEST_LEVEL_TASK_1_4_REAL_ID = -1;
    protected static int TEST_LEVEL_TASK_2_4_REAL_ID = -1;
    // Level task name
    protected static String TEST_LEVEL_TASK_1_1_SIMULATED_NAME = "TEST_LEVEL_TASK_1_1_SIMULATED_NAME";
    protected static String TEST_LEVEL_TASK_3_3_SIMULATED_NAME = "TEST_LEVEL_TASK_3_3_SIMULATED_NAME";
    protected static String TEST_LEVEL_TASK_4_3_SIMULATED_NAME = "TEST_LEVEL_TASK_4_3_SIMULATED_NAME";
    protected static String TEST_LEVEL_TASK_1_2_REAL_NAME = "TEST_LEVEL_TASK_1_2_REAL_NAME";
    protected static String TEST_LEVEL_TASK_2_2_REAL_NAME = "TEST_LEVEL_TASK_2_2_REAL_NAME";
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

    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private TerrainImageService terrainService;
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private XpService xpService;
    @Autowired
    private XpService xpServic;
    //@Autowired
    private ResourceService resourceService;
    private ServerPlanetServices serverServices;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private RegionService regionService;
    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;
    private MockHttpSession mockHttpSession;
    private SecurityContext securityContext;
    private MovableService movableService;
    private PlaybackServiceImpl playbackService;
    private JdbcTemplate jdbcTemplate;

    protected PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    // ---------------------- planet -----------------------

    protected void overrideConnectionService(ServerPlanetServicesImpl serverPlanetServices, ServerConnectionService serverConnectionService) {
        serverPlanetServices.getConnectionService().deactivate();
        serverPlanetServices.setServerConnectionService(serverConnectionService);
    }

    // ---------------------- Region -----------------------
    public Region createRegion(Rectangle rectangle, int id) {
        Rectangle tileRect = TerrainUtil.convertToTilePosition(rectangle);
        return new Region(id, Collections.singletonList(tileRect));
    }

    protected Region createSimpleRegion(int id) {
        return createRegion(new Rectangle(5000, 5000, 5000, 5000), id);
    }

    public DbRegion createDbRegion(Rectangle rectangle) {
        DbRegion dbRegion = regionService.getRegionCrud().createDbChild();
        dbRegion.setRegion(createRegion(rectangle, 1));
        regionService.getRegionCrud().updateDbChild(dbRegion);
        return dbRegion;
    }

    // ---------------------- GWT Servlets -----------------------

    public MovableService getMovableService() {
        if (movableService == null) {
            MovableServiceImpl movableServiceImpl = new MovableServiceImpl();
            applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(movableServiceImpl, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            movableService = movableServiceImpl;
        }
        return movableService;
    }

    public PlaybackServiceImpl getPlaybackService() {
        if (playbackService == null) {
            PlaybackServiceImpl movableServiceImpl = new PlaybackServiceImpl();
            applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(movableServiceImpl, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            playbackService = movableServiceImpl;
        }
        return playbackService;
    }

    // ---------------------- Base -----------------------

    protected DbBaseItemType getDbBaseItemTypeInSession(int id) {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBaseItemType dbBaseItemType = serverItemTypeService.getDbBaseItemType(id);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        return dbBaseItemType;
    }

    protected void assertBaseCount(int planetId, int count) {
        Assert.assertEquals(count, planetSystemService.getServerPlanetServices(planetId).getBaseService().getAllBaseAttributes().size());
    }

    // ------------------- DbItemType helpers --------------------

    protected void setupDbItemTypeId(DbItemType dbItemType, int id) throws Exception {
        setPrivateField(DbItemType.class, dbItemType, "id", id);
        dbItemType.init(null);
    }

    // ------------------- Sync Items --------------------

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id, GlobalServices globalServices, PlanetServices planetServices, SimpleBase simpleBase) throws Exception {
        SyncBaseItem syncBaseItem = new SyncBaseItem(id, null, (BaseItemType) serverItemTypeService.getItemType(itemTypeId), globalServices, planetServices, simpleBase);
        syncBaseItem.setBuildup(1.0);
        syncBaseItem.getSyncItemArea().setPosition(position);
        return syncBaseItem;
    }

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id, GlobalServices globalServices, PlanetServices planetServices) throws Exception {
        return createSyncBaseItem(itemTypeId, position, id, globalServices, planetServices, new SimpleBase(1, planetServices.getPlanetInfo().getPlanetId()));
    }

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id, SimpleBase simpleBase) throws Exception {
        return createSyncBaseItem(itemTypeId, position, id, createMockGlobalServices(), createMockPlanetServices(), simpleBase);
    }

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id) throws Exception {
        return createSyncBaseItem(itemTypeId, position, id, new SimpleBase(1, 1));
    }

    protected SyncResourceItem createSyncResourceItem(int itemTypeId, Index position, Id id) throws Exception {
        SyncResourceItem syncResourceItem = new SyncResourceItem(id, null, (ResourceType) serverItemTypeService.getItemType(itemTypeId), createMockGlobalServices(), createMockPlanetServices());
        syncResourceItem.getSyncItemArea().setPosition(position);
        return syncResourceItem;
    }

    protected SyncBoxItem createSyncBoxItem(int itemTypeId, Index position, Id id) throws Exception {
        return new SyncBoxItem(id, position, (BoxItemType) serverItemTypeService.getItemType(itemTypeId), createMockGlobalServices(), createMockPlanetServices());
    }

    /**
     * Attention: closes the current connection!!!
     *
     * @return Simple Base
     */
    protected SimpleBase getMyBase() {
        try {
            return getMovableService().getRealGameInfo(START_UID_1).getBase();
        } catch (InvalidLevelStateException invalidLevelStateException) {
            throw new RuntimeException(invalidLevelStateException);
        }
    }

    protected SimpleBase getFirstBotBase(int planetId) {
        BaseService baseService = planetSystemService.getServerPlanetServices(planetId).getBaseService();
        for (Base base : baseService.getBases()) {
            if (baseService.isBot(base.getSimpleBase())) {
                return base.getSimpleBase();
            }
        }
        throw new IllegalStateException("No botbase found");
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
        for (SyncItemInfo syncItemInfo : getMovableService().getAllSyncInfo()) {
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

    protected List<Id> getAllSynItemId(int itemTypeId) {
        return getAllSynItemId(getMyBase(), itemTypeId, null);
    }

    protected List<Id> getAllSynItemId(SimpleBase simpleBase, int itemTypeId, Rectangle region) {
        return getAllSynItemId(simpleBase, itemTypeId, region, null);
    }

    protected List<Id> getAllSynItemId(SimpleBase simpleBase, int itemTypeId, Rectangle region, Integer planetId) {
        List<Id> ids = new ArrayList<>();
        Collection<SyncItemInfo> allSyncInfos;
        if (planetId != null) {
            allSyncInfos = planetSystemService.getServerPlanetServices(planetId).getItemService().getSyncInfo();
        } else {
            allSyncInfos = getMovableService().getAllSyncInfo();
        }
        for (SyncItemInfo syncItemInfo : allSyncInfos) {
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

    protected void assertWholeItemCount(int planetId, int count) {
        Assert.assertEquals(count, planetSystemService.getServerPlanetServices(planetId).getItemService().getItemsCopy().size());
    }

    protected void killSyncItem(int planetId, Id id) throws Exception {
        SyncItem syncItem = planetSystemService.getServerPlanetServices(planetId).getItemService().getItem(id);
        planetSystemService.getServerPlanetServices(planetId).getItemService().killSyncItem(syncItem, null, true, false);
    }

    protected Collection<SyncResourceItem> getAllResourceItems(int planetId, int itemTypeId) throws NoSuchItemTypeException {
        return (Collection<SyncResourceItem>) planetSystemService.getServerPlanetServices(planetId).getItemService().getItems(serverItemTypeService.getItemType(itemTypeId), null);
    }

    protected Id getFirstResourceItem(int planetId, int itemTypeId) throws NoSuchItemTypeException {
        return new ArrayList<>(getAllResourceItems(planetId, itemTypeId)).get(0).getId();
    }

    // ------------------- Connection --------------------

    protected void clearPackets() throws Exception {
        getMovableService().getSyncInfo(START_UID_1);
    }

    protected List<Packet> getPackagesIgnoreSyncItemInfoAndClear(boolean ignoreAccountBalancePackets) throws Exception {
        List<Packet> receivedPackets = new ArrayList<Packet>(getMovableService().getSyncInfo(START_UID_1));
        for (Iterator<Packet> iterator = receivedPackets.iterator(); iterator.hasNext(); ) {
            Packet packet = iterator.next();
            if (packet instanceof SyncItemInfo) {
                iterator.remove();
            } else if (ignoreAccountBalancePackets && packet instanceof AccountBalancePacket) {
                iterator.remove();
            }
        }
        return receivedPackets;
    }

    protected <T extends Packet> List<T> getPackages(Class<T> packetFilter) throws Exception {
        List<T> packets = new ArrayList<>();
        List receivedPackets = new ArrayList<>(getMovableService().getSyncInfo(START_UID_1));
        for (Object packet : receivedPackets) {
            if (packetFilter.isAssignableFrom(packet.getClass())) {
                packets.add((T) packet);
            }
        }
        return packets;
    }


    protected void assertPackagesIgnoreSyncItemInfoAndClear(Packet... expectedPackets) throws Exception {
        assertPackagesIgnoreSyncItemInfoAndClear(false, expectedPackets);
    }

    protected void assertPackagesIgnoreSyncItemInfoAndClear(boolean ignoreAccountBalance, Packet... expectedPackets) throws Exception {
        List<Packet> receivedPackets = getPackagesIgnoreSyncItemInfoAndClear(ignoreAccountBalance);

        StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append("[");
        for (int i = 0, expectedPacketsLength = expectedPackets.length; i < expectedPacketsLength; i++) {
            Packet expectedPacket = expectedPackets[i];
            expectedBuilder.append(expectedPacket);
            if (i + 1 < expectedPacketsLength) {
                expectedBuilder.append(", ");
            }
        }
        expectedBuilder.append("]");
        System.out.println("Expected: " + expectedBuilder);
        System.out.println("Received: " + receivedPackets);
        if (expectedPackets.length != receivedPackets.size()) {
            Assert.assertEquals(expectedPackets.length, receivedPackets.size());
        }



        for (Packet expectedPacket : expectedPackets) {
            Packet receivedPacket = null;
            for (Packet tmpReceivedPacket : receivedPackets) {
                if(comparePacket(expectedPacket, tmpReceivedPacket)) {
                    receivedPacket = tmpReceivedPacket;
                    break;
                }
            }
            if(receivedPacket == null) {
                Assert.fail("Packet was not sent: " + expectedPacket);
            }
            receivedPackets.remove(receivedPacket);
        }

        if(!receivedPackets.isEmpty()) {
            Assert.fail("More packages sent than expected: " + receivedPackets);
        }

    }

    protected boolean comparePacket(Packet expectedPacket, Packet receivedPacket) {
        if (expectedPacket instanceof AccountBalancePacket) {
            AccountBalancePacket expected = (AccountBalancePacket) expectedPacket;
            AccountBalancePacket received = (AccountBalancePacket) receivedPacket;
            return MathHelper.compareWithPrecision(expected.getAccountBalance(), received.getAccountBalance());
        } else if (expectedPacket instanceof LevelTaskPacket) {
            throw new UnsupportedOperationException();
            //  LevelTaskPacket expected = (LevelTaskPacket) expectedPacket;
            //  LevelTaskPacket received = (LevelTaskPacket) receivedPacket;
            //  return expected.isCompleted() == received.isCompleted() && ;
            //  Assert.assertEquals(expected.getQuestProgressInfo(), received.getQuestProgressInfo());
            //  Assert.assertEquals(expected.getQuestInfo(), received.getQuestInfo());
        } else if (expectedPacket instanceof LevelPacket) {
            LevelPacket expected = (LevelPacket) expectedPacket;
            LevelPacket received = (LevelPacket) receivedPacket;
            return expected.getLevel().equals(received.getLevel());
        } else if (expectedPacket instanceof XpPacket) {
            XpPacket expected = (XpPacket) expectedPacket;
            XpPacket received = (XpPacket) receivedPacket;
            return expected.getXp() == received.getXp() && expected.getXp2LevelUp() == received.getXp2LevelUp();
        } else if (expectedPacket instanceof HouseSpacePacket) {
            HouseSpacePacket expected = (HouseSpacePacket) expectedPacket;
            HouseSpacePacket received = (HouseSpacePacket) receivedPacket;
            return expected.getHouseSpace() == received.getHouseSpace();
        } else if (expectedPacket instanceof Message) {
            Message expected = (Message) expectedPacket;
            Message received = (Message) receivedPacket;
            return expected.getMessage().equals(received.getMessage());
        } else if (expectedPacket instanceof BaseChangedPacket) {
            BaseChangedPacket expected = (BaseChangedPacket) expectedPacket;
            BaseChangedPacket received = (BaseChangedPacket) receivedPacket;
            List<SimpleBase> alliances1 = new ArrayList<>(expected.getBaseAttributes().getAlliances());
            List<SimpleBase> alliances2 = new ArrayList<>(received.getBaseAttributes().getAlliances());
            alliances1.removeAll(alliances2);
            return expected.getType() == received.getType()
                    && expected.getBaseAttributes().getSimpleBase().equals(received.getBaseAttributes().getSimpleBase())
                    && expected.getBaseAttributes().getName().equals(received.getBaseAttributes().getName())
                    && expected.getBaseAttributes().isBot() == received.getBaseAttributes().isBot()
                    && expected.getBaseAttributes().isAbandoned() == received.getBaseAttributes().isAbandoned()
                    && expected.getBaseAttributes().getAlliances().size() == received.getBaseAttributes().getAlliances().size()
                    && alliances1.isEmpty();
        } else if (expectedPacket instanceof ChatMessage) {
            ChatMessage expected = (ChatMessage) expectedPacket;
            ChatMessage received = (ChatMessage) receivedPacket;
            return expected.getMessage().equals(received.getMessage())
                    && expected.getName().equals(received.getName())
                    && expected.getMessageId() == received.getMessageId();
        } else {
            Assert.fail("Unhandled packet: " + expectedPacket);
            return false;
        }
    }

    protected Message createMessage(String message, boolean showRegisterDialog) {
        Message messagePacket = new Message();
        messagePacket.setMessage(message);
        messagePacket.setShowRegisterDialog(showRegisterDialog);
        return messagePacket;
    }
    // ------------------- Action Service --------------------

    protected void waitForActionServiceDone(Integer planetId) throws TimeoutException, InterruptedException {
        ServerPlanetServices serverPlanetServices;
        if (planetId != null) {
            serverPlanetServices = planetSystemService.getServerPlanetServices(planetId);
        } else {
            serverPlanetServices = planetSystemService.getServerPlanetServices();
        }
        long maxTime = System.currentTimeMillis() + 100000;
        while (serverPlanetServices.getActionService().isBusy()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    protected void waitForActionServiceDone() throws TimeoutException, InterruptedException {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
        long maxTime = System.currentTimeMillis() + 100000;
        while (serverPlanetServices.getActionService().isBusy()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    protected void sendMoveCommand(Id movable, Index destination) throws Exception {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
        SyncBaseItem syncItem = (SyncBaseItem) serverPlanetServices.getItemService().getItem(movable);
        serverPlanetServices.getActionService().move(syncItem, destination);
    }

    protected void sendBuildCommand(Id builderId, Index toBeBuiltPosition, int toBeBuiltId) throws Exception {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
        SyncBaseItem builder = (SyncBaseItem) serverPlanetServices.getItemService().getItem(builderId);
        BaseItemType itemType = (BaseItemType) serverItemTypeService.getItemType(toBeBuiltId);
        serverPlanetServices.getActionService().build(builder, toBeBuiltPosition, itemType);
    }

    protected void sendFactoryCommand(Id factoryId, int toBeBuiltId) throws Exception {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
        SyncBaseItem factory = (SyncBaseItem) serverPlanetServices.getItemService().getItem(factoryId);
        BaseItemType itemType = (BaseItemType) serverItemTypeService.getItemType(toBeBuiltId);
        serverPlanetServices.getActionService().fabricate(factory, itemType);
    }

    protected void sendAttackCommand(Id actorId, Id targetId, Integer planetId) throws Exception {
        ServerPlanetServices serverPlanetServices;
        if (planetId != null) {
            serverPlanetServices = planetSystemService.getServerPlanetServices(planetId);
        } else {
            serverPlanetServices = planetSystemService.getServerPlanetServices();
        }
        SyncBaseItem actor = (SyncBaseItem) serverPlanetServices.getItemService().getItem(actorId);
        SyncBaseItem target = (SyncBaseItem) serverPlanetServices.getItemService().getItem(targetId);
        AttackFormationItem attackFormationItem = serverPlanetServices.getCollisionService().getDestinationHint(actor,
                actor.getBaseItemType().getWeaponType().getRange(),
                target.getSyncItemArea(),
                target.getTerrainType());
        if (!attackFormationItem.isInRange()) {
            throw new IllegalStateException("Not in range");
        }
        serverPlanetServices.getActionService().attack(actor, target, attackFormationItem.getDestinationHint(), attackFormationItem.getDestinationAngel(), true);
    }

    protected void sendAttackCommand(Id actorId, Id targetId) throws Exception {
        sendAttackCommand(actorId, targetId, null);
    }

    protected void sendAttackCommands(Collection<Id> attackers, Id targetId) throws Exception {
        for (Id attacker : attackers) {
            sendAttackCommand(attacker, targetId);
        }
    }

    protected void sendCollectCommand(Id harvesterId, Id resourceId) throws Exception {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
        SyncBaseItem harvester = (SyncBaseItem) serverPlanetServices.getItemService().getItem(harvesterId);
        SyncResourceItem syncResourceItem = (SyncResourceItem) serverPlanetServices.getItemService().getItem(resourceId);
        AttackFormationItem attackFormationItem = serverPlanetServices.getCollisionService().getDestinationHint(harvester,
                harvester.getBaseItemType().getHarvesterType().getRange(),
                syncResourceItem.getSyncItemArea(),
                syncResourceItem.getTerrainType());
        if (!attackFormationItem.isInRange()) {
            throw new IllegalStateException("Not in range");
        }
        serverPlanetServices.getActionService().collect(harvester, syncResourceItem, attackFormationItem.getDestinationHint(), attackFormationItem.getDestinationAngel());
    }

    protected void sendContainerLoadCommand(Id item, Id containerId) throws Exception {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
        SyncBaseItem container = (SyncBaseItem) serverPlanetServices.getItemService().getItem(containerId);
        SyncBaseItem syncItem = (SyncBaseItem) serverPlanetServices.getItemService().getItem(item);
        serverPlanetServices.getActionService().loadContainer(container, syncItem);
    }

    protected void sendUnloadContainerCommand(Id containerId, Index position) throws Exception {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices();
        SyncBaseItem container = (SyncBaseItem) serverPlanetServices.getItemService().getItem(containerId);
        serverPlanetServices.getActionService().unloadContainer(container, position);
    }

    protected void sendPickupBoxCommand(int planetId, Id picker, Id box) throws Exception {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(planetId);
        SyncBaseItem baseItem = (SyncBaseItem) serverPlanetServices.getItemService().getItem(picker);
        SyncBoxItem boxItem = (SyncBoxItem) serverPlanetServices.getItemService().getItem(box);
        AttackFormationItem attackFormationItem = serverPlanetServices.getCollisionService().getDestinationHint(baseItem,
                baseItem.getBaseItemType().getBoxPickupRange(),
                boxItem.getSyncItemArea(),
                boxItem.getTerrainType());
        if (!attackFormationItem.isInRange()) {
            throw new IllegalStateException("Not in range");
        }

        serverPlanetServices.getActionService().pickupBox(baseItem, boxItem, attackFormationItem.getDestinationHint(), attackFormationItem.getDestinationAngel());
    }

    // -------------------  Game Config --------------------

    protected void configureItemTypes() throws Exception {
        System.out.println("---- Configure Item Types ---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    protected void configureSimplePlanetNoResources() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Item Types
        setupItemTypes();
        // Planet
        DbPlanet dbPlanet1 = setupPlanet1();
        // Terrain
        setupMinimalTerrain(dbPlanet1);
        // QuestHubs
        setupOneLevel(dbPlanet1);

        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        planetSystemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    protected void configureSimplePlanet() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Item Types
        setupItemTypes();
        // Planet
        DbPlanet dbPlanet1 = setupPlanet1();
        // Terrain
        setupMinimalTerrain(dbPlanet1);
        // QuestHubs
        setupOneLevel(dbPlanet1);
        // Resource
        setupResource1(dbPlanet1, 1, new Rectangle(5000, 5000, 300, 300));

        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        planetSystemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    protected void configureMultiplePlanetsAndLevels() throws Exception {
        System.out.println("---- configureMultiplePlanetsAndLevels Game ---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Item Types
        setupItemTypes();

        DbPlanet dbPlanet1 = setupPlanet1();
        DbPlanet dbPlanet2 = setupPlanet2();
        DbPlanet dbPlanet3 = setupPlanet3();

        // Terrain
        setupMinimalTerrain(dbPlanet1);
        setupComplexTerrain(dbPlanet2);
        setupComplexTerrain2(dbPlanet3);
        // User Guidance
        setupMultipleLevels(dbPlanet1);
        setupMultipleLevels2(dbPlanet2);
        // Resource fields
        setupResource1(dbPlanet1, 10, new Rectangle(5000, 5000, 1000, 1000));
        setupResource1(dbPlanet2, 5, new Rectangle(5000, 5000, 1000, 1000));
        setupResource1(dbPlanet3, 6, new Rectangle(0, 0, 1000, 1000));

        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.activate();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    protected void setupItemTypes() {
        createSimpleBuilding();
        createHarvesterItemType();
        createAttackBaseItemType();
        createContainerBaseItemType();
        createHouseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        createAttackBaseItemType2();
        createMoney();
    }

    protected void configureOneLevelOnePlaneComplexTerrain() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Item Types
        createHarvesterItemType();
        createAttackBaseItemType();
        createContainerBaseItemType();
        createHouseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        createSimpleBuilding();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        createMoney();
        // Planet
        DbPlanet dbPlanet1 = setupPlanet1();
        // Terrain
        setupComplexTerrain(dbPlanet1);
        // QuestHubs
        setupOneLevel(dbPlanet1);
        //setupXpSettings();
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        planetSystemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    protected void configureOneLevelOnePlaneComplexTerrain2() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Item Types
        createHarvesterItemType();
        createAttackBaseItemType();
        createContainerBaseItemType();
        createHouseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        createSimpleBuilding();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        createMoney();
        // Planet
        DbPlanet dbPlanet1 = setupPlanet1();
        // Terrain
        setupComplexTerrain2(dbPlanet1);
        // QuestHubs
        setupOneLevel(dbPlanet1);
        //setupXpSettings();
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        planetSystemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // ------------------- Setup Item Types --------------------

    protected DbBaseItemType createBuilderBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_START_BUILDER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(1);
        dbBaseItemType.setXpOnKilling(1);
        dbBaseItemType.setConsumingHouseSpace(1);
        // DbBuilderType
        DbBuilderType dbBuilderType = new DbBuilderType();
        dbBuilderType.setProgress(1000);
        dbBuilderType.setRange(100);
        Set<DbBaseItemType> ableToBuild = new HashSet<DbBaseItemType>();
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HOUSE_ID));
        dbBuilderType.setAbleToBuild(ableToBuild);
        dbBaseItemType.setDbBuilderType(dbBuilderType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbBaseItemType.setDbMovableType(dbMovableType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_START_BUILDER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createFactoryBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 1);
        dbBaseItemType.setName(TEST_FACTORY_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(2);
        dbBaseItemType.setXpOnKilling(2);
        dbBaseItemType.setConsumingHouseSpace(2);
        // DbBuilderType
        DbFactoryType dbFactoryType = new DbFactoryType();
        dbFactoryType.setProgress(1000);
        Set<DbBaseItemType> ableToBuild = new HashSet<DbBaseItemType>();
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        dbFactoryType.setAbleToBuild(ableToBuild);
        dbBaseItemType.setDbFactoryType(dbFactoryType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_FACTORY_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createAttackBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_ATTACK_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(3);
        dbBaseItemType.setImageWidth(80);
        dbBaseItemType.setImageHeight(100);
        dbBaseItemType.setConsumingHouseSpace(2);
        // DbWeaponType
        DbWeaponType dbWeaponType = new DbWeaponType();
        dbWeaponType.setRange(100);
        dbWeaponType.setReloadTime(1);
        dbWeaponType.setDamage(1000);
        dbBaseItemType.setDbWeaponType(dbWeaponType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbBaseItemType.setDbMovableType(dbMovableType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_ATTACK_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private void finishAttackBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID);
        // DbWeaponType
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID), true);
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID), true);
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID), true);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
    }

    protected DbBaseItemType createAttackBaseItemType2() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_ATTACK_ITEM_2);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(3);
        // DbWeaponType
        DbWeaponType dbWeaponType = new DbWeaponType();
        dbWeaponType.setRange(100);
        dbWeaponType.setReloadTime(1);
        dbWeaponType.setDamage(1000);
        dbBaseItemType.setDbWeaponType(dbWeaponType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbBaseItemType.setDbMovableType(dbMovableType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        TEST_ATTACK_ITEM_ID_2 = dbBaseItemType.getId();
        // DbWeaponType
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID_2), true);
        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        return dbBaseItemType;
    }

    protected DbBaseItemType createContainerBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_CONTAINER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
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
        dbBaseItemType.setDbMovableType(dbMovableType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_CONTAINER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createSimpleBuilding() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 1);
        dbBaseItemType.setName(TEST_SIMPLE_BUILDING);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_SIMPLE_BUILDING_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createHouseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 1);
        dbBaseItemType.setName(TEST_HOUSE);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        // DbHouse
        DbHouseType dbHouseType = new DbHouseType();
        dbHouseType.setSpace(10);
        dbBaseItemType.setDbHouseType(dbHouseType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_HOUSE_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private void finishContainerBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID);
        // DbItemContainerType
        Set<DbBaseItemType> ableToContain = new HashSet<DbBaseItemType>();
        ableToContain.add(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbBaseItemType.getDbItemContainerType().setAbleToContain(ableToContain);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
    }

    private DbResourceItemType createMoney() {
        DbResourceItemType dbResourceItemType = (DbResourceItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbResourceItemType.class);
        setupImages(dbResourceItemType, 1);
        dbResourceItemType.setName(TEST_RESOURCE_ITEM);
        dbResourceItemType.setTerrainType(TerrainType.LAND);
        dbResourceItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbResourceItemType.setAmount(3);

        serverItemTypeService.saveDbItemType(dbResourceItemType);
        serverItemTypeService.activate();
        TEST_RESOURCE_ITEM_ID = dbResourceItemType.getId();
        return dbResourceItemType;
    }

    protected DbBaseItemType createHarvesterItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_HARVESTER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(4);
        // DbWeaponType
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
        TEST_HARVESTER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected void setupImages(DbItemType dbItemType, int count) {
        CrudChildServiceHelper<DbItemTypeImage> crud = dbItemType.getItemTypeImageCrud();
        for (int i = 0; i < count; i++) {
            DbItemTypeImage dbItemTypeImage = crud.createDbChild();
            dbItemTypeImage.setContentType("image");
            dbItemTypeImage.setAngelIndex(i);
            dbItemTypeImage.setStep(0);
            dbItemTypeImage.setFrame(0);
            dbItemTypeImage.setType(ItemTypeSpriteMap.SyncObjectState.RUN_TIME);
        }
    }

    protected DbBoxItemType createDbBoxItemType1() {
        DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        setupImages(dbBoxItemType, 1);
        dbBoxItemType.setName(TEST_BOX_ITEM_1);
        dbBoxItemType.setTerrainType(TerrainType.LAND);
        dbBoxItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBoxItemType.setTtl(100);

        serverItemTypeService.saveDbItemType(dbBoxItemType);
        serverItemTypeService.activate();
        TEST_BOX_ITEM_1_ID = dbBoxItemType.getId();
        return dbBoxItemType;
    }

    protected DbBoxItemType createDbBoxItemType2() {
        DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        setupImages(dbBoxItemType, 1);
        dbBoxItemType.setName(TEST_BOX_ITEM_2);
        dbBoxItemType.setTerrainType(TerrainType.LAND);
        dbBoxItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBoxItemType.setTtl(5000);

        serverItemTypeService.saveDbItemType(dbBoxItemType);
        serverItemTypeService.activate();
        TEST_BOX_ITEM_2_ID = dbBoxItemType.getId();
        return dbBoxItemType;
    }
    // ------------------- Setup Planet --------------------

    private DbPlanet setupPlanet1() {
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        dbPlanet.setName(TEST_PLANET_1);
        dbPlanet.setStartItemFreeRange(300);
        dbPlanet.setStartMoney(1000);
        dbPlanet.setStartItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        dbPlanet.setHouseSpace(20);
        dbPlanet.setMaxMoney(10000);
        dbPlanet.setStartRegion(createDbRegion(new Rectangle(0, 0, 5000, 5000)));

        DbPlanetItemTypeLimitation builder = dbPlanet.getItemLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbPlanetItemTypeLimitation factory = dbPlanet.getItemLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbPlanetItemTypeLimitation attacker = dbPlanet.getItemLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbPlanetItemTypeLimitation harvester = dbPlanet.getItemLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbPlanetItemTypeLimitation container = dbPlanet.getItemLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbPlanetItemTypeLimitation simpleBuilding = dbPlanet.getItemLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);
        DbPlanetItemTypeLimitation house = dbPlanet.getItemLimitationCrud().createDbChild();
        house.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HOUSE_ID));
        house.setCount(10);

        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        TEST_PLANET_1_ID = dbPlanet.getId();
        return dbPlanet;
    }

    private DbPlanet setupPlanet2() {
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        dbPlanet.setName(TEST_PLANET_2);
        dbPlanet.setStartItemFreeRange(300);
        dbPlanet.setStartMoney(1000);
        dbPlanet.setStartItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        dbPlanet.setHouseSpace(20);
        dbPlanet.setStartRegion(createDbRegion(new Rectangle(0, 0, 5000, 5000)));

        DbPlanetItemTypeLimitation builder = dbPlanet.getItemLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbPlanetItemTypeLimitation factory = dbPlanet.getItemLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbPlanetItemTypeLimitation attacker = dbPlanet.getItemLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbPlanetItemTypeLimitation harvester = dbPlanet.getItemLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbPlanetItemTypeLimitation container = dbPlanet.getItemLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbPlanetItemTypeLimitation simpleBuilding = dbPlanet.getItemLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);

        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        TEST_PLANET_2_ID = dbPlanet.getId();
        return dbPlanet;
    }


    private DbPlanet setupPlanet3() {
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        dbPlanet.setName(TEST_PLANET_3);
        dbPlanet.setStartItemFreeRange(300);
        dbPlanet.setStartMoney(1000);
        dbPlanet.setStartItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        dbPlanet.setHouseSpace(20);
        dbPlanet.setStartRegion(createDbRegion(new Rectangle(0, 0, 5000, 5000)));

        DbPlanetItemTypeLimitation builder = dbPlanet.getItemLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbPlanetItemTypeLimitation factory = dbPlanet.getItemLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbPlanetItemTypeLimitation attacker = dbPlanet.getItemLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbPlanetItemTypeLimitation harvester = dbPlanet.getItemLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbPlanetItemTypeLimitation container = dbPlanet.getItemLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbPlanetItemTypeLimitation simpleBuilding = dbPlanet.getItemLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);

        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        TEST_PLANET_3_ID = dbPlanet.getId();
        return dbPlanet;
    }

    // ------------------- Setup Terrain --------------------

    protected DbTerrainSetting setupComplexTerrain(DbPlanet dbPlanet) {
        setupTerrainImages();
        DbTerrainSetting dbTerrainSetting = setupComplexRealGameTerrain(createDbSurfaceImage(SurfaceType.LAND));
        dbPlanet.setDbTerrainSetting(dbTerrainSetting);
        return dbTerrainSetting;
    }

    protected DbTerrainSetting setupComplexTerrain2(DbPlanet dbPlanet) {
        setupTerrainImages();
        DbTerrainSetting dbTerrainSetting = setupComplexRealGameTerrain2();
        dbPlanet.setDbTerrainSetting(dbTerrainSetting);
        return dbTerrainSetting;
    }

    protected void setupTerrainImages() {
        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        DbTerrainImage dbTerrainImage1 = dbTerrainImageGroup.getTerrainImageCrud().createDbChild();
        dbTerrainImage1.setTiles(4, 10);

        DbTerrainImage dbTerrainImage2 = dbTerrainImageGroup.getTerrainImageCrud().createDbChild();
        dbTerrainImage2.setTiles(10, 4);

        DbTerrainImage dbTerrainImage3 = dbTerrainImageGroup.getTerrainImageCrud().createDbChild();
        dbTerrainImage3.setTiles(4, 4);

        DbTerrainImage dbTerrainImage4 = dbTerrainImageGroup.getTerrainImageCrud().createDbChild();
        dbTerrainImage4.setTiles(10, 10);

        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup);

        TERRAIN_IMAGE_4x10 = dbTerrainImage1.getId();
        TERRAIN_IMAGE_10x4 = dbTerrainImage2.getId();
        TERRAIN_IMAGE_4x4 = dbTerrainImage3.getId();
        TERRAIN_IMAGE_10x10 = dbTerrainImage4.getId();
    }

    protected DbTerrainSetting setupMinimalTerrain(DbPlanet dbPlanet) {
        DbTerrainSetting dbTerrainSetting = setupMinimalRealGameTerrain(createDbSurfaceImage(SurfaceType.LAND));
        dbPlanet.setDbTerrainSetting(dbTerrainSetting);
        return dbTerrainSetting;
    }

    protected DbSurfaceImage createDbSurfaceImage(SurfaceType surfaceType) {
        DbSurfaceImage dbSurfaceImage = terrainService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage.setSurfaceType(surfaceType);
        terrainService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage);
        terrainService.activate();
        return dbSurfaceImage;
    }

    protected DbTerrainImage createDbTerrainImage(int tileWidth, int tileHeight) {
        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        DbTerrainImage dbTerrainImage = dbTerrainImageGroup.getTerrainImageCrud().createDbChild();
        dbTerrainImage.setTiles(tileWidth, tileHeight);
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup);
        terrainService.activate();
        return dbTerrainImage;
    }

    protected DbTerrainSetting setupMinimalRealGameTerrain(DbSurfaceImage dbSurfaceImage) {
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.init(null);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), dbSurfaceImage);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        return dbTerrainSetting;
    }

    protected DbTerrainSetting setupComplexRealGameTerrain(DbSurfaceImage dbSurfaceImage) {
        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChildren().iterator().next();

        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.init(null);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), dbSurfaceImage);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        // Setup Terrain Images
        Collection<DbTerrainImagePosition> dbTerrainImagePositions = new ArrayList<>();
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(10, 0), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10), TerrainImagePosition.ZIndex.LAYER_1));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(0, 13), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4), TerrainImagePosition.ZIndex.LAYER_2));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(0, 21), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4), TerrainImagePosition.ZIndex.LAYER_1));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(13, 22), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4), TerrainImagePosition.ZIndex.LAYER_2));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(20, 16), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4), TerrainImagePosition.ZIndex.LAYER_1));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(20, 7), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4), TerrainImagePosition.ZIndex.LAYER_2));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(10, 29), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10), TerrainImagePosition.ZIndex.LAYER_1));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(15, 26), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10), TerrainImagePosition.ZIndex.LAYER_2));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(21, 29), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10), TerrainImagePosition.ZIndex.LAYER_1));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(35, 20), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10), TerrainImagePosition.ZIndex.LAYER_2));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(36, 5), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10), TerrainImagePosition.ZIndex.LAYER_1));
        dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().updateDbChildren(dbTerrainImagePositions);
        return dbTerrainSetting;
    }

    protected DbTerrainSetting setupComplexRealGameTerrain2() {
        DbSurfaceImage land = createDbSurfaceImage(SurfaceType.LAND);
        DbSurfaceImage landCoast = createDbSurfaceImage(SurfaceType.LAND_COAST);
        DbSurfaceImage waterCoast = createDbSurfaceImage(SurfaceType.WATER_COAST);
        DbSurfaceImage water = createDbSurfaceImage(SurfaceType.WATER);

        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChildren().iterator().next();

        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.init(null);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        // Surface images
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 0, 25, 40), land), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(25, 0, 1, 41), landCoast), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 40, 25, 1), landCoast), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 41, 27, 1), waterCoast), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(26, 0, 1, 41), waterCoast), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(27, 0, 25, 50), water), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 42, 27, 8), water), null);
        return dbTerrainSetting;
    }

    // ------------------- Setup Levels --------------------

    private void setupOneLevel(DbPlanet dbPlanet) throws Exception {
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel.setXp(Integer.MAX_VALUE);
        dbLevel.setNumber(TEST_LEVEL_2_REAL);
        dbLevel.setDbPlanet(dbPlanet);
        // Limitation
        DbLevelItemTypeLimitation builder = dbLevel.getItemTypeLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbLevelItemTypeLimitation factory = dbLevel.getItemTypeLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbLevelItemTypeLimitation attacker = dbLevel.getItemTypeLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbLevelItemTypeLimitation harvester = dbLevel.getItemTypeLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbLevelItemTypeLimitation container = dbLevel.getItemTypeLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbLevelItemTypeLimitation simpleBuilding = dbLevel.getItemTypeLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);
        DbLevelItemTypeLimitation house = dbLevel.getItemTypeLimitationCrud().createDbChild();
        house.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HOUSE_ID));
        house.setCount(10);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        TEST_LEVEL_2_REAL_ID = dbLevel.getId();
    }

    private void setupMultipleLevels(DbPlanet dbPlanet) throws Exception {
        // Setup Level - Task - Tutorial
        DbTutorialConfig tut1 = createTutorial1();
        DbLevel dbSimLevel = userGuidanceService.getDbLevelCrud().createDbChild();
        dbSimLevel.setNumber(TEST_LEVEL_1_SIMULATED);
        dbSimLevel.setXp(1);
        DbLevelTask dbSimLevelTask = dbSimLevel.getLevelTaskCrud().createDbChild();
        dbSimLevelTask.setDbTutorialConfig(tut1);
        dbSimLevelTask.setName(TEST_LEVEL_TASK_1_1_SIMULATED_NAME);
        dbSimLevelTask.setHtml("Description");
        dbSimLevelTask.setXp(1);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbSimLevel);
        TEST_LEVEL_1_SIMULATED_ID = dbSimLevel.getId();
        TEST_LEVEL_TASK_1_1_SIMULATED_ID = dbSimLevelTask.getId();

        DbLevel dbLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel1.setDbPlanet(dbPlanet);
        dbLevel1.setNumber(TEST_LEVEL_2_REAL);
        dbLevel1.setXp(220);
        setLimitation(dbLevel1);
        DbLevelTask dbLevelTask1 = setupCreateLevelTask1RealGameLevel(dbLevel1);
        DbLevelTask dbLevelTask2 = setupCreateLevelTask2RealGameLevel(dbLevel1);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);

        DbLevel dbLevel2 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel2.setDbPlanet(dbPlanet);
        dbLevel2.setNumber(TEST_LEVEL_3_REAL);
        setLimitation(dbLevel2);
        dbLevel2.setXp(400);
        DbLevelTask dbLevelTask3 = setupCreateLevelTask3RealGameLevel(dbLevel2);
        DbLevelTask dbLevelTask4 = setupCreateLevelTask4RealGameLevel(dbLevel2);
        DbTutorialConfig tut2 = createTutorial1();
        DbTutorialConfig tut3 = createTutorial1();
        DbLevelTask dbSimLevelTask2 = dbLevel2.getLevelTaskCrud().createDbChild();
        dbSimLevelTask2.setDbTutorialConfig(tut2);
        dbSimLevelTask2.setXp(2);
        dbSimLevelTask2.setName(TEST_LEVEL_TASK_3_3_SIMULATED_NAME);
        dbSimLevelTask2.setHtml("Task3Level2Descr");
        DbLevelTask dbSimLevelTask3 = dbLevel2.getLevelTaskCrud().createDbChild();
        dbSimLevelTask3.setDbTutorialConfig(tut3);
        dbSimLevelTask3.setXp(3);
        dbSimLevelTask3.setName(TEST_LEVEL_TASK_4_3_SIMULATED_NAME);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel2);

        DbLevel dbLevel3 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel3.setDbPlanet(dbPlanet);
        dbLevel3.setXp(Integer.MAX_VALUE);
        dbLevel3.setNumber(TEST_LEVEL_4_REAL);
        setLimitation(dbLevel3);
        DbLevelTask dbLevelTask5 = setupCreateLevelTask5RealGameLevel(dbLevel3);
        DbLevelTask dbLevelTask6 = setupCreateLevelTask6RealGameLevel(dbLevel3);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel3);


        TEST_LEVEL_2_REAL_ID = dbLevel1.getId();
        TEST_LEVEL_TASK_1_2_REAL_ID = dbLevelTask1.getId();
        TEST_LEVEL_TASK_2_2_REAL_ID = dbLevelTask2.getId();
        TEST_LEVEL_3_REAL_ID = dbLevel2.getId();
        TEST_LEVEL_TASK_1_3_REAL_ID = dbLevelTask3.getId();
        TEST_LEVEL_TASK_2_3_REAL_ID = dbLevelTask4.getId();
        TEST_LEVEL_TASK_3_3_SIMULATED_ID = dbSimLevelTask2.getId();
        TEST_LEVEL_TASK_4_3_SIMULATED_ID = dbSimLevelTask3.getId();
        TEST_LEVEL_4_REAL_ID = dbLevel3.getId();
        TEST_LEVEL_TASK_1_4_REAL_ID = dbLevelTask5.getId();
        TEST_LEVEL_TASK_2_4_REAL_ID = dbLevelTask6.getId();

        userGuidanceService.activateLevels();
    }

    private void setupMultipleLevels2(DbPlanet dbPlanet2) throws Exception {
        DbLevel dbLevel5 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel5.setDbPlanet(dbPlanet2);
        dbLevel5.setNumber(TEST_LEVEL_5_REAL);
        dbLevel5.setXp(220);
        setLimitation(dbLevel5);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel5);

        TEST_LEVEL_5_REAL_ID = dbLevel5.getId();
        userGuidanceService.activateLevels();
    }

    private void setLimitation(DbLevel dbLevel) {
        DbLevelItemTypeLimitation builder = dbLevel.getItemTypeLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbLevelItemTypeLimitation factory = dbLevel.getItemTypeLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbLevelItemTypeLimitation attacker = dbLevel.getItemTypeLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbLevelItemTypeLimitation harvester = dbLevel.getItemTypeLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbLevelItemTypeLimitation container = dbLevel.getItemTypeLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbLevelItemTypeLimitation simpleBuilding = dbLevel.getItemTypeLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);
    }

    private DbLevel createDbLevel2(DbPlanet realGamePlanet) {
        // TODO
        Assert.fail();
        return null;
        /*
        DbLevel dbLevel1 = realGamePlanet.getLevelCrud().createDbChild();
        dbLevel1.setHouseSpace(20);
        dbLevel1.setMaxMoney(10000);
        dbLevel1.setItemSellFactor(0.5);
        dbLevel1.setNumber(TEST_LEVEL_2_REAL);
        // Limitation
        DbLevelItemTypeLimitation builder = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbLevelItemTypeLimitation factory = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbLevelItemTypeLimitation attacker = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbLevelItemTypeLimitation harvester = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbLevelItemTypeLimitation container = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbLevelItemTypeLimitation simpleBuilding = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);

        return dbLevel1;
        */
    }

    protected DbTutorialConfig createTutorial1() {
        // Tutorial
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        // Terrain
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        dbTutorialConfig.setDbTerrainSetting(dbTerrainSetting);
        // Task
        DbTaskConfig dbTaskConfig = dbTutorialConfig.getDbTaskConfigCrudChildServiceHelper().createDbChild();
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(100);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbTaskConfig.setConditionConfig(dbConditionConfig);
        tutorialService.getDbTutorialCrudRootServiceHelper().updateDbChild(dbTutorialConfig);
        return dbTutorialConfig;
    }

    private DbLevelTask setupCreateLevelTask1RealGameLevel(DbLevel dbLevel) {
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask.setName(TEST_LEVEL_TASK_1_2_REAL_NAME);
        dbLevelTask.setHtml("Descr2");
        // Rewards
        dbLevelTask.setMoney(10);
        dbLevelTask.setXp(100);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(3);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        return dbLevelTask;
    }

    private DbLevelTask setupCreateLevelTask3RealGameLevel(DbLevel dbLevel) {
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask.setName("Task3Level2");
        dbLevelTask.setHtml("DecrTask3Level2");
        // Rewards
        dbLevelTask.setMoney(10);
        dbLevelTask.setXp(100);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(200);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        return dbLevelTask;
    }

    private DbLevelTask setupCreateLevelTask4RealGameLevel(DbLevel dbLevel) {
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        // Rewards
        dbLevelTask.setMoney(10);
        dbLevelTask.setXp(100);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(300);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        return dbLevelTask;
    }


    private DbLevelTask setupCreateLevelTask2RealGameLevel(DbLevel dbLevel) {
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask.setName(TEST_LEVEL_TASK_2_2_REAL_NAME);
        dbLevelTask.setHtml("Descr222");
        // Rewards
        dbLevelTask.setMoney(80);
        dbLevelTask.setXp(120);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(2);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        return dbLevelTask;
    }

    protected DbLevelTask setupCreateLevelTask5RealGameLevel(DbLevel dbLevel) {
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        // Rewards
        dbLevelTask.setMoney(10);
        dbLevelTask.setXp(80);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_POSITION);
        DbItemTypePositionComparisonConfig dbItemTypePositionComparisonConfig = new DbItemTypePositionComparisonConfig();
        dbItemTypePositionComparisonConfig.setRegion(createDbRegion(new Rectangle(200, 200, 1000, 1000)));
        dbItemTypePositionComparisonConfig.setTimeInMinutes(1);
        DbComparisonItemCount dbComparisonItemCount = dbItemTypePositionComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setItemType(serverItemTypeService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        dbComparisonItemCount.setCount(1);
        dbComparisonItemCount = dbItemTypePositionComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setItemType(serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        dbComparisonItemCount.setCount(1);
        dbConditionConfig.setDbAbstractComparisonConfig(dbItemTypePositionComparisonConfig);
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        return dbLevelTask;
    }

    private DbLevelTask setupCreateLevelTask6RealGameLevel(DbLevel dbLevel) {
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        // Rewards
        dbLevelTask.setMoney(20);
        dbLevelTask.setXp(90);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
        DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig = new DbSyncItemTypeComparisonConfig();
        DbComparisonItemCount limit = dbSyncItemTypeComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        limit.setItemType(serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        limit.setCount(5);
        limit = dbSyncItemTypeComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        limit.setItemType(serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        limit.setCount(1);
        dbConditionConfig.setDbAbstractComparisonConfig(dbSyncItemTypeComparisonConfig);
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        return dbLevelTask;
    }

    // ------------------- Setup minimal bot --------------------

    protected DbBotConfig setupMinimalNoAttackBot(int planetId, Rectangle realmRectangle) {
        DbRegion realm = createDbRegion(realmRectangle);
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(planetId);
        DbBotConfig dbBotConfig = dbPlanet.getBotCrud().createDbChild();
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(realm);
        DbBotEnragementStateConfig dbBotEnragementStateConfig = dbBotConfig.getEnrageStateCrud().createDbChild();
        dbBotEnragementStateConfig.setName("NormalTest");
        DbBotItemConfig builder = dbBotEnragementStateConfig.getBotItemCrud().createDbChild();
        builder.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(1);
        builder.setCreateDirectly(true);
        builder.setRegion(realm);
        DbBotItemConfig factory = dbBotEnragementStateConfig.getBotItemCrud().createDbChild();
        factory.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(1);
        factory.setRegion(realm);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.getPlanet(planetId).deactivate();
        planetSystemService.getPlanet(planetId).activate(dbPlanet);
        return dbBotConfig;
    }

    protected DbBotConfig setupMinimalBot(int planetId, Rectangle rectangleRealm) {
        DbRegion realm = createDbRegion(rectangleRealm);
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(planetId);
        DbBotConfig dbBotConfig = dbPlanet.getBotCrud().createDbChild();
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(realm);
        DbBotEnragementStateConfig dbBotEnragementStateConfig = dbBotConfig.getEnrageStateCrud().createDbChild();
        dbBotEnragementStateConfig.setName("NormalTest");
        DbBotItemConfig builder = dbBotEnragementStateConfig.getBotItemCrud().createDbChild();
        builder.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(1);
        builder.setCreateDirectly(true);
        builder.setRegion(realm);
        DbBotItemConfig factory = dbBotEnragementStateConfig.getBotItemCrud().createDbChild();
        factory.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(1);
        factory.setRegion(realm);
        DbBotItemConfig defence = dbBotEnragementStateConfig.getBotItemCrud().createDbChild();
        defence.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        defence.setCount(2);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.getPlanet(planetId).deactivate();
        planetSystemService.getPlanet(planetId).activate(dbPlanet);
        return dbBotConfig;
    }

    protected void waitForBotToBuildup(int planetId, BotConfig botConfig) throws InterruptedException, TimeoutException {
        waitForBotToBuildup(planetId, botConfig, 100000);
    }

    protected void waitForBotToBuildup(int planetId, BotConfig botConfig, int timeOut) throws InterruptedException, TimeoutException {
        long maxTime = System.currentTimeMillis() + timeOut;
        BotService botService = planetSystemService.getPlanet(planetId).getPlanetServices().getBotService();
        while (!botService.getBotRunner(botConfig).isBuildupUseInTestOnly()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    // ------------------- Setup Resource --------------------

    protected DbRegionResource setupResource1(DbPlanet dbPlanet, int count, Rectangle region) {
        DbRegionResource dbRegionResource = dbPlanet.getRegionResourceCrud().createDbChild();
        dbRegionResource.setResourceItemType(serverItemTypeService.getDbResourceItemType(TEST_RESOURCE_ITEM_ID));
        dbRegionResource.setCount(count);
        dbRegionResource.setMinDistanceToItems(100);
        dbRegionResource.setRegion(createDbRegion(region));
        return dbRegionResource;
    }

    // ------------------- History helpers --------------------

    protected List<DbHistoryElement> getAllHistoryEntriesOfType(DbHistoryElement.Type type) throws Exception {
        List<DbHistoryElement> dbHistoryElements = HibernateUtil.loadAll(sessionFactory, DbHistoryElement.class);
        for (Iterator<DbHistoryElement> iterator = dbHistoryElements.iterator(); iterator.hasNext(); ) {
            DbHistoryElement dbHistoryElement = iterator.next();
            if (dbHistoryElement.getType() != type) {
                iterator.remove();
            }
        }
        return dbHistoryElements;
    }


    protected void waitForHistoryType(DbHistoryElement.Type type) throws Exception {
        long maxTime = System.currentTimeMillis() + 100000;

        while (true) {
            List<DbHistoryElement> dbHistoryElements = HibernateUtil.loadAll(sessionFactory, DbHistoryElement.class);
            for (DbHistoryElement dbHistoryElement : dbHistoryElements) {
                if (dbHistoryElement.getType() == type) {
                    return;
                }
            }
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    protected void assertNoHistoryType(DbHistoryElement.Type type) throws Exception {
        List<DbHistoryElement> dbHistoryElements = HibernateUtil.loadAll(sessionFactory, DbHistoryElement.class);
        for (DbHistoryElement dbHistoryElement : dbHistoryElements) {
            if (dbHistoryElement.getType() == type) {
                Assert.fail("Unexpected history entry found: " + type);
            }
        }
    }

    // ------------------- Mgmt helpers --------------------

    protected void assertBackupSummery(int backupCount, int itemCount, int baseCount, int userStateCount) {
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        Assert.assertEquals("backupCount", backupCount, backupSummaries.size());
        BackupSummary backupSummary = backupSummaries.get(0);
        Assert.assertEquals("itemCount", itemCount, backupSummary.getItemCount());
        Assert.assertEquals("baseCount", baseCount, backupSummary.getBaseCount());
        Assert.assertEquals("userStateCount", userStateCount, backupSummary.getUserStateCount());
    }

    // ------------------- Session Config --------------------

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private void beginOpenSessionInViewFilter() {
        HibernateUtil.openSession4InternalCall(sessionFactory);
    }

    private void endOpenSessionInViewFilter() {
        sessionFactory.getCurrentSession().clear();
        HibernateUtil.closeSession4InternalCall(sessionFactory);
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

    // ------------------- Wicket --------------------

    /**
     * Asserts that that the BookmarkablePageLink identified by "id" points to the page as expected
     * - including parameters.
     *
     * @param id
     * @param pageClass
     * @param parameters
     */
    protected void assertBookmarkablePageLink(WicketTester wicketTester, final String id,
                                              final Class<? extends WebPage> pageClass, final PageParameters parameters) {
        BookmarkablePageLink<?> pageLink = null;
        try {
            pageLink = (BookmarkablePageLink<?>) wicketTester.getComponentFromLastRenderedPage(id);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Component with id:" + id +
                    " is not a BookmarkablePageLink");
        }

        junit.framework.Assert.assertEquals("BookmarkablePageLink: " + id + " is pointing to the wrong page",
                pageClass, pageLink.getPageClass());

        junit.framework.Assert.assertEquals(
                "One or more of the parameters associated with the BookmarkablePageLink: " + id +
                        " do not match", parameters, pageLink.getPageParameters());
    }

    public void assertCssClass(WicketTester tester, String path, String cssClass) {
        Component component = tester.getComponentFromLastRenderedPage(path);
        Assert.assertNotNull("No such component: " + path, component);
        for (IBehavior iBehavior : component.getBehaviors()) {
            if (iBehavior instanceof SimpleAttributeModifier) {
                SimpleAttributeModifier simpleAttributeModifier = (SimpleAttributeModifier) iBehavior;
                if (simpleAttributeModifier.getAttribute().equals("class") && simpleAttributeModifier.getValue().equals(cssClass)) {
                    return;
                }
            }
        }
        Assert.fail("No such CSS class: " + cssClass);
    }

    public void assertCmsImage(WicketTester tester, String path, DbCmsImage descImg) throws Exception {
        Component component = tester.getComponentFromLastRenderedPage(path);
        Assert.assertNotNull("No such component: " + path, component);
        Image image = (Image) component;
        LocalizedImageResource localizedImageResource = (LocalizedImageResource) getPrivateField(Image.class, image, "localizedImageResource");
        ValueMap valueMap = (ValueMap) getPrivateField(LocalizedImageResource.class, localizedImageResource, "resourceParameters");
        Assert.assertEquals((int) descImg.getId(), valueMap.getInt(CmsImageResource.ID));
    }

    // ------------------- User --------------------
    protected UserState getUserState() {
        return userService.getUserState();
    }
    // ------------------- Div --------------------

    protected GlobalServices createMockGlobalServices() {
        GlobalServices planetServices = EasyMock.createNiceMock(GlobalServices.class);
        EasyMock.replay(planetServices);
        return planetServices;
    }

    protected PlanetServices createMockPlanetServices() {
        PlanetServices planetServices = EasyMock.createNiceMock(PlanetServices.class);
        AbstractTerrainService terrainService = EasyMock.createNiceMock(AbstractTerrainService.class);
        EasyMock.expect(planetServices.getTerrainService()).andReturn(terrainService);
        EasyMock.replay(planetServices);
        return planetServices;
    }

    public static void setPrivateField(Class clazz, Object object, String fieldName, Object value) throws Exception {
        if (AopUtils.isJdkDynamicProxy(object)) {
            object = ((Advised) object).getTargetSource().getTarget();
        }
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    public static void setPrivateStaticField(Class clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
        field.setAccessible(false);
    }

    public static Object getPrivateField(Class clazz, Object object, String fieldName) throws Exception {
        if (AopUtils.isJdkDynamicProxy(object)) {
            object = ((Advised) object).getTargetSource().getTarget();
        }
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object result = field.get(object);
        field.setAccessible(false);
        return result;
    }

    public static Object deAopProxy(Object object) throws Exception {
        if (AopUtils.isJdkDynamicProxy(object)) {
            return ((Advised) object).getTargetSource().getTarget();
        } else {
            return object;
        }
    }

    // ---------- DB Helper -------
    @Autowired
    public void setSessionFactory(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void printQueryDb(String sql) {
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("SQL: " + sql);
        System.out.println("------------------------------------------------------------------------------------------");
        final StringBuilder header = new StringBuilder();
        final StringBuilder data = new StringBuilder();
        jdbcTemplate.query(sql, new RowMapper<Void>() {
            @Override
            public Void mapRow(ResultSet resultSet, int i) throws SQLException {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
                for (int column = 0; column < columnCount; column++) {
                    if (i == 0) {
                        header.append(resultSetMetaData.getColumnName(column + 1));
                    }
                    data.append(resultSet.getString(column + 1));
                    header.append("\t");
                    data.append("\t");
                }
                data.append("\n");
                return null;
            }
        });
        System.out.println(header.toString());
        System.out.print(data.toString());
        System.out.println("------------------------------------------------------------------------------------------");
    }

    public void assertQueryDb(String sql, String expected) {
        final StringBuilder data = new StringBuilder();
        jdbcTemplate.query(sql, new RowMapper<Void>() {
            @Override
            public Void mapRow(ResultSet resultSet, int i) throws SQLException {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
                for (int column = 0; column < columnCount; column++) {
                    data.append(resultSet.getString(column + 1));
                    if (column + 1 < columnCount) {
                        data.append(",");
                    }
                }
                return null;
            }
        });
        Assert.assertEquals(expected, data.toString());
    }

}
