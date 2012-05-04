package com.btxtech.game.services;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.ChatMessage;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.HouseSpacePacket;
import com.btxtech.game.jsre.common.LevelStatePacket;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.bot.DbBotItemConfig;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.gwt.MovableServiceImpl;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBuilderType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.item.itemType.DbItemContainerType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbMovableType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.playback.impl.PlaybackServiceImpl;
import com.btxtech.game.services.resource.DbRegionResource;
import com.btxtech.game.services.resource.ResourceService;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.DbTerritory;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbItemTypeLimitation;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.DbQuestHub;
import com.btxtech.game.services.utg.DbXpSettings;
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
    protected static final String TEST_RESOURCE_ITEM = "TestResourceItem";
    protected static int TEST_RESOURCE_ITEM_ID = -1;
    protected static final String TEST_HARVESTER_ITEM = "TEST_HARVESTER_ITEM";
    protected static int TEST_HARVESTER_ITEM_ID = -1;
    // Quest Hubs
    protected static final String TEST_QUEST_HUB_1 = "TEST_QUEST_HUB_1";
    protected static final String TEST_QUEST_HUB_2 = "TEST_QUEST_HUB_2";
    // Level ID
    protected static int TEST_LEVEL_1_SIMULATED_ID = -1;
    protected static int TEST_LEVEL_2_REAL_ID = -1;
    protected static int TEST_LEVEL_3_REAL_ID = -1;
    protected static int TEST_LEVEL_4_REAL_ID = -1;
    // Level numbers
    protected static final int TEST_LEVEL_1_SIMULATED = 1;
    protected static final int TEST_LEVEL_2_REAL = 2;
    protected static final int TEST_LEVEL_3_REAL = 3;
    protected static final int TEST_LEVEL_4_REAL = 4;
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

    // Territories
    protected static String COMPLEX_TERRITORY = "ComplexTerritory";
    protected static int COMPLEX_TERRITORY_ID = -1;
    protected static final String TEST_NOOB_TERRITORY = "TEST_NOOB_TERRITORY";
    protected static int TEST_NOOB_TERRITORY_ID = -1;

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
    private XpService xpService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private BotService botService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private XpService xpServic;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ServerServices serverServices;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ApplicationContext applicationContext;
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

        DbBaseItemType dbBaseItemType = itemService.getDbBaseItemType(id);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        return dbBaseItemType;
    }

    protected void assertBaseCount(int count) {
        Assert.assertEquals(count, baseService.getAllBaseAttributes().size());
    }
    // ------------------- Sync Items --------------------

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id, Services services, SimpleBase simpleBase) throws Exception {
        SyncBaseItem syncBaseItem = new SyncBaseItem(id, null, (BaseItemType) itemService.getItemType(itemTypeId), services, simpleBase);
        syncBaseItem.getSyncItemArea().setPosition(position);
        return syncBaseItem;
    }

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id, Services services) throws Exception {
        return createSyncBaseItem(itemTypeId, position, id, services, new SimpleBase(1));
    }

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id, SimpleBase simpleBase) throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem(itemTypeId, position, id, createMockServices(), simpleBase);
        syncBaseItem.setBuildup(1.0);
        return syncBaseItem;
    }

    protected SyncBaseItem createSyncBaseItem(int itemTypeId, Index position, Id id) throws Exception {
        return createSyncBaseItem(itemTypeId, position, id, new SimpleBase(1));
    }

    protected SyncResourceItem createSyncResourceItem(int itemTypeId, Index position, Id id) throws Exception {
        SyncResourceItem syncResourceItem = new SyncResourceItem(id, null, (ResourceType) itemService.getItemType(itemTypeId), createMockServices());
        syncResourceItem.getSyncItemArea().setPosition(position);
        return syncResourceItem;
    }

    /**
     * Attention: closes the current connection!!!
     *
     * @return Simple Base
     */
    protected SimpleBase getMyBase() {
        try {
            return getMovableService().getRealGameInfo().getBase();
        } catch (InvalidLevelState invalidLevelState) {
            throw new RuntimeException(invalidLevelState);
        }
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
        List<Id> ids = new ArrayList<Id>();
        for (SyncItemInfo syncItemInfo : getMovableService().getAllSyncInfo()) {
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


    protected void assertWholeItemCount(int count) {
        Assert.assertEquals(count, itemService.getItemsCopy().size());
    }

    protected void killSyncItem(Id id) throws Exception {
        SyncItem syncItem = itemService.getItem(id);
        itemService.killSyncItem(syncItem, null, true, false);
    }

    // ------------------- Connection --------------------

    protected void clearPackets() throws Exception {
        getMovableService().getSyncInfo();
    }

    protected List<Packet> getPackagesIgnoreSyncItemInfoAndClear(boolean ignoreAccountBalancePackets) throws Exception {
        List<Packet> receivedPackets = new ArrayList<Packet>(getMovableService().getSyncInfo());
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
        } else if (expectedPacket instanceof LevelStatePacket) {
            LevelStatePacket expected = (LevelStatePacket) expectedPacket;
            LevelStatePacket received = (LevelStatePacket) receivedPacket;
            Assert.assertEquals(expected.getXp(), received.getXp());
            Assert.assertEquals(expected.getXp2LevelUp(), received.getXp2LevelUp());
            Assert.assertEquals(expected.getActiveQuestTitle(), received.getActiveQuestTitle());
            Assert.assertEquals(expected.getActiveQuestProgress(), received.getActiveQuestProgress());
            Assert.assertEquals(expected.getActiveQuestLevelTaskId(), received.getActiveQuestLevelTaskId());
            Assert.assertEquals(expected.isQuestDeactivated(), received.isQuestDeactivated());
            Assert.assertEquals(expected.getQuestsDone(), received.getQuestsDone());
            Assert.assertEquals(expected.getTotalQuests(), received.getTotalQuests());
            Assert.assertEquals(expected.getMissionsDone(), received.getMissionsDone());
            Assert.assertEquals(expected.getTotalMissions(), received.getTotalMissions());
            Assert.assertEquals(expected.isMissionQuestCompleted(), received.isMissionQuestCompleted());
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
        } else if (expectedPacket instanceof ChatMessage) {
            ChatMessage expected = (ChatMessage) expectedPacket;
            ChatMessage received = (ChatMessage) receivedPacket;
            Assert.assertEquals(expected.getMessage(), received.getMessage());
            Assert.assertEquals(expected.getName(), received.getName());
            Assert.assertEquals(expected.getMessageId(), received.getMessageId());
        } else {
            Assert.fail("Unhandled packet: " + expectedPacket);
        }
    }

    protected Message createMessage(String message, boolean showRegisterDialog) {
        Message messagePacket = new Message();
        messagePacket.setMessage(message);
        messagePacket.setShowRegisterDialog(showRegisterDialog);
        return messagePacket;
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

    protected void sendMoveCommand(Id movable, Index destination) throws Exception {
        SyncBaseItem syncItem = (SyncBaseItem) itemService.getItem(movable);
        actionService.move(syncItem, destination);
    }

    protected void sendBuildCommand(Id builderId, Index toBeBuiltPosition, int toBeBuiltId) throws Exception {
        SyncBaseItem builder = (SyncBaseItem) itemService.getItem(builderId);
        BaseItemType itemType = (BaseItemType) itemService.getItemType(toBeBuiltId);
        actionService.build(builder, toBeBuiltPosition, itemType);
    }

    protected void sendFactoryCommand(Id factoryId, int toBeBuiltId) throws Exception {
        SyncBaseItem factory = (SyncBaseItem) itemService.getItem(factoryId);
        BaseItemType itemType = (BaseItemType) itemService.getItemType(toBeBuiltId);
        actionService.fabricate(factory, itemType);
    }

    protected void sendAttackCommand(Id actorId, Id targetId) throws Exception {
        SyncBaseItem actor = (SyncBaseItem) itemService.getItem(actorId);
        SyncBaseItem target = (SyncBaseItem) itemService.getItem(targetId);
        AttackFormationItem attackFormationItem = collisionService.getDestinationHint(actor,
                actor.getBaseItemType().getWeaponType().getRange(),
                target.getSyncItemArea(),
                target.getTerrainType());
        if (!attackFormationItem.isInRange()) {
            throw new IllegalStateException("Not in range");
        }
        actionService.attack(actor, target, attackFormationItem.getDestinationHint(), attackFormationItem.getDestinationAngel(), true);
    }

    protected void sendAttackCommands(Collection<Id> attackers, Id targetId) throws Exception {
        for (Id attacker : attackers) {
            sendAttackCommand(attacker, targetId);
        }
    }

    protected void sendCollectCommand(Id harvesterId, Id resourceId) throws Exception {
        SyncBaseItem harvester = (SyncBaseItem) itemService.getItem(harvesterId);
        SyncResourceItem syncResourceItem = (SyncResourceItem) itemService.getItem(resourceId);
        AttackFormationItem attackFormationItem = collisionService.getDestinationHint(harvester,
                harvester.getBaseItemType().getHarvesterType().getRange(),
                syncResourceItem.getSyncItemArea(),
                syncResourceItem.getTerrainType());
        if (!attackFormationItem.isInRange()) {
            throw new IllegalStateException("Not in range");
        }
        actionService.collect(harvester, syncResourceItem, attackFormationItem.getDestinationHint(), attackFormationItem.getDestinationAngel());
    }

    protected void sendContainerLoadCommand(Id item, Id containerId) throws Exception {
        SyncBaseItem container = (SyncBaseItem) itemService.getItem(containerId);
        SyncBaseItem syncItem = (SyncBaseItem) itemService.getItem(item);
        actionService.loadContainer(container, syncItem);
    }

    protected void sendUnloadContainerCommand(Id containerId, Index position) throws Exception {
        SyncBaseItem container = (SyncBaseItem) itemService.getItem(containerId);
        actionService.unloadContainer(container, position);
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

    protected void configureRealGame() throws Exception {
        System.out.println("---- Configure Real Game ---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Item Types
        setupItemTypes();
        // Terrain
        setupMinimalTerrain();
        // Setup territory
        setupNoobTerritory();
        // QuestHubs
        setupQuestHubWithOneRealGame(TEST_NOOB_TERRITORY_ID);
        // Xp
        setupXpSettings();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    protected void configureGameMultipleLevel() throws Exception {
        System.out.println("---- configureGameMultipleLevel Game ---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Item Types
        setupItemTypes();
        // Terrain
        setupMinimalTerrain();
        // Setup territory
        setupNoobTerritory();
        // User Guidance
        setupQuestHubMultipleLevels();
        // Xp
        setupXpSettings();
        // Resource fields
        setupResource();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void setupItemTypes() {
        createSimpleBuilding();
        createHarvesterItemType();
        createAttackBaseItemType();
        createContainerBaseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        createAttackBaseItemType2();
        createMoney();
    }

    protected void configureComplexGameOneRealLevel() throws Exception {
        System.out.println("---- Configure complex Game ---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Item Types
        createHarvesterItemType();
        createAttackBaseItemType();
        createContainerBaseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        createSimpleBuilding();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        createMoney();
        // Terrain
        setupComplexTerrain();
        // Setup territory
        setupComplexTerritory();
        // QuestHubs
        setupQuestHubWithOneRealGame(COMPLEX_TERRITORY_ID);
        // Market
        //setupMinimalMarket();
        //setupXpSettings();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    protected void configureComplexGameOneRealLevel2() throws Exception {
        System.out.println("---- Configure complex Game 2---");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        // Item Types
        createHarvesterItemType();
        createAttackBaseItemType();
        createContainerBaseItemType();
        createFactoryBaseItemType();
        createBuilderBaseItemType();
        createSimpleBuilding();
        finishAttackBaseItemType();
        finishContainerBaseItemType();
        createMoney();
        // Terrain
        setupComplexTerrain2();
        // Setup territory
        setupComplexTerritory();
        // QuestHubs
        setupQuestHubWithOneRealGame(COMPLEX_TERRITORY_ID);
        // Market
        //setupMinimalMarket();
        //setupXpSettings();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // ------------------- Setup Item Types --------------------

    protected DbBaseItemType createBuilderBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_START_BUILDER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_24));
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
        dbBaseItemType.setDbBuilderType(dbBuilderType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbBaseItemType.setDbMovableType(dbMovableType);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_START_BUILDER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createFactoryBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 1);
        dbBaseItemType.setName(TEST_FACTORY_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_1));
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
        dbBaseItemType.setDbFactoryType(dbFactoryType);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_FACTORY_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createAttackBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_ATTACK_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_24));
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

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_ATTACK_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private void finishAttackBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID);
        // DbWeaponType
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID), true);
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID), true);
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID), true);

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
    }

    protected DbBaseItemType createAttackBaseItemType2() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_ATTACK_ITEM_2);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_24));
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

        itemService.saveDbItemType(dbBaseItemType);
        TEST_ATTACK_ITEM_ID_2 = dbBaseItemType.getId();
        // DbWeaponType
        dbBaseItemType.getDbWeaponType().setItemTypeAllowed((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID_2), true);
        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        return dbBaseItemType;
    }

    protected DbBaseItemType createContainerBaseItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_CONTAINER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_24));
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

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_CONTAINER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    protected DbBaseItemType createSimpleBuilding() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 1);
        dbBaseItemType.setName(TEST_SIMPLE_BUILDING);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_1));
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
        DbResourceItemType dbResourceItemType = (DbResourceItemType) itemService.getDbItemTypeCrud().createDbChild(DbResourceItemType.class);
        setupImages(dbResourceItemType, 1);
        dbResourceItemType.setName(TEST_RESOURCE_ITEM);
        dbResourceItemType.setTerrainType(TerrainType.LAND);
        dbResourceItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_1));
        dbResourceItemType.setAmount(3);

        itemService.saveDbItemType(dbResourceItemType);
        itemService.activate();
        TEST_RESOURCE_ITEM_ID = dbResourceItemType.getId();
        return dbResourceItemType;
    }

    protected DbBaseItemType createHarvesterItemType() {
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setName(TEST_HARVESTER_ITEM);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(100, 100, 80, 80, ANGELS_24));
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

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        TEST_HARVESTER_ITEM_ID = dbBaseItemType.getId();
        return dbBaseItemType;
    }

    private void setupImages(DbItemType dbItemType, int count) {
        CrudChildServiceHelper<DbItemTypeImage> crud = dbItemType.getItemTypeImageCrud();
        for (int i = 0; i < count; i++) {
            DbItemTypeImage dbItemTypeImage = crud.createDbChild();
            dbItemTypeImage.setNumber(i + 1);
            dbItemTypeImage.setContentType("image");
        }
    }

    // ------------------- Setup Terrain --------------------

    protected DbTerrainSetting setupComplexTerrain() {
        setupTerrainImages();
        DbTerrainSetting dbTerrainSetting = setupComplexRealGameTerrain(createDbSurfaceImage(SurfaceType.LAND));
        terrainService.activateTerrain();
        return dbTerrainSetting;
    }

    protected DbTerrainSetting setupComplexTerrain2() {
        setupTerrainImages();
        DbTerrainSetting dbTerrainSetting = setupComplexRealGameTerrain2();
        terrainService.activateTerrain();
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
        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        DbTerrainImage dbTerrainImage = dbTerrainImageGroup.getTerrainImageCrud().createDbChild();
        dbTerrainImage.setTiles(tileWidth, tileHeight);
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup);
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
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
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
        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChildren().iterator().next();

        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().createDbChild();
        dbTerrainSetting.setRealGame(true);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        dbTerrainSetting.setTileWidth(100);
        dbTerrainSetting.setTileHeight(100);
        DbSurfaceRect dbSurfaceRect = new DbSurfaceRect(new Rectangle(0, 0, 100, 100), dbSurfaceImage);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(dbSurfaceRect, null);
        // Setup Terrain Images
        Collection<DbTerrainImagePosition> dbTerrainImagePositions = new ArrayList<DbTerrainImagePosition>();
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(10, 0), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(0, 13), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(0, 21), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(13, 22), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(20, 16), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(20, 7), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_10x4)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(10, 29), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(15, 26), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(21, 29), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(35, 20), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10)));
        dbTerrainImagePositions.add(new DbTerrainImagePosition(new Index(36, 5), dbTerrainImageGroup.getTerrainImageCrud().readDbChild(TERRAIN_IMAGE_4x10)));


        dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().updateDbChildren(dbTerrainImagePositions);

        terrainService.getDbTerrainSettingCrudServiceHelper().updateDbChild(dbTerrainSetting);
        return dbTerrainSetting;
    }

    protected DbTerrainSetting setupComplexRealGameTerrain2() {
        DbSurfaceImage land = createDbSurfaceImage(SurfaceType.LAND);
        DbSurfaceImage landCoast = createDbSurfaceImage(SurfaceType.LAND_COAST);
        DbSurfaceImage waterCoast = createDbSurfaceImage(SurfaceType.WATER_COAST);
        DbSurfaceImage water = createDbSurfaceImage(SurfaceType.WATER);

        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().readDbChildren().iterator().next();

        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().createDbChild();
        dbTerrainSetting.setRealGame(true);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        dbTerrainSetting.setTileWidth(100);
        dbTerrainSetting.setTileHeight(100);
        // Surface images
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 0, 25, 40), land), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(25, 0, 1, 41), landCoast), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 40, 25, 1), landCoast), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 41, 27, 1), waterCoast), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(26, 0, 1, 41), waterCoast), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(27, 0, 25, 50), water), null);
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(new Rectangle(0, 42, 27, 8), water), null);

        terrainService.getDbTerrainSettingCrudServiceHelper().updateDbChild(dbTerrainSetting);
        return dbTerrainSetting;
    }

    // ------------------- Setup Levels --------------------

    private void setupQuestHubWithOneRealGame(int territoryId) throws Exception {
        DbQuestHub realGameQuestHub = userGuidanceService.getCrudQuestHub().createDbChild();
        realGameQuestHub.setName(TEST_QUEST_HUB_1);
        realGameQuestHub.setStartItemFreeRange(300);
        realGameQuestHub.setStartMoney(1000);
        realGameQuestHub.setStartTerritory(territoryService.getDbTerritoryCrudServiceHelper().readDbChild(territoryId));
        realGameQuestHub.setStartItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        DbLevel dbLevel = createDbLevel2(realGameQuestHub);
        dbLevel.setXp(Integer.MAX_VALUE);
        dbLevel.setNumber(TEST_LEVEL_2_REAL);
        // Limitation
        DbItemTypeLimitation builder = dbLevel.getItemTypeLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbItemTypeLimitation factory = dbLevel.getItemTypeLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbItemTypeLimitation attacker = dbLevel.getItemTypeLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbItemTypeLimitation harvester = dbLevel.getItemTypeLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbItemTypeLimitation container = dbLevel.getItemTypeLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbItemTypeLimitation simpleBuilding = dbLevel.getItemTypeLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);
        userGuidanceService.getCrudQuestHub().updateDbChild(realGameQuestHub);
        userGuidanceService.activateLevels();
        TEST_LEVEL_2_REAL_ID = dbLevel.getId();
    }

    private void setupQuestHubMultipleLevels() throws Exception {
        DbTutorialConfig tut1 = createTutorial1();

        // Setup QuestHub1 - Level - Task - Tutorial
        DbQuestHub startQuestHub = userGuidanceService.getCrudQuestHub().createDbChild();
        startQuestHub.setName(TEST_QUEST_HUB_1);
        startQuestHub.setRealBaseRequired(false);
        DbLevel dbSimLevel = startQuestHub.getLevelCrud().createDbChild();
        dbSimLevel.setNumber(TEST_LEVEL_1_SIMULATED);
        dbSimLevel.setXp(1);
        DbLevelTask dbSimLevelTask = dbSimLevel.getLevelTaskCrud().createDbChild();
        dbSimLevelTask.setDbTutorialConfig(tut1);
        dbSimLevelTask.setName(TEST_LEVEL_TASK_1_1_SIMULATED_NAME);
        dbSimLevelTask.setXp(1);
        userGuidanceService.getCrudQuestHub().updateDbChild(startQuestHub);
        TEST_LEVEL_1_SIMULATED_ID = dbSimLevel.getId();
        TEST_LEVEL_TASK_1_1_SIMULATED_ID = dbSimLevelTask.getId();

        // Setup QuestHub1 - Level1 - 2*LevelTask
        DbQuestHub realGameQuestHub1 = userGuidanceService.getCrudQuestHub().createDbChild();
        realGameQuestHub1.setName(TEST_QUEST_HUB_2);
        realGameQuestHub1.setStartItemFreeRange(300);
        realGameQuestHub1.setStartMoney(1000);
        realGameQuestHub1.setStartTerritory(territoryService.getDbTerritoryCrudServiceHelper().readDbChild(TEST_NOOB_TERRITORY_ID));
        realGameQuestHub1.setStartItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));

        DbLevel dbLevel1 = createDbLevel2(realGameQuestHub1);
        dbLevel1.setHouseSpace(5);
        dbLevel1.setNumber(TEST_LEVEL_2_REAL);
        dbLevel1.setXp(220);
        setLimitation(dbLevel1);
        DbLevelTask dbLevelTask1 = setupCreateLevelTask1RealGameLevel(dbLevel1);
        DbLevelTask dbLevelTask2 = setupCreateLevelTask2RealGameLevel(dbLevel1);

        DbLevel dbLevel2 = realGameQuestHub1.getLevelCrud().createDbChild();
        dbLevel2.setHouseSpace(5);
        dbLevel2.setNumber(TEST_LEVEL_3_REAL);
        dbLevel2.setMaxMoney(10000);
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
        DbLevelTask dbSimLevelTask3 = dbLevel2.getLevelTaskCrud().createDbChild();
        dbSimLevelTask3.setDbTutorialConfig(tut3);
        dbSimLevelTask3.setXp(3);
        dbSimLevelTask3.setName(TEST_LEVEL_TASK_4_3_SIMULATED_NAME);

        DbLevel dbLevel3 = realGameQuestHub1.getLevelCrud().createDbChild();
        dbLevel3.setXp(Integer.MAX_VALUE);
        dbLevel3.setMaxMoney(10000);
        dbLevel3.setHouseSpace(20);
        dbLevel3.setNumber(TEST_LEVEL_4_REAL);
        setLimitation(dbLevel3);
        DbLevelTask dbLevelTask5 = setupCreateLevelTask5RealGameLevel(dbLevel3);
        DbLevelTask dbLevelTask6 = setupCreateLevelTask6RealGameLevel(dbLevel3);


        userGuidanceService.getCrudQuestHub().updateDbChild(startQuestHub);
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

    private void setLimitation(DbLevel dbLevel) {
        DbItemTypeLimitation builder = dbLevel.getItemTypeLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbItemTypeLimitation factory = dbLevel.getItemTypeLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbItemTypeLimitation attacker = dbLevel.getItemTypeLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbItemTypeLimitation harvester = dbLevel.getItemTypeLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbItemTypeLimitation container = dbLevel.getItemTypeLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbItemTypeLimitation simpleBuilding = dbLevel.getItemTypeLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);
    }

    private DbLevel createDbLevel2(DbQuestHub realGameQuestHub) {
        DbLevel dbLevel1 = realGameQuestHub.getLevelCrud().createDbChild();
        dbLevel1.setHouseSpace(20);
        dbLevel1.setMaxMoney(10000);
        dbLevel1.setItemSellFactor(0.5);
        dbLevel1.setNumber(TEST_LEVEL_2_REAL);
        // Limitation
        DbItemTypeLimitation builder = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        builder.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(10);
        DbItemTypeLimitation factory = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        factory.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(10);
        DbItemTypeLimitation attacker = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        attacker.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        attacker.setCount(10);
        DbItemTypeLimitation harvester = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        harvester.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_HARVESTER_ITEM_ID));
        harvester.setCount(10);
        DbItemTypeLimitation container = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        container.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_CONTAINER_ITEM_ID));
        container.setCount(10);
        DbItemTypeLimitation simpleBuilding = dbLevel1.getItemTypeLimitationCrud().createDbChild();
        simpleBuilding.setDbBaseItemType((DbBaseItemType) itemService.getDbItemType(TEST_SIMPLE_BUILDING_ID));
        simpleBuilding.setCount(10);

        return dbLevel1;
    }

    protected DbTutorialConfig createTutorial1() {
        // Tutorial
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        // Terrain
        DbTerrainSetting dbTerrainSetting = setupMinimalSimulatedTerrain();
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
        // Rewards
        dbLevelTask.setMoney(10);
        dbLevelTask.setXp(100);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(3);
        dbCountComparisonConfig.setHtmlProgressTemplate("Money: #C of 3");
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        return dbLevelTask;
    }

    private DbLevelTask setupCreateLevelTask3RealGameLevel(DbLevel dbLevel) {
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
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
        // Rewards
        dbLevelTask.setMoney(80);
        dbLevelTask.setXp(120);
        // Condition
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(2);
        dbCountComparisonConfig.setHtmlProgressTemplate("build #C");
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
        dbItemTypePositionComparisonConfig.setRegion(new Rectangle(200, 200, 1000, 1000));
        dbItemTypePositionComparisonConfig.setTimeInMinutes(1);
        DbComparisonItemCount dbComparisonItemCount = dbItemTypePositionComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setItemType(itemService.getDbItemType(TEST_START_BUILDER_ITEM_ID));
        dbComparisonItemCount.setCount(1);
        dbComparisonItemCount = dbItemTypePositionComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        dbComparisonItemCount.setItemType(itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
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
        limit.setItemType(itemService.getDbItemType(TEST_ATTACK_ITEM_ID));
        limit.setCount(5);
        limit = dbSyncItemTypeComparisonConfig.getCrudDbComparisonItemCount().createDbChild();
        limit.setItemType(itemService.getDbItemType(TEST_FACTORY_ITEM_ID));
        limit.setCount(1);
        dbConditionConfig.setDbAbstractComparisonConfig(dbSyncItemTypeComparisonConfig);
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        return dbLevelTask;
    }

    // ------------------- Setup minimal bot --------------------

    protected DbBotConfig setupMinimalNoAttackBot(Rectangle realm) {
        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(realm);
        dbBotConfig.setRealGameBot(true);
        DbBotItemConfig builder = dbBotConfig.getBotItemCrud().createDbChild();
        builder.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(1);
        builder.setCreateDirectly(true);
        builder.setRegion(realm);
        DbBotItemConfig factory = dbBotConfig.getBotItemCrud().createDbChild();
        factory.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(1);
        factory.setRegion(realm);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);
        botService.activate();
        return dbBotConfig;
    }

    protected DbBotConfig setupMinimalBot(Rectangle realm) {
        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(realm);
        dbBotConfig.setRealGameBot(true);
        DbBotItemConfig builder = dbBotConfig.getBotItemCrud().createDbChild();
        builder.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(1);
        builder.setCreateDirectly(true);
        builder.setRegion(realm);
        DbBotItemConfig factory = dbBotConfig.getBotItemCrud().createDbChild();
        factory.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        factory.setCount(1);
        factory.setRegion(realm);
        DbBotItemConfig defence = dbBotConfig.getBotItemCrud().createDbChild();
        defence.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        defence.setCount(2);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);
        botService.activate();
        return dbBotConfig;
    }

    protected void waitForBotToBuildup(BotConfig botConfig) throws InterruptedException, TimeoutException {
        waitForBotToBuildup(botConfig, 100000);
    }

    protected void waitForBotToBuildup(BotConfig botConfig, int timeOut) throws InterruptedException, TimeoutException {
        long maxTime = System.currentTimeMillis() + timeOut;
        while (!botService.getBotRunner(botConfig).isBuildup()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    // ------------------- Territory Config --------------------

    protected DbTerritory setupSimpleTerritory(String name, int itemTypeId) {
        DbTerritory dbTerritory = setupTerritory(name,
                new int[]{itemTypeId},
                new Rectangle(50, 50, 50, 50));
        return dbTerritory;
    }

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

    // ------------------- DbXpSettings Config --------------------

    protected DbXpSettings setupXpSettings() {
        DbXpSettings dbXpSettings = xpServic.getXpPointSettings();
        dbXpSettings.setKillPriceFactor(1);
        dbXpSettings.setKillQueuePeriod(20);
        dbXpSettings.setKillQueueSize(10000);
        dbXpSettings.setBuiltPriceFactor(0.5);
        xpServic.saveXpPointSettings(dbXpSettings);
        return dbXpSettings;
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


    // ------------------- Div --------------------

    protected Services createMockServices() {
        Services services = EasyMock.createNiceMock(Services.class);
        AbstractTerrainService terrainService = EasyMock.createNiceMock(AbstractTerrainService.class);
        EasyMock.expect(services.getTerrainService()).andReturn(terrainService);
        EasyMock.replay(services);
        return services;
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

}
