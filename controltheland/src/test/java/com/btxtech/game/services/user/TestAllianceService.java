package com.btxtech.game.services.user;

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
    private AllianceService allianceService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void addAllianceAndBreak() throws Exception {
        configureSimplePlanet();
        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        ((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()).setServerConnectionService(connectionServiceTestHelper);

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

        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertMessageInPackets(connectionServiceTestHelper, new SimpleBase(2, 1), "The user u1 has accepted your alliance");
        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1), "u2");
        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1), "u1");
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
        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));

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
        configureSimplePlanet();
        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        ((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()).setServerConnectionService(connectionServiceTestHelper);

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
        configureSimplePlanet();
        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        ((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()).setServerConnectionService(connectionServiceTestHelper);

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
        configureSimplePlanet();
        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        ((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()).setServerConnectionService(connectionServiceTestHelper);

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

        assertAlliancesInPackets(connectionServiceTestHelper, simpleBase1);
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

        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1), "u2");
        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(3, 1), "u1");
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(1, 1));
        assertNoMessagesInPackets(connectionServiceTestHelper, new SimpleBase(2, 1));
        assertBaseCreatedPacket(connectionServiceTestHelper, simpleBase3);

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
        configureSimplePlanet();

        ServerConnectionServiceTestHelper connectionServiceTestHelper = new ServerConnectionServiceTestHelper();
        ((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()).setServerConnectionService(connectionServiceTestHelper);

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
        configureSimplePlanet();

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
        for (BaseAttributes baseAttributes : myBaseAttributes.getAlliances()) {
            if (Arrays.binarySearch(allianceNames, baseAttributes.getName()) == -1) {
                Assert.fail("Alliance does not exits: " + baseAttributes.getName());
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

    private AllianceOfferPacket createAllianceOfferPacket(String userName) {
        AllianceOfferPacket allianceOfferPacket = new AllianceOfferPacket();
        allianceOfferPacket.setActorUserName(userName);
        return allianceOfferPacket;
    }

    private void assertAlliancesInPackets(ServerConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase myBase, String... allianceNames) {
        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntries(myBase, BaseChangedPacket.class);
        Assert.assertEquals(1, packets.size());
        BaseChangedPacket baseChangedPacket = (BaseChangedPacket) packets.get(0).getPacket();
        Assert.assertEquals(BaseChangedPacket.Type.CHANGED, baseChangedPacket.getType());
        Assert.assertEquals(allianceNames.length, baseChangedPacket.getBaseAttributes().getAlliances().size());
        for (BaseAttributes baseAttributes : baseChangedPacket.getBaseAttributes().getAlliances()) {
            if (Arrays.binarySearch(allianceNames, baseAttributes.getName()) == -1) {
                Assert.fail("Alliance does not exits: " + baseAttributes.getName());
            }
        }
    }

    private void assertBaseDeletedPacket(ServerConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase deletedBase) {
        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(1, packets.size());
        BaseChangedPacket baseChangedPacket = (BaseChangedPacket) packets.get(0).getPacket();
        Assert.assertEquals(BaseChangedPacket.Type.REMOVED, baseChangedPacket.getType());
        Assert.assertEquals(deletedBase, baseChangedPacket.getBaseAttributes().getSimpleBase());
    }

    private void assertBaseCreatedPacket(ServerConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase createdBase) {
        List<ServerConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntriesToAllBases(BaseChangedPacket.class);
        Assert.assertEquals(1, packets.size());
        BaseChangedPacket baseChangedPacket = (BaseChangedPacket) packets.get(0).getPacket();
        Assert.assertEquals(BaseChangedPacket.Type.CREATED, baseChangedPacket.getType());
        Assert.assertEquals(createdBase, baseChangedPacket.getBaseAttributes().getSimpleBase());
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
