package com.btxtech.game.mockservlet;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.NotAGuildMemberException;
import com.btxtech.game.jsre.client.PositionInBotException;
import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.cockpit.item.InvitingUnregisteredBaseException;
import com.btxtech.game.jsre.client.common.AbstractGwtTest;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RazarionCostInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildDetailedInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildsResult;
import com.btxtech.game.jsre.client.dialogs.highscore.CurrentStatisticEntryInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryElementInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryFilter;
import com.btxtech.game.jsre.client.dialogs.incentive.FriendInvitationBonus;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.news.NewsEntryInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapInfo;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemContainerType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.services.AbstractServiceTest;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 04.11.2011
 * Time: 21:38:41
 */
public class MockMovableService extends RemoteServiceServlet implements MovableService {
    protected static final BaseItemType MOVABLE_ITEM_TYPE;
    protected static final BaseItemType MOVABLE_CONTAINER_ITEM_TYPE;
    protected static final Collection<ItemType> ITEM_TYPES = new ArrayList<ItemType>();

    static {
        // Simple movable
        MOVABLE_ITEM_TYPE = new BaseItemType();
        MOVABLE_ITEM_TYPE.setName("MOVABLE_ITEM_TYPE");
        MOVABLE_ITEM_TYPE.setId(AbstractGwtTest.ITEM_MOVABLE);
        MOVABLE_ITEM_TYPE.setHealth(10);
        MOVABLE_ITEM_TYPE.setTerrainType(TerrainType.LAND);
        MOVABLE_ITEM_TYPE.setBoundingBox(new BoundingBox(50, AbstractServiceTest.ANGELS_24));
        MOVABLE_ITEM_TYPE.setMovableType(new MovableType(100));
        MOVABLE_ITEM_TYPE.setTerrainType(TerrainType.LAND);
        ITEM_TYPES.add(MOVABLE_ITEM_TYPE);
        // Item Container
        MOVABLE_CONTAINER_ITEM_TYPE = new BaseItemType();
        MOVABLE_CONTAINER_ITEM_TYPE.setName("MOVABLE_CONTAINER_ITEM_TYPE");
        MOVABLE_CONTAINER_ITEM_TYPE.setId(AbstractGwtTest.ITEM_CONTAINER);
        MOVABLE_CONTAINER_ITEM_TYPE.setHealth(10);
        MOVABLE_CONTAINER_ITEM_TYPE.setTerrainType(TerrainType.LAND);
        MOVABLE_CONTAINER_ITEM_TYPE.setBoundingBox(new BoundingBox(80, AbstractServiceTest.ANGELS_24));
        MOVABLE_CONTAINER_ITEM_TYPE.setMovableType(new MovableType(100));
        MOVABLE_CONTAINER_ITEM_TYPE.setTerrainType(TerrainType.WATER_WATER_COAST_LAND_COAST);
        Collection<Integer> ableToContain = new ArrayList<Integer>();
        ableToContain.add(MOVABLE_ITEM_TYPE.getId());
        MOVABLE_CONTAINER_ITEM_TYPE.setItemContainerType(new ItemContainerType(ableToContain, 5, 150));
        ITEM_TYPES.add(MOVABLE_CONTAINER_ITEM_TYPE);
    }

    @Override
    public RealGameInfo getRealGameInfo(String startUuid, Integer planetId) throws InvalidLevelStateException {
        return null;
    }

    @Override
    public SimulationInfo getSimulationGameInfo(int levelTaskId) {
        SimulationInfo simulationInfo = new SimulationInfo();
        simulationInfo.setRegisterDialogDelay(1000);
        simulationInfo.setLevelTaskId(1);
        Map<CmsUtil.CmsPredefinedPage, String> predefinedUrls = new HashMap<CmsUtil.CmsPredefinedPage, String>();
        predefinedUrls.put(CmsUtil.CmsPredefinedPage.LEVEL_TASK_DONE, "TestLevelTaskDone");
        simulationInfo.setPredefinedUrls(predefinedUrls);
        setupTerrain(simulationInfo);
        simulationInfo.setItemTypes(ITEM_TYPES);
        setupTutorialConfig(simulationInfo);
        return simulationInfo;
    }

    protected void setupTutorialConfig(SimulationInfo simulationInfo) {
        Assert.fail("...TODO...");
        // Condition Simulation with two tasks
       /* Map<ItemType, Integer> conditionItemType = new HashMap<ItemType, Integer>();
        conditionItemType.put(MOVABLE_ITEM_TYPE, 1);
        ConditionConfig conditionConfig1 = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(conditionItemType, creanew Rectangle(500, 500, 400, 400), null, true, null), null);
        ConditionConfig conditionConfig2 = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(conditionItemType, new Rectangle(500, 500, 400, 400), null, true, null), null);

        // Setup SimulationInfo
        List<ItemTypeAndPosition> ownItems = new ArrayList<ItemTypeAndPosition>();
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(100, 100), 0));
        Map<Integer, Integer> itemTypeLimitation = new HashMap<Integer, Integer>();
        List<AbstractTaskConfig> taskConfigs = new ArrayList<AbstractTaskConfig>();
        taskConfigs.add(new AbstractTaskConfig(ownItems, null, conditionConfig1, 10, 100, 1000, "TestTask1", null, itemTypeLimitation, RadarMode.MAP, null));
        taskConfigs.add(new AbstractTaskConfig(ownItems, null, conditionConfig2, 10, 100, 1000, "TestTask2", null, itemTypeLimitation, RadarMode.MAP, null));
        TutorialConfig tutorialConfig = new TutorialConfig(taskConfigs, "MyTestBase", 500, 500, false, "", false);
        simulationInfo.setTutorialConfig(tutorialConfig);
        simulationInfo.setLevelNumber(1); */
    }

