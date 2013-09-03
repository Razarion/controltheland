package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.control.ColdRealGameStartupTaskEnum;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
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
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.GameHistoryFilter;
import com.btxtech.game.services.history.GameHistoryFrame;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.user.DbGuild;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.tracker.DbSessionDetail;
import com.btxtech.game.services.utg.tracker.DbStartupTask;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    @Autowired
    private HistoryService historyService;

    @Test
    @DirtiesContext
    public void testSimple() throws Exception {
        configureSimplePlanetNoResources();

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
        configureSimplePlanetNoResources();

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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        String cookieId1 = session.getTrackingCookieId();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 2", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        String cookieId2 = session.getTrackingCookieId();
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
        configureSimplePlanetNoResources();

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
    public void testFacebookAd() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        saveOrUpdateInTransaction(new DbSessionDetail("123", null, "", "", "", "", false, null, null));
        DbFacebookSource dbFacebookSource = new DbFacebookSource();
        dbFacebookSource.setOptionalAdValue("xxx");
        saveOrUpdateInTransaction(new DbSessionDetail("123", null, "", "", "", "", false, dbFacebookSource, null));
        dbFacebookSource = new DbFacebookSource();
        dbFacebookSource.setOptionalAdValue("qqq");
        saveOrUpdateInTransaction(new DbSessionDetail("123", null, "", "", "", "", false, dbFacebookSource, null));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        UserTrackingFilter userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.BOTH);
        userTrackingFilter.setOptionalFacebookAdValue("xxx");
        List<SessionOverviewDto> sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(1, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.BOTH);
        userTrackingFilter.setOptionalFacebookAdValue("qqq");
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(1, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setOptionalFacebookAdValue("qqq");
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(0, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.BOTH);
        userTrackingFilter.setOptionalFacebookAdValue("hhh");
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(0, sessionOverviewDto.size());

        userTrackingFilter = UserTrackingFilter.newDefaultFilter();
        userTrackingFilter.setJsEnabled(UserTrackingFilter.BOTH);
        userTrackingFilter.setOptionalFacebookAdValue("%");
        sessionOverviewDto = userTrackingService.getSessionOverviewDtos(userTrackingFilter);
        Assert.assertEquals(2, sessionOverviewDto.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSessionFilter() throws Exception {
        configureSimplePlanetNoResources();

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
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.PRELOAD_IMAGE_SPRITE_MAPS, 3500, 150), uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTerminated(true, 2000, uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.PRELOAD_IMAGE_SPRITE_MAPS, 1900, 60), uuidReal, null);
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
        // TODO no more basename Assert.assertEquals("Base 1", lifecycleTrackingInfo.getBaseName());
        Assert.assertEquals(Integer.toString(TEST_LEVEL_3_REAL), lifecycleTrackingInfo.getLevel());
        Assert.assertEquals(1000, lifecycleTrackingInfo.getStartupDuration());
        Assert.assertEquals(4, lifecycleTrackingInfo.getGameStartups().size());
        Assert.assertNull(lifecycleTrackingInfo.getLevelTaskName());
        Assert.assertEquals("Load JavaScript", lifecycleTrackingInfo.getGameStartups().get(0).getTask());
        Assert.assertEquals("Preload image sprite maps", lifecycleTrackingInfo.getGameStartups().get(1).getTask());
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
        Assert.assertEquals("Preload image sprite maps", lifecycleTrackingInfo.getGameStartups().get(2).getTask());
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
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.PRELOAD_IMAGE_SPRITE_MAPS, 3500, 150), uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTerminated(false, 2000, uuidTut, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.PRELOAD_IMAGE_SPRITE_MAPS, 1900, 60), uuidReal, null);
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
        // TODO no more basename Assert.assertEquals("Base 1", lifecycleTrackingInfo.getBaseName());
        Assert.assertEquals(Integer.toString(TEST_LEVEL_3_REAL), lifecycleTrackingInfo.getLevel());
        Assert.assertEquals(0, lifecycleTrackingInfo.getStartupDuration());
        Assert.assertEquals(4, lifecycleTrackingInfo.getGameStartups().size());
        Assert.assertNull(lifecycleTrackingInfo.getLevelTaskName());
        Assert.assertEquals("Load JavaScript", lifecycleTrackingInfo.getGameStartups().get(0).getTask());
        Assert.assertEquals("Preload image sprite maps", lifecycleTrackingInfo.getGameStartups().get(1).getTask());
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
        Assert.assertEquals("Preload image sprite maps", lifecycleTrackingInfo.getGameStartups().get(2).getTask());
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
        Collection<EventTrackingItem> eventTrackingItems = new ArrayList<>();
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 1, 1, 1000));
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 10, 2, 1100));
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 10, 3, 1200));
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 10, 4, 1300));
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 10, 5, 1400));
        // SyncItemInfo                                                                     1
        Collection<SyncItemInfo> itemInfos = new ArrayList<>();
        SyncItemInfo syncItemInfo = new SyncItemInfo();
        syncItemInfo.setStartUuid(uuid);
        syncItemInfo.setAmount(0.5);
        syncItemInfo.setId(new Id(1, 1));
        setPrivateField(SyncItemInfo.class, syncItemInfo, "clientTimeStamp", 1200L);
        itemInfos.add(syncItemInfo);
        // Selection
        Collection<SelectionTrackingItem> selectionTrackingItems = new ArrayList<>();
        SelectionTrackingItem selectionTrackingItem = new SelectionTrackingItem(uuid);
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem, "timeStamp", 1300);
        selectionTrackingItems.add(selectionTrackingItem);
        // Terrain scrolling
        Collection<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<>();
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 2, 1, 1100));
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 3, 1, 1150));
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 1, 1, 1050));
        // Browser window
        Collection<BrowserWindowTracking> browserWindowTrackings = new ArrayList<>();
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 2, 3, 4, 5, 6, 1100));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 2, 2, 3, 4, 5, 6, 1200));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 3, 2, 3, 4, 5, 6, 1300));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 4, 2, 3, 4, 5, 6, 1400));
        // Dialogs
        Collection<DialogTracking> dialogTrackings = new ArrayList<>();
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
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.PRELOAD_IMAGE_SPRITE_MAPS, 1900, 200), uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();


        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TASK, uuid, TEST_LEVEL_TASK_3_3_SIMULATED_ID, "task3", 1, 3000);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();

        // Tracking start
        userTrackingService.onEventTrackingStart(new EventTrackingStart(uuid, 201, 202, 203, 204, 205, 206, 1900));
        // Mouse
        Collection<EventTrackingItem> eventTrackingItems = new ArrayList<>();
        eventTrackingItems.add(new EventTrackingItem(uuid, 1, 1, 1, 1910));
        eventTrackingItems.add(new EventTrackingItem(uuid, 2, 10, 1, 1920));
        eventTrackingItems.add(new EventTrackingItem(uuid, 3, 10, 1, 1930));
        eventTrackingItems.add(new EventTrackingItem(uuid, 4, 10, 1, 1940));
        eventTrackingItems.add(new EventTrackingItem(uuid, 5, 10, 1, 1950));
        // SyncItemInfos                                                                     1
        Collection<SyncItemInfo> syncItemInfos = new ArrayList<>();
        SyncItemInfo syncItemInfo = new SyncItemInfo();
        syncItemInfo.setStartUuid(uuid);
        syncItemInfo.setFollowTarget(true);
        syncItemInfo.setId(new Id(2, 2));
        setPrivateField(SyncItemInfo.class, syncItemInfo, "clientTimeStamp", 2000L);
        syncItemInfos.add(syncItemInfo);
        // Selection
        Collection<SelectionTrackingItem> selectionTrackingItems = new ArrayList<>();
        SelectionTrackingItem selectionTrackingItem1 = new SelectionTrackingItem(uuid);
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem1, "timeStamp", 2050);
        selectionTrackingItems.add(selectionTrackingItem1);
        SelectionTrackingItem selectionTrackingItem2 = new SelectionTrackingItem(uuid);
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem2, "timeStamp", 2100);
        selectionTrackingItems.add(selectionTrackingItem2);
        // Terrain scrolling
        Collection<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<>();
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 1, 1, 2100));
        terrainScrollTrackings.add(new TerrainScrollTracking(uuid, 1, 2, 2200));
        // Browser window
        Collection<BrowserWindowTracking> browserWindowTrackings = new ArrayList<>();
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 1, 3, 4, 5, 6, 2100));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 2, 3, 4, 5, 6, 2120));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 3, 3, 4, 5, 6, 2130));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 4, 3, 4, 5, 6, 2140));
        browserWindowTrackings.add(new BrowserWindowTracking(uuid, 1, 5, 3, 4, 5, 6, 2150));
        // Dialogs
        Collection<DialogTracking> dialogTrackings = new ArrayList<>();
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
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.PRELOAD_IMAGE_SPRITE_MAPS, 3300, 400), uuid, null);
        getMovableService().sendStartupTask(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_UNITS, 3700, 500), uuid, null);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getOrCreateBase();
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
        SimpleBase simpleBase = getOrCreateBase();
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
        Assert.assertEquals(new Id(1, 1), playbackInfo.getSyncItemInfos().get(0).getId());
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
        Assert.assertEquals(ColdRealGameStartupTaskEnum.PRELOAD_IMAGE_SPRITE_MAPS.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(3).getTask());
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
        Assert.assertEquals(new Id(2, 2), playbackInfo.getSyncItemInfos().get(0).getId());
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
        Assert.assertEquals(ColdRealGameStartupTaskEnum.PRELOAD_IMAGE_SPRITE_MAPS.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(1).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_UNITS.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(2).getTask());
        // Real Game
        RealGameTrackingInfo realGameTrackingInfo = userTrackingService.getGameTracking(lifecycleTrackingInfos.get(2).createGameHistoryFrame(), createGameHistoryFilter(true, DbHistoryElement.Type.ITEM_CREATED));
        List<UserCommandHistoryElement> userCommandHistoryElements = realGameTrackingInfo.getUserCommandHistoryElements();
        System.out.println("----------History----------");
        for (UserCommandHistoryElement userCommandHistoryElement : userCommandHistoryElements) {
            System.out.println(userCommandHistoryElement.getTimeStamp() + "|" + userCommandHistoryElement.getInfo1() + "|" + userCommandHistoryElement.getInfo2());
        }
        Assert.assertEquals(3, userCommandHistoryElements.size());
        Assert.assertEquals("Item created: TestStartBuilderItem", userCommandHistoryElements.get(0).getInfo1());
        Assert.assertEquals("Item created: TestFactoryItem", userCommandHistoryElements.get(1).getInfo1());
        Assert.assertEquals("Item created: TestAttackItem", userCommandHistoryElements.get(2).getInfo1());
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
        RealGameTrackingInfo realGameTrackingInfo = userTrackingService.getGameTracking(lifecycleTrackingInfos.get(3).createGameHistoryFrame(), createGameHistoryFilter(true, DbHistoryElement.Type.ITEM_CREATED));
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
    public void testGetGameTrackingFilter() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        User u1 = userService.getUser("U1");
        createBase(new Index(1000, 1000));
        String session1 = getHttpSessionId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        User u2 = userService.getUser("U2");
        createBase(new Index(2000, 2000));
        String session2 = getHttpSessionId();
        DbGuild dbGuild = new DbGuild();
        dbGuild.setName("ThaGuild");
        setPrivateField(DbGuild.class, dbGuild, "id", 1);
        historyService.addGuildCreated(u2, 10, dbGuild);
        historyService.addGuildInvitation(u2, u1, dbGuild);
        historyService.addGuildJoined(u1, dbGuild);
        historyService.addGuildMemberKicked(u2, u1, dbGuild);
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setId(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        moveCommand.setTimeStamp();
        moveCommand.setPathToDestination(new Path(new Index(1000, 1000), new Index(3000, 3000), true));
        userTrackingService.saveUserCommand(moveCommand);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<UserCommandHistoryElement> userCommandHistoryElements = userTrackingService.getGameTracking(new GameHistoryFrame(session1, null, 0, 0), createGameHistoryFilter(false)).getUserCommandHistoryElements();
        Assert.assertTrue(userCommandHistoryElements.isEmpty());
        userCommandHistoryElements = userTrackingService.getGameTracking(new GameHistoryFrame(session2, null, 0, 0), createGameHistoryFilter(false, DbHistoryElement.Type.GUILD_CREATED)).getUserCommandHistoryElements();
        Assert.assertEquals(1, userCommandHistoryElements.size());
        Assert.assertEquals("U2 created ThaGuild guild", userCommandHistoryElements.get(0).getInfo1());
        userCommandHistoryElements = userTrackingService.getGameTracking(new GameHistoryFrame(session2, null, 0, 0), createGameHistoryFilter(false, DbHistoryElement.Type.GUILD_USER_INVITED, DbHistoryElement.Type.GUILD_MEMBER_KICKED)).getUserCommandHistoryElements();
        Assert.assertEquals(2, userCommandHistoryElements.size());
        userCommandHistoryElements = userTrackingService.getGameTracking(new GameHistoryFrame(session2, null, 0, 0), createGameHistoryFilter(true)).getUserCommandHistoryElements();
        Assert.assertEquals(1, userCommandHistoryElements.size());
        Assert.assertTrue(userCommandHistoryElements.get(0).getInfo1().contains("MoveCommand"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private GameHistoryFilter createGameHistoryFilter(boolean showCommands, DbHistoryElement.Type... types) {
        GameHistoryFilter gameHistoryFilter = new GameHistoryFilter();
        gameHistoryFilter.setShowCommands(showCommands);
        for (DbHistoryElement.Type type : types) {
            gameHistoryFilter.setType(type, true);
        }
        return gameHistoryFilter;
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

    @Test
    @DirtiesContext
    public void testUserTracking() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        String sessionId1 = getHttpSessionId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        String sessionId2 = getHttpSessionId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        String sessionId3 = getHttpSessionId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        String sessionId4 = getHttpSessionId();
        userService.logout();
        loginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        String sessionId5 = getHttpSessionId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDtos = userTrackingService.getSessionOverviewDtos(userService.getUser("U1"));
        Assert.assertEquals(2, sessionOverviewDtos.size());
        Assert.assertEquals(sessionId3, sessionOverviewDtos.get(0).getSessionId());
        Assert.assertEquals(sessionId1, sessionOverviewDtos.get(1).getSessionId());
        Assert.assertEquals(2, userTrackingService.getLoginCount(userService.getUser("U1")));
        sessionOverviewDtos = userTrackingService.getSessionOverviewDtos(userService.getUser("U2"));
        Assert.assertEquals(3, sessionOverviewDtos.size());
        Assert.assertEquals(sessionId5, sessionOverviewDtos.get(0).getSessionId());
        Assert.assertEquals(sessionId4, sessionOverviewDtos.get(1).getSessionId());
        Assert.assertEquals(sessionId2, sessionOverviewDtos.get(2).getSessionId());
        Assert.assertEquals(4, userTrackingService.getLoginCount(userService.getUser("U2")));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewUserTrackingFrom() throws Exception {
        configureSimplePlanetNoResources();
        createUserInSession("U1", new Date(100000000000L));
        createUserInSession("U2", new Date(200000000000L));
        createUserInSession("U3", new Date(300000000000L));
        createUserInSession("U4", new Date(400000000000L));
        createUserInSession("U5", new Date(500000000000L));
        createUserInSession("U6", new Date(600000000000L));
        createUserInSession("U7", new Date(700000000000L));
        createUserInSession("U8", new Date(800000000000L));
        createUserInSession("U9", new Date(900000000000L));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // test from 1
        NewUserTrackingFilter newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(400000000000L));
        List<User> users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(6, users.size());
        Assert.assertEquals("U9", users.get(0).getUsername());
        Assert.assertEquals("U8", users.get(1).getUsername());
        Assert.assertEquals("U7", users.get(2).getUsername());
        Assert.assertEquals("U6", users.get(3).getUsername());
        Assert.assertEquals("U5", users.get(4).getUsername());
        Assert.assertEquals("U4", users.get(5).getUsername());
        // test from 2
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(800000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("U9", users.get(0).getUsername());
        Assert.assertEquals("U8", users.get(1).getUsername());
        // test from too high
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(1000000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(0, users.size());
        // test from too low
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(10000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(9, users.size());
        Assert.assertEquals("U9", users.get(0).getUsername());
        Assert.assertEquals("U8", users.get(1).getUsername());
        Assert.assertEquals("U7", users.get(2).getUsername());
        Assert.assertEquals("U6", users.get(3).getUsername());
        Assert.assertEquals("U5", users.get(4).getUsername());
        Assert.assertEquals("U4", users.get(5).getUsername());
        Assert.assertEquals("U3", users.get(6).getUsername());
        Assert.assertEquals("U2", users.get(7).getUsername());
        Assert.assertEquals("U1", users.get(8).getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewUserTrackingTo() throws Exception {
        configureSimplePlanetNoResources();
        createUserInSession("U1", new Date(100000000000L));
        createUserInSession("U2", new Date(200000000000L));
        createUserInSession("U3", new Date(300000000000L));
        createUserInSession("U4", new Date(400000000000L));
        createUserInSession("U5", new Date(500000000000L));
        createUserInSession("U6", new Date(600000000000L));
        createUserInSession("U7", new Date(700000000000L));
        createUserInSession("U8", new Date(800000000000L));
        createUserInSession("U9", new Date(900000000000L));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // test from 1
        NewUserTrackingFilter newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setToDate(new Date(400000000000L));
        List<User> users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(4, users.size());
        Assert.assertEquals("U4", users.get(0).getUsername());
        Assert.assertEquals("U3", users.get(1).getUsername());
        Assert.assertEquals("U2", users.get(2).getUsername());
        Assert.assertEquals("U1", users.get(3).getUsername());
        // test from 2
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setToDate(new Date(600000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(6, users.size());
        Assert.assertEquals("U6", users.get(0).getUsername());
        Assert.assertEquals("U5", users.get(1).getUsername());
        Assert.assertEquals("U4", users.get(2).getUsername());
        Assert.assertEquals("U3", users.get(3).getUsername());
        Assert.assertEquals("U2", users.get(4).getUsername());
        Assert.assertEquals("U1", users.get(5).getUsername());
        // test from too high
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setToDate(new Date(1000000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(9, users.size());
        Assert.assertEquals("U9", users.get(0).getUsername());
        Assert.assertEquals("U8", users.get(1).getUsername());
        Assert.assertEquals("U7", users.get(2).getUsername());
        Assert.assertEquals("U6", users.get(3).getUsername());
        Assert.assertEquals("U5", users.get(4).getUsername());
        Assert.assertEquals("U4", users.get(5).getUsername());
        Assert.assertEquals("U3", users.get(6).getUsername());
        Assert.assertEquals("U2", users.get(7).getUsername());
        Assert.assertEquals("U1", users.get(8).getUsername());
        // test from too low
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setToDate(new Date(10000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(0, users.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewUserTrackingFromAndTo() throws Exception {
        configureSimplePlanetNoResources();
        createUserInSession("U1", new Date(100000000000L));
        createUserInSession("U2", new Date(200000000000L));
        createUserInSession("U3", new Date(300000000000L));
        createUserInSession("U4", new Date(400000000000L));
        createUserInSession("U5", new Date(500000000000L));
        createUserInSession("U6", new Date(600000000000L));
        createUserInSession("U7", new Date(700000000000L));
        createUserInSession("U8", new Date(800000000000L));
        createUserInSession("U9", new Date(900000000000L));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // test 1
        NewUserTrackingFilter newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(200000000000L));
        newUserTrackingFilter.setToDate(new Date(300000000000L));
        List<User> users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("U3", users.get(0).getUsername());
        Assert.assertEquals("U2", users.get(1).getUsername());
        // test 2
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(600000000000L));
        newUserTrackingFilter.setToDate(new Date(900000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(4, users.size());
        Assert.assertEquals("U9", users.get(0).getUsername());
        Assert.assertEquals("U8", users.get(1).getUsername());
        Assert.assertEquals("U7", users.get(2).getUsername());
        Assert.assertEquals("U6", users.get(3).getUsername());
        // test too high
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(1000000000000L));
        newUserTrackingFilter.setToDate(new Date(2000000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(0, users.size());
        // test too low
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(10000000000L));
        newUserTrackingFilter.setToDate(new Date(20000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(0, users.size());
        // test lower border
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(10000000000L));
        newUserTrackingFilter.setToDate(new Date(100000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("U1", users.get(0).getUsername());
        // test upper border
        newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(900000000000L));
        newUserTrackingFilter.setToDate(new Date(1000000000000L));
        users = userTrackingService.getNewUsers(newUserTrackingFilter);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("U9", users.get(0).getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testInGameTime() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getOrCreateBase();
        Thread.sleep(100);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        getOrCreateBase();
        Thread.sleep(200);
        getOrCreateBase();
        Thread.sleep(300);
        getOrCreateBase(); // This session will not count due to session time-out is to long
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        System.out.println(userTrackingService.calculateInGameTime(userService.getUser("U1")));
        Assert.assertTrue(userTrackingService.calculateInGameTime(userService.getUser("U1")) > 600);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testInGameTimeRegisterAfter() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase();
        Thread.sleep(100);
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        getOrCreateBase();
        Thread.sleep(200);
        getOrCreateBase();
        Thread.sleep(300);
        getOrCreateBase(); // This session will not count due to session time-out is to long
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        System.out.println(userTrackingService.calculateInGameTime(userService.getUser("U1")));
        Assert.assertTrue(userTrackingService.calculateInGameTime(userService.getUser("U1")) > 500);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getNewUserDailyDto() throws Exception {
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);
        configureMultiplePlanetsAndLevels();
        // Prepare users
        createFacebookAdUser(null, null, "25.05.2012 21:59:00", null);
        createFacebookAdUser(null, "AD01", "25.05.2012 21:59:00", null);
        createFacebookAdUser("U01", "AD01", "25.05.2012 21:59:00", TEST_LEVEL_2_REAL_ID);
        createFacebookAdUser("U901", "AD99", "25.05.2012 21:59:00", null);
        createFacebookAdUser("U02", "AD01", "25.05.2012 22:00:00", TEST_LEVEL_2_REAL_ID);
        createFacebookAdUser("U03", "AD01", "25.05.2012 23:00:00", TEST_LEVEL_5_REAL_ID);
        createFacebookAdUser("U04", "AD01", "26.05.2012 00:00:00", TEST_LEVEL_3_REAL_ID);
        createFacebookAdUser(null, null, "26.05.2012 00:00:00", null);
        createFacebookAdUser(null, "AD01", "26.05.2012 01:03:00", TEST_LEVEL_3_REAL_ID);
        createFacebookAdUser(null, "AD01", "26.05.2012 01:05:00", null);
        createFacebookAdUser(null, "AD01", "26.05.2012 01:05:00", TEST_LEVEL_2_REAL_ID);
        createFacebookAdUser("U05", "AD01", "26.05.2012 01:03:00", null);
        createFacebookAdUser("U06", "AD01", "26.05.2012 05:02:06", TEST_LEVEL_5_REAL_ID);
        createFacebookAdUser("U906", "AD99", "26.05.2012 05:02:06", null);
        createFacebookAdUser("U07", "AD01", "26.05.2012 08:12:06", TEST_LEVEL_2_REAL_ID);
        createFacebookAdUser("U08", "AD01", "26.05.2012 12:12:16", null);
        createFacebookAdUser("U09", "AD01", "26.05.2012 16:22:16", TEST_LEVEL_6_REAL_ID);
        createFacebookAdUser(null, null, "26.05.2012 16:22:16", null);
        createFacebookAdUser("U10", "AD01", "26.05.2012 20:22:16", TEST_LEVEL_2_REAL_ID);
        createFacebookAdUser("U11", "AD01", "26.05.2012 21:59:00", null);
        createFacebookAdUser("U12", "AD01", "26.05.2012 22:00:00", TEST_LEVEL_3_REAL_ID);
        createFacebookAdUser("U912", "AD99", "26.05.2012 22:00:00", null);
        createFacebookAdUser("U13", "AD01", "26.05.2012 23:00:00", TEST_LEVEL_2_REAL_ID);
        createFacebookAdUser(null, "AD01", "26.05.2012 23:00:00", TEST_LEVEL_5_REAL_ID);
        createFacebookAdUser(null, "AD01", "26.05.2012 23:00:01", null);
        createFacebookAdUser("U14", "AD01", "27.05.2012 00:00:00", TEST_LEVEL_2_REAL_ID);
        createFacebookAdUser("U15", "AD01", "27.05.2012 01:00:00", null);
        createFacebookAdUser(null, null, "27.05.2012 01:00:00", null);
        createFacebookAdUser("U16", "AD01", "27.05.2012 02:00:00", TEST_LEVEL_6_REAL_ID);
        createFacebookAdUser("U17", "AD01", "27.05.2012 03:00:00", TEST_LEVEL_3_REAL_ID);
        createFacebookAdUser("U917", "AD99", "27.05.2012 03:00:00", null);

        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Check no date no time zone no FacebookAdId
        NewUserDailyTrackingFilter newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFromDate(simpleDateTimeFormat.parse("25.05.2012 02:00:00"));
        newUserDailyTrackingFilter.setToDate(simpleDateTimeFormat.parse("27.05.2012 02:00:00"));
        List<NewUserDailyDto> dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(3, dtos.size());
        assertNewUserDailyTrackingFilter("25.05.2012", 6, 4, 4, 3, 1, 1, 1, 0, 0, dtos);
        assertNewUserDailyTrackingFilter("26.05.2012", 19, 12, 12, 7, 4, 2, 2, 1, 1, dtos);
        assertNewUserDailyTrackingFilter("27.05.2012", 6, 5, 5, 3, 2, 1, 1, 1, 2, dtos);
        // Check no date no time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(3, dtos.size());
        assertNewUserDailyTrackingFilter("25.05.2012", 4, 3, 3, 3, 1, 1, 1, 0, 0, dtos);
        assertNewUserDailyTrackingFilter("26.05.2012", 15, 10, 10, 7, 4, 2, 2, 1, 1, dtos);
        assertNewUserDailyTrackingFilter("27.05.2012", 4, 4, 4, 3, 2, 1, 1, 1, 2, dtos);
        // Check from date no time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        newUserDailyTrackingFilter.setFromDate(simpleDateTimeFormat.parse("27.05.2012 02:00:00"));
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(1, dtos.size());
        assertNewUserDailyTrackingFilter("27.05.2012", 4, 4, 4, 3, 2, 1, 1, 1, 0, dtos);
        // Check to date no time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        newUserDailyTrackingFilter.setToDate(simpleDateTimeFormat.parse("26.05.2012 23:00:00"));
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(2, dtos.size());
        assertNewUserDailyTrackingFilter("25.05.2012", 4, 3, 3, 3, 1, 1, 1, 0, 0, dtos);
        assertNewUserDailyTrackingFilter("26.05.2012", 15, 10, 10, 7, 4, 2, 2, 1, 1, dtos);
        // Check from & to no time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        newUserDailyTrackingFilter.setFromDate(simpleDateTimeFormat.parse("26.05.2012 23:10:00"));
        newUserDailyTrackingFilter.setToDate(simpleDateTimeFormat.parse("26.05.2012 23:20:00"));
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(1, dtos.size());
        assertNewUserDailyTrackingFilter("26.05.2012",  15, 10, 10, 7, 4, 2, 2, 1, 0, dtos);
        // Check no date time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        newUserDailyTrackingFilter.setTimeZone(TimeZone.getTimeZone("GMT"));
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(3, dtos.size());
        assertNewUserDailyTrackingFilter("25.05.2012", 9, 5, 5, 4, 2, 1, 1, 0, 0, dtos);
        assertNewUserDailyTrackingFilter("26.05.2012", 12, 10, 10, 7, 3, 2, 2, 1, 1, dtos);
        assertNewUserDailyTrackingFilter("27.05.2012", 2, 2, 2, 2, 2, 1, 1, 1, 2, dtos);
        // Check from date time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        newUserDailyTrackingFilter.setFromDate(simpleDateTimeFormat.parse("27.05.2012 02:00:00"));
        newUserDailyTrackingFilter.setTimeZone(TimeZone.getTimeZone("GMT"));
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(1, dtos.size());
        assertNewUserDailyTrackingFilter("27.05.2012", 2, 2, 2, 2, 2, 1, 1, 1, 0, dtos);
        // Check to date and time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        newUserDailyTrackingFilter.setToDate(simpleDateTimeFormat.parse("25.05.2012 02:00:00"));
        newUserDailyTrackingFilter.setTimeZone(TimeZone.getTimeZone("GMT"));
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(1, dtos.size());
        assertNewUserDailyTrackingFilter("25.05.2012", 9, 5, 5, 4, 2, 1, 1, 0, 0, dtos);
        // Check to date2 and time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        newUserDailyTrackingFilter.setToDate(simpleDateTimeFormat.parse("26.05.2012 02:00:00"));
        newUserDailyTrackingFilter.setTimeZone(TimeZone.getTimeZone("GMT"));
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(2, dtos.size());
        assertNewUserDailyTrackingFilter("25.05.2012", 9, 5, 5, 4, 2, 1, 1, 0, 0, dtos);
        assertNewUserDailyTrackingFilter("26.05.2012", 12, 10, 10, 7, 3, 2, 2, 1, 1, dtos);
        // Check from & to date and time zone
        newUserDailyTrackingFilter = new NewUserDailyTrackingFilter();
        newUserDailyTrackingFilter.setFacebookAdId("AD01");
        newUserDailyTrackingFilter.setFromDate(simpleDateTimeFormat.parse("26.05.2012 02:00:00"));
        newUserDailyTrackingFilter.setToDate(simpleDateTimeFormat.parse("26.05.2012 02:00:00"));
        newUserDailyTrackingFilter.setTimeZone(TimeZone.getTimeZone("GMT"));
        dtos = userTrackingService.getNewUserDailyDto(newUserDailyTrackingFilter);
        Assert.assertEquals(1, dtos.size());
        assertNewUserDailyTrackingFilter("26.05.2012", 12, 10, 10, 7, 3, 2, 2, 1, 0, dtos);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void createFacebookAdUser(String userName, String facebookAdId, String dateString, Integer levelId) throws Exception {
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT_STRING);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Create Session
        DbFacebookSource dbFacebookSourceSession = new DbFacebookSource();
        if (facebookAdId != null) {
            dbFacebookSourceSession.setOptionalAdValue(facebookAdId);
        }
        DbSessionDetail dbSessionDetail = new DbSessionDetail("", "", "", "", "", "", true, dbFacebookSourceSession, null);
        setPrivateField(DbSessionDetail.class, dbSessionDetail, "timeStamp", simpleDateTimeFormat.parse(dateString));
        saveOrUpdateInTransaction(dbSessionDetail);
        // Create User
        if (userName != null) {
            userService.createUser(userName, "xxx", "xxx", "");
            User user = userService.getUser(userName);
            if (facebookAdId != null) {
                DbFacebookSource dbFacebookSourceUser = new DbFacebookSource();
                dbFacebookSourceUser.setOptionalAdValue(facebookAdId);
                user.registerFacebookUser(new FacebookSignedRequest("", 0, null, null, null), userName, dbFacebookSourceUser, null);
            }
            setPrivateField(User.class, user, "registerDate", simpleDateTimeFormat.parse(dateString));
            saveOrUpdateInTransaction(user);
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        if (levelId != null && userName != null) {
            beginHttpSession();
            beginHttpRequestAndOpenSessionInViewFilter();
            userService.login(userName, "xxx");
            userGuidanceService.promote(getUserState(),levelId);
            endHttpRequestAndOpenSessionInViewFilter();
            endHttpSession();
        }
    }

    private void assertNewUserDailyTrackingFilter(String dateString, int sessions, int registered, int level1, int level2, int level3, int level4, int level5, int level6, int index, List<NewUserDailyDto> dtos) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATE_FORMAT_STRING);
        NewUserDailyDto newUserDailyDto = dtos.get(index);
        Assert.assertEquals(simpleDateFormat.parse(dateString), newUserDailyDto.getDate());
        Assert.assertEquals(sessions, newUserDailyDto.getSessions());
        Assert.assertEquals(registered, newUserDailyDto.getRegistered());
        Assert.assertEquals(level1, newUserDailyDto.getLevel1());
        Assert.assertEquals(level2, newUserDailyDto.getLevel2());
        Assert.assertEquals(level3, newUserDailyDto.getLevel3());
        Assert.assertEquals(level4, newUserDailyDto.getLevel4());
        Assert.assertEquals(level5, newUserDailyDto.getLevel5());
        Assert.assertEquals(level6, newUserDailyDto.getLevel6());
    }
}
