package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.AllianceOfferPacket;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.ConnectionServiceTestHelper;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.base.impl.BaseServiceImpl;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.user.impl.AllianceServiceImpl;
import org.easymock.EasyMock;
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
    private BaseService baseService;

    @Test
    @DirtiesContext
    public void addAllianceAndBreak() throws Exception {
        ConnectionService mockConnectionService = EasyMock.createStrictMock(ConnectionService.class);
        mockConnectionService.sendPacket(new SimpleBase(1), createAllianceOfferPacket("u2"));
        mockConnectionService.sendPacket(new SimpleBase(2), createMessage("The user u1 has accepted your alliance", false));
        mockConnectionService.sendPacket(new SimpleBase(2), createMessage("The user u1 has broken the alliance", false));
        EasyMock.replay(mockConnectionService);
        setPrivateField(AllianceServiceImpl.class, allianceService, "connectionService", mockConnectionService);

        ConnectionServiceTestHelper connectionServiceTestHelper = new ConnectionServiceTestHelper();
        setPrivateField(BaseServiceImpl.class, baseService, "connectionService", connectionServiceTestHelper);

        configureRealGame();

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

        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1));
        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2));

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

        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1), "u2");
        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2), "u1");
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

        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1));
        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2));

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
        ConnectionService mockConnectionService = EasyMock.createStrictMock(ConnectionService.class);
        mockConnectionService.sendPacket(new SimpleBase(1), createAllianceOfferPacket("u2"));
        mockConnectionService.sendPacket(new SimpleBase(2), createMessage("The user u1 has rejected your alliance", false));
        EasyMock.replay(mockConnectionService);
        setPrivateField(AllianceServiceImpl.class, allianceService, "connectionService", mockConnectionService);

        ConnectionServiceTestHelper connectionServiceTestHelper = new ConnectionServiceTestHelper();
        setPrivateField(BaseServiceImpl.class, baseService, "connectionService", connectionServiceTestHelper);

        configureRealGame();

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

        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1));
        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2));

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

        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1));
        assertNoAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(2));

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
        ConnectionService mockAllianceConnectionService = EasyMock.createStrictMock(ConnectionService.class);
        mockAllianceConnectionService.sendPacket(new SimpleBase(2), createMessage("Only registered user can form alliances.", true));
        setPrivateField(AllianceServiceImpl.class, allianceService, "connectionService", mockAllianceConnectionService);
        EasyMock.replay(mockAllianceConnectionService);


        configureRealGame();

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
    }

    @Test
    @DirtiesContext
    public void createDeleteBase() throws Exception {
        ConnectionService mockAllianceConnectionService = EasyMock.createStrictMock(ConnectionService.class);
        mockAllianceConnectionService.sendPacket(new SimpleBase(1), createAllianceOfferPacket("u2"));
        mockAllianceConnectionService.sendPacket(new SimpleBase(2), createMessage("The user u1 has accepted your alliance", false));
        mockAllianceConnectionService.sendPacket(new SimpleBase(2), createMessage("The user u1 has broken the alliance", false));
        setPrivateField(AllianceServiceImpl.class, allianceService, "connectionService", mockAllianceConnectionService);
        EasyMock.replay(mockAllianceConnectionService);

        ConnectionServiceTestHelper connectionServiceTestHelper = new ConnectionServiceTestHelper();
        setPrivateField(BaseServiceImpl.class, baseService, "connectionService", connectionServiceTestHelper);

        configureRealGame();

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

        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1));
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
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(1), "u2");
        assertAlliancesInPackets(connectionServiceTestHelper, new SimpleBase(3), "u1");

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
        ConnectionService mockConnectionService = EasyMock.createStrictMock(ConnectionService.class);
        mockConnectionService.sendPacket(new SimpleBase(1), createMessage("u2 offers you an alliance.<br />Only registered user can form alliances.", true));
        mockConnectionService.sendPacket(new SimpleBase(2), createMessage("The player Base 1 is not registered.<br />Only registered user can form alliances.<br />Use the chat to persuade him to register!", false));
        EasyMock.replay(mockConnectionService);
        setPrivateField(AllianceServiceImpl.class, allianceService, "connectionService", mockConnectionService);

        configureRealGame();

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
        allianceService.proposeAlliance(simpleBase1);
        verifyAllianceOffers();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void backupRestore() throws Exception {
        configureRealGame();

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

    private void verifyAlliancesFromUser(String... allianceNames) throws InvalidLevelState {
        Assert.assertEquals(allianceNames.length, allianceService.getAllAlliances().size());
        for (String alliance : allianceService.getAllAlliances()) {
            if (Arrays.binarySearch(allianceNames, alliance) == -1) {
                Assert.fail("Alliance does not exits: " + alliance);
            }
        }
    }

    private void verifyAlliances(String... allianceNames) throws InvalidLevelState {
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo();
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

    private void verifyAllianceOffers(String... allianceOfferNames) throws InvalidLevelState {
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo();
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

    private void assertAlliancesInPackets(ConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase myBase, String... allianceNames) {
        List<ConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntries(myBase, BaseChangedPacket.class);
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

    private void assertNoAlliancesInPackets(ConnectionServiceTestHelper connectionServiceTestHelper, SimpleBase myBase) {
        List<ConnectionServiceTestHelper.PacketEntry> packets = connectionServiceTestHelper.getPacketEntries(myBase, BaseChangedPacket.class);
        Assert.assertEquals(0, packets.size());
    }
}
