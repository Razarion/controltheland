package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.packets.AllianceOfferPacket;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.ServerConnectionServiceTestHelper;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;

/**
 * User: beat
 * Date: 24.04.12
 * Time: 21:50
 */
public class TestAllianceService extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private AllianceService allianceService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void addAllianceAndBreak() throws Exception {
        configureSimplePlanetNoResources();
        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        overrideConnectionService(((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()), connectionServiceTestHelper);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        SimpleBase simpleBase1 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        SimpleBase simpleBase2 = getMyBase();
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        connectionServiceTestHelper.clearReceivedPackets();
        allianceService.proposeAlliance(simpleBase1);
        verifyAllianceOffers();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers("u2");
        verifyAlliances();
        verifyAlliancesFromUser();
        connectionServiceTestHelper.clearReceivedPackets();
        allianceService.acceptAllianceOffer("u2");
        verifyAllianceOffers();
        verifyAlliances("u2");
        verifyAlliancesFromUser("u2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoMessagesInPackets(connectionServiceTestHelper, simpleBase1);
        assertMessageInPackets(connectionServiceTestHelper, simpleBase2, "The user u1 has accepted your alliance");
        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(2, packets.size());
        assertAlliancesInPacketToAll(packets, simpleBase1, "u2");
        assertAlliancesInPacketToAll(packets, simpleBase2, "u1");
        connectionServiceTestHelper.clearReceivedPackets();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances("u2");
        verifyAlliancesFromUser("u2");
        allianceService.breakAlliance("u2");
        verifyAlliances();
        verifyAlliancesFromUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertMessageInPackets(connectionServiceTestHelper, new SimpleBase(2, 1), "The user u1 has broken the alliance");
        packets = connectionServiceTestHelper.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(2, packets.size());
        assertAlliancesInPacketToAll(packets, simpleBase1);
        assertAlliancesInPacketToAll(packets, simpleBase2);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void rejectAlliance() throws Exception {
        configureSimplePlanetNoResources();
        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        overrideConnectionService(((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()), connectionServiceTestHelper);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        SimpleBase simpleBase1 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        connectionServiceTestHelper.clearReceivedPackets();
        allianceService.proposeAlliance(simpleBase1);
        verifyAllianceOffers();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers("u2");
        verifyAlliances();
        verifyAlliancesFromUser();
        connectionServiceTestHelper.clearReceivedPackets();
        allianceService.rejectAllianceOffer("u2");
        verifyAllianceOffers();
        verifyAlliancesFromUser();
        verifyAlliances();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertMessageInPackets(connectionServiceTestHelper, new SimpleBase(2, 1), "The user u1 has rejected your alliance");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void addAllianceBothUnregistered() throws Exception {
        configureSimplePlanetNoResources();
        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        overrideConnectionService(((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()), connectionServiceTestHelper);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        verifyAllianceOffers();
        verifyAlliances();
        SimpleBase simpleBase1 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        verifyAllianceOffers();
        verifyAlliances();
        allianceService.proposeAlliance(simpleBase1);
        verifyAllianceOffers();
        verifyAlliances();
        try {
            verifyAlliancesFromUser();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertMessageInPackets(connectionServiceTestHelper, new SimpleBase(2, 1), "Only registered user can form alliances.");
    }

    @Test
    @DirtiesContext
    public void createDeleteBase() throws Exception {
        configureSimplePlanetNoResources();
        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        overrideConnectionService(((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()), connectionServiceTestHelper);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        SimpleBase simpleBase1 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        SimpleBase simpleBase2 = getMyBase();
        allianceService.proposeAlliance(simpleBase1);
        verifyAllianceOffers();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers("u2");
        verifyAlliances();
        verifyAlliancesFromUser();
        allianceService.acceptAllianceOffer("u2");
        verifyAllianceOffers();
        verifyAlliances("u2");
        verifyAlliancesFromUser("u2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertMessageInPackets(connectionServiceTestHelper, new SimpleBase(2, 1), "The user u1 has accepted your alliance");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances("u1");
        verifyAlliancesFromUser("u1");
        connectionServiceTestHelper.clearReceivedPackets();
        getMovableService().sellItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(2, packets.size()); // REMOVE and CHANGE packet
        assertAlliancesInPacketToAll(packets, simpleBase1);
        assertBaseDeletedPacket(connectionServiceTestHelper, simpleBase2);
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));
        connectionServiceTestHelper.clearReceivedPackets();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser("u2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances("u1");
        verifyAlliancesFromUser("u1");
        SimpleBase simpleBase3 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        packets = connectionServiceTestHelper.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(3, packets.size());
        Assert.assertEquals(BaseChangedPacket.Type.CREATED, ((BaseChangedPacket) packets.get(0).getPacket()).getType());
        Assert.assertEquals(simpleBase3, ((BaseChangedPacket) packets.get(0).getPacket()).getBaseAttributes().getSimpleBase());
        Assert.assertEquals(BaseChangedPacket.Type.CHANGED, ((BaseChangedPacket) packets.get(1).getPacket()).getType());
        Assert.assertEquals(BaseChangedPacket.Type.CHANGED, ((BaseChangedPacket) packets.get(2).getPacket()).getType());

        assertAlliancesInPacketToAll(packets, simpleBase1, "u2");
        assertAlliancesInPacketToAll(packets, simpleBase3, "u1");

        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances("u2");
        verifyAlliancesFromUser("u2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void addAlliancePartnerUnregistered() throws Exception {
        configureSimplePlanetNoResources();

        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        overrideConnectionService(((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()), connectionServiceTestHelper);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        verifyAllianceOffers();
        verifyAlliances();
        SimpleBase simpleBase1 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        SimpleBase simpleBase2 = getMyBase();
        allianceService.proposeAlliance(simpleBase1);
        verifyAllianceOffers();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertMessageInPackets(connectionServiceTestHelper, simpleBase1, "u2 offers you an alliance. Only registered user can form alliances.");
        assertMessageInPackets(connectionServiceTestHelper, simpleBase2, "The player 'Base 1' is not registered. Only registered user can form alliances. Use the chat to persuade him to register!");

    }

    @Test
    @DirtiesContext
    public void backupRestore() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        SimpleBase simpleBase1 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        allianceService.proposeAlliance(simpleBase1);
        verifyAllianceOffers();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.restore(mgmtService.getBackupSummary().get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers("u2");
        verifyAlliancesFromUser();
        verifyAlliances();
        allianceService.acceptAllianceOffer("u2");
        verifyAllianceOffers();
        verifyAlliancesFromUser("u2");
        verifyAlliances("u2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.restore(mgmtService.getBackupSummary().get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances("u2");
        verifyAlliancesFromUser("u2");
        allianceService.breakAlliance("u2");
        verifyAlliances();
        verifyAlliancesFromUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.restore(mgmtService.getBackupSummary().get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.restore(mgmtService.getBackupSummary().get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        verifyAllianceOffers();
        verifyAlliances();
        verifyAlliancesFromUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createDeleteBaseDifferentPlanets() throws Exception {
        configureMultiplePlanetsAndLevels();
        ServerConnectionServiceTestHelper connectionServiceTestHelperP1 = new ServerConnectionServiceTestHelper();
        overrideConnectionService(((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()), connectionServiceTestHelperP1);
        ServerConnectionServiceTestHelper connectionServiceTestHelperP2 = new ServerConnectionServiceTestHelper();
        overrideConnectionService(((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_2_ID).getPlanetServices()), connectionServiceTestHelperP2);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1p2", "xxx", "xxx", "");
        userService.login("u1p2", "xxx");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase simpleBase1P1 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2p1", "xxx", "xxx", "");
        userService.login("u2p1", "xxx");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase simpleBase2P1 = getMyBase();
        allianceService.proposeAlliance(simpleBase1P1);
        verifyAllianceOffers();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U1 promote give up base on planet 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1p2", "xxx");
        allianceService.acceptAllianceOffer("u2p1");
        connectionServiceTestHelperP1.clearReceivedPackets();
        connectionServiceTestHelperP2.clearReceivedPackets();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_5_REAL_ID);
        getMovableService().surrenderBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelperP1.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(3, packets.size());
        Assert.assertEquals("Base 1", (((BaseChangedPacket) packets.get(0).getPacket()).getBaseAttributes()).getName());
        Assert.assertEquals(BaseChangedPacket.Type.CHANGED, (((BaseChangedPacket) packets.get(0).getPacket()).getType()));
        Assert.assertTrue(getBaseChangedPacket(packets.subList(1, 3), simpleBase1P1).getAlliances().isEmpty());
        Assert.assertTrue(getBaseChangedPacket(packets.subList(1, 3), simpleBase2P1).getAlliances().isEmpty());

        // U1 Move to next planet
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        connectionServiceTestHelperP1.clearReceivedPackets();
        connectionServiceTestHelperP2.clearReceivedPackets();
        userService.login("u1p2", "xxx");
        SimpleBase simpleBase1P2 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        packets = connectionServiceTestHelperP1.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(1, packets.size());
        Assert.assertEquals(BaseChangedPacket.Type.CHANGED, ((BaseChangedPacket) packets.get(0).getPacket()).getType());
        Assert.assertTrue(((BaseChangedPacket) packets.get(0).getPacket()).getBaseAttributes().getAlliances().isEmpty());

        packets = connectionServiceTestHelperP2.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(2, packets.size());
        Assert.assertEquals(BaseChangedPacket.Type.CREATED, ((BaseChangedPacket) packets.get(0).getPacket()).getType());
        Assert.assertEquals("u1p2", ((BaseChangedPacket) packets.get(0).getPacket()).getBaseAttributes().getName());
        Assert.assertEquals(simpleBase1P2, ((BaseChangedPacket) packets.get(0).getPacket()).getBaseAttributes().getSimpleBase());
        Assert.assertEquals(BaseChangedPacket.Type.CHANGED, ((BaseChangedPacket) packets.get(1).getPacket()).getType());
        Assert.assertTrue(((BaseChangedPacket) packets.get(1).getPacket()).getBaseAttributes().getAlliances().isEmpty());

        // U2 promote give up base on planet 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2p1", "xxx");
        connectionServiceTestHelperP1.clearReceivedPackets();
        connectionServiceTestHelperP2.clearReceivedPackets();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_5_REAL_ID);
        getMovableService().surrenderBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        packets = connectionServiceTestHelperP1.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(2, packets.size());
        Assert.assertEquals("Base 2", (((BaseChangedPacket) packets.get(0).getPacket()).getBaseAttributes()).getName());
        Assert.assertEquals(BaseChangedPacket.Type.CHANGED, (((BaseChangedPacket) packets.get(0).getPacket()).getType()));
        Assert.assertTrue(getBaseChangedPacket(packets.subList(1, 2), simpleBase2P1).getAlliances().isEmpty());

        packets = connectionServiceTestHelperP2.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(1, packets.size());
        Assert.assertEquals(BaseChangedPacket.Type.CHANGED, ((BaseChangedPacket) packets.get(0).getPacket()).getType());
        Assert.assertTrue(getBaseChangedPacket(packets, simpleBase1P2).getAlliances().isEmpty());

        // U1 Move to next planet
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        connectionServiceTestHelperP1.clearReceivedPackets();
        connectionServiceTestHelperP2.clearReceivedPackets();
        userService.login("u2p1", "xxx");
        SimpleBase simpleBase2P2 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        packets = connectionServiceTestHelperP1.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(0, packets.size());

        packets = connectionServiceTestHelperP2.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(3, packets.size());
        Assert.assertEquals(BaseChangedPacket.Type.CREATED, ((BaseChangedPacket) packets.get(0).getPacket()).getType());
        Assert.assertEquals("u2p1", ((BaseChangedPacket) packets.get(0).getPacket()).getBaseAttributes().getName());
        Assert.assertEquals(simpleBase2P2, ((BaseChangedPacket) packets.get(0).getPacket()).getBaseAttributes().getSimpleBase());
        Assert.assertEquals(1, getBaseChangedPacket(packets.subList(1, 3), simpleBase1P2).getAlliances().size());
        Assert.assertTrue(getBaseChangedPacket(packets.subList(1, 3), simpleBase1P2).isAlliance(simpleBase2P2));
        Assert.assertEquals(1, getBaseChangedPacket(packets.subList(1, 3), simpleBase2P2).getAlliances().size());
        Assert.assertTrue(getBaseChangedPacket(packets.subList(1, 3), simpleBase2P2).isAlliance(simpleBase1P2));
    }

    @Test
    @DirtiesContext
    public void breakAllianceAndAttack() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        SimpleBase simpleBase1 = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        SimpleBase simpleBase2 = getMyBase();
        sendMoveCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000));
        waitForActionServiceDone();
        allianceService.proposeAlliance(simpleBase1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        allianceService.acceptAllianceOffer("u2");
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(300, 300), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendMoveCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), new Index(1800, 2000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        allianceService.breakAlliance("u1");
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
        waitForActionServiceDone();
        // TODO failed on 05.12.2012
        assertWholeItemCount(TEST_PLANET_1_ID, 3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertFalse(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().isAlive(simpleBase2));
    }

    private BaseAttributes getBaseChangedPacket(List<ServerConnectionServiceTestHelper.PacketEntry> packets, SimpleBase simpleBase) {
        for (ServerConnectionServiceTestHelper.PacketEntry packet : packets) {
            if (((BaseChangedPacket) packet.getPacket()).getBaseAttributes().getSimpleBase().equals(simpleBase)) {
                return ((BaseChangedPacket) packet.getPacket()).getBaseAttributes();
            }
        }
        Assert.fail("No BaseAttributes in packet found for base: " + simpleBase);
        return null; // unreachable
    }

    private void verifyAlliancesFromUser(String... allianceNames) throws InvalidLevelStateException {
        Assert.assertEquals(allianceNames.length, allianceService.getAllAlliances().size());
        for (String alliance : allianceService.getAllAlliances()) {
            if (Arrays.binarySearch(allianceNames, alliance) == -1) {
                Assert.fail("Alliance does not exits: " + alliance);
            }
        }
    }

    private void verifyAlliances(String... allianceNames) throws InvalidLevelStateException {
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1);
        SimpleBase myBase = realGameInfo.getBase();
        for (BaseAttributes baseAttributes : realGameInfo.getAllBase()) {
            if (baseAttributes.getSimpleBase().equals(myBase)) {
                verifyAlliances(baseAttributes, allianceNames);
                return;
            }
        }
        Assert.fail("Own base not found");
    }

    private void verifyAlliances(BaseAttributes myBaseAttributes, String... allianceNames) {
        Assert.assertEquals(allianceNames.length, myBaseAttributes.getAlliances().size());
        for (SimpleBase allianceBase : myBaseAttributes.getAlliances()) {
            String allianceBaseName = planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices().getBaseService().getBaseName(allianceBase);
            if (Arrays.binarySearch(allianceNames, allianceBaseName) == -1) {
                Assert.fail("Alliance does not exits: " + allianceBaseName);
            }
        }
    }

    private void verifyAllianceOffers(String... allianceOfferNames) throws InvalidLevelStateException {
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1);
        Assert.assertEquals(allianceOfferNames.length, realGameInfo.getAllianceOffers().size());
        for (AllianceOfferPacket allianceOfferPacket : realGameInfo.getAllianceOffers()) {
            if (Arrays.binarySearch(allianceOfferNames, allianceOfferPacket.getActorUserName()) == -1) {
                Assert.fail("Alliance does not exits: " + allianceOfferPacket.getActorUserName());
            }
        }
    }

    private void assertAlliancesInPacketToAll(List<ServerConnectionServiceTestHelper.PacketEntry> packets, SimpleBase simpleBase, String... allianceNames) {
        for (ServerConnectionServiceTestHelper.PacketEntry packet : packets) {
            BaseChangedPacket baseChangedPacket = (BaseChangedPacket) packet.getPacket();
            if (!baseChangedPacket.getBaseAttributes().getSimpleBase().equals(simpleBase)) {
                continue;
            }
            if (baseChangedPacket.getType() != BaseChangedPacket.Type.CHANGED) {
                continue;
            }
            Assert.assertEquals(allianceNames.length, baseChangedPacket.getBaseAttributes().getAlliances().size());
            for (SimpleBase allianceBase : baseChangedPacket.getBaseAttributes().getAlliances()) {
                String allianceBaseName = planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices().getBaseService().getBaseName(allianceBase);
                if (Arrays.binarySearch(allianceNames, allianceBaseName) == -1) {
                    Assert.fail("Alliance does not exits: " + allianceBaseName);
                }
            }
            return;
        }
        Assert.fail("No BaseChangedPacket for base found: " + simpleBase);
    }

    private void assertBaseDeletedPacket(ServerConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase deletedBase) {
        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntriesToAllBases(BaseChangedPacket.class);
        for (ServerConnectionServiceTestHelper.PacketEntry packet : packets) {
            BaseChangedPacket baseChangedPacket = (BaseChangedPacket) packet.getPacket();
            if (baseChangedPacket.getType() == BaseChangedPacket.Type.REMOVED) {
                Assert.assertEquals(deletedBase, baseChangedPacket.getBaseAttributes().getSimpleBase());
                return;
            }
        }
        Assert.fail("No delete BaseChangedPacket for base found");
    }

    private void assertMessageInPackets(ServerConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase myBase, String messages) {
        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntries(myBase, Message.class);
        Assert.assertEquals(1, packets.size());
        Message messagePacket = (Message) packets.get(0).getPacket();
        Assert.assertEquals(messages, messagePacket.getMessage());
    }

    private void assertNoAlliancesInPackets(ServerConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase myBase) {
        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntries(myBase, BaseChangedPacket.class);
        Assert.assertEquals(0, packets.size());
    }

    private void assertNoMessagesInPackets(ServerConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase myBase) {
        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntries(myBase, Message.class);
        Assert.assertEquals(0, packets.size());
    }

}
