package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.CrystalCostInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildsResult;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.impl.GuildServiceImpl;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 24.04.12
 * Time: 21:50
 */
public class TestGuildService extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private PlanetSystemService planetSystemService;

    public static DbGuildMember getMember(DbGuild dbGuild, String userName) {
        for (DbGuildMember dbGuildMember : dbGuild.getGuildMembers()) {
            if (dbGuildMember.getUser().getUsername().equals(userName)) {
                return dbGuildMember;
            }
        }
        throw new IllegalArgumentException("No such guild member: " + userName);
    }

    @Test
    @DirtiesContext
    public void getCreateGuildCrystalCost() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 111);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setCrystals(222);
        CrystalCostInfo crystalCostInfo = guildService.getCreateGuildCrystalCost();
        Assert.assertEquals(111, crystalCostInfo.getCost());
        Assert.assertEquals(222, crystalCostInfo.getCrystalAmount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void guildMembershipRequestNotRegistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("AAAAAA").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.guildMembershipRequest(guildId, "");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildMembershipRequest.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void guildMembershipRequestHasGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("AAAAAA").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        try {
            guildService.guildMembershipRequest(guildId, "");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User has already a guild: User: 'U1' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildMembershipRequest.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void guildMembershipRequestWrongGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        try {
            guildService.guildMembershipRequest(11111, "");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Guild does not exist: 11111", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildMembershipRequest.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void guildMembershipRequest() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("AAAAAA").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildMembershipRequest(createUserMatcher("U2"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        guildService.guildMembershipRequest(guildId, "asdfgasdgsad asgdfadsgds");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbGuildMembershipRequest> dbGuildMembershipRequests = loadAll(DbGuildMembershipRequest.class);
        Assert.assertEquals(1, dbGuildMembershipRequests.size());
        DbGuildMembershipRequest dbGuildMembershipRequest = dbGuildMembershipRequests.get(0);
        Assert.assertEquals("U2", dbGuildMembershipRequest.getUser().getUsername());
        Assert.assertEquals(guildId, (int) dbGuildMembershipRequest.getDbGuild().getId());
        Assert.assertEquals("asdfgasdgsad asgdfadsgds", dbGuildMembershipRequest.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void guildMembershipRequestDoubleEntries() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("AAAAAA").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        guildService.guildMembershipRequest(guildId, "asdfgasdgsad asgdfadsgds");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbGuildMembershipRequest> dbGuildMembershipRequests = loadAll(DbGuildMembershipRequest.class);
        Assert.assertEquals(1, dbGuildMembershipRequests.size());
        DbGuildMembershipRequest dbGuildMembershipRequest = dbGuildMembershipRequests.get(0);
        Assert.assertEquals("U2", dbGuildMembershipRequest.getUser().getUsername());
        Assert.assertEquals(guildId, (int) dbGuildMembershipRequest.getDbGuild().getId());
        Assert.assertEquals("asdfgasdgsad asgdfadsgds", dbGuildMembershipRequest.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        guildService.guildMembershipRequest(guildId, "aaa sss");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbGuildMembershipRequests = loadAll(DbGuildMembershipRequest.class);
        Assert.assertEquals(1, dbGuildMembershipRequests.size());
        dbGuildMembershipRequest = dbGuildMembershipRequests.get(0);
        Assert.assertEquals("U2", dbGuildMembershipRequest.getUser().getUsername());
        Assert.assertEquals(guildId, (int) dbGuildMembershipRequest.getDbGuild().getId());
        Assert.assertEquals("asdfgasdgsad asgdfadsgds", dbGuildMembershipRequest.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void guildMembershipRequestSendPacket() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        // Generate users
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Generate Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("Manager");
        int guildId = guildService.createGuild("AAAAAA").getId();
        guildService.inviteUserToGuild("U1");
        guildService.inviteUserToGuild("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Add user to guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        PlanetSystemService planetSystemServiceMock = EasyMock.createStrictMock(PlanetSystemService.class);
        planetSystemServiceMock.sendPacket(createUserStateMatcher("Manager"), createUserAttentionPacketMatcher(null, UserAttentionPacket.Type.RAISE, null));
        planetSystemServiceMock.sendPacket(createUserStateMatcher("U1"), createUserAttentionPacketMatcher(null, UserAttentionPacket.Type.RAISE, null));
        planetSystemServiceMock.sendPacket(createUserStateMatcher("U2"), createUserAttentionPacketMatcher(null, UserAttentionPacket.Type.RAISE, null));
        EasyMock.replay(planetSystemServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemServiceMock);
        // Generate request
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Actor");
        guildService.guildMembershipRequest(guildId, "asdfgasdgsad asgdfadsgds");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Make double request -> no packet send expected
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("Actor");
        guildService.guildMembershipRequest(guildId, "aaa sss");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void dismissGuildMemberRequestNotRegistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.dismissGuildMemberRequest(111);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void dismissGuildMemberRequestNoGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        try {
            guildService.dismissGuildMemberRequest(111);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User has no guild: User: 'U1' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void dismissGuildMemberRequestDismissUserNotRegistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        guildService.createGuild("xxxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        try {
            guildService.dismissGuildMemberRequest(111);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Dismiss user id does not exist or is not registered: 111", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void dismissGuildMemberRequestNoRequest() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        guildService.createGuild("xxxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        int userId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        try {
            guildService.dismissGuildMemberRequest(userId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("No membership request for user: User: 'U2' id: 2 to guild: DbGuild{name='xxxx', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void dismissGuildMemberRequest() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        guildService.guildMembershipRequest(guildId, "");
        int userId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addDismissGuildMemberRequest(createUserMatcher("U1"), createUserMatcher("U2"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        assertDbEntryCount(1, DbGuildMembershipRequest.class);
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        FullGuildInfo fullGuildInfo = guildService.dismissGuildMemberRequest(userId);
        Assert.assertNotNull(fullGuildInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildMembershipRequest.class);

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void kickGuildMemberErrorCases() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        PlanetSystemService planetSystemServiceMock = EasyMock.createStrictMock(PlanetSystemService.class);
        EasyMock.replay(planetSystemServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemServiceMock);

        // Unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.kickGuildMember(111);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // No Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("master");
        try {
            guildService.kickGuildMember(111);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User has no guild: User: 'master' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // No user to kick
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("master");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemService);
        guildService.createGuild("XXXX");
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemServiceMock);
        try {
            guildService.kickGuildMember(111);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("User to kick id does not exist or is not registered: 111", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // -- Create user to kick
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("kick1");
        int kickUserId1 = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Kick User no guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("master");
        try {
            guildService.kickGuildMember(kickUserId1);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User to kick has no guild: User: 'kick1' id: 2", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // -- Create user to kick different guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("kick2");
        int kickUserId2 = userService.getUser().getId();
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemService);
        guildService.createGuild("BBBBBB");
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemServiceMock);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Kick User different Guils
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("master");
        try {
            guildService.kickGuildMember(kickUserId2);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User to kick and user are nor in the same guild. User to kick: User: 'kick2' id: 3 Guild: DbGuild{name='BBBBBB', id=2} User: User: 'master' id: 1 Guild: DbGuild{name='XXXX', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void kickGuildMemberWrongRank() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        // Create mgmt user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("manager");
        int managerId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create member user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int memberId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create presi user, guild and send invitations
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        int presiId = userService.getUser().getId();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("XXXX").getId();
        guildService.inviteUserToGuild("manager");
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change mgmt user rank
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.changeGuildMemberRank(managerId, GuildMemberInfo.Rank.MANAGEMENT);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        // Fail presis kicks presis
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        try {
            guildService.kickGuildMember(presiId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User User: 'presi' id: 3 has not enough permission to kick: User: 'presi' id: 3 from Guild: DbGuild{name='XXXX', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Fail manager kicks manager
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        try {
            guildService.kickGuildMember(managerId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User User: 'manager' id: 1 has not enough permission to kick: User: 'manager' id: 1 from Guild: DbGuild{name='XXXX', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Fail member kicks member
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        try {
            guildService.kickGuildMember(memberId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User User: 'member' id: 2 has not enough permission to kick: User: 'member' id: 2 from Guild: DbGuild{name='XXXX', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals(3, dbGuild.getGuildMembers().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void kickGuildMemberPresiMember() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildJoined(EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildMemberKicked(createUserMatcher("presi"), createUserMatcher("member"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int memberId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        int presiId = userService.getUser().getId();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("XXXX").getId();
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Fail member kick presi
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        try {
            guildService.kickGuildMember(presiId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User User: 'member' id: 1 has not enough permission to kick: User: 'presi' id: 2 from Guild: DbGuild{name='XXXX', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Success presi kicks user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        FullGuildInfo fullGuildInfo = guildService.kickGuildMember(memberId);
        Assert.assertNotNull(fullGuildInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals(1, dbGuild.getGuildMembers().size());
        DbGuildMember memberU1 = getMember(dbGuild, "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, memberU1.getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void kickGuildMemberPresiMemberSendNoGuildPacket() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int memberId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("XXXX").getId();
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Success presi kicks user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        PlanetSystemService planetSystemServiceMock = EasyMock.createStrictMock(PlanetSystemService.class);
        User memberUser = userService.getUser("member");
        EasyMock.expect(planetSystemServiceMock.getServerPlanetServices(memberUser)).andReturn(planetSystemService.getServerPlanetServices(userService.getUserState(memberUser)));
        EasyMock.expect(planetSystemServiceMock.getPlanet4BaselessConnection(userService.getUserState(memberUser))).andReturn(null);
        EasyMock.expect(planetSystemServiceMock.getServerPlanetServices(memberUser)).andReturn(null);
        planetSystemServiceMock.sendPacket(createUserStateMatcher("member"), createStorablePacket(StorablePacket.Type.GUILD_LOST));
        EasyMock.replay(planetSystemServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemServiceMock);

        FullGuildInfo fullGuildInfo = guildService.kickGuildMember(memberId);
        Assert.assertNotNull(fullGuildInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(planetSystemServiceMock);
    }

    @Test
    @DirtiesContext
    public void kickGuildMemberPresiMgmt() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildJoined(EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addChangeGuildMemberRank(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<GuildMemberInfo.Rank>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildMemberKicked(createUserMatcher("presi"), createUserMatcher("manager"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("manager");
        int managerId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        int presiId = userService.getUser().getId();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("XXXX").getId();
        guildService.inviteUserToGuild("manager");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.changeGuildMemberRank(managerId, GuildMemberInfo.Rank.MANAGEMENT);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Fail manager kick presi
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        try {
            guildService.kickGuildMember(presiId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User User: 'manager' id: 1 has not enough permission to kick: User: 'presi' id: 2 from Guild: DbGuild{name='XXXX', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Success presi kicks user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.kickGuildMember(managerId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals(1, dbGuild.getGuildMembers().size());
        DbGuildMember memberU1 = getMember(dbGuild, "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, memberU1.getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void kickGuildMemberMgmtMember() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildJoined(EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildJoined(EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addChangeGuildMemberRank(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<GuildMemberInfo.Rank>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildMemberKicked(createUserMatcher("manager"), createUserMatcher("member"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("manager");
        int managerId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int memberId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        userService.getUser().getId();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("XXXX").getId();
        guildService.inviteUserToGuild("manager");
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.changeGuildMemberRank(managerId, GuildMemberInfo.Rank.MANAGEMENT);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Fail manager kick presi
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        try {
            guildService.kickGuildMember(managerId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User User: 'member' id: 2 has not enough permission to kick: User: 'manager' id: 1 from Guild: DbGuild{name='XXXX', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Success manager kicks user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        guildService.kickGuildMember(memberId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals(2, dbGuild.getGuildMembers().size());
        DbGuildMember memberU1 = getMember(dbGuild, "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, memberU1.getRank());
        DbGuildMember memberU2 = getMember(dbGuild, "manager");
        Assert.assertEquals(GuildMemberInfo.Rank.MANAGEMENT, memberU2.getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void changeGuildMemberRankErrorCases() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        // Unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.changeGuildMemberRank(111, GuildMemberInfo.Rank.MEMBER);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // No Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        try {
            guildService.changeGuildMemberRank(111, GuildMemberInfo.Rank.MEMBER);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User has no guild: User: 'presi' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Wrong Rank
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        guildService.createGuild("XXXX");
        try {
            guildService.changeGuildMemberRank(111, GuildMemberInfo.Rank.PRESIDENT);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid rank: PRESIDENT", e.getMessage());
        }
        try {
            guildService.changeGuildMemberRank(111, null);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid rank: null", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // No user to change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        try {
            guildService.changeGuildMemberRank(111, GuildMemberInfo.Rank.MEMBER);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("User to change id does not exist or is not registered: 111", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // -- Create user to change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("change1");
        int changeUserId1 = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change user no guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        try {
            guildService.changeGuildMemberRank(changeUserId1, GuildMemberInfo.Rank.MEMBER);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User to change has no guild: User: 'change1' id: 2", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // -- Create user to change different guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("change2");
        int changeUserId2 = userService.getUser().getId();
        guildService.createGuild("BBBBBB");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change user different guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        try {
            guildService.changeGuildMemberRank(changeUserId2, GuildMemberInfo.Rank.MEMBER);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User to change and user are nor in the same guild. User to change: User: 'change2' id: 3 Guild: DbGuild{name='BBBBBB', id=2} User: User: 'presi' id: 1 Guild: DbGuild{name='XXXX', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void changeGuildMemberRankWrongRank() throws Exception {
        configureSimplePlanetNoResources();
        // Preparation
        // Create user U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int memberId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create user U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("management");
        int managementId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create user presi and guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        int presidentId = userService.getUser().getId();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("qqqqq").getId();
        guildService.inviteUserToGuild("member");
        guildService.inviteUserToGuild("management");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("management");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // change management user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.changeGuildMemberRank(managementId, GuildMemberInfo.Rank.MANAGEMENT);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Mock history
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Run tests
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        try {
            guildService.changeGuildMemberRank(managementId, GuildMemberInfo.Rank.MANAGEMENT);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Only guild president can change the member rank: User: 'member' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("management");
        try {
            guildService.changeGuildMemberRank(memberId, GuildMemberInfo.Rank.MANAGEMENT);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Only guild president can change the member rank: User: 'management' id: 2", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        try {
            guildService.changeGuildMemberRank(presidentId, GuildMemberInfo.Rank.MANAGEMENT);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Guild president rank can not be change: User: 'presi' id: 3", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void changeGuildMemberRank() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        EasyMock.expectLastCall().anyTimes();
        historyServiceMock.addGuildInvitation(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        EasyMock.expectLastCall().anyTimes();
        historyServiceMock.addGuildJoined(EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        EasyMock.expectLastCall().anyTimes();
        historyServiceMock.addChangeGuildMemberRank(createUserMatcher("presi"), createUserMatcher("U1"), EasyMock.eq(GuildMemberInfo.Rank.MANAGEMENT), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addChangeGuildMemberRank(createUserMatcher("presi"), createUserMatcher("U1"), EasyMock.eq(GuildMemberInfo.Rank.MEMBER), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        // Preparation
        // Create user U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        int user1Id = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create user U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create user presi and guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("qqqqq").getId();
        guildService.inviteUserToGuild("U1");
        guildService.inviteUserToGuild("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        DbGuildMember dbGuildMember = getMember(dbGuild, "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, dbGuildMember.getRank());
        dbGuildMember = getMember(dbGuild, "U1");
        Assert.assertEquals(GuildMemberInfo.Rank.MEMBER, dbGuildMember.getRank());
        dbGuildMember = getMember(dbGuild, "U2");
        Assert.assertEquals(GuildMemberInfo.Rank.MEMBER, dbGuildMember.getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Run test rank up
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        FullGuildInfo fullGuildInfo = guildService.changeGuildMemberRank(user1Id, GuildMemberInfo.Rank.MANAGEMENT);
        Assert.assertNotNull(fullGuildInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbGuild = get(DbGuild.class, guildId);
        dbGuildMember = getMember(dbGuild, "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, dbGuildMember.getRank());
        dbGuildMember = getMember(dbGuild, "U1");
        Assert.assertEquals(GuildMemberInfo.Rank.MANAGEMENT, dbGuildMember.getRank());
        dbGuildMember = getMember(dbGuild, "U2");
        Assert.assertEquals(GuildMemberInfo.Rank.MEMBER, dbGuildMember.getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Run test rank down
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.changeGuildMemberRank(user1Id, GuildMemberInfo.Rank.MEMBER);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbGuild = get(DbGuild.class, guildId);
        dbGuildMember = getMember(dbGuild, "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, dbGuildMember.getRank());
        dbGuildMember = getMember(dbGuild, "U1");
        Assert.assertEquals(GuildMemberInfo.Rank.MEMBER, dbGuildMember.getRank());
        dbGuildMember = getMember(dbGuild, "U2");
        Assert.assertEquals(GuildMemberInfo.Rank.MEMBER, dbGuildMember.getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void saveGuildTextErrorCases() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildJoined(EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildJoined(EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Create manager
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("manager");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create manager
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Fail Unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.saveGuildText("");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // No Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        try {
            guildService.saveGuildText("");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User has no guild: User: 'presi' id: 3", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create guild and invite members
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxxx").getId();
        guildService.inviteUserToGuild("manager");
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Fail change text as manager
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        guildService.joinGuild(guildId);
        try {
            guildService.saveGuildText("");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Only guild president can change guild text: User: 'manager' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Fail change text as member
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        try {
            guildService.saveGuildText("");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Only guild president can change guild text: User: 'member' id: 2", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void saveGuildText() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildTextChanged(createUserMatcher("presi"), EasyMock.eq("jfiwhnfcidnhsi"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxxx").getId();
        FullGuildInfo fullGuildInfo = guildService.saveGuildText("jfiwhnfcidnhsi");
        Assert.assertNotNull(fullGuildInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals("jfiwhnfcidnhsi", dbGuild.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void saveGuildTextEscapeHtml() throws Exception {
        configureSimplePlanetNoResources();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxxx").getId();
        guildService.saveGuildText("<a href='xxxx'>qqqq</a>");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals("<a rel=\"nofollow\">qqqq</a>", dbGuild.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.saveGuildText("<script type=\"text/javascript\">function helloWorld() {alert('Hello World!');}</script>");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals("", dbGuild.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.saveGuildText("<input type=\"button\" id=\"hello-world2\" value=\"Hello\" onClick=\"helloWorld();\" />");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals("", dbGuild.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void isGuildNameValid() throws Exception {
        configureSimplePlanetNoResources();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        guildService.createGuild("hallo").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(VerificationRequestCallback.ErrorResult.TO_SHORT, guildService.isGuildNameValid(""));
        Assert.assertEquals(VerificationRequestCallback.ErrorResult.TO_SHORT, guildService.isGuildNameValid("h"));
        Assert.assertEquals(VerificationRequestCallback.ErrorResult.TO_SHORT, guildService.isGuildNameValid("ha"));
        Assert.assertEquals(VerificationRequestCallback.ErrorResult.ALREADY_USED, guildService.isGuildNameValid("hallo"));
        Assert.assertNull(guildService.isGuildNameValid("hallo1"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void searchGuilds() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Preparation
        for (int i = 99; i >= 0; i--) {
            beginHttpSession();
            beginHttpRequestAndOpenSessionInViewFilter();
            createAndLoginUser("aa" + i);
            guildService.createGuild(String.format("aa%02d", i));
            endHttpRequestAndOpenSessionInViewFilter();
            endHttpSession();
        }
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SearchGuildsResult searchGuildsResult = guildService.searchGuilds(0, 100, "");
        Assert.assertEquals(0, searchGuildsResult.getStartRow());
        Assert.assertEquals(100, searchGuildsResult.getTotalRowCount());
        Assert.assertEquals(100, searchGuildsResult.getGuildDetailedInfos().size());
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(String.format("aa%02d", i), searchGuildsResult.getGuildDetailedInfos().get(i).getName());
        }
        // Test
        searchGuildsResult = guildService.searchGuilds(0, 20, "");
        Assert.assertEquals(0, searchGuildsResult.getStartRow());
        Assert.assertEquals(100, searchGuildsResult.getTotalRowCount());
        Assert.assertEquals(20, searchGuildsResult.getGuildDetailedInfos().size());
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(String.format("aa%02d", i), searchGuildsResult.getGuildDetailedInfos().get(i).getName());
        }
        // Test
        searchGuildsResult = guildService.searchGuilds(0, 20, "aa1");
        Assert.assertEquals(0, searchGuildsResult.getStartRow());
        Assert.assertEquals(10, searchGuildsResult.getTotalRowCount());
        Assert.assertEquals(10, searchGuildsResult.getGuildDetailedInfos().size());
        for (int i = 10; i < 20; i++) {
            Assert.assertEquals(String.format("aa%02d", i), searchGuildsResult.getGuildDetailedInfos().get(i - 10).getName());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void realGameInfo() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(getMovableService().getRealGameInfo(START_UID_1, null).getMySimpleGuild());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        Assert.assertNull(getMovableService().getRealGameInfo(START_UID_1, null).getMySimpleGuild());
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        guildService.createGuild("lkjlkj");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Registered no base
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        SimpleGuild simpleGuild = getMovableService().getRealGameInfo(START_UID_1, null).getMySimpleGuild();
        Assert.assertNotNull(simpleGuild);
        Assert.assertEquals("lkjlkj", simpleGuild.getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Registered no base
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        createBase(new Index(1000, 1000));
        simpleGuild = getMovableService().getRealGameInfo(START_UID_1, null).getMySimpleGuild();
        Assert.assertNotNull(simpleGuild);
        Assert.assertEquals("lkjlkj", simpleGuild.getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void leaveGuildErrorCases() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        // Unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.leaveGuild();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // No Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        try {
            guildService.leaveGuild();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User has no guild: User: 'presi' id: 1", e.getMessage());
        }
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        try {
            guildService.leaveGuild();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("President can not leave the guild: User: 'presi' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verfify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals(1, dbGuild.getGuildMembers().size());
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, getMember(dbGuild, "presi").getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void leaveGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("manager");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Make guild and invite
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        guildService.inviteUserToGuild("member");
        guildService.inviteUserToGuild("manager");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        int managerId = userService.getUser().getId();
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.changeGuildMemberRank(managerId, GuildMemberInfo.Rank.MANAGEMENT);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Mock
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildLeft(createUserMatcher("member"), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildLeft(createUserMatcher("manager"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Run tests
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.leaveGuild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verfify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals(2, dbGuild.getGuildMembers().size());
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, getMember(dbGuild, "presi").getRank());
        Assert.assertEquals(GuildMemberInfo.Rank.MANAGEMENT, getMember(dbGuild, "manager").getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Run tests
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        guildService.leaveGuild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verfify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals(1, dbGuild.getGuildMembers().size());
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, getMember(dbGuild, "presi").getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void closeGuildErrorCases() throws Exception {
        configureSimplePlanetNoResources();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(EasyMock.<User>anyObject(), EasyMock.anyInt(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildJoined(EasyMock.<User>anyObject(), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addChangeGuildMemberRank(EasyMock.<User>anyObject(), EasyMock.<User>anyObject(), EasyMock.<GuildMemberInfo.Rank>anyObject(), EasyMock.<DbGuild>anyObject());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Run tests
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int memberId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.closeGuild();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // No Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        try {
            guildService.closeGuild();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User has no guild: User: 'presi' id: 2", e.getMessage());
        }
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join guild and check close as member
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        try {
            guildService.closeGuild();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Only president can close guild: User: 'member' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change rank to manager
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.changeGuildMemberRank(memberId, GuildMemberInfo.Rank.MANAGEMENT);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join guild and check close as member
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        try {
            guildService.closeGuild();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Only president can close guild: User: 'member' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verfify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, guildId);
        Assert.assertEquals(2, dbGuild.getGuildMembers().size());
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, getMember(dbGuild, "presi").getRank());
        Assert.assertEquals(GuildMemberInfo.Rank.MANAGEMENT, getMember(dbGuild, "member").getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void closeGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("manager");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("invitee");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("requester");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Make guild and invite
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        guildService.inviteUserToGuild("member");
        guildService.inviteUserToGuild("manager");
        guildService.inviteUserToGuild("invitee");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("manager");
        int managerId = userService.getUser().getId();
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("requester");
        guildService.guildMembershipRequest(guildId, "xxxxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.changeGuildMemberRank(managerId, GuildMemberInfo.Rank.MANAGEMENT);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Mock
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addKickedGuildClosed(createUserMatcher("presi"), createUserMatcher("member"), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addKickedGuildClosed(createUserMatcher("presi"), createUserMatcher("manager"), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildClosed(createUserMatcher("presi"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Run tests
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.closeGuild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        assertNoDbEntry(DbGuild.class);
        assertNoDbEntry(DbGuildMember.class);
        assertNoDbEntry(DbGuildMembershipRequest.class);
        assertNoDbEntry(DbGuildInvitation.class);

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void closeGuildSendNoGuildPackage() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Make guild and invite
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        guildService.inviteUserToGuild("member1");
        guildService.inviteUserToGuild("member2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member1");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member2");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Run tests
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        PlanetSystemService planetSystemServiceMock = EasyMock.createStrictMock(PlanetSystemService.class);
        // Handle presi
        User presi = userService.getUser("presi");
        EasyMock.expect(planetSystemServiceMock.getServerPlanetServices(presi)).andReturn(planetSystemService.getServerPlanetServices(userService.getUserState(presi)));
        EasyMock.expect(planetSystemServiceMock.getPlanet4BaselessConnection(userService.getUserState(presi))).andReturn(null);
        // Handle member 1
        User member1 = userService.getUser("member1");
        planetSystemServiceMock.sendPacket(createUserStateMatcher("member1"), createStorablePacket(StorablePacket.Type.GUILD_LOST));
        EasyMock.expect(planetSystemServiceMock.getServerPlanetServices(member1)).andReturn(planetSystemService.getServerPlanetServices(userService.getUserState(member1)));
        EasyMock.expect(planetSystemServiceMock.getPlanet4BaselessConnection(userService.getUserState(member1))).andReturn(null);
        // Handle member 2
        User member2 = userService.getUser("member2");
        planetSystemServiceMock.sendPacket(createUserStateMatcher("member2"), createStorablePacket(StorablePacket.Type.GUILD_LOST));
        EasyMock.expect(planetSystemServiceMock.getServerPlanetServices(member2)).andReturn(planetSystemService.getServerPlanetServices(userService.getUserState(member2)));
        EasyMock.expect(planetSystemServiceMock.getPlanet4BaselessConnection(userService.getUserState(member2))).andReturn(null);
        // Handle enemies
        EasyMock.expect(planetSystemServiceMock.getServerPlanetServices(presi)).andReturn(null);
        EasyMock.expect(planetSystemServiceMock.getServerPlanetServices(member1)).andReturn(null);
        EasyMock.expect(planetSystemServiceMock.getServerPlanetServices(member2)).andReturn(null);
        EasyMock.replay(planetSystemServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemServiceMock);
        guildService.closeGuild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(planetSystemServiceMock);
    }

    @Test
    @DirtiesContext
    public void fillUserAttentionPacket() throws Exception {
        configureSimplePlanetNoResources();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserAttentionPacket userAttentionPacket = new UserAttentionPacket();
        guildService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertNull(userAttentionPacket.getGuildInvitation());
        Assert.assertNull(userAttentionPacket.getGuildMembershipRequest());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Make guild and invite
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        guildService.inviteUserToGuild("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        userAttentionPacket = new UserAttentionPacket();
        guildService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertEquals(UserAttentionPacket.Type.RAISE, userAttentionPacket.getGuildInvitation());
        Assert.assertNull(userAttentionPacket.getGuildMembershipRequest());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Make 2 guild and invite
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi2");
        guildService.createGuild("yyyy");
        guildService.inviteUserToGuild("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        userAttentionPacket = new UserAttentionPacket();
        guildService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertEquals(UserAttentionPacket.Type.RAISE, userAttentionPacket.getGuildInvitation());
        Assert.assertNull(userAttentionPacket.getGuildMembershipRequest());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test presi
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        userAttentionPacket = new UserAttentionPacket();
        guildService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertNull(userAttentionPacket.getGuildInvitation());
        Assert.assertNull(userAttentionPacket.getGuildMembershipRequest());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Make user and request
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        guildService.guildMembershipRequest(guildId, "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        userAttentionPacket = new UserAttentionPacket();
        guildService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertEquals(UserAttentionPacket.Type.RAISE, userAttentionPacket.getGuildInvitation());
        Assert.assertNull(userAttentionPacket.getGuildMembershipRequest());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test presi
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        userAttentionPacket = new UserAttentionPacket();
        guildService.fillUserAttentionPacket(userService.getUser(), userAttentionPacket);
        Assert.assertNull(userAttentionPacket.getGuildInvitation());
        Assert.assertEquals(UserAttentionPacket.Type.RAISE, userAttentionPacket.getGuildMembershipRequest());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void backupRestore() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("nonmember");
        createBase(new Index(500, 500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member1");
        createBase(new Index(1000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("pendingMember");
        createBase(new Index(1500, 1500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        guildService.inviteUserToGuild("member1");
        guildService.inviteUserToGuild("pendingMember");
        createBase(new Index(2000, 2000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member1");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild().getId());
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "member1").getSimpleGuild().getId());
        Assert.assertNull(getBaseAttributes(TEST_PLANET_1_ID, "nonmember").getSimpleGuild());
        Assert.assertNull(getBaseAttributes(TEST_PLANET_1_ID, "pendingMember").getSimpleGuild());
        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.restore(backupService.getBackupSummary().get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild().getId());
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "member1").getSimpleGuild().getId());
        Assert.assertNull(getBaseAttributes(TEST_PLANET_1_ID, "nonmember").getSimpleGuild());
        Assert.assertNull(getBaseAttributes(TEST_PLANET_1_ID, "pendingMember").getSimpleGuild());
    }

    @Test
    @DirtiesContext
    public void createDeleteBase() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("nomember");
        createBase(new Index(1000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        SimpleBase simpleBase1 = createBase(new Index(1500, 1500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxxx").getId();
        guildService.inviteUserToGuild("member");
        createBase(new Index(2000, 2000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        Assert.assertNull(getBaseAttributes(TEST_PLANET_1_ID, "nomember").getSimpleGuild());
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild().getId());
        Assert.assertEquals(simpleBase1, getBaseAttributes(TEST_PLANET_1_ID, "member").getSimpleBase());
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "member").getSimpleGuild().getId());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        Assert.assertEquals(simpleBase1, getOrCreateBase());
        getMovableService().surrenderBase();
        SimpleBase simpleBase2 = createBase(new Index(2500, 2500));
        Assert.assertFalse(simpleBase1.equals(simpleBase2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild().getId());
        Assert.assertEquals(simpleBase2, getBaseAttributes(TEST_PLANET_1_ID, "member").getSimpleBase());
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "member").getSimpleGuild().getId());
    }

    @Test
    @DirtiesContext
    public void createDeleteBaseDifferentPlanets() throws Exception {
        configureMultiplePlanetsAndLevels();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("xxx").getId();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase simpleBase1 = createBase(new Index(1000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        Assert.assertEquals(simpleBase1, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleBase());
        Assert.assertEquals(TEST_PLANET_1_ID, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleBase().getPlanetId());
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild().getId());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_5_REAL_ID);
        getMovableService().surrenderBase();
        SimpleBase simpleBase2 = createBase(new Index(8000, 8000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_2_ID, "presi").getSimpleGuild().getId());
        Assert.assertEquals(simpleBase2, getBaseAttributes(TEST_PLANET_2_ID, "presi").getSimpleBase());
        Assert.assertEquals(TEST_PLANET_2_ID, getBaseAttributes(TEST_PLANET_2_ID, "presi").getSimpleBase().getPlanetId());
    }
}
