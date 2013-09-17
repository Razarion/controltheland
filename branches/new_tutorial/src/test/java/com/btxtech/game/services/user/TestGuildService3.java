package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.impl.ServerGlobalServicesImpl;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

/**
 * User: beat
 * Date: 24.04.12
 * Time: 21:50
 */
public class TestGuildService3 extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerGlobalServices serverGlobalServices;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void baseUpdateCreateGuild() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        createBase(new Index(1000, 1000));
        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        serverConnectionService.sendPacket(createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "presi", false, 1));
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);

        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals(guildId, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild().getId());
        EasyMock.verify(serverConnectionService);
    }

    @Test
    @DirtiesContext
    public void baseUpdateJoinAndLeaveGuild() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        createBase(new Index(1500, 1500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        createBase(new Index(1000, 1000));
        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        serverConnectionService.sendPacket(createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member", false, guildId));
        serverConnectionService.sendPacket(createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member", false, null));
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);
        guildService.joinGuild(guildId);
        Assert.assertEquals(guildId, (int) getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild().getId());
        Assert.assertEquals(guildId, (int) getBaseAttributes(TEST_PLANET_1_ID, "member").getSimpleGuild().getId());
        guildService.leaveGuild();
        Assert.assertNull(getBaseAttributes(TEST_PLANET_1_ID, "member").getSimpleGuild());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConnectionService);
    }

    @Test
    @DirtiesContext
    public void baseUpdateKickMember() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int userId = userService.getSimpleUser().getId();
        createBase(new Index(1500, 1500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        createBase(new Index(1000, 1000));
        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        serverConnectionService.sendPacket(createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member", false, null));
        EasyMock.expect(serverConnectionService.sendPacket(EasyMock.<UserState>anyObject(), EasyMock.<Packet>anyObject())).andReturn(true);
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);
        guildService.kickGuildMember(userId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals(guildId, (int) getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild().getId());
        Assert.assertNull(getBaseAttributes(TEST_PLANET_1_ID, "member").getSimpleGuild());

        EasyMock.verify(serverConnectionService);
    }

    @Test
    @DirtiesContext
    public void baseUpdateClose() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member1");
        userService.getSimpleUser();
        createBase(new Index(1000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member2");
        userService.getSimpleUser();
        createBase(new Index(1500, 1500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        createBase(new Index(2000, 2000));
        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        guildService.inviteUserToGuild("member1");
        guildService.inviteUserToGuild("member2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join
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
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        serverConnectionService.sendPacket(createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "presi", false, null));
        EasyMock.expect(serverConnectionService.sendPacket(EasyMock.<UserState>anyObject(), EasyMock.<Packet>anyObject())).andReturn(true);
        serverConnectionService.sendPacket(createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member1", false, null));
        EasyMock.expect(serverConnectionService.sendPacket(EasyMock.<UserState>anyObject(), EasyMock.<Packet>anyObject())).andReturn(true);
        serverConnectionService.sendPacket(createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member2", false, null));
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);
        guildService.closeGuild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertNull(null, getBaseAttributes(TEST_PLANET_1_ID, "presi").getSimpleGuild());
        Assert.assertNull(null, getBaseAttributes(TEST_PLANET_1_ID, "member1").getSimpleGuild());
        Assert.assertNull(null, getBaseAttributes(TEST_PLANET_1_ID, "member2").getSimpleGuild());

        EasyMock.verify(serverConnectionService);
    }

    @Test
    @DirtiesContext
    public void sendBaseUpdateCreateGuildNoBase() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("presi"))).andReturn(true);
        EasyMock.expect(serverConnectionService.sendPacket(createUserStateMatcher("presi"), createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "presi", false, 1))).andReturn(true);
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);

        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConnectionService);

    }

    @Test
    @DirtiesContext
    public void sendBaseUpdateJoinAndLeaveGuildNoBase() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        createBase(new Index(1000, 1000));
        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("member"))).andReturn(true);
        EasyMock.expect(serverConnectionService.sendPacket(createUserStateMatcher("member"), createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member", false, 1))).andReturn(true);
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("member"))).andReturn(true);
        EasyMock.expect(serverConnectionService.sendPacket(createUserStateMatcher("member"), createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member", false, null))).andReturn(true);
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);
        guildService.joinGuild(guildId);
        guildService.leaveGuild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConnectionService);
    }

    @Test
    @DirtiesContext
    public void sendBaseUpdateKickMemberNoBase() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int userId = userService.getSimpleUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        createBase(new Index(1000, 1000));
        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("member"))).andReturn(true);
        EasyMock.expect(serverConnectionService.sendPacket(createUserStateMatcher("member"), createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member", false, null))).andReturn(true);
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);
        guildService.kickGuildMember(userId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConnectionService);
    }

    @Test
    @DirtiesContext
    public void sendBaseUpdateKickMemberNoBaseNoConnection() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        int userId = userService.getSimpleUser().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        createBase(new Index(1000, 1000));
        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("member"))).andReturn(false);
        EasyMock.expect(serverConnectionService.sendPacket(createUserStateMatcher("member"), createStorablePacket(StorablePacket.Type.GUILD_LOST))).andReturn(true);
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);
        guildService.kickGuildMember(userId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConnectionService);
    }

    @Test
    @DirtiesContext
    public void sendBaseUpdateCloseNoConnection() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create Guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        createAndLoginUser("presi");
        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertEquals(1, guildId);
        guildService.inviteUserToGuild("member1");
        guildService.inviteUserToGuild("member2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join
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
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("presi"))).andReturn(true);
        EasyMock.expect(serverConnectionService.sendPacket(createUserStateMatcher("presi"), createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "presi", false, null))).andReturn(true);
        EasyMock.expect(serverConnectionService.sendPacket(EasyMock.<UserState>anyObject(), EasyMock.<StorablePacket>anyObject())).andReturn(true);
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("member1"))).andReturn(true);
        EasyMock.expect(serverConnectionService.sendPacket(createUserStateMatcher("member1"), createBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, "member1", false, null))).andReturn(true);
        EasyMock.expect(serverConnectionService.sendPacket(EasyMock.<UserState>anyObject(), EasyMock.<StorablePacket>anyObject())).andReturn(true);
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("member2"))).andReturn(false);
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);
        guildService.closeGuild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConnectionService);
    }


    @Test
    @DirtiesContext
    public void surrenderBaseGuildUnregistered() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getOrCreateBase();
        waitForActionServiceDone(TEST_PLANET_1_ID);

        GuildService guildServiceMock = EasyMock.createStrictMock(GuildService.class);
        EasyMock.expect(guildServiceMock.getGuildId(EasyMock.<UserState>anyObject())).andReturn(null);
        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices, "guildService", guildServiceMock);
        EasyMock.replay(guildServiceMock);

        baseService.surrenderBase(baseService.getBase(simpleBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(guildServiceMock);
    }

    @Test
    @DirtiesContext
    public void surrenderBaseGuildRegistered() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        SimpleBase simpleBase = createBase(new Index(1000, 1000)); // Setup connection
        waitForActionServiceDone();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        guildService.createGuild("GuildName").getId();

        Thread.sleep(200); // Wait for packages
        ServerConnectionService serverConnectionService = EasyMock.createStrictMock(ServerConnectionService.class);
        serverConnectionService.sendPacket(createUnregisteredBaseChangedPacketMatcher(BaseChangedPacket.Type.CHANGED, simpleBase, true));
        EasyMock.expect(serverConnectionService.hasConnection(createUserStateMatcher("U1"))).andReturn(false);
        EasyMock.replay(serverConnectionService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerConnectionService(serverConnectionService);
        // Test
        baseService.surrenderBase(baseService.getBase(simpleBase));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertNull(getBaseAttributes(TEST_PLANET_1_ID, simpleBase).getSimpleGuild());

        EasyMock.verify(serverConnectionService);
    }

    @Test
    @DirtiesContext
    public void surrenderBaseGuildRegisteredHandleEnemies() throws Exception {
        configureSimplePlanetNoResources();
        SimpleBase simpleBase = setupBase4Destruction();

        // test
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(baseService.isAlive(simpleBase));
        loginUser("presi");
        baseService.surrenderBase(baseService.getBase(simpleBase));
        Thread.sleep(100); // Wait for action service to become active
        waitForActionServiceDone();
        Assert.assertFalse(baseService.isAlive(simpleBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void kickMemberHandleEnemies() throws Exception {
        configureSimplePlanetNoResources();
        SimpleBase simpleBase = setupBase4Destruction();

        // test
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(baseService.isAlive(simpleBase));
        loginUser("presi");
        guildService.kickGuildMember(userService.getUser("member").getId());
        Thread.sleep(100); // Wait for action service to become active
        waitForActionServiceDone();
        Assert.assertFalse(baseService.isAlive(simpleBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void leaveGuildHandleEnemies() throws Exception {
        configureSimplePlanetNoResources();
        SimpleBase simpleBase = setupBase4Destruction();

        // test
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(baseService.isAlive(simpleBase));
        loginUser("member");
        guildService.leaveGuild();
        Thread.sleep(100); // Wait for action service to become active
        waitForActionServiceDone();
        Assert.assertFalse(baseService.isAlive(simpleBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void closeGuildHandleEnemies() throws Exception {
        configureSimplePlanetNoResources();
        SimpleBase simpleBase = setupBase4Destruction();

        // test
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(baseService.isAlive(simpleBase));
        loginUser("presi");
        guildService.closeGuild();
        Thread.sleep(100); // Wait for action service to become active
        waitForActionServiceDone();
        Assert.assertFalse(baseService.isAlive(simpleBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private SimpleBase setupBase4Destruction() throws Exception {
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member");
        createBase(new Index(5000, 5000));
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5000, 5000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        SimpleBase simpleBase = createBase(new Index(1000, 1000)); // Setup connection
        waitForActionServiceDone();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("GuildName").getId();
        guildService.inviteUserToGuild("member");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Join guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member");
        guildService.joinGuild(guildId);
        sendMoveCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), new Index(1200, 1000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        return simpleBase;
    }

    @Test
    @DirtiesContext
    public void getGuildBases() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("member1_1");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase base1 = createBase(new Index(1000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        createAndLoginUser("member2_1");
        SimpleBase base2 = createBase(new Index(1500, 1500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_5_REAL_ID);
        createAndLoginUser("member3_2");
        SimpleBase base3 = createBase(new Index(1500, 1500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_5_REAL_ID);
        createAndLoginUser("member4");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // test & join
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi");
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        int guildId = guildService.createGuild("GuildName").getId();
        Assert.assertTrue(guildService.getGuildBases(userService.getUserState(), TEST_PLANET_1_ID).isEmpty());
        guildService.inviteUserToGuild("member1_1");
        guildService.inviteUserToGuild("member2_1");
        guildService.inviteUserToGuild("member3_2");
        guildService.inviteUserToGuild("member4");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // test & join
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member1_1");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member2_1");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member3_2");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member4");
        guildService.joinGuild(guildId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // test & join
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        Set<SimpleBase> simpleBases = guildService.getGuildBases(userService.getUserState(), TEST_PLANET_1_ID);
        Assert.assertEquals(2, simpleBases.size());
        Assert.assertTrue(simpleBases.contains(base1));
        Assert.assertTrue(simpleBases.contains(base2));
        Assert.assertFalse(simpleBases.contains(base3));
        simpleBases = guildService.getGuildBases(userService.getUserState(), TEST_PLANET_2_ID);
        Assert.assertEquals(1, simpleBases.size());
        Assert.assertFalse(simpleBases.contains(base1));
        Assert.assertFalse(simpleBases.contains(base2));
        Assert.assertTrue(simpleBases.contains(base3));
        simpleBases = guildService.getGuildBases(userService.getUserState(), TEST_PLANET_3_ID);
        Assert.assertTrue(simpleBases.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


    }

}
