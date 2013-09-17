package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildDetailedInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMembershipRequest;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.UserIsAlreadyGuildMemberException;
import com.btxtech.game.jsre.common.gameengine.services.user.NoSuchUserException;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.impl.GuildServiceImpl;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 24.04.12
 * Time: 21:50
 */
public class TestGuildService2 extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private PropertyService propertyService;

    @Test
    @DirtiesContext
    public void createGuildUnregistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.createGuild("Hallo");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuild.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void createGuildNoCrystals() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        try {
            guildService.createGuild("Hallo");
            Assert.fail("IllegalStateException due to crystals expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Not enough crystals to create a guild: User: 'U1' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuild.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void createGuildHasGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(10), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 10);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getUserState().addCrystals(100);
        guildService.createGuild("AAAAAA");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        try {
            guildService.createGuild("BBBBB");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User has already a guild: User: 'U1' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void createGuildWrongName() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(10), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 10);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getUserState().addCrystals(100);
        try {
            guildService.createGuild("Ha");
            Assert.fail("IllegalArgumentException e");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Guild name is invalid: TO_SHORT Ha", e.getMessage());
        }
        SimpleGuild simpleGuild = guildService.createGuild("Hallo");
        Assert.assertEquals(1, simpleGuild.getId());
        Assert.assertEquals("Hallo", simpleGuild.getName());
        Assert.assertEquals(90, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(10), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 10);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getUserState().addCrystals(100);
        SimpleGuild simpleGuild = guildService.createGuild("Hallo");
        Assert.assertEquals(1, simpleGuild.getId());
        Assert.assertEquals("Hallo", simpleGuild.getName());
        Assert.assertEquals(90, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbGuild> guilds = loadAll(DbGuild.class);
        Assert.assertEquals(1, guilds.size());
        DbGuild dbGuild = guilds.get(0);
        Assert.assertEquals("Hallo", dbGuild.getName());
        Collection<DbGuildMember> guildMembers = dbGuild.getGuildMembers();
        Assert.assertEquals(1, guildMembers.size());
        DbGuildMember dbGuildMember = CommonJava.getFirst(guildMembers);
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, dbGuildMember.getRank());
        Assert.assertEquals(dbGuild, dbGuildMember.getDbGuild());
        Assert.assertEquals(userService.getUser("U1"), dbGuildMember.getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void createGuildRemoveInvitationsRemoveGuildMembershipRequests() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        guildService.createGuild("Guild1");
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        guildService.createGuild("Guild2");
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U3");
        guildService.createGuild("Guild3");
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U4");
        int guild4Id = guildService.createGuild("Guild4").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U5");
        int guild5Id = guildService.createGuild("Guild5").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U");
        guildService.guildMembershipRequest(guild4Id, "");
        guildService.guildMembershipRequest(guild5Id, "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        assertDbEntryCount(5, DbGuild.class);
        assertDbEntryCount(3, DbGuildInvitation.class);
        assertDbEntryCount(2, DbGuildMembershipRequest.class);
        // Actual test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U");
        guildService.createGuild("Guild");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        assertNoDbEntry(DbGuildInvitation.class);
        assertNoDbEntry(DbGuildMembershipRequest.class);
        assertDbEntryCount(6, DbGuild.class);
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuildNotRegistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.inviteUserToGuild("xxx");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildInvitation.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuildNoGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        try {
            guildService.inviteUserToGuild("xxx");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("User is not member of a guild: User: 'U1' id: 1", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildInvitation.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuildNoUser() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        guildService.createGuild("Hallo");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        try {
            guildService.inviteUserToGuild("xxx");
            Assert.fail("NoSuchUserException expected");
        } catch (NoSuchUserException e) {
            Assert.assertEquals("Nu such user: xxx", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildInvitation.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuildInviteeIsGuildMember() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildCreated(createUserMatcher("U2"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        guildService.createGuild("Hallo");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U2");
        guildService.createGuild("Hallo2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        try {
            guildService.inviteUserToGuild("U2");
            Assert.fail("UserIsAlreadyGuildMemberException expected");
        } catch (UserIsAlreadyGuildMemberException e) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildInvitation.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(createUserMatcher("U1"), createUserMatcher("U2"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        guildService.createGuild("Hallo");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        FullGuildInfo fullGuildInfo = getMovableService().inviteUserToGuild("U2");
        Assert.assertNotNull(fullGuildInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbGuildInvitation> dbGuildInvitations = loadAll(DbGuildInvitation.class);
        Assert.assertEquals(1, dbGuildInvitations.size());
        DbGuildInvitation dbGuildInvitation = dbGuildInvitations.get(0);
        Assert.assertEquals("U2", dbGuildInvitation.getUser().getUsername());
        Assert.assertEquals("Hallo", dbGuildInvitation.getDbGuild().getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuildIgnoreDoubles() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(createUserMatcher("U1"), createUserMatcher("U2"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        guildService.createGuild("Hallo");
        guildService.inviteUserToGuild("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbGuildInvitation> dbGuildInvitations = loadAll(DbGuildInvitation.class);
        Assert.assertEquals(1, dbGuildInvitations.size());
        DbGuildInvitation dbGuildInvitation = dbGuildInvitations.get(0);
        Assert.assertEquals("U2", dbGuildInvitation.getUser().getUsername());
        Assert.assertEquals("Hallo", dbGuildInvitation.getDbGuild().getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Second invite
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        guildService.inviteUserToGuild("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbGuildInvitations = loadAll(DbGuildInvitation.class);
        Assert.assertEquals(1, dbGuildInvitations.size());
        dbGuildInvitation = dbGuildInvitations.get(0);
        Assert.assertEquals("U2", dbGuildInvitation.getUser().getUsername());
        Assert.assertEquals("Hallo", dbGuildInvitation.getDbGuild().getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }


    @Test
    @DirtiesContext
    public void inviteUserToGuildSendPackage() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        guildService.createGuild("Hallo");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        PlanetSystemService planetSystemServiceMock = EasyMock.createStrictMock(PlanetSystemService.class);
        planetSystemServiceMock.sendPacket(createUserStateMatcher("U2"), createUserAttentionPacketMatcher(null, null, UserAttentionPacket.Type.RAISE));
        EasyMock.replay(planetSystemServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "planetSystemService", planetSystemServiceMock);
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        guildService.inviteUserToGuild("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Second invite no package sent
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        guildService.inviteUserToGuild("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(planetSystemServiceMock);
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuildRemoveGuildMembershipRequest() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("Hallo").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        guildService.guildMembershipRequest(guildId, "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertDbEntryCount(1, DbGuildMembershipRequest.class);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        FullGuildInfo fullGuildInfo = getMovableService().inviteUserToGuild("U2");
        Assert.assertTrue(fullGuildInfo.getRequests().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoDbEntry(DbGuildMembershipRequest.class);
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuildViaBaseErrorCases() throws Exception {
        configureSimplePlanetNoResources();
        // Unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Unregistered
        try {
            guildService.inviteUserToGuild(new SimpleBase(-100, -100));
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "User is not registered");
        }
        // Abandoned
        createAndLoginUser("U1");
        try {
            guildService.inviteUserToGuild(new SimpleBase(-100, TEST_PLANET_1_ID));
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Base is isAbandoned: Base Id: -100 Planet Id: 1");
        }
        // TODO UNregistered user which will be invited, can not be tested
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void inviteUserToGuildVia() throws Exception {
        configureSimplePlanetNoResources();
        // Create base to invite
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        SimpleBase simpleBase = createBase(new Index(1000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        guildService.createGuild("xxxx");
        guildService.inviteUserToGuild(simpleBase);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbGuildInvitation> dbGuildInvitations = loadAll(DbGuildInvitation.class);
        Assert.assertEquals(1, dbGuildInvitations.size());
        DbGuildInvitation dbGuildInvitation = dbGuildInvitations.get(0);
        Assert.assertEquals("member", dbGuildInvitation.getUser().getUsername());
        Assert.assertEquals("xxxx", dbGuildInvitation.getDbGuild().getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void joinGuildNotRegistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.joinGuild(99);
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
    public void joinGuildNoGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        try {
            guildService.joinGuild(99);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Guild does not exist: 99", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void joinGuildNoInvitation() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("Hallo").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        try {
            guildService.joinGuild(guildId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User does not have an invitation to guild. User: User: 'U2' id: 2 Guild: DbGuild{name='Hallo', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertDbEntryCount(1, DbGuildMember.class);
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void joinGuild() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(createUserMatcher("U1"), createUserMatcher("U2"), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildJoined(createUserMatcher("U2"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("AAAAAA").getId();
        guildService.inviteUserToGuild("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        SimpleGuild simpleGuild = guildService.joinGuild(guildId);
        Assert.assertEquals(guildId, simpleGuild.getId());
        Assert.assertEquals("AAAAAA", simpleGuild.getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify db
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbGuild dbGuild = get(DbGuild.class, simpleGuild.getId());
        Assert.assertEquals(2, dbGuild.getGuildMembers().size());
        DbGuildMember memberU1 = TestGuildService.getMember(dbGuild, "U1");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, memberU1.getRank());
        DbGuildMember memberU2 = TestGuildService.getMember(dbGuild, "U2");
        Assert.assertEquals(GuildMemberInfo.Rank.MEMBER, memberU2.getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
        assertNoDbEntry(DbGuildInvitation.class);
    }

    @Test
    @DirtiesContext
    public void joinGuildRemoveInvitationsRemoveGuildMembershipRequests() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("Guild1").getId();
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        guildService.createGuild("Guild2");
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U3");
        guildService.createGuild("Guild3");
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U4");
        int guild4Id = guildService.createGuild("Guild4").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U5");
        int guild5Id = guildService.createGuild("Guild5").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U");
        guildService.guildMembershipRequest(guild4Id, "");
        guildService.guildMembershipRequest(guild5Id, "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        assertDbEntryCount(5, DbGuild.class);
        assertDbEntryCount(3, DbGuildInvitation.class);
        assertDbEntryCount(2, DbGuildMembershipRequest.class);
        // Actual test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        assertNoDbEntry(DbGuildInvitation.class);
        assertNoDbEntry(DbGuildMembershipRequest.class);
        assertDbEntryCount(5, DbGuild.class);
    }

    @Test
    @DirtiesContext
    public void dismissGuildNotRegistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.dismissGuildInvitation(99);
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
    public void dismissGuildNoInvitation() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("Hallo").getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        try {
            guildService.dismissGuildInvitation(guildId);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User does not have an invitation to guild. User: User: 'U2' id: 2 Guild: DbGuild{name='Hallo', id=1}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void dismissGuild() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addGuildCreated(createUserMatcher("U1"), EasyMock.eq(0), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildInvitation(createUserMatcher("U1"), createUserMatcher("U2"), EasyMock.<DbGuild>anyObject());
        historyServiceMock.addGuildDismissInvitation(createUserMatcher("U2"), EasyMock.<DbGuild>anyObject());
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId = guildService.createGuild("AAAAAA").getId();
        guildService.inviteUserToGuild("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        List<GuildDetailedInfo> openInvitations = guildService.dismissGuildInvitation(guildId);
        Assert.assertTrue(openInvitations.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
        assertNoDbEntry(DbGuildInvitation.class);
    }

    @Test
    @DirtiesContext
    public void getGuildInvitationsNotRegistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.getGuildInvitations();
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
    public void getGuildInvitationsNoGuild() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        EasyMock.replay(historyServiceMock);
        setPrivateField(GuildServiceImpl.class, guildService, "historyService", historyServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        List<GuildDetailedInfo> openInvitations = guildService.getGuildInvitations();
        Assert.assertTrue(openInvitations.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void getGuildInvitationsAndDismissGuild() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("U1");
        int guildId1 = guildService.createGuild("Aaa").getId();
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        int guildId2 = guildService.createGuild("Zzz").getId();
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U3");
        int guildId3 = guildService.createGuild("Ccc").getId();
        guildService.inviteUserToGuild("U");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U");
        List<GuildDetailedInfo> openInvitations = guildService.getGuildInvitations();
        Assert.assertEquals(3, openInvitations.size());
        Assert.assertEquals(guildId1, openInvitations.get(0).getId());
        Assert.assertEquals("Aaa", openInvitations.get(0).getName());
        Assert.assertEquals(guildId3, openInvitations.get(1).getId());
        Assert.assertEquals("Ccc", openInvitations.get(1).getName());
        Assert.assertEquals(guildId2, openInvitations.get(2).getId());
        Assert.assertEquals("Zzz", openInvitations.get(2).getName());
        openInvitations = guildService.dismissGuildInvitation(guildId1);
        Assert.assertEquals(2, openInvitations.size());
        Assert.assertEquals(guildId3, openInvitations.get(0).getId());
        Assert.assertEquals("Ccc", openInvitations.get(0).getName());
        Assert.assertEquals(guildId2, openInvitations.get(1).getId());
        Assert.assertEquals("Zzz", openInvitations.get(1).getName());
        openInvitations = guildService.getGuildInvitations();
        Assert.assertEquals(2, openInvitations.size());
        Assert.assertEquals(guildId3, openInvitations.get(0).getId());
        Assert.assertEquals("Ccc", openInvitations.get(0).getName());
        Assert.assertEquals(guildId2, openInvitations.get(1).getId());
        Assert.assertEquals("Zzz", openInvitations.get(1).getName());
        openInvitations = guildService.dismissGuildInvitation(guildId3);
        Assert.assertEquals(1, openInvitations.size());
        Assert.assertEquals(guildId2, openInvitations.get(0).getId());
        Assert.assertEquals("Zzz", openInvitations.get(0).getName());
        openInvitations = guildService.getGuildInvitations();
        Assert.assertEquals(1, openInvitations.size());
        Assert.assertEquals(guildId2, openInvitations.get(0).getId());
        Assert.assertEquals("Zzz", openInvitations.get(0).getName());
        openInvitations = guildService.dismissGuildInvitation(guildId2);
        Assert.assertTrue(openInvitations.isEmpty());
        openInvitations = guildService.getGuildInvitations();
        Assert.assertTrue(openInvitations.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getFullGuildInfoNoGuild() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.getFullGuildInfo(11111);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No such guild: 11111", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getFullGuildInfoWrongGuild() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            guildService.getFullGuildInfo(111);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No such guild: 111", e.getMessage());
        }
        endHttpSession();
        beginHttpSession();
    }

    @Test
    @DirtiesContext
    public void getFullGuildInfo() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        int guildId = guildService.createGuild("GuildName").getId();
        guildService.saveGuildText("disfhoasudf isudhfoiash");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FullGuildInfo fullGuildInfo = guildService.getFullGuildInfo(guildId);
        Assert.assertEquals(guildId, fullGuildInfo.getGuildInfo().getId());
        Assert.assertEquals("GuildName", fullGuildInfo.getGuildInfo().getName());
        Assert.assertEquals("disfhoasudf isudhfoiash", fullGuildInfo.getGuildInfo().getText());
        Assert.assertEquals(1, fullGuildInfo.getMembers().size());
        GuildMemberInfo guildMemberInfo = getMember(fullGuildInfo.getMembers(), "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, guildMemberInfo.getRank());
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, guildMemberInfo.getDetailedUser().getLevel());
        Assert.assertEquals(null, guildMemberInfo.getDetailedUser().getPlanet());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create Base
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_5_REAL_ID);
        getMovableService().getRealGameInfo(START_UID_1, null);// Make connection
        getMovableService().createBase(START_UID_1, new Index(100, 100));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        fullGuildInfo = guildService.getFullGuildInfo(guildId);
        Assert.assertEquals(guildId, fullGuildInfo.getGuildInfo().getId());
        Assert.assertEquals("GuildName", fullGuildInfo.getGuildInfo().getName());
        Assert.assertEquals("disfhoasudf isudhfoiash", fullGuildInfo.getGuildInfo().getText());
        Assert.assertEquals(1, fullGuildInfo.getMembers().size());
        guildMemberInfo = getMember(fullGuildInfo.getMembers(), "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, guildMemberInfo.getRank());
        Assert.assertEquals(TEST_LEVEL_5_REAL, guildMemberInfo.getDetailedUser().getLevel());
        Assert.assertEquals(TEST_PLANET_2, guildMemberInfo.getDetailedUser().getPlanet());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Add members
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("manager");
        int managerId = userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        userService.getUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Invite and change rank
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        guildService.inviteUserToGuild("manager");
        guildService.inviteUserToGuild("member");
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
        // Create Base
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        getMovableService().getRealGameInfo(START_UID_1, null);// Make connection
        getMovableService().createBase(START_UID_1, new Index(100, 100));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        fullGuildInfo = guildService.getFullGuildInfo(guildId);
        Assert.assertEquals(guildId, fullGuildInfo.getGuildInfo().getId());
        Assert.assertEquals("GuildName", fullGuildInfo.getGuildInfo().getName());
        Assert.assertEquals("disfhoasudf isudhfoiash", fullGuildInfo.getGuildInfo().getText());
        Assert.assertEquals(3, fullGuildInfo.getMembers().size());
        guildMemberInfo = getMember(fullGuildInfo.getMembers(), "presi");
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, guildMemberInfo.getRank());
        Assert.assertEquals(TEST_LEVEL_5_REAL, guildMemberInfo.getDetailedUser().getLevel());
        Assert.assertEquals(TEST_PLANET_2, guildMemberInfo.getDetailedUser().getPlanet());
        guildMemberInfo = getMember(fullGuildInfo.getMembers(), "manager");
        Assert.assertEquals(GuildMemberInfo.Rank.MANAGEMENT, guildMemberInfo.getRank());
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, guildMemberInfo.getDetailedUser().getLevel());
        Assert.assertNull(guildMemberInfo.getDetailedUser().getPlanet());
        guildMemberInfo = getMember(fullGuildInfo.getMembers(), "member");
        Assert.assertEquals(GuildMemberInfo.Rank.MEMBER, guildMemberInfo.getRank());
        Assert.assertEquals(TEST_LEVEL_2_REAL, guildMemberInfo.getDetailedUser().getLevel());
        Assert.assertEquals(TEST_PLANET_1, guildMemberInfo.getDetailedUser().getPlanet());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        SimpleUser simpleUser = userService.getUser().createSimpleUser();
        fullGuildInfo = guildService.getFullGuildInfo(guildId);
        guildMemberInfo = fullGuildInfo.getMember(simpleUser);
        Assert.assertNotNull(guildMemberInfo);
        Assert.assertEquals(GuildMemberInfo.Rank.PRESIDENT, guildMemberInfo.getRank());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private GuildMemberInfo getMember(Collection<GuildMemberInfo> members, String userName) {
        for (GuildMemberInfo member : members) {
            if (member.getDetailedUser().getSimpleUser().getName().equals(userName)) {
                return member;
            }
        }
        throw new IllegalArgumentException("No such user: " + userName);
    }

    @Test
    @DirtiesContext
    public void getFullGuildInfoMemberShipRequests() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        int guildId = guildService.createGuild("GuildName").getId();
        guildService.saveGuildText("disfhoasudf isudhfoiash");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FullGuildInfo fullGuildInfo = guildService.getFullGuildInfo(guildId);
        Assert.assertTrue(fullGuildInfo.getRequests().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // User
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        guildService.guildMembershipRequest(guildId, "hhhshhhshhhs");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        fullGuildInfo = guildService.getFullGuildInfo(guildId);
        Assert.assertEquals(1, fullGuildInfo.getRequests().size());
        GuildMembershipRequest guildMembershipRequest = getGuildMembershipRequest(fullGuildInfo, "U1");
        Assert.assertEquals("hhhshhhshhhs", guildMembershipRequest.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // User
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        guildService.guildMembershipRequest(guildId, "qqwweeerrrr dsgasdg dd");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        fullGuildInfo = guildService.getFullGuildInfo(guildId);
        Assert.assertEquals(2, fullGuildInfo.getRequests().size());
        guildMembershipRequest = getGuildMembershipRequest(fullGuildInfo, "U1");
        Assert.assertEquals("hhhshhhshhhs", guildMembershipRequest.getText());
        guildMembershipRequest = getGuildMembershipRequest(fullGuildInfo, "U2");
        Assert.assertEquals("qqwweeerrrr dsgasdg dd", guildMembershipRequest.getText());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private GuildMembershipRequest getGuildMembershipRequest(FullGuildInfo fullGuildInfo, String userName) {
        for (GuildMembershipRequest guildMembershipRequest : fullGuildInfo.getRequests()) {
            if (guildMembershipRequest.getDetailedUser().getSimpleUser().getName().equals(userName)) {
                return guildMembershipRequest;
            }
        }
        throw new IllegalArgumentException("No such request fpr user name: " + userName);
    }
}
