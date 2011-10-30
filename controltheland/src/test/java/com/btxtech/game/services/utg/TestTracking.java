package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.control.ColdRealGameStartupTaskEnum;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.jsre.playback.PlaybackInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.playback.PlaybackService;
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
    private MovableService movableService;
    @Autowired
    private PlaybackService playbackService;
    @Autowired
    private Session session;

    @Test
    @DirtiesContext
    public void testSimple() throws Exception {
        configureMinimalGame();

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
    public void testSimplePageHits() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SessionOverviewDto> sessionOverviewDto = userTrackingService.getSessionOverviewDtos(UserTrackingFilter.newDefaultFilter());
        Assert.assertEquals(1, sessionOverviewDto.size());
        Assert.assertEquals(1, sessionOverviewDto.get(0).getPageHits());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCookie() throws Exception {
        configureMinimalGame();

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
        configureMinimalGame();

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
    public void tesSessionFilter() throws Exception {
        configureMinimalGame();

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
    public void testTutorialTracking() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        tutorial1();
        tutorial2();
        realGame1();
        endHttpSession();

        // Verify
        verifyTutorial1();
        verifyTutorial2();
        verifyRealGame1();
    }

    private void tutorial1() throws Exception {
        // 0 until 1550 (client time )
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        List<StartupTaskInfo> infos = new ArrayList<StartupTaskInfo>();
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 1000, 100));
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GAME, 1100, 150));
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GUI, 1250, 50));
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 1300, 200));
        movableService.sendStartupInfo(infos, 500);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();


        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onTutorialProgressChanged(TutorialConfig.TYPE.STEP, "step1", "task1", 1, 1100);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onTutorialProgressChanged(TutorialConfig.TYPE.TASK, "task1", "Tutorial1", 1, 1500);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();

        // Tracking start
        userTrackingService.onEventTrackingStart(new EventTrackingStart(101, 102, 103, 104, 105, 106, 1200));
        // Mouse
        Collection<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
        eventTrackingItems.add(new EventTrackingItem(1, 1, 1, 1000));
        eventTrackingItems.add(new EventTrackingItem(1, 10, 2, 1100));
        eventTrackingItems.add(new EventTrackingItem(1, 10, 3, 1200));
        eventTrackingItems.add(new EventTrackingItem(1, 10, 4, 1300));
        eventTrackingItems.add(new EventTrackingItem(1, 10, 5, 1400));
        // SyncItemInfo                                                                     1
        Collection<SyncItemInfo> itemInfos = new ArrayList<SyncItemInfo>();
        SyncItemInfo syncItemInfo = new SyncItemInfo();
        syncItemInfo.setAmount(0.5);
        syncItemInfo.setId(new Id(1, 1, 1));
        setPrivateField(SyncItemInfo.class, syncItemInfo, "clientTimeStamp", 1200L);
        itemInfos.add(syncItemInfo);
        // Selection
        Collection<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
        SelectionTrackingItem selectionTrackingItem = new SelectionTrackingItem();
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem, "timeStamp", 1300);
        selectionTrackingItems.add(selectionTrackingItem);
        // Terrain scrolling
        Collection<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
        terrainScrollTrackings.add(new TerrainScrollTracking(2, 1, 2, 3, 1100));
        terrainScrollTrackings.add(new TerrainScrollTracking(3, 1, 2, 3, 1150));
        terrainScrollTrackings.add(new TerrainScrollTracking(1, 1, 2, 3, 1050));
        // Browser window
        Collection<BrowserWindowTracking> browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
        browserWindowTrackings.add(new BrowserWindowTracking(1, 2, 3, 4, 5, 6, 1100));
        browserWindowTrackings.add(new BrowserWindowTracking(2, 2, 3, 4, 5, 6, 1200));
        browserWindowTrackings.add(new BrowserWindowTracking(3, 2, 3, 4, 5, 6, 1300));
        browserWindowTrackings.add(new BrowserWindowTracking(4, 2, 3, 4, 5, 6, 1400));

        movableService.sendEventTrackerItems(eventTrackingItems, itemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings);
        userTrackingService.onTutorialProgressChanged(TutorialConfig.TYPE.TASK, "tutorial1", null, 1, 1550);
        endHttpRequestAndOpenSessionInViewFilter();
    }

    private void tutorial2() throws Exception {
        // 1600 until 3100 (client time )
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        List<StartupTaskInfo> infos = new ArrayList<StartupTaskInfo>();
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 1600, 100));
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GAME, 1700, 150));
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.INIT_GUI, 1850, 50));
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 1900, 200));
        movableService.sendStartupInfo(infos, 500);
        userTrackingService.onJavaScriptDetected(true);
        endHttpRequestAndOpenSessionInViewFilter();


        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onTutorialProgressChanged(TutorialConfig.TYPE.STEP, "step2", "task2", 1, 2500);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.onTutorialProgressChanged(TutorialConfig.TYPE.TASK, "task2", "Tutorial2", 1, 3000);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();

        // Tracking start
        userTrackingService.onEventTrackingStart(new EventTrackingStart(201, 202, 203, 204, 205, 206, 1900));
        // Mouse
        Collection<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
        eventTrackingItems.add(new EventTrackingItem(1, 1, 1, 1910));
        eventTrackingItems.add(new EventTrackingItem(2, 10, 1, 1920));
        eventTrackingItems.add(new EventTrackingItem(3, 10, 1, 1930));
        eventTrackingItems.add(new EventTrackingItem(4, 10, 1, 1940));
        eventTrackingItems.add(new EventTrackingItem(5, 10, 1, 1950));
        // SyncItemInfos                                                                     1
        Collection<SyncItemInfo> syncItemInfos = new ArrayList<SyncItemInfo>();
        SyncItemInfo syncItemInfo = new SyncItemInfo();
        syncItemInfo.setFollowTarget(true);
        syncItemInfo.setId(new Id(2, 2, 2));
        setPrivateField(SyncItemInfo.class, syncItemInfo, "clientTimeStamp", 2000L);
        syncItemInfos.add(syncItemInfo);
        // Selection
        Collection<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
        SelectionTrackingItem selectionTrackingItem1 = new SelectionTrackingItem();
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem1, "timeStamp", 2050);
        selectionTrackingItems.add(selectionTrackingItem1);
        SelectionTrackingItem selectionTrackingItem2 = new SelectionTrackingItem();
        setPrivateField(SelectionTrackingItem.class, selectionTrackingItem2, "timeStamp", 2100);
        selectionTrackingItems.add(selectionTrackingItem2);
        // Terrain scrolling
        Collection<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
        terrainScrollTrackings.add(new TerrainScrollTracking(1, 1, 2, 3, 2100));
        terrainScrollTrackings.add(new TerrainScrollTracking(1, 2, 2, 3, 2200));
        // Browser window
        Collection<BrowserWindowTracking> browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
        browserWindowTrackings.add(new BrowserWindowTracking(1, 1, 3, 4, 5, 6, 2100));
        browserWindowTrackings.add(new BrowserWindowTracking(1, 2, 3, 4, 5, 6, 2120));
        browserWindowTrackings.add(new BrowserWindowTracking(1, 3, 3, 4, 5, 6, 2130));
        browserWindowTrackings.add(new BrowserWindowTracking(1, 4, 3, 4, 5, 6, 2140));
        browserWindowTrackings.add(new BrowserWindowTracking(1, 5, 3, 4, 5, 6, 2150));

        movableService.sendEventTrackerItems(eventTrackingItems, syncItemInfos, selectionTrackingItems, terrainScrollTrackings, browserWindowTrackings);
        userTrackingService.onTutorialProgressChanged(TutorialConfig.TYPE.TUTORIAL, "tutorial2", null, 1, 3100);
        endHttpRequestAndOpenSessionInViewFilter();
    }

    private void realGame1() throws Exception {
        // 1600 until 3100 (client time )
        beginHttpRequestAndOpenSessionInViewFilter();
        userTrackingService.pageAccess("Page 1", null);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        List<StartupTaskInfo> infos = new ArrayList<StartupTaskInfo>();
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT, 3200, 100));
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_MAP, 3300, 400));
        infos.add(new StartupTaskInfo(ColdRealGameStartupTaskEnum.LOAD_UNITS, 3700, 500));
        movableService.sendStartupInfo(infos, 500);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getMyBase();
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
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
        Assert.assertEquals(3, lifecycleTrackingInfos.size());
        // Start ups
        List<DbStartupTask> dbStartupTasks = lifecycleTrackingInfos.get(0).getGameStartups();
        Assert.assertEquals(4, dbStartupTasks.size());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_JAVA_SCRIPT.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(0).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.INIT_GAME.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(1).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.INIT_GUI.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(2).getTask());
        Assert.assertEquals(ColdRealGameStartupTaskEnum.LOAD_MAP.getStartupTaskEnumHtmlHelper().getNiceText(), dbStartupTasks.get(3).getTask());
        // Tutorial progress
        TutorialTrackingInfo tutorialTrackingInfo = userTrackingService.getTutorialTrackingInfo(lifecycleTrackingInfos.get(0));
        Assert.assertEquals(3, tutorialTrackingInfo.getDbTutorialProgresss().size());
        Assert.assertEquals("step1", tutorialTrackingInfo.getDbTutorialProgresss().get(0).getName());
        Assert.assertEquals("task1", tutorialTrackingInfo.getDbTutorialProgresss().get(1).getName());
        Assert.assertEquals("tutorial1", tutorialTrackingInfo.getDbTutorialProgresss().get(2).getName());
        Assert.assertEquals(101, tutorialTrackingInfo.getDbEventTrackingStart().getClientWidth());
        Assert.assertEquals(102, tutorialTrackingInfo.getDbEventTrackingStart().getClientHeight());
        // Playback
        PlaybackInfo playbackInfo = playbackService.getPlaybackInfo(sessionOverviewDto.getSessionId(), lifecycleTrackingInfos.get(0).getStartServer(), lifecycleTrackingInfos.get(0).getLevel());
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
        Assert.assertEquals(3, lifecycleTrackingInfos.size());
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
        Assert.assertEquals("step2", tutorialTrackingInfo.getDbTutorialProgresss().get(0).getName());
        Assert.assertEquals("task2", tutorialTrackingInfo.getDbTutorialProgresss().get(1).getName());
        Assert.assertEquals("tutorial2", tutorialTrackingInfo.getDbTutorialProgresss().get(2).getName());
        Assert.assertEquals(201, tutorialTrackingInfo.getDbEventTrackingStart().getClientWidth());
        Assert.assertEquals(202, tutorialTrackingInfo.getDbEventTrackingStart().getClientHeight());
        // Playback
        PlaybackInfo playbackInfo = playbackService.getPlaybackInfo(sessionOverviewDto.getSessionId(), lifecycleTrackingInfos.get(1).getStartServer(), lifecycleTrackingInfos.get(1).getLevel());
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
        Assert.assertEquals(3, lifecycleTrackingInfos.size());
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
        System.out.println("----------History----------");
        int offset = 0;
        if (userCommandHistoryElements.size() == 2) {
            offset = 0;
        } else if (userCommandHistoryElements.size() == 3) {
            offset = 1;
        } else {
            Assert.fail("2 or 3 expected. Received " + userCommandHistoryElements.size() + ". Depend if the create base is captured or not 'Item created: TestStartBuilderItem'");
        }

        Assert.assertEquals(2, userCommandHistoryElements.size());
        //Assert.assertEquals(BuilderCommand.class.getName(), userCommandHistoryElements.get(offset).getInfo1());
        Assert.assertEquals("Item created: TestFactoryItem", userCommandHistoryElements.get(0 + offset).getInfo1());
        //Assert.assertEquals(FactoryCommand.class.getName(), userCommandHistoryElements.get(2 + offset).getInfo1());
        Assert.assertEquals("Item created: TestAttackItem", userCommandHistoryElements.get(1 + offset).getInfo1());
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