    protected void setupTerrain(GameInfo gameInfo) {
        gameInfo.setTerrainSettings(new TerrainSettings(50, 50));
        gameInfo.setTerrainImagePositions(new ArrayList<TerrainImagePosition>());
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 50, 50), 0));
        gameInfo.setSurfaceRects(surfaceRects);
        Collection<SurfaceImage> surfaceImages = new ArrayList<SurfaceImage>();
        surfaceImages.add(new SurfaceImage(SurfaceType.LAND, 0, null, ""));
        gameInfo.setSurfaceImages(surfaceImages);
        gameInfo.setTerrainImages(new ArrayList<TerrainImage>());
    }

    @Override
    public void sendDebug(Date date, String category, String message) {
        System.out.println("MockMovableService.log: " + date + " message: " + message);
    }

    @Override
    public void sendCommands(String startUuid, List<BaseCommand> baseCommands) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Packet> getSyncInfo(String startUuid, boolean resendLast) throws NoConnectionException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<SyncItemInfo> getAllSyncInfo(String startUuid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SimpleUser register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    @Override
    public SimpleUser createAndLoginFacebookUser(String signedRequestParameter, String nickname, String email) throws UserAlreadyExistsException, PasswordNotMatchException {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    @Override
    public void loginFacebookUser(String signedRequestParameter) throws UserAlreadyExistsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SimpleUser login(String name, String password) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void logout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isFacebookUserRegistered(String signedRequestParameter) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public VerificationRequestCallback.ErrorResult isNickNameValid(String nickname) throws UserAlreadyExistsException, PasswordNotMatchException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage, ChatMessageFilter chatMessageFilter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<MessageIdPacket> setChatMessageFilter(ChatMessageFilter chatMessageFilter) throws NotAGuildMemberException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<MessageIdPacket> pollMessageIdPackets(Integer lastMessageId, ChatMessageFilter chatMessageFilter, GameEngineMode gameEngineMode) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendStartupTask(StartupTaskInfo startupTaskInfo, String uuid, Integer levelTaskId) {
        System.out.println("MockMovableService.sendStartupTask: startupTaskInfo:" + startupTaskInfo + " uuid: " + uuid + " levelTaskId: " + levelTaskId);
    }

    @Override
    public void sendStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId) {
        System.out.println("---sendStartupTerminated Success:" + successful);
    }

    @Override
    public GameFlow sendTutorialProgress(TutorialConfig.TYPE type, String startUuid, int levelTaskId, String name, long duration, long clientTimeStamp) {
        System.out.println("---sendTutorialProgress " + type);
//        if (type == TutorialConfig.TYPE.TUTORIAL) {
//            return new GameFlow(GameFlow.Type.SHOW_LEVEL_TASK_DONE_PAGE, null);
//        } else {
        return null;
//        }
    }

    @Override
    public void sendEventTrackingStart(EventTrackingStart eventTrackingStart) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems, Collection<SyncItemInfo> syncItemInfos, Collection<SelectionTrackingItem> selectionTrackingItems, Collection<TerrainScrollTracking> terrainScrollTrackings, Collection<BrowserWindowTracking> browserWindowTrackings, Collection<DialogTracking> dialogTrackings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sellItem(String startUuid, Id id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryInfo getInventory(Integer filterPlanetId, boolean filterLevel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryInfo assembleInventoryItem(int inventoryItemId, Integer filterPlanetId, boolean filterLevel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryInfo buyInventoryItem(int inventoryItemId, Integer filterPlanetId, boolean filterLevel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryInfo buyInventoryArtifact(int inventoryArtifactId, Integer filterPlanetId, boolean filterLevel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QuestOverview loadQuestOverview() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void activateQuest(int questId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<CurrentStatisticEntryInfo> loadCurrentStatisticEntryInfos() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendPerfmonData(Map<PerfmonEnum, Integer> workTimes, int totalTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getRazarion() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UnlockContainer unlockItemType(int itemTypeId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UnlockContainer unlockQuest(int questId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UnlockContainer unlockPlanet(int planetId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void surrenderBase() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RealGameInfo createBase(String startUuid, Index position) throws PositionInBotException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HistoryElementInfo getHistoryElements(HistoryFilter historyFilter) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NewsEntryInfo getNewsEntry(int index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FullGuildInfo saveGuildText(String text) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FullGuildInfo kickGuildMember(int userId) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FullGuildInfo changeGuildMemberRank(int userId, GuildMemberInfo.Rank rank) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SuggestOracle.Response getSuggestedUserName(String query, int limit) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FullGuildInfo inviteUserToGuild(String userName) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void inviteUserToGuild(SimpleBase simpleBase) throws InvitingUnregisteredBaseException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FullGuildInfo dismissGuildMemberRequest(int userId) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FullGuildInfo getFullGuildInfo(int guildId) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RazarionCostInfo getCreateGuildRazarionCost() {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SimpleGuild createGuild(String guildName) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public VerificationRequestCallback.ErrorResult isGuildNameValid(String guildName) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void guildMembershipRequest(int guildId, String text) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SearchGuildsResult searchGuilds(int start, int length, String guildNameQuery) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SimpleGuild joinGuild(int guildId) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<GuildDetailedInfo> dismissGuildInvitation(int guildId) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<GuildDetailedInfo> getGuildInvitations() {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void leaveGuild() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeGuild() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMailInvite(String emailAddress) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFacebookInvite(String fbRequestId, Collection<String> fbUserIds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<FriendInvitationBonus> getFriendInvitationBonuses() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StarMapInfo getStarMapInfo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
