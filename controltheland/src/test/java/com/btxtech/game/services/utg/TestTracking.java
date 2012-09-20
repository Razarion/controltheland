package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.control.ColdRealGameStartupTaskEnum;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.jsre.playback.PlaybackInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.tracker.DbStartupTask;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 10.04.2011
 * Time: 15:42:16
 */
public class TestTracking extends AbstractServiceTest {
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private Session session;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testSimple() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDto = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(1, sessionOverviewDto.size());

        UserTrackingFilter userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.DISABLED);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(0, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.BOTH);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(1, sessionOverviewDto.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPageHits() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(true);
        userTrackingService.pageAccess("Page 1", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(true);
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.pageAccess("Page 2", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(true);
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.pageAccess("Page 2", null);
        userTrackingService.pageAccess("Page 3", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(true);
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.pageAccess("Page 2", null);
        userTrackingService.pageAccess("Page 3", null);
        userTrackingService.pageAccess("Page 4", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDto = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(5, sessionOverviewDto.size());
        Assert.assertEquals(4, sessionOverviewDto.get(0).getPageHits());
        Assert.assertEquals(3, sessionOverviewDto.get(1).getPageHits());
        Assert.assertEquals(2, sessionOverviewDto.get(2).getPageHits());
        Assert.assertEquals(1, sessionOverviewDto.get(3).getPageHits());
        Assert.assertEquals(0, sessionOverviewDto.get(4).getPageHits());

        UserTrackingFilter filter = UserTrackingFilter.newDefaultFilter();
        filter.setHits(1);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(filter);
        Assert.assertEquals(4, sessionOverviewDto.size());
        Assert.assertEquals(4, sessionOverviewDto.get(0).getPageHits());
        Assert.assertEquals(3, sessionOverviewDto.get(1).getPageHits());
        Assert.assertEquals(2, sessionOverviewDto.get(2).getPageHits());
        Assert.assertEquals(1, sessionOverviewDto.get(3).getPageHits());

        filter = UserTrackingFilter.newDefaultFilter();
        filter.setHits(2);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(filter);
        Assert.assertEquals(3, sessionOverviewDto.size());
        Assert.assertEquals(4, sessionOverviewDto.get(0).getPageHits());
        Assert.assertEquals(3, sessionOverviewDto.get(1).getPageHits());
        Assert.assertEquals(2, sessionOverviewDto.get(2).getPageHits());

        filter = UserTrackingFilter.newDefaultFilter();
        filter.setHits(3);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(filter);
        Assert.assertEquals(2, sessionOverviewDto.size());
        Assert.assertEquals(4, sessionOverviewDto.get(0).getPageHits());
        Assert.assertEquals(3, sessionOverviewDto.get(1).getPageHits());

        filter = UserTrackingFilter.newDefaultFilter();
        filter.setHits(4);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(filter);
        Assert.assertEquals(1, sessionOverviewDto.size());
        Assert.assertEquals(4, sessionOverviewDto.get(0).getPageHits());

        filter = UserTrackingFilter.newDefaultFilter();
        filter.setHits(5);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(filter);
        Assert.assertEquals(0, sessionOverviewDto.size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCookie() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        String cookieId1 = session.getCookieId();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 2", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        String cookieId2 = session.getCookieId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        UserTrackingFilter userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setCookieId(cookieId1);
        List<SessionOverviewDto> sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(1, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setCookieId(cookieId2);
        userTrackingFilter.setJsEnabled(UserTrackingFilter.DISABLED);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(1, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setCookieId("hol' ihn der Klabautermann!");
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(0, sessionOverviewDto.size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testMulti() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDto = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(2, sessionOverviewDto.size());

        UserTrackingFilter userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.DISABLED);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(1, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.BOTH);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(3, sessionOverviewDto.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSessionFilter() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        String sessionId1 = getHttpSessionId();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        String sessionId2 = getHttpSessionId();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        String sessionId3 = getHttpSessionId();
        userTrackingService.pageAccess("Page 1", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDto = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(2, sessionOverviewDto.size());

        UserTrackingFilter userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.BOTH);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(3, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setSessionId(sessionId1);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(1, sessionOverviewDto.size());
        Assert.assertEquals(sessionId1, sessionOverviewDto.get(0).getSessionId());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setSessionId(sessionId2);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(1, sessionOverviewDto.size());
        Assert.assertEquals(sessionId2, sessionOverviewDto.get(0).getSessionId());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setSessionId(sessionId3);
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(0, sessionOverviewDto.size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testStartup() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();

        String uuidReal = "00001";
        String uuidTut = "00002";
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeS1 = System.currentTimeMillis();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 1000, 100), uuidReal, null);
        long timeS2 = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeT1 = System.currentTimeMillis();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 1500, 200), uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        long timeT2 = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GAME, 2000, 300), uuidReal, null);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GUI, 2500, 110), uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_UNITS, 3000, 130), uuidReal, null);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 3500, 150), uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTerminated(true, 2000, uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 1900, 60), uuidReal, null);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTerminated(true, 1000, uuidReal, null);
        endHttpRequestAndOpenSessionInViewFilter();

        // Verify
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDtos = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(1, sessionOverviewDtos.size());
        SessionOverviewDto sessionOverviewDto = sessionOverviewDtos.get(0);
        Assert.assertEquals(2, sessionOverviewDto.getStartAttempts());
        Assert.assertEquals(2, sessionOverviewDto.getStartSucceeded());
        Assert.assertFalse(sessionOverviewDto.isStartupFailure());

        SessionDetailDto sessionDetailDto = userTrackingService.getSessionDetailDto(sessionOverviewDto.getSessionId());
        Assert.assertEquals(2, sessionDetailDto.getLifecycleTrackingInfos().size());
        // Real game
        LifecycleTrackingInfo lifecycleTrackingInfo = sessionDetailDto.getLifecycleTrackingInfos().get(0);
        Assert.assertTrue(lifecycleTrackingInfo.isRealGame());
        Assert.assertTrue(timeS1 <= lifecycleTrackingInfo.getStartServer());
        Assert.assertTrue(timeS2 >= lifecycleTrackingInfo.getStartServer());
        Assert.assertEquals("Base 1", lifecycleTrackingInfo.getBaseName());
        Assert.assertEquals(Integer.toString(TEST_LEVEL_3_REAL), lifecycleTrackingInfo.getLevel());
        Assert.assertEquals(1000, lifecycleTrackingInfo.getStartupDuration());
        Assert.assertEquals(4, lifecycleTrackingInfo.getGameStartups().size());
        Assert.assertNull(lifecycleTrackingInfo.getLevelTaskName());
        Assert.assertEquals("Load JavaScript", lifecycleTrackingInfo.getGameStartups().get(0).getTask());
        Assert.assertEquals("Load Map", lifecycleTrackingInfo.getGameStartups().get(1).getTask());
        Assert.assertEquals("Init real Game", lifecycleTrackingInfo.getGameStartups().get(2).getTask());
        Assert.assertEquals("Load Units", lifecycleTrackingInfo.getGameStartups().get(3).getTask());
        // Tutorial
        lifecycleTrackingInfo = sessionDetailDto.getLifecycleTrackingInfos().get(1);
        Assert.assertFalse(lifecycleTrackingInfo.isRealGame());
        Assert.assertTrue(timeT1 <= lifecycleTrackingInfo.getStartServer());
        Assert.assertTrue(timeT2 >= lifecycleTrackingInfo.getStartServer());
        Assert.assertNull(lifecycleTrackingInfo.getBaseName());
        Assert.assertEquals(Integer.toString(TEST_LEVEL_3_REAL), lifecycleTrackingInfo.getLevel());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_NAME, lifecycleTrackingInfo.getLevelTaskName());
        Assert.assertEquals(2000, lifecycleTrackingInfo.getStartupDuration());
        Assert.assertEquals(3, lifecycleTrackingInfo.getGameStartups().size());
        Assert.assertEquals("Load JavaScript", lifecycleTrackingInfo.getGameStartups().get(0).getTask());
        Assert.assertEquals("Init GUI", lifecycleTrackingInfo.getGameStartups().get(1).getTask());
        Assert.assertEquals("Load Map", lifecycleTrackingInfo.getGameStartups().get(2).getTask());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testStartupFailure() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();

        String uuidReal = "00001";
        String uuidTut = "00002";
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeS1 = System.currentTimeMillis();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 1000, 100), uuidReal, null);
        long timeS2 = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeT1 = System.currentTimeMillis();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 1500, 200), uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        long timeT2 = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GAME, 2000, 300), uuidReal, null);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GUI, 2500, 110), uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_UNITS, 3000, 130), uuidReal, null);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 3500, 150), uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTerminated(false, 2000, uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 1900, 60), uuidReal, null);
        endHttpRequestAndOpenSessionInViewFilter();

        // Verify
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDtos = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(1, sessionOverviewDtos.size());
        SessionOverviewDto sessionOverviewDto = sessionOverviewDtos.get(0);
        Assert.assertEquals(2, sessionOverviewDto.getStartAttempts());
        Assert.assertEquals(0, sessionOverviewDto.getStartSucceeded());
        Assert.assertTrue(sessionOverviewDto.isStartupFailure());

        SessionDetailDto sessionDetailDto = userTrackingService.getSessionDetailDto(sessionOverviewDto.getSessionId());
        Assert.assertEquals(2, sessionDetailDto.getLifecycleTrackingInfos().size());
        // Real game
        LifecycleTrackingInfo lifecycleTrackingInfo = sessionDetailDto.getLifecycleTrackingInfos().get(0);
        Assert.assertTrue(lifecycleTrackingInfo.isRealGame());
        Assert.assertTrue(timeS1 <= lifecycleTrackingInfo.getStartServer());
        Assert.assertTrue(timeS2 >= lifecycleTrackingInfo.getStartServer());
        Assert.assertEquals("Base 1", lifecycleTrackingInfo.getBaseName());
        Assert.assertEquals(Integer.toString(TEST_LEVEL_3_REAL), lifecycleTrackingInfo.getLevel());
        Assert.assertEquals(0, lifecycleTrackingInfo.getStartupDuration());
        Assert.assertEquals(4, lifecycleTrackingInfo.getGameStartups().size());
        Assert.assertNull(lifecycleTrackingInfo.getLevelTaskName());
        Assert.assertEquals("Load JavaScript", lifecycleTrackingInfo.getGameStartups().get(0).getTask());
        Assert.assertEquals("Load Map", lifecycleTrackingInfo.getGameStartups().get(1).getTask());
        Assert.assertEquals("Init real Game", lifecycleTrackingInfo.getGameStartups().get(2).getTask());
        Assert.assertEquals("Load Units", lifecycleTrackingInfo.getGameStartups().get(3).getTask());
        // Tutorial
        lifecycleTrackingInfo = sessionDetailDto.getLifecycleTrackingInfos().get(1);
        Assert.assertFalse(lifecycleTrackingInfo.isRealGame());
        Assert.assertTrue(timeT1 <= lifecycleTrackingInfo.getStartServer());
        Assert.assertTrue(timeT2 >= lifecycleTrackingInfo.getStartServer());
        Assert.assertNull(lifecycleTrackingInfo.getBaseName());
        Assert.assertEquals(Integer.toString(TEST_LEVEL_3_REAL), lifecycleTrackingInfo.getLevel());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_NAME, lifecycleTrackingInfo.getLevelTaskName());
        Assert.assertEquals(2000, lifecycleTrackingInfo.getStartupDuration());
        Assert.assertEquals(3, lifecycleTrackingInfo.getGameStartups().size());
        Assert.assertEquals("Load JavaScript", lifecycleTrackingInfo.getGameStartups().get(0).getTask());
        Assert.assertEquals("Init GUI", lifecycleTrackingInfo.getGameStartups().get(1).getTask());
        Assert.assertEquals("Load Map", lifecycleTrackingInfo.getGameStartups().get(2).getTask());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTutorialTracking() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();

        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();

        tutorial1();
        tutorial2();
        realGame1();
        realGame2();
        endHttpSession();

        // Verify
        verifyTutorial1();
        verifyTutorial2();
        verifyRealGame1();
        verifyRealGame2();
    }

    private void tutorial1() throws Exception {
        String uuid = "00002";
        // 0 until 1550 (client time )
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 1000, 100), uuid, TEST_LEVEL_TASK_4_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TASK, uuid, TEST_LEVEL_TASK_4_3_SIMULATED_ID, "task1", 1, 1500);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();

        // Tracking start
        userTrackingService.onEventTrackingStart(new EventTrackingStart(uuid, 101, 102, 103, 104, 105, 106, 1200));
        // Mouse
        Collection<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 1, 1, 1000));
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 10, 2, 1100));
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 10, 3, 1200));
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 10, 4, 1300));
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 10, 5, 1400));
        // SyncItemInfo                                                                     1
        Collection<SyncItemInfo> itemInfos = new ArrayList<SyncItemInfo>();
        SyncItemInfo syncItemInfo = new SyncItemInfo();
        syncItemInfo.setStartUuid(uuid);
        syncItemInfo.setAmount(0.5);
        syncItemInfo.setId(new Id(1, 1, 1));
        setPrivateField(SyncItemInfo.class, syncItemInfo, "clientTimeStamp", 1200L);
        itemInfos.add(syncItemInfo);
        // Selection
        Collection<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
        SelectionTrackingItem selectionTrackingItem = new SelectionTrackingItem(uuid);
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem, "timeStamp", 1300);
        selectionTrackingItems.add(selectionTrackingItem);
        // Terrain scrolling
        Collection<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 2, 1, 1100));
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 3, 1, 1150));
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 1, 1, 1050));
        // Browser window
        Collection<BrowserWindowTracking> browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 2, 3, 4, 5, 6, 1100));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 2, 2, 3, 4, 5, 6, 1200));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 3, 2, 3, 4, 5, 6, 1300));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 4, 2, 3, 4, 5, 6, 1400));
        // Dialogs
        Collection<DialogTracking> dialogTrackings = new ArrayList<DialogTracking>();
        dialogTrackings.add(new DialogTracking(uuid, 1, 2, 3, 4, 1, "dialog1", false, 42, 1100));
        dialogTrackings.add(new DialogTracking(uuid, 5, 6, 7, 8, 2, "dialog2", false, 43, 1120));
        dialogTrackings.add(new DialogTracking(uuid, null, null, null, null, null, "dialog3", false, 44, 1140));

        getMovableService().sendEventTrackerItems(eventTrackingItems, itemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings, dialogTrackings);
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TASK, uuid, TEST_LEVEL_TASK_4_3_SIMULATED_ID, "task2", 1, 1550);
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, uuid, TEST_LEVEL_TASK_4_3_SIMULATED_ID, "tutorial1", 1, 1550);
        endHttpRequestAndOpenSessionInViewFilter();
    }

    private void tutorial2() throws Exception {
        String uuid = "00052";
        // 1600 until 3100 (client time )
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 1600, 100), uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GAME, 1700, 150), uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GUI, 1850, 50), uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 1900, 200), uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();


        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TASK, uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID, "task3", 1, 3000);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();

        // Tracking start
        userTrackingService.onEventTrackingStart(new EventTrackingStart(uuid, 201, 202, 203, 204, 205, 206, 1900));
        // Mouse
        Collection<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 1, 1, 1910));
        eventTrackingItems.add(new EventTrackingItem(uuid, 2, 10, 1, 1920));
        eventTrackingItems.add(new EventTrackingItem(uuid, 3, 10, 1, 1930));
        eventTrackingItems.add(new EventTrackingItem(uuid, 4, 10, 1, 1940));
        eventTrackingItems.add(new EventTrackingItem(uuid, 5, 10, 1, 1950));
        // SyncItemInfos                                                                     1
        Collection<SyncItemInfo> syncItemInfos = new ArrayList<SyncItemInfo>();
        SyncItemInfo syncItemInfo = new SyncItemInfo();
        syncItemInfo.setStartUuid(uuid);
        syncItemInfo.setFollowTarget(true);
        syncItemInfo.setId(new Id(2, 2, 2));
        setPrivateField(SyncItemInfo.class, syncItemInfo, "clientTimeStamp", 2000L);
        syncItemInfos.add(syncItemInfo);
        // Selection
        Collection<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
        SelectionTrackingItem selectionTrackingItem1 = new SelectionTrackingItem(uuid);
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem1, "timeStamp", 2050);
        selectionTrackingItems.add(selectionTrackingItem1);
        SelectionTrackingItem selectionTrackingItem2 = new SelectionTrackingItem(uuid);
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem2, "timeStamp", 2100);
        selectionTrackingItems.add(selectionTrackingItem2);
        // Terrain scrolling
        Collection<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 1, 1, 2100));
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 1, 2, 2200));
        // Browser window
        Collection<BrowserWindowTracking> browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 1, 3, 4, 5, 6, 2100));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 2, 3, 4, 5, 6, 2120));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 3, 3, 4, 5, 6, 2130));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 4, 3, 4, 5, 6, 2140));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 5, 3, 4, 5, 6, 2150));
        // Dialogs
        Collection<DialogTracking> dialogTrackings = new ArrayList<DialogTracking>();
        dialogTrackings.add(new DialogTracking(uuid, 10, 2, 3, 4, 19, "dialog11", true, 52, 2100));
        dialogTrackings.add(new DialogTracking(uuid, 50, 6, 7, 8, 29, "dialog12", true, 53, 2120));
        dialogTrackings.add(new DialogTracking(uuid, 90, 10, 11, 12, 39, "dialog13", true, 54, 2140));
        dialogTrackings.add(new DialogTracking(uuid, 130, 14, 15, 16, 49, "dialog14", true, 55, 2160));

        getMovableService().sendEventTrackerItems(eventTrackingItems, syncItemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings, dialogTrackings);
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TASK, uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID, "task4", 1, 3100);
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID, "tutorial2", 1, 3100);
        endHttpRequestAndOpenSessionInViewFilter();
    }

    private void realGame1() throws Exception {
        String uuid = "00018";
        // 1600 until 3100 (client time )
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 3200, 100), uuid, null);
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 3300, 400), uuid, null);
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_UNITS, 3700, 500), uuid, null);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getMyBase();
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();

        endHttpRequestAndOpenSessionInViewFilter();
    }

    private void realGame2() throws Exception {
        String uuid = "00033";
        // 1600 until 3100 (client time )
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GAME, 3200, 100), uuid, null);
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.DOWNLOAD_GAME_INFO, 3300, 400), uuid, null);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getMyBase();
        sendFactoryCommand(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID), TEST_CONTAINER_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID), TEST_HARVESTER_ITEM_ID);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
    }

    private void verifyTutorial1() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDtos = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(1, sessionOverviewDtos.size());
        SessionOverviewDto sessionOverviewDto = sessionOverviewDtos.get(0);
        SessionDetailDto sessionDetailDto = userTrackingService.getSessionDetailDto(sessionOverviewDto.getSessionId());
        // Life cycle
        List<LifecycleTrackingInfo> lifecycleTrackingInfos = sessionDetailDto.getLifecycleTrackingInfos();
        Assert.assertEquals(4, lifecycleTrackingInfos.size());
        // Start ups
        List<DbStartupTask> dbStartupTasks = lifecycleTrackingInfos.get(0).getGameStartups();
        Assert.assertEquals(1, dbStartupTasks.size());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(0).getTask());
        // Tutorial progress
        TutorialTrackingInfo tutorialTrackingInfo = userTrackingService.getTutorialTrackingInfo(lifecycleTrackingInfos.get(0));
        Assert.assertEquals(3, tutorialTrackingInfo.getDbTutorialProgresss().size());
        Assert.assertEquals("task1", tutorialTrackingInfo.getDbTutorialProgresss().get(0).getTutorialTaskName());
        Assert.assertEquals("task2", tutorialTrackingInfo.getDbTutorialProgresss().get(1).getTutorialTaskName());
        Assert.assertEquals("tutorial1", tutorialTrackingInfo.getDbTutorialProgresss().get(2).getTutorialTaskName());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_NAME, tutorialTrackingInfo.getDbTutorialProgresss().get(0).getLevelTaskName());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_NAME, tutorialTrackingInfo.getDbTutorialProgresss().get(1).getLevelTaskName());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_NAME, tutorialTrackingInfo.getDbTutorialProgresss().get(2).getLevelTaskName());
        Assert.assertEquals(101, tutorialTrackingInfo.getDbEventTrackingStart().getClientWidth());
        Assert.assertEquals(102, tutorialTrackingInfo.getDbEventTrackingStart().getClientHeight());
        // Playback
        PlaybackInfo playbackInfo = getPlaybackService().getPlaybackInfo(lifecycleTrackingInfos.get(0).getStartUuid());
        // Tracking start
        Assert.assertEquals(101, playbackInfo.getEventTrackingStart().getClientWidth());
        Assert.assertEquals(102, playbackInfo.getEventTrackingStart().getClientHeight());
        Assert.assertEquals(1200, playbackInfo.getEventTrackingStart().getClientTimeStamp());
        Assert.assertEquals(103, playbackInfo.getEventTrackingStart().getScrollLeft());
        Assert.assertEquals(104, playbackInfo.getEventTrackingStart().getScrollTop());
        Assert.assertEquals(105, playbackInfo.getEventTrackingStart().getScrollWidth());
        Assert.assertEquals(106, playbackInfo.getEventTrackingStart().getScrollHeight());
        // Mouse
        Assert.assertEquals(5, playbackInfo.getEventTrackingItems().size());
        Assert.assertEquals(1, playbackInfo.getEventTrackingItems().get(0).getEventType());
        Assert.assertEquals(2, playbackInfo.getEventTrackingItems().get(1).getEventType());
        Assert.assertEquals(3, playbackInfo.getEventTrackingItems().get(2).getEventType());
        Assert.assertEquals(4, playbackInfo.getEventTrackingItems().get(3).getEventType());
        Assert.assertEquals(5, playbackInfo.getEventTrackingItems().get(4).getEventType());
        // SyncItemInfos
        Assert.assertEquals(1, playbackInfo.getSyncItemInfos().size());
        Assert.assertEquals(new Id(1, 1, 1), playbackInfo.getSyncItemInfos().get(0).getId());
        Assert.assertEquals(0.5, playbackInfo.getSyncItemInfos().get(0).getAmount(), 0.001);
        // Selection
        Assert.assertEquals(1, playbackInfo.getSelectionTrackingItems().size());
        Assert.assertNull(playbackInfo.getSelectionTrackingItems().get(0).isOwn());
        // Terrain scrolling
        Assert.assertEquals(3, playbackInfo.getScrollTrackingItems().size());
        Assert.assertEquals(1, playbackInfo.getScrollTrackingItems().get(0).getLeft());
        Assert.assertEquals(2, playbackInfo.getScrollTrackingItems().get(1).getLeft());
        Assert.assertEquals(3, playbackInfo.getScrollTrackingItems().get(2).getLeft());
        // Browser Window Tracking
        Assert.assertEquals(4, playbackInfo.getBrowserWindowTrackings().size());
        Assert.assertEquals(1, playbackInfo.getBrowserWindowTrackings().get(0).getClientWidth());
        Assert.assertEquals(2, playbackInfo.getBrowserWindowTrackings().get(1).getClientWidth());
        Assert.assertEquals(3, playbackInfo.getBrowserWindowTrackings().get(2).getClientWidth());
        Assert.assertEquals(4, playbackInfo.getBrowserWindowTrackings().get(3).getClientWidth());
        // Dialog trcking
        Assert.assertEquals(3, playbackInfo.getDialogTrackings().size());
        Assert.assertEquals(1, (int) playbackInfo.getDialogTrackings().get(0).getLeft());
        Assert.assertEquals(2, (int) playbackInfo.getDialogTrackings().get(0).getTop());
        Assert.assertEquals(3, (int) playbackInfo.getDialogTrackings().get(0).getWidth());
        Assert.assertEquals(4, (int) playbackInfo.getDialogTrackings().get(0).getHeight());
        Assert.assertEquals(1, (int) playbackInfo.getDialogTrackings().get(0).getZIndex());
        Assert.assertEquals("dialog1", playbackInfo.getDialogTrackings().get(0).getDescription());
        Assert.assertEquals(42, playbackInfo.getDialogTrackings().get(0).getIdentityHashCode());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void verifyTutorial2() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDtos = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(1, sessionOverviewDtos.size());
        SessionOverviewDto sessionOverviewDto = sessionOverviewDtos.get(0);
        SessionDetailDto sessionDetailDto = userTrackingService.getSessionDetailDto(sessionOverviewDto.getSessionId());
        // Life cycle
        List<LifecycleTrackingInfo> lifecycleTrackingInfos = sessionDetailDto.getLifecycleTrackingInfos();
        Assert.assertEquals(4, lifecycleTrackingInfos.size());
        // Start ups
        List<DbStartupTask> dbStartupTasks = lifecycleTrackingInfos.get(1).getGameStartups();
        Assert.assertEquals(4, dbStartupTasks.size());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(0).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.INIT_GAME.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(1).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.INIT_GUI.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(2).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_MAP.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(3).getTask());
        // Tutorial progress
        TutorialTrackingInfo tutorialTrackingInfo = userTrackingService.getTutorialTrackingInfo(lifecycleTrackingInfos.get(1));
        Assert.assertEquals(3, tutorialTrackingInfo.getDbTutorialProgresss().size());
        Assert.assertEquals("task3", tutorialTrackingInfo.getDbTutorialProgresss().get(0).getTutorialTaskName());
        Assert.assertEquals("task4", tutorialTrackingInfo.getDbTutorialProgresss().get(1).getTutorialTaskName());
        Assert.assertEquals("tutorial2", tutorialTrackingInfo.getDbTutorialProgresss().get(2).getTutorialTaskName());
        Assert.assertEquals(201, tutorialTrackingInfo.getDbEventTrackingStart().getClientWidth());
        Assert.assertEquals(202, tutorialTrackingInfo.getDbEventTrackingStart().getClientHeight());
        // Playback
        PlaybackInfo playbackInfo = getPlaybackService().getPlaybackInfo(lifecycleTrackingInfos.get(1).getStartUuid());
        // Tracking start
        Assert.assertEquals(201, playbackInfo.getEventTrackingStart().getClientWidth());
        Assert.assertEquals(202, playbackInfo.getEventTrackingStart().getClientHeight());
        Assert.assertEquals(1900, playbackInfo.getEventTrackingStart().getClientTimeStamp());
        Assert.assertEquals(203, playbackInfo.getEventTrackingStart().getScrollLeft());
        Assert.assertEquals(204, playbackInfo.getEventTrackingStart().getScrollTop());
        Assert.assertEquals(205, playbackInfo.getEventTrackingStart().getScrollWidth());
        Assert.assertEquals(206, playbackInfo.getEventTrackingStart().getScrollHeight());
        // Mouse
        Assert.assertEquals(5, playbackInfo.getEventTrackingItems().size());
        Assert.assertEquals(1, playbackInfo.getEventTrackingItems().get(0).getXPos());
        Assert.assertEquals(2, playbackInfo.getEventTrackingItems().get(1).getXPos());
        Assert.assertEquals(3, playbackInfo.getEventTrackingItems().get(2).getXPos());
        Assert.assertEquals(4, playbackInfo.getEventTrackingItems().get(3).getXPos());
        Assert.assertEquals(5, playbackInfo.getEventTrackingItems().get(4).getXPos());
        // Commands
        Assert.assertEquals(1, playbackInfo.getSyncItemInfos().size());
        Assert.assertEquals(new Id(2, 2, 2), playbackInfo.getSyncItemInfos().get(0).getId());
        Assert.assertTrue(playbackInfo.getSyncItemInfos().get(0).isFollowTarget());
        // Selection
        Assert.assertEquals(2, playbackInfo.getSelectionTrackingItems().size());
        Assert.assertNull(playbackInfo.getSelectionTrackingItems().get(0).isOwn());
        Assert.assertNull(playbackInfo.getSelectionTrackingItems().get(1).isOwn());
        // Terrain scrolling
        Assert.assertEquals(2, playbackInfo.getScrollTrackingItems().size());
        Assert.assertEquals(1, playbackInfo.getScrollTrackingItems().get(0).getTop());
        Assert.assertEquals(2, playbackInfo.getScrollTrackingItems().get(1).getTop());
        // Browser Window Tracking
        Assert.assertEquals(5, playbackInfo.getBrowserWindowTrackings().size());
        Assert.assertEquals(1, playbackInfo.getBrowserWindowTrackings().get(0).getClientHeight());
        Assert.assertEquals(2, playbackInfo.getBrowserWindowTrackings().get(1).getClientHeight());
        Assert.assertEquals(3, playbackInfo.getBrowserWindowTrackings().get(2).getClientHeight());
        Assert.assertEquals(4, playbackInfo.getBrowserWindowTrackings().get(3).getClientHeight());
        Assert.assertEquals(5, playbackInfo.getBrowserWindowTrackings().get(4).getClientHeight());
        // Dialog trcking
        Assert.assertEquals(4, playbackInfo.getDialogTrackings().size());
        Assert.assertEquals(10, (int) playbackInfo.getDialogTrackings().get(0).getLeft());
        Assert.assertEquals(50, (int) playbackInfo.getDialogTrackings().get(1).getLeft());
        Assert.assertEquals(90, (int) playbackInfo.getDialogTrackings().get(2).getLeft());
        Assert.assertEquals(130, (int) playbackInfo.getDialogTrackings().get(3).getLeft());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void verifyRealGame1() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDtos = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(1, sessionOverviewDtos.size());
        SessionOverviewDto sessionOverviewDto = sessionOverviewDtos.get(0);
        SessionDetailDto sessionDetailDto = userTrackingService.getSessionDetailDto(sessionOverviewDto.getSessionId());
        // Life cycle
        List<LifecycleTrackingInfo> lifecycleTrackingInfos = sessionDetailDto.getLifecycleTrackingInfos();
        Assert.assertEquals(4, lifecycleTrackingInfos.size());
        // Start ups
        List<DbStartupTask> dbStartupTasks = lifecycleTrackingInfos.get(2).getGameStartups();
        Assert.assertEquals(3, dbStartupTasks.size());

        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(0).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_MAP.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(1).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_UNITS.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(2).getTask());
        // Real Game
        RealGameTrackingInfo realGameTrackingInfo = userTrackingService.getGameTracking(lifecycleTrackingInfos.get(2));
        List<UserCommandHistoryElement> userCommandHistoryElements = realGameTrackingInfo.getUserCommandHistoryElements();
        System.out.println("----------History----------");
        for (UserCommandHistoryElement userCommandHistoryElement : userCommandHistoryElements) {
            System.out.println(userCommandHistoryElement.getTimeStamp() + "|" + userCommandHistoryElement.getInfo1() + "|" + userCommandHistoryElement.getInfo2());
        }
        Assert.assertEquals(2, userCommandHistoryElements.size());
        Assert.assertEquals("Item created: TestFactoryItem", userCommandHistoryElements.get(0).getInfo1());
        Assert.assertEquals("Item created: TestAttackItem", userCommandHistoryElements.get(1).getInfo1());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void verifyRealGame2() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDtos = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(1, sessionOverviewDtos.size());
        SessionOverviewDto sessionOverviewDto = sessionOverviewDtos.get(0);
        SessionDetailDto sessionDetailDto = userTrackingService.getSessionDetailDto(sessionOverviewDto.getSessionId());
        // Life cycle
        List<LifecycleTrackingInfo> lifecycleTrackingInfos = sessionDetailDto.getLifecycleTrackingInfos();
        Assert.assertEquals(4, lifecycleTrackingInfos.size());
        // Start ups
        List<DbStartupTask> dbStartupTasks = lifecycleTrackingInfos.get(3).getGameStartups();
        Assert.assertEquals(2, dbStartupTasks.size());

        Assert.assertEquals(ColdRealGameStartupTaskEnum.INIT_GAME.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(0).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.DOWNLOAD_GAME_INFO.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(1).getTask());
        // Real Game
        RealGameTrackingInfo realGameTrackingInfo = userTrackingService.getGameTracking(lifecycleTrackingInfos.get(3));
        List<UserCommandHistoryElement> userCommandHistoryElements = realGameTrackingInfo.getUserCommandHistoryElements();
        System.out.println("----------History----------");
        for (UserCommandHistoryElement userCommandHistoryElement : userCommandHistoryElements) {
            System.out.println(userCommandHistoryElement.getTimeStamp() + "|" + userCommandHistoryElement.getInfo1() + "|" + userCommandHistoryElement.getInfo2());
        }
        Assert.assertEquals(2, userCommandHistoryElements.size());
        Assert.assertEquals("Item created: TestContainerItem", userCommandHistoryElements.get(0).getInfo1());
        Assert.assertEquals("Item created: TEST_HARVESTER_ITEM", userCommandHistoryElements.get(1).getInfo1());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testHtml5Detection() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(userTrackingService.isHtml5Support());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(null);
        Assert.assertTrue(userTrackingService.isHtml5Support());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(true);
        Assert.assertTrue(userTrackingService.isHtml5Support());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(false);
        Assert.assertFalse(userTrackingService.isHtml5Support());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
