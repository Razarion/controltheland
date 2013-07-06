package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.user.UserState;
import junit.framework.Assert;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 03.05.12
 * Time: 11:22
 */
public class TestConnection extends AbstractServiceTest {
    private ServerGlobalServices serverGlobalServicesMock;

    @Before
    public void init() {
        Session sessionMock = EasyMock.createNiceMock(Session.class);
        EasyMock.expect(sessionMock.getSessionId()).andReturn("1234");
        MessageIdPacketQueue messageIdPacketQueueMock = EasyMock.createNiceMock(MessageIdPacketQueue.class);
        final Capture<Packet> capture = new Capture<>();
        EasyMock.expect(messageIdPacketQueueMock.convertPacketIfNecessary(EasyMock.capture(capture), EasyMock.<ChatMessageFilter>anyObject(), EasyMock.<UserState>anyObject())).andAnswer(new IAnswer<Packet>() {
            @Override
            public Packet answer() throws Throwable {
                return capture.getValue();
            }
        }).anyTimes();
        ServerGlobalConnectionService serverGlobalConnectionServiceMock = EasyMock.createNiceMock(ServerGlobalConnectionService.class);
        EasyMock.expect(serverGlobalConnectionServiceMock.getSession()).andReturn(sessionMock);
        EasyMock.expect(serverGlobalConnectionServiceMock.getMessageIdPacketQueue()).andReturn(messageIdPacketQueueMock);
        serverGlobalServicesMock = EasyMock.createNiceMock(ServerGlobalServices.class);
        EasyMock.expect(serverGlobalServicesMock.getServerGlobalConnectionService()).andReturn(serverGlobalConnectionServiceMock).anyTimes();
        EasyMock.replay(sessionMock, messageIdPacketQueueMock, serverGlobalConnectionServiceMock, serverGlobalServicesMock);
    }

    @Test
    @DirtiesContext
    public void noPendingPackets() {
        Connection connection = new Connection(new UserState(), null, serverGlobalServicesMock, null);
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
    }

    @Test
    @DirtiesContext
    public void pendingPackets() throws Exception {
        configureSimplePlanetNoResources();

        Connection connection = new Connection(new UserState(), null, serverGlobalServicesMock, null);
        SyncBaseItem attackItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1));
        connection.sendBaseSyncItem(attackItem);
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(112);
        connection.sendPacket(accountBalancePacket);

        List<Packet> packets = connection.getAndRemovePendingPackets(false);
        Assert.assertEquals(2, packets.size());
        Assert.assertTrue(packets.get(0) instanceof AccountBalancePacket);
        Assert.assertTrue(packets.get(1) instanceof SyncItemInfo);
    }

    @Test
    @DirtiesContext
    public void pendingPacketsResendLast() throws Exception {
        configureSimplePlanetNoResources();

        Connection connection = new Connection(new UserState(), null, serverGlobalServicesMock, null);
        SyncBaseItem attackItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1));
        connection.sendBaseSyncItem(attackItem);
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(112);
        connection.sendPacket(accountBalancePacket);

        List<Packet> packets = connection.getAndRemovePendingPackets(true);
        Assert.assertEquals(2, packets.size());
        Assert.assertTrue(packets.get(0) instanceof AccountBalancePacket);
        Assert.assertTrue(packets.get(1) instanceof SyncItemInfo);

        packets = connection.getAndRemovePendingPackets(true);
        Assert.assertEquals(2, packets.size());
        Assert.assertTrue(packets.get(0) instanceof AccountBalancePacket);
        Assert.assertTrue(packets.get(1) instanceof SyncItemInfo);

        packets = connection.getAndRemovePendingPackets(false);
        Assert.assertEquals(0, packets.size());
    }
}
