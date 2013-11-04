package com.btxtech.game.services;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.common.info.StartPointInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
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
import com.btxtech.game.jsre.common.packets.BaseLostPacket;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.EnergyPacket;
import com.btxtech.game.jsre.common.packets.HouseSpacePacket;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.ServerRebootMessagePacket;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.packets.UnlockContainerPacket;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.jsre.common.packets.UserPacket;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.bot.DbBotEnragementStateConfig;
import com.btxtech.game.services.bot.DbBotItemConfig;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.gwt.MovableServiceImpl;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBuilderType;
import com.btxtech.game.services.item.itemType.DbConsumerType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.services.item.itemType.DbGeneratorType;
import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.item.itemType.DbHouseType;
import com.btxtech.game.services.item.itemType.DbItemContainerType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbMovableType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerTerrainService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.db.DbPlanetItemTypeLimitation;
import com.btxtech.game.services.planet.db.DbRegionResource;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.playback.impl.PlaybackServiceImpl;
import com.btxtech.game.services.socialnet.facebook.FacebookAge;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUser;
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
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelItemTypeLimitation;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbComparisonItemCount;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import com.btxtech.game.services.utg.condition.DbItemTypePositionComparisonConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import com.btxtech.game.wicket.WicketApplication;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.LocalizedImageResource;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
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
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.subethamail.wiser.Wiser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletRequest;
import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    protected static final String TEST_CONSUMER_TYPE = "TEST_CONSUMER_TYPE";
    protected static int TEST_CONSUMER_TYPE_ID = -1;
    protected static final String TEST_CONSUMER_ATTACK_MOVABLE_TYPE = "TEST_CONSUMER_ATTACK_MOVABLE_TYPE";
    protected static int TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID = -1;
    protected static final String TEST_GENERATOR_TYPE = "TEST_GENERATOR_TYPE";
    protected static int TEST_GENERATOR_TYPE_ID = -1;
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
    protected static int TEST_LEVEL_6_REAL_ID = -1;
    // Level numbers
    protected static final int TEST_LEVEL_1_SIMULATED = 1;
    protected static final int TEST_LEVEL_2_REAL = 2;
    protected static final int TEST_LEVEL_3_REAL = 3;
    protected static final int TEST_LEVEL_4_REAL = 4;
    protected static final int TEST_LEVEL_5_REAL = 5;
    protected static final int TEST_LEVEL_6_REAL = 6;
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
    private BackupService backupService;
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
    @Autowired
    private WicketApplication wicketApplication;
    @Autowired
    private GuildService guildService;
    @Autowired
    private PropertyService propertyService;
    @PersistenceContext
    private EntityManager entityManager;
    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;
    private MockHttpSession mockHttpSession;
    private SecurityContext securityContext;
    private MovableService movableService;
    private PlaybackServiceImpl playbackService;
    private JdbcTemplate jdbcTemplate;
    private Wiser wiser;
    protected Log log = LogFactory.getLog(AbstractServiceTest.class);
    private WicketTester wicketTester;

    // ---------------------- Spring  -----------------------
    protected PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
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

    protected BaseAttributes getBaseAttributes(int planetId, String baseName) {
        BaseService baseService = planetSystemService.getServerPlanetServices(planetId).getBaseService();
        for (BaseAttributes baseAttributes : baseService.getAllBaseAttributes()) {
            if (baseAttributes.getName().equals(baseName)) {
                return baseAttributes;
            }
        }
        throw new IllegalArgumentException("No such base: " + baseName);
    }

    protected BaseAttributes getBaseAttributes(int planetId, SimpleBase simpleBase) {
        BaseService baseService = planetSystemService.getServerPlanetServices(planetId).getBaseService();
        for (BaseAttributes baseAttributes : baseService.getAllBaseAttributes()) {
            if (baseAttributes.getSimpleBase().equals(simpleBase)) {
                return baseAttributes;
            }
        }
        throw new IllegalArgumentException("No such base: " + simpleBase);
    }

    // ------------------- DbItemType helpers --------------------

    protected void setupDbItemTypeId(DbItemType dbItemType, int id) throws Exception {
        setPrivateField(DbItemType.class, dbItemType, "id", id);
        dbItemType.init(null);
    }

    // ------------------- Sync Items --------------------

    public static SyncBaseItem createSyncBaseItem(BaseItemType baseItemType, Index position, Id id, GlobalServices globalServices, PlanetServices planetServices, SimpleBase simpleBase) throws Exception {
        SyncBaseItem syncBaseItem = new SyncBaseItem(id, null, baseItemType, globalServices, planetServices, simpleBase);
        syncBaseItem.setBuildup(1.0);
        syncBaseItem.getSyncItemArea().setPosition(position);
        return syncBaseItem;
    }

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id, GlobalServices globalServices, PlanetServices planetServices, SimpleBase simpleBase) throws Exception {
        return createSyncBaseItem((BaseItemType) serverItemTypeService.getItemType(itemTypeId), position, id, globalServices, planetServices, simpleBase);
    }

    protected SyncBaseItem createFactorySyncBaseItem(int itemTypeId, Index position, Id id, GlobalServices globalServices, PlanetServices planetServices, SimpleBase simpleBase) throws Exception {
        // Due to rally point calculation
        SyncBaseItem syncBaseItem = new SyncBaseItem(id, position, (BaseItemType) serverItemTypeService.getItemType(itemTypeId), globalServices, planetServices, simpleBase);
        syncBaseItem.setBuildup(1.0);
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
    protected SimpleBase getOrCreateBase() {
        try {
            if (getMovableService().getRealGameInfo(START_UID_1, null).getStartPointInfo() != null) {
                return createBase(new Index(1000, 1000));
            }
            SimpleBase simpleBase = getMovableService().getRealGameInfo(START_UID_1, null).getBase();
            if (simpleBase == null) {
                throw new IllegalStateException("simpleBase == null");
            }
            return simpleBase;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attention: closes the current connection!!!
     *
     * @return Simple Base
     */
    protected SimpleBase createBase(Index startPoint) {
        try {
            getMovableService().getRealGameInfo(START_UID_1, null); // create connection
            RealGameInfo realGameInfo = getMovableService().createBase(START_UID_1, startPoint);
            if (realGameInfo == null) {
                throw new IllegalStateException("realGameInfo == null");
            }
            SimpleBase simpleBase = getMovableService().getRealGameInfo(START_UID_1, null).getBase();
            if (simpleBase == null) {
                throw new IllegalStateException("simpleBase == null");
            }
            return simpleBase;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        return getFirstSynItemId(getOrCreateBase(), itemTypeId, region);
    }

    protected Id getFirstSynItemId(SimpleBase simpleBase, int itemTypeId) {
        return getFirstSynItemId(simpleBase, itemTypeId, null);
    }

    protected Id getFirstSynItemId(SimpleBase simpleBase, int itemTypeId, Rectangle region) {
        try {
            for (SyncItemInfo syncItemInfo : getMovableService().getAllSyncInfo(START_UID_1)) {
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
        } catch (NoConnectionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("No such sync item: ItemTypeID=" + itemTypeId + " simpleBase=" + simpleBase);
    }

    protected List<Id> getAllSynItemId(int itemTypeId) {
        return getAllSynItemId(getOrCreateBase(), itemTypeId, null);
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
            try {
                allSyncInfos = getMovableService().getAllSyncInfo(START_UID_1);
            } catch (NoConnectionException e) {
                throw new RuntimeException(e);
            }
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

    protected void assertWholeBaseItemCount(int planetId, int count) {
        int actualCount = 0;
        for (SyncItem syncItem : planetSystemService.getServerPlanetServices(planetId).getItemService().getItemsCopy()) {
            if (syncItem instanceof SyncBaseItem) {
                actualCount++;
            }
        }
        Assert.assertEquals(count, actualCount);
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
        getMovableService().getSyncInfo(START_UID_1, false);
    }

    protected List<Packet> getPackagesIgnoreSyncItemInfoAndClear(boolean ignoreAccountBalancePackets) throws Exception {
        List<Packet> receivedPackets = new ArrayList<>(getMovableService().getSyncInfo(START_UID_1, false));
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
        List receivedPackets = new ArrayList<>(getMovableService().getSyncInfo(START_UID_1, false));
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
                if (comparePacket(expectedPacket, tmpReceivedPacket)) {
                    receivedPacket = tmpReceivedPacket;
                    break;
                }
            }
            if (receivedPacket == null) {
                Assert.fail("Packet was not sent: " + expectedPacket);
            }
            receivedPackets.remove(receivedPacket);
        }

        if (!receivedPackets.isEmpty()) {
            Assert.fail("More packages sent than expected: " + receivedPackets);
        }

    }

    protected boolean comparePacket(Packet expectedPacket, Packet receivedPacket) {
        if (expectedPacket instanceof AccountBalancePacket) {
            AccountBalancePacket expected = (AccountBalancePacket) expectedPacket;
            AccountBalancePacket received = (AccountBalancePacket) receivedPacket;
            return MathHelper.compareWithPrecision(expected.getAccountBalance(), received.getAccountBalance());
        } else if (expectedPacket instanceof LevelTaskPacket) {
            LevelTaskPacket expected = (LevelTaskPacket) expectedPacket;
            LevelTaskPacket received = (LevelTaskPacket) receivedPacket;
            if (expected.isCompleted() != received.isCompleted()) {
                return false;
            }
            if (!ObjectUtils.equals(expected.getQuestInfo(), received.getQuestInfo())) {
                return false;
            }
            QuestProgressInfo expectedProgressInfo = expected.getQuestProgressInfo();
            QuestProgressInfo receivedProgressInfo = received.getQuestProgressInfo();
            if (((expectedProgressInfo == null) && (receivedProgressInfo != null)) || ((expectedProgressInfo != null) && (receivedProgressInfo == null))) {
                return false;
            }
            if (expectedProgressInfo != null && receivedProgressInfo != null) {
                if (expectedProgressInfo.getConditionTrigger() != receivedProgressInfo.getConditionTrigger()) {
                    return false;
                }
                if (!ObjectUtils.equals(expectedProgressInfo.getAmount(), receivedProgressInfo.getAmount())) {
                    return false;
                }
                Map<Integer, QuestProgressInfo.Amount> expectedMap = expectedProgressInfo.getItemIdAmounts();
                Map<Integer, QuestProgressInfo.Amount> receivedMap = receivedProgressInfo.getItemIdAmounts();
                if (((expectedMap == null) && (receivedMap != null)) || ((expectedMap != null) && (receivedMap == null))) {
                    return false;
                }
                if (expectedMap != null && receivedMap != null) {
                    if (expectedMap.size() != receivedMap.size()) {
                        return false;
                    }
                    for (Map.Entry<Integer, QuestProgressInfo.Amount> expectedEntry : expectedMap.entrySet()) {
                        if (!receivedMap.containsKey(expectedEntry.getKey())) {
                            return false;
                        }
                        if (!expectedEntry.getValue().equals(receivedMap.get(expectedEntry.getKey()))) {
                            return false;
                        }
                    }
                }
                Map<InventoryArtifactInfo, QuestProgressInfo.Amount> expectedArtifactMap = expectedProgressInfo.getInventoryArtifactInfoAmount();
                Map<InventoryArtifactInfo, QuestProgressInfo.Amount> receivedArtifactMap = receivedProgressInfo.getInventoryArtifactInfoAmount();
                if (((expectedArtifactMap == null) && (receivedArtifactMap != null)) || ((expectedArtifactMap != null) && (receivedArtifactMap == null))) {
                    return false;
                }
                if (expectedArtifactMap != null && receivedArtifactMap != null) {
                    if (expectedArtifactMap.size() != receivedArtifactMap.size()) {
                        return false;
                    }
                    for (Map.Entry<InventoryArtifactInfo, QuestProgressInfo.Amount> expectedEntry : expectedArtifactMap.entrySet()) {
                        if (!receivedArtifactMap.containsKey(expectedEntry.getKey())) {
                            return false;
                        }
                        if (!expectedEntry.getValue().equals(receivedArtifactMap.get(expectedEntry.getKey()))) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else if (expectedPacket instanceof LevelPacket) {
            LevelScope expected = ((LevelPacket) expectedPacket).getLevel();
            LevelScope received = ((LevelPacket) receivedPacket).getLevel();
            if (!expected.equals(received)) {
                return false;
            }
            if (expected.getNumber() != received.getNumber()) {
                return false;
            }
            if (expected.getXp2LevelUp() != received.getXp2LevelUp()) {
                return false;
            }
            Map<Integer, Integer> expectedMap = expected.getItemTypeLimitation();
            Map<Integer, Integer> receivedMap = received.getItemTypeLimitation();
            if (((expectedMap == null) && (receivedMap != null)) || ((expectedMap != null) && (receivedMap == null))) {
                return false;
            }
            if (expectedMap != null && receivedMap != null) {
                if (expectedMap.size() != receivedMap.size()) {
                    return false;
                }
                for (Map.Entry<Integer, Integer> expectedEntry : expectedMap.entrySet()) {
                    if (!receivedMap.containsKey(expectedEntry.getKey())) {
                        return false;
                    }
                    if (!expectedEntry.getValue().equals(receivedMap.get(expectedEntry.getKey()))) {
                        return false;
                    }
                }
            }
            PlanetLiteInfo expectedPlanetLiteInfo = expected.getPlanetLiteInfo();
            PlanetLiteInfo receivedPlanetLiteInfo = received.getPlanetLiteInfo();
            if (((expectedPlanetLiteInfo == null) && (receivedPlanetLiteInfo != null)) || ((expectedPlanetLiteInfo != null) && (receivedPlanetLiteInfo == null))) {
                return false;
            }
            if (expectedPlanetLiteInfo != null && receivedPlanetLiteInfo != null) {
                if (!ObjectUtils.equals(expectedPlanetLiteInfo.getName(), receivedPlanetLiteInfo.getName())) {
                    return false;
                }
                if (!ObjectUtils.equals(expectedPlanetLiteInfo.getUnlockCrystals(), receivedPlanetLiteInfo.getUnlockCrystals())) {
                    return false;
                }
                if (expectedPlanetLiteInfo.getPlanetId() != receivedPlanetLiteInfo.getPlanetId()) {
                    return false;
                }
            }
            return true;
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
            return expected.getType() == received.getType()
                    && expected.getBaseAttributes().getSimpleBase().equals(received.getBaseAttributes().getSimpleBase())
                    && expected.getBaseAttributes().getName().equals(received.getBaseAttributes().getName())
                    && expected.getBaseAttributes().isBot() == received.getBaseAttributes().isBot()
                    && expected.getBaseAttributes().isAbandoned() == received.getBaseAttributes().isAbandoned()
                    && ObjectUtils.equals(expected.getBaseAttributes().getSimpleGuild(), received.getBaseAttributes().getSimpleGuild());
        } else if (expectedPacket instanceof ChatMessage) {
            ChatMessage expected = (ChatMessage) expectedPacket;
            ChatMessage received = (ChatMessage) receivedPacket;
            return expected.getMessage().equals(received.getMessage())
                    && expected.getName().equals(received.getName())
                    && expected.getMessageId() == received.getMessageId()
                    && expected.getType() == received.getType()
                    && ObjectUtils.equals(expected.getGuildId(), received.getGuildId())
                    && ObjectUtils.equals(expected.getUserId(), received.getUserId());
        } else if (expectedPacket instanceof UnlockContainerPacket) {
            try {
                Set<Integer> expectedItems = ((UnlockContainerPacket) expectedPacket).getUnlockContainer().getItemTypes();
                Set<Integer> receivedTypes = ((UnlockContainerPacket) receivedPacket).getUnlockContainer().getItemTypes();
                if (expectedItems.size() != receivedTypes.size() || !expectedItems.containsAll(receivedTypes) || !receivedTypes.containsAll(expectedItems)) {
                    return false;
                }
                Set<Integer> expectedQuests = ((UnlockContainerPacket) expectedPacket).getUnlockContainer().getQuests();
                Set<Integer> receivedQuests = ((UnlockContainerPacket) receivedPacket).getUnlockContainer().getQuests();
                if (expectedQuests.size() != receivedQuests.size() || !expectedQuests.containsAll(receivedQuests) || !receivedQuests.containsAll(expectedQuests)) {
                    return false;
                }
                Set<Integer> expectedPlanets = ((UnlockContainerPacket) expectedPacket).getUnlockContainer().getPlanets();
                Set<Integer> receivedPlanets = ((UnlockContainerPacket) receivedPacket).getUnlockContainer().getPlanets();
                if (expectedPlanets.size() != receivedPlanets.size() || !expectedPlanets.containsAll(receivedPlanets) || !receivedPlanets.containsAll(expectedPlanets)) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else if (expectedPacket instanceof EnergyPacket) {
            EnergyPacket expected = (EnergyPacket) expectedPacket;
            EnergyPacket received = (EnergyPacket) receivedPacket;
            return expected.equals(received);
        } else if (expectedPacket instanceof ServerRebootMessagePacket) {
            ServerRebootMessagePacket expected = (ServerRebootMessagePacket) expectedPacket;
            ServerRebootMessagePacket received = (ServerRebootMessagePacket) receivedPacket;
            return expected.getMessageId() == received.getMessageId()
                    && expected.getRebootInSeconds() == received.getRebootInSeconds()
                    && expected.getDownTimeInMinutes() == received.getDownTimeInMinutes();
        } else if (expectedPacket instanceof UserPacket) {
            SimpleUser expected = ((UserPacket) expectedPacket).getSimpleUser();
            SimpleUser received = ((UserPacket) receivedPacket).getSimpleUser();
            return expected.getId() == received.getId()
                    && expected.getName().equals(received.getName())
                    && expected.isVerified() == received.isVerified()
                    && expected.isFacebook() == received.isFacebook();
        } else if (expectedPacket instanceof UserAttentionPacket) {
            UserAttentionPacket expected = (UserAttentionPacket) expectedPacket;
            UserAttentionPacket received = (UserAttentionPacket) receivedPacket;
            return expected.getNews() == received.getNews();
        } else if (expectedPacket instanceof StorablePacket) {
            StorablePacket expected = (StorablePacket) expectedPacket;
            StorablePacket received = (StorablePacket) receivedPacket;
            return expected.getType() == received.getType();
        } else if (expectedPacket instanceof BaseLostPacket) {
            BaseLostPacket expected = (BaseLostPacket) expectedPacket;
            BaseLostPacket received = (BaseLostPacket) receivedPacket;
            RealGameInfo expectedRealGameInfo = expected.getRealGameInfo();
            RealGameInfo receivedRealGameInfo = received.getRealGameInfo();
            StartPointInfo expectedStartPointInfo = expectedRealGameInfo.getStartPointInfo();
            StartPointInfo receivedStartPointInfo = receivedRealGameInfo.getStartPointInfo();
            if (expectedStartPointInfo == null || receivedStartPointInfo == null) {
                return false;
            }
            return expectedStartPointInfo.getBaseItemTypeId() == receivedStartPointInfo.getBaseItemTypeId()
                    && expectedStartPointInfo.getItemFreeRange() == receivedStartPointInfo.getItemFreeRange()
                    && ObjectUtils.equals(expectedStartPointInfo.getSuggestedPosition(), receivedStartPointInfo.getSuggestedPosition());

        } else {
            Assert.fail("Unknown packet: " + expectedPacket);
            return false;
        }
    }

    protected Message createMessage(String message, boolean showRegisterDialog) {
        Message messagePacket = new Message();
        messagePacket.setMessage(message);
        messagePacket.setShowRegisterDialog(showRegisterDialog);
        return messagePacket;
    }

    protected void createConnection() throws InvalidLevelStateException {
        ServerPlanetServices serverPlanetServices = planetSystemService.getPlanetSystemService(getUserState(), null);
        serverPlanetServices.getConnectionService().createConnection(getUserState(), START_UID_1);
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
        DbLevel dbLevel = setupOneLevel(dbPlanet1);

        dbPlanet1.setMinLevel(dbLevel);
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
        DbLevel dbLevel = setupOneLevel(dbPlanet1);
        // Resource
        setupResource1(dbPlanet1, 1, new Rectangle(5000, 5000, 300, 300));

        dbPlanet1.setMinLevel(dbLevel);
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
        setupMultipleLevels3(dbPlanet3);
        // Resource fields
        setupResource1(dbPlanet1, 10, new Rectangle(5000, 5000, 1000, 1000));
        setupResource1(dbPlanet2, 5, new Rectangle(5000, 5000, 1000, 1000));
        setupResource1(dbPlanet3, 6, new Rectangle(0, 0, 1000, 1000));

        dbPlanet1.setMinLevel(userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet1);
        dbPlanet2.setMinLevel(userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_5_REAL_ID));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        dbPlanet3.setMinLevel(userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_6_REAL_ID));
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
        createConsumerType();
        createConsumerAttackMovableType();
        createGeneratorType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        finishConsumerAttackMovableType();
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
        createConsumerType();
        createConsumerAttackMovableType();
        createGeneratorType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        createSimpleBuilding();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        finishConsumerAttackMovableType();
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
        createConsumerType();
        createConsumerAttackMovableType();
        createGeneratorType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        createSimpleBuilding();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        finishConsumerAttackMovableType();
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
        Set<DbBaseItemType> ableToBuild = new HashSet<>();
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_FACTORY_ITEM_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HOUSE_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONSUMER_TYPE_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_GENERATOR_TYPE_ID));
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
        Set<DbBaseItemType> ableToBuild = new HashSet<>();
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        ableToBuild.add((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID));
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
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID), true);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
    }

    private void finishConsumerAttackMovableType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID);
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


    private DbBaseItemType createConsumerType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 1);
        dbBaseItemType.setName(TEST_CONSUMER_TYPE);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        // DbConsumer
        DbConsumerType dbConsumer = new DbConsumerType();
        dbConsumer.setWattage(20);
        dbBaseItemType.setDbConsumerType(dbConsumer);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_CONSUMER_TYPE_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private DbBaseItemType createConsumerAttackMovableType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_CONSUMER_ATTACK_MOVABLE_TYPE);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        // DbConsumer
        DbConsumerType dbConsumer = new DbConsumerType();
        dbConsumer.setWattage(10);
        dbBaseItemType.setDbConsumerType(dbConsumer);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbBaseItemType.setDbMovableType(dbMovableType);
        // DbWeaponType
        DbWeaponType dbWeaponType = new DbWeaponType();
        dbWeaponType.setRange(100);
        dbWeaponType.setReloadTime(1);
        dbWeaponType.setDamage(1000);
        dbBaseItemType.setDbWeaponType(dbWeaponType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private DbBaseItemType createGeneratorType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 1);
        dbBaseItemType.setName(TEST_GENERATOR_TYPE);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        // DbConsumer
        DbGeneratorType dbGeneratorType = new DbGeneratorType();
        dbGeneratorType.setWattage(30);
        dbBaseItemType.setDbGeneratorType(dbGeneratorType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        TEST_GENERATOR_TYPE_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private void finishContainerBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONTAINER_ITEM_ID);
        // DbItemContainerType
        Set<DbBaseItemType> ableToContain = new HashSet<>();
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
        dbPlanet.setStartPosition(new Index(1001, 1001));
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
        DbPlanetItemTypeLimitation generator = dbPlanet.getItemLimitationCrud().createDbChild();
        generator.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_GENERATOR_TYPE_ID));
        generator.setCount(10);
        DbPlanetItemTypeLimitation consumer = dbPlanet.getItemLimitationCrud().createDbChild();
        consumer.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONSUMER_TYPE_ID));
        consumer.setCount(10);
        DbPlanetItemTypeLimitation consumerMovableAttacker = dbPlanet.getItemLimitationCrud().createDbChild();
        consumerMovableAttacker.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID));
        consumerMovableAttacker.setCount(10);

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

    private DbLevel setupOneLevel(DbPlanet dbPlanet) throws Exception {
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
        DbLevelItemTypeLimitation generator = dbLevel.getItemTypeLimitationCrud().createDbChild();
        generator.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_GENERATOR_TYPE_ID));
        generator.setCount(10);
        DbLevelItemTypeLimitation consumer = dbLevel.getItemTypeLimitationCrud().createDbChild();
        consumer.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONSUMER_TYPE_ID));
        consumer.setCount(10);
        DbLevelItemTypeLimitation consumerMovableAttacker = dbLevel.getItemTypeLimitationCrud().createDbChild();
        consumerMovableAttacker.setDbBaseItemType((DbBaseItemType) serverItemTypeService.getDbItemType(TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID));
        consumerMovableAttacker.setCount(10);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        TEST_LEVEL_2_REAL_ID = dbLevel.getId();
        return dbLevel;
    }

    private void setupMultipleLevels(DbPlanet dbPlanet) throws Exception {
        // Setup Level - Task - Tutorial
        DbTutorialConfig tut1 = createTutorial1();
        DbLevel dbSimLevel = userGuidanceService.getDbLevelCrud().createDbChild();
        dbSimLevel.setNumber(TEST_LEVEL_1_SIMULATED);
        dbSimLevel.setXp(1);
        DbLevelTask dbSimLevelTask = dbSimLevel.getLevelTaskCrud().createDbChild();
        dbSimLevelTask.setDbTutorialConfig(tut1);
        dbSimLevelTask.getI18nTitle().putString(TEST_LEVEL_TASK_1_1_SIMULATED_NAME);
        dbSimLevelTask.setName(TEST_LEVEL_TASK_1_1_SIMULATED_NAME);
        dbSimLevelTask.getI18nDescription().putString("Description");
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
        dbSimLevelTask2.getI18nTitle().putString(TEST_LEVEL_TASK_3_3_SIMULATED_NAME);
        dbSimLevelTask2.setName(TEST_LEVEL_TASK_3_3_SIMULATED_NAME);
        dbSimLevelTask2.getI18nDescription().putString("Task3Level2Descr");
        DbLevelTask dbSimLevelTask3 = dbLevel2.getLevelTaskCrud().createDbChild();
        dbSimLevelTask3.setDbTutorialConfig(tut3);
        dbSimLevelTask3.setXp(3);
        dbSimLevelTask3.getI18nTitle().putString(TEST_LEVEL_TASK_4_3_SIMULATED_NAME);
        dbSimLevelTask3.setName(TEST_LEVEL_TASK_4_3_SIMULATED_NAME);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel2);

        DbLevel dbLevel3 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel3.setDbPlanet(dbPlanet);
        dbLevel3.setXp(100000);
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

    private void setupMultipleLevels3(DbPlanet dbPlanet3) throws LevelActivationException {
        DbLevel dbLevel6 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel6.setDbPlanet(dbPlanet3);
        dbLevel6.setNumber(TEST_LEVEL_6_REAL);
        dbLevel6.setXp(220);
        setLimitation(dbLevel6);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel6);

        TEST_LEVEL_6_REAL_ID = dbLevel6.getId();
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

    protected DbLevelTask setupCreateLevelTask1RealGameLevel(DbLevel dbLevel) {
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask.getI18nTitle().putString(TEST_LEVEL_TASK_1_2_REAL_NAME);
        dbLevelTask.setName(TEST_LEVEL_TASK_1_2_REAL_NAME);
        dbLevelTask.getI18nDescription().putString("Descr2");
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
        dbLevelTask.getI18nTitle().putString("Task3Level2");
        dbLevelTask.setName("Task3Level2");
        dbLevelTask.getI18nDescription().putString("DecrTask3Level2");
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
        dbLevelTask.getI18nTitle().putString(TEST_LEVEL_TASK_2_2_REAL_NAME);
        dbLevelTask.setName(TEST_LEVEL_TASK_2_2_REAL_NAME);
        dbLevelTask.getI18nDescription().putString("Descr222");
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

    // -------------------  User --------------------

    protected void createUser(String userName, String password, String email) {
        try {
            userService.createUser(userName, password, password, email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createUser(String userName, String password) {
        createUser(userName, password, "fakeemail");
    }

    protected void loginUser(String userName) {
        loginUser(userName, "test");
    }

    protected void loginUser(String userName, String password) {
        userService.login(userName, password);
    }

    protected void createAndLoginUser(String userName, String password) {
        createUser(userName, password);
        loginUser(userName, password);
    }

    protected void createAndLoginUser(String userName) {
        createAndLoginUser(userName, "test");
    }

    protected void createAndLoginFacebookUser(String userId, String nickName) {
        try {
            FacebookAge facebookAge = new FacebookAge(20);
            FacebookUser facebookUser = new FacebookUser("", "", facebookAge);
            FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest("", 0, facebookUser, "", userId);
            userService.createAndLoginFacebookUser(facebookSignedRequest, nickName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createUserInSession(String userName, Date registerDate) {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            userService.createUser(userName, "xxx", "xxx", "");
            User user = userService.getUser(userName);
            setPrivateField(User.class, user, "registerDate", registerDate);
            saveOrUpdateInTransaction(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    // ------------------- History helpers --------------------

    protected List<DbHistoryElement> getAllHistoryEntriesOfType(DbHistoryElement.Type type) throws Exception {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // Query for DbHistoryElement
        CriteriaQuery<DbHistoryElement> query = criteriaBuilder.createQuery(DbHistoryElement.class);
        Root<DbHistoryElement> from = query.from(DbHistoryElement.class);
        CriteriaQuery<DbHistoryElement> select = query.select(from);
        if (type != null) {
            Predicate predicate = criteriaBuilder.equal(from.<DbHistoryElement.Type>get("type"), type);
            query.where(predicate);
        }
        query.orderBy(criteriaBuilder.asc(from.<String>get("timeStampMs")), criteriaBuilder.asc(from.<String>get("id")));
        TypedQuery<DbHistoryElement> typedQuery = entityManager.createQuery(select);
        return typedQuery.getResultList();
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

    protected void assertHistoryType(DbHistoryElement.Type type) throws Exception {
        List<DbHistoryElement> dbHistoryElements = HibernateUtil.loadAll(sessionFactory, DbHistoryElement.class);
        for (DbHistoryElement dbHistoryElement : dbHistoryElements) {
            if (dbHistoryElement.getType() == type) {
                return;
            }
        }
        Assert.fail("History entry not found: " + type);
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
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
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

    private void beginHttpRequest(String httpUrl) {
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
        if (httpUrl != null) {
            Url url = Url.parse(httpUrl);
            for (Url.QueryParameter queryParameter : url.getQueryParameters()) {
                mockHttpServletRequest.setParameter(queryParameter.getName(), queryParameter.getValue());
            }
            mockHttpServletRequest.setQueryString(url.getQueryString());
            mockHttpServletRequest.setRequestURI(httpUrl);
        }
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

    protected void beginHttpRequestAndOpenSessionInViewFilter(String httpUrl) {
        beginHttpRequest(httpUrl);
        beginOpenSessionInViewFilter();
    }

    protected void beginHttpRequestAndOpenSessionInViewFilter() {
        beginHttpRequestAndOpenSessionInViewFilter(null);
    }

    protected void endHttpRequestAndOpenSessionInViewFilter() {
        endOpenSessionInViewFilter();
        endHttpRequest();
    }

    @Before
    public void setup() throws Exception {
        configurableListableBeanFactory.registerResolvableDependency(ServletRequest.class, new ObjectFactory<ServletRequest>() {
            // This is used to inject HttpServletRequest into SessionImpl
            @Override
            public ServletRequest getObject() throws BeansException {
                return mockHttpServletRequest;
            }
        });
    }

    // ------------------- Guild --------------------
    public static boolean equalsGuildId(SimpleGuild simpleGuild, Integer guildId) {
        if (simpleGuild == null && guildId == null) {
            return true;
        } else if (simpleGuild != null) {
            return ObjectUtils.equals(simpleGuild.getId(), guildId);
        } else {
            return false;
        }
    }


    protected int createGuildAnd2Users() throws Exception {
        // Create member
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create guild + presi
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("guild").getId();
        guildService.inviteUserToGuild("member1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member1");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        return guildId;
    }

    // ------------------- Wicket --------------------
    @Before
    public void setupWicketTester() {
        wicketTester = new WicketTester(wicketApplication);
    }

    public WicketTester getWicketTester() {
        return wicketTester;
    }

    public void assertCmsImage(WicketTester tester, String path, DbCmsImage descImg) throws Exception {
        Component component = tester.getComponentFromLastRenderedPage(path);
        Assert.assertNotNull("No such component: " + path, component);
        Image image = (Image) component;
        LocalizedImageResource localizedImageResource = (LocalizedImageResource) getPrivateField(Image.class, image, "localizedImageResource");
        PageParameters pageParameters = (PageParameters) getPrivateField(LocalizedImageResource.class, localizedImageResource, "resourceParameters");
        Assert.assertEquals((int) descImg.getId(), pageParameters.get(CmsImageResource.ID).toInt());
    }

    public void setWicketParameterTrackingCookie(String trackingCookieId) throws Exception {
        WicketAuthenticatedWebSession wicketSession = (WicketAuthenticatedWebSession) getWicketTester().getSession();
        setPrivateField(WicketAuthenticatedWebSession.class, wicketSession, "trackingCookieId", trackingCookieId);
    }

    public void setWicketParameterTrackingCookieNeeded(boolean cookieNeeded) throws Exception {
        WicketAuthenticatedWebSession wicketSession = (WicketAuthenticatedWebSession) getWicketTester().getSession();
        setPrivateField(WicketAuthenticatedWebSession.class, wicketSession, "isTrackingCookieIdCookieNeeded", cookieNeeded);
    }

    public void assertCssClass(WicketTester tester, String path, String cssClass) {
        Component component = tester.getComponentFromLastRenderedPage(path);
        Assert.assertNotNull("No such component: " + path, component);
        for (Behavior behavior : component.getBehaviors()) {
            if (behavior instanceof AttributeModifier) {
                AttributeModifier attributeModifier = (AttributeModifier) behavior;
                XmlTag xmlTag = new XmlTag();
                ComponentTag tag = new ComponentTag(xmlTag);
                tag.setId("class");
                tag.setName("id");
                attributeModifier.replaceAttributeValue(null, tag);
                Map<String, Object> attributes = tag.getAttributes();
                Assert.assertFalse(attributes.isEmpty());
                String replacement = (String) attributes.get("class");
                Assert.assertNotNull(replacement);
                Assert.assertEquals(cssClass, replacement);
                return;
            }
        }
        Assert.fail("No such CSS class: " + cssClass);
    }

    public void assertAttributeModifier(Component component, int index, String attribute, String expectedValue) {
        AttributeModifier attributeModifier = (AttributeModifier) component.getBehaviors().get(index);
        XmlTag xmlTag = new XmlTag();
        ComponentTag tag = new ComponentTag(xmlTag);
        tag.setId(attribute);
        tag.setName("id");
        attributeModifier.replaceAttributeValue(null, tag);
        Map<String, Object> attributes = tag.getAttributes();
        Assert.assertFalse(attributes.isEmpty());
        String replacement = (String) attributes.get(attribute);
        Assert.assertNotNull(replacement);
        Assert.assertEquals(expectedValue, replacement);
    }

    public void assertAttributeModifier(String path, int index, String attribute, String expectedValue) {
        Component component = getWicketTester().getComponentFromLastRenderedPage(path);
        Assert.assertNotNull("No component for path: " + path);
        assertAttributeModifier(component, index, attribute, expectedValue);
    }

    public String getLastRenderedHeaderString() {
        String response = getWicketTester().getLastResponseAsString();
        int end = response.lastIndexOf("</head>");
        if (end > -1) {
            int start = response.indexOf("<head>") + "<head>".length();
            return response.substring(start, end);
        } else {
            return null;
        }
    }

    public String getLastRenderedBodyString() {
        String response = getWicketTester().getLastResponseAsString();
        int end = response.lastIndexOf("</body>");
        if (end > -1) {
            int start = response.indexOf("<body>") + "<body>".length();
            return response.substring(start, end);
        } else {
            return null;
        }
    }

    public void assertStringInHeader(String expected) {
        String head = getLastRenderedHeaderString();
        Assert.assertNotNull(head);
        assertContainsStringIgnoreWhitespace(head, expected);
    }

    public void assertStringNotInHeader(String expected) {
        String head = getLastRenderedHeaderString();
        Assert.assertNotNull(head);
        assertNotContainsStringIgnoreWhitespace(head, expected);
    }

    public void assertStringInBody(String expected) {
        String body = getLastRenderedBodyString();
        Assert.assertNotNull(body);
        assertContainsStringIgnoreWhitespace(body, expected);
    }

    public void debugWholeLastResponse() {
        System.out.println("---------------------------------------------------");
        System.out.println(getWicketTester().getLastResponseAsString());
        System.out.println("---------------------------------------------------");
    }

    // ------------------- User --------------------
    protected UserState getUserState() {
        return userService.getUserState();
    }

    protected User getUser() {
        return userService.getUser();
    }

    protected Integer getUserId() {
        return userService.getUser().getId();
    }
    // ------------------- Div --------------------

    public static GlobalServices createMockGlobalServices() {
        GlobalServices planetServices = EasyMock.createNiceMock(GlobalServices.class);
        EasyMock.replay(planetServices);
        return planetServices;
    }

    public static ServerPlanetServicesImpl createMockPlanetServices() {
        ServerPlanetServicesImpl serverPlanetServices = new ServerPlanetServicesImpl();
        ServerTerrainService terrainService = EasyMock.createNiceMock(ServerTerrainService.class);
        EasyMock.replay(terrainService);
        serverPlanetServices.setTerrainService(terrainService);
        return serverPlanetServices;
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

    public static void assertPrivateField(Class clazz, Object expected, Object actual, String fieldName) throws Exception {
        Object expectedObject = getPrivateField(clazz, expected, fieldName);
        Object actualObject = getPrivateField(clazz, actual, fieldName);
        Assert.assertEquals(expectedObject, actualObject);
    }

    public static Object deAopProxy(Object object) throws Exception {
        if (AopUtils.isJdkDynamicProxy(object)) {
            return ((Advised) object).getTargetSource().getTarget();
        } else {
            return object;
        }
    }

    public static void assertStringIgnoreWhitespace(String expected, String actual) {
        expected = expected.replaceAll("\\s", "");
        actual = actual.replaceAll("\\s", "");
        Assert.assertEquals(expected, actual);
    }

    public static void assertContainsStringIgnoreWhitespace(String wholeString, String expectedToBeContained) {
        wholeString = wholeString.replaceAll("\\s", "");
        expectedToBeContained = expectedToBeContained.replaceAll("\\s", "");
        if (!wholeString.contains(expectedToBeContained)) {
            Assert.fail("String\n" + expectedToBeContained + "\nnot contained in\n" + wholeString);
        }
    }

    public static void assertNotContainsStringIgnoreWhitespace(String wholeString, String expectedNotToBeContained) {
        wholeString = wholeString.replaceAll("\\s", "");
        expectedNotToBeContained = expectedNotToBeContained.replaceAll("\\s", "");
        Assert.assertFalse(wholeString.contains(expectedNotToBeContained));
    }

    public static void assertDate(long before, long after, Date time) {
        Assert.assertTrue("Time is too small", before <= time.getTime());
        Assert.assertTrue("Time is too big", after >= time.getTime());
    }

    public static void debugDate(String message, Date time) {
        System.out.println(message + ": " + new SimpleDateFormat("ddd.MM.yyy HH:mm:ss.S").format(time));
    }

    // ---------- Mail Helper -------

    public void startFakeMailServer() {
        if (wiser != null) {
            throw new IllegalStateException("Fake email server is already running");
        }
        wiser = new Wiser(2500);
        wiser.start();


        Map<String, JavaMailSenderImpl> ofType = applicationContext.getBeansOfType(org.springframework.mail.javamail.JavaMailSenderImpl.class);

        for (Map.Entry<String, JavaMailSenderImpl> bean : ofType.entrySet()) {
            //log.info(String.format("configuring mailsender %s to use local Wiser SMTP", bean.getKey()));
            JavaMailSenderImpl mailSender = bean.getValue();
            mailSender.setHost("localhost");
            mailSender.setPort(wiser.getServer().getPort());
        }
    }

    public void stopFakeMailServer() {
        if (wiser == null) {
            throw new IllegalStateException("Fake email server is not running");
        }
        wiser.stop();
        wiser = null;
    }

    public Wiser getFakeMailServer() {
        if (wiser == null) {
            throw new IllegalStateException("Fake email server is not running");
        }
        return wiser;
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

    public void saveOrUpdateInTransaction(final Object object) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                sessionFactory.getCurrentSession().saveOrUpdate(object);
            }
        });
    }

    public <T> List<T> loadAll(Class<T> theClass) {
        return HibernateUtil.loadAll(getSessionFactory(), theClass);
    }

    public <T> T get(Class<T> theClass, Serializable id) {
        return HibernateUtil.get(getSessionFactory(), theClass, id);
    }

    public void assertDbEntryCount(int count, Class theClass) {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        int actualCount = HibernateUtil.loadAll(getSessionFactory(), theClass).size();
        Assert.assertEquals("Unexpected entries in DB of class: " + theClass + " Expected count: " + count + " Actual count: " + actualCount, count, actualCount);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    public void assertNoDbEntry(Class theClass) {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue("Unexpected entry in DB of class: " + theClass, HibernateUtil.loadAll(getSessionFactory(), theClass).isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // ------------------- EasyMock Helpers --------------------
    public static User createUserMatcher(String userName) {
        EasyMock.reportMatcher(new UserNameMatcher(userName));
        return null;
    }

    public static class UserNameMatcher implements IArgumentMatcher {
        private String userName;
        private String errorString;

        public UserNameMatcher(String userName) {
            this.userName = userName;
        }

        @Override
        public boolean matches(Object o) {
            User user = (User) o;
            if (user.getUsername().equals(userName)) {
                return true;
            } else {
                errorString = "Invalid user. Expected user name '" + userName + "' actual user '" + user.getUsername() + "'";
                return false;
            }
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }

    public UserState createUserStateMatcher(String userName) {
        EasyMock.reportMatcher(new UserStateMatcher(userName));
        return null;
    }

    public class UserStateMatcher implements IArgumentMatcher {
        private String userName;
        private String errorString;

        public UserStateMatcher(String userName) {
            this.userName = userName;
        }

        @Override
        public boolean matches(Object o) {
            User user = userService.getUser(userName);
            if (user == null) {
                errorString = "User does not exist: " + userName;
                return false;
            }
            UserState userState = (UserState) o;
            if (user.getId().equals(userState.getUser())) {
                return true;
            } else {
                errorString = "Invalid UserState. Expected User '" + user + "' actual UserState '" + userState + "'";
                return false;
            }
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }

    public static UserAttentionPacket createUserAttentionPacketMatcher(UserAttentionPacket.Type news, UserAttentionPacket.Type guildMembershipRequest, UserAttentionPacket.Type guildInvitation) {
        EasyMock.reportMatcher(new UserAttentionPacketMatcher(news, guildMembershipRequest, guildInvitation));
        return null;
    }

    public static class UserAttentionPacketMatcher implements IArgumentMatcher {
        private UserAttentionPacket expected;
        private String errorString;

        public UserAttentionPacketMatcher(UserAttentionPacket.Type news, UserAttentionPacket.Type guildMembershipRequest, UserAttentionPacket.Type guildInvitation) {
            expected = new UserAttentionPacket();
            expected.setNews(news);
            expected.setGuildMembershipRequest(guildMembershipRequest);
            expected.setGuildInvitation(guildInvitation);
        }

        @Override
        public boolean matches(Object o) {
            UserAttentionPacket actual = (UserAttentionPacket) o;
            if (expected.getNews() != actual.getNews()) {
                errorString = "Invalid UserAttentionPacket. Expected '" + expected + "' actual '" + actual + "'";
                return false;
            }
            if (expected.getGuildMembershipRequest() != actual.getGuildMembershipRequest()) {
                errorString = "Invalid UserAttentionPacket. Expected '" + expected + "' actual '" + actual + "'";
                return false;
            }
            if (expected.getGuildInvitation() != actual.getGuildInvitation()) {
                errorString = "Invalid UserAttentionPacket. Expected '" + expected + "' actual '" + actual + "'";
                return false;
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }

    public Packet createBaseChangedPacketMatcher(BaseChangedPacket.Type type, String name, boolean bot, Integer guildId) {
        EasyMock.reportMatcher(new BaseChangedPacketMatcher(type, name, bot, guildId));
        return null;
    }

    private class BaseChangedPacketMatcher implements IArgumentMatcher {
        private String errorString;
        private BaseChangedPacket.Type type;
        private String name;
        private boolean bot;
        private Integer guildId;

        public BaseChangedPacketMatcher(BaseChangedPacket.Type type, String name, boolean bot, Integer guildId) {
            this.type = type;
            this.name = name;
            this.bot = bot;
            this.guildId = guildId;
        }

        @Override
        public boolean matches(Object o) {
            BaseChangedPacket actual = (BaseChangedPacket) o;
            if (type != actual.getType()) {
                errorString = "Invalid BaseChangedPacket type. Expected '" + type + "' actual '" + actual.getType() + "'";
                return false;
            }
            BaseAttributes baseAttributes = actual.getBaseAttributes();
            if (!(name.equals(baseAttributes.getName()))) {
                errorString = "Invalid BaseChangedPacket. Expected baseAttributes name '" + name + "' actual '" + baseAttributes.getName() + "'";
                return false;
            }
            if (bot != baseAttributes.isBot()) {
                errorString = "Invalid BaseChangedPacket. Expected baseAttributes bot '" + bot + "' actual '" + baseAttributes.isBot() + "'";
                return false;
            }
            if (!equalsGuildId(baseAttributes.getSimpleGuild(), guildId)) {
                errorString = "Invalid BaseChangedPacket. Expected baseAttributes guildId '" + guildId + "' actual '" + baseAttributes.getSimpleGuild() + "'";
                return false;
            }
            if (baseAttributes.isAbandoned()) {
                errorString = "Invalid BaseChangedPacket. Expected baseAttributes isAbandoned should be false";
                return false;
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }

    public Packet createUnregisteredBaseChangedPacketMatcher(BaseChangedPacket.Type type, SimpleBase simpleBase, boolean abandoned) {
        EasyMock.reportMatcher(new UnregisteredBaseChangedPacketMatcher(type, simpleBase, abandoned));
        return null;
    }

    private class UnregisteredBaseChangedPacketMatcher implements IArgumentMatcher {
        private String errorString;
        private BaseChangedPacket.Type type;
        private SimpleBase simpleBase;
        private boolean abandoned;

        public UnregisteredBaseChangedPacketMatcher(BaseChangedPacket.Type type, SimpleBase simpleBase, boolean abandoned) {
            this.type = type;
            this.simpleBase = simpleBase;
            this.abandoned = abandoned;
        }

        @Override
        public boolean matches(Object o) {
            BaseChangedPacket actual = (BaseChangedPacket) o;
            if (type != actual.getType()) {
                errorString = "Invalid BaseChangedPacket type. Expected '" + type + "' actual '" + actual.getType() + "'";
                return false;
            }
            BaseAttributes baseAttributes = actual.getBaseAttributes();
            if (!(simpleBase.equals(baseAttributes.getSimpleBase()))) {
                errorString = "Invalid BaseChangedPacket. Expected baseAttributes simpleBase '" + simpleBase + "' actual '" + baseAttributes.getName() + "'";
                return false;
            }
            if (baseAttributes.isBot()) {
                errorString = "Invalid BaseChangedPacket. Actural baseAttributes is bot";
                return false;
            }
            if (baseAttributes.getSimpleGuild() == null) {
                errorString = "Invalid BaseChangedPacket. Expected baseAttributes guild not set: " + baseAttributes.getSimpleGuild();
                return false;
            }
            if (abandoned != baseAttributes.isAbandoned()) {
                errorString = "Invalid BaseChangedPacket. Expected baseAttributes abandoned '" + abandoned + "' actual '" + baseAttributes.isAbandoned() + "'";
                return false;
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }

    public Packet createStorablePacket(StorablePacket.Type type) {
        EasyMock.reportMatcher(new StorablePacketMatcher(type));
        return null;
    }

    private class StorablePacketMatcher implements IArgumentMatcher {
        private String errorString;
        private StorablePacket.Type type;

        public StorablePacketMatcher(StorablePacket.Type type) {
            this.type = type;
        }

        @Override
        public boolean matches(Object o) {
            StorablePacket actual = (StorablePacket) o;
            if (type != actual.getType()) {
                errorString = "Invalid StorablePacket type. Expected '" + type + "' actual '" + actual.getType() + "'";
                return false;
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }

    public static SyncItem createSyncItemMatcher(Id id) {
        EasyMock.reportMatcher(new SyncItemMatcher(id));
        return null;
    }

    private static class SyncItemMatcher implements IArgumentMatcher {
        private String errorString;
        private Id id;

        public SyncItemMatcher(Id id) {
            this.id = id;
        }

        @Override
        public boolean matches(Object o) {
            SyncItem actual = (SyncItem) o;
            if (!actual.getId().equals(id)) {
                errorString = "Invalid SyncItem id. Expected '" + id + "' actual '" + actual.getId() + "'";
                return false;
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(errorString);
        }
    }


}
