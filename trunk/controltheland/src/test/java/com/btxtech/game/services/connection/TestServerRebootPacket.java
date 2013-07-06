package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.ServerRebootMessagePacket;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 03.04.2012
 * Time: 00:25:06
 */
public class TestServerRebootPacket extends AbstractServiceTest {
    @Autowired
    private ServerGlobalConnectionService connectionService;

    @Test
    @DirtiesContext
    public void testRealGame() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase(); // Setup connection
        Thread.sleep(1000); // Wait for the account balance package
        getPackagesIgnoreSyncItemInfoAndClear(false);
        connectionService.sendServerRebootMessage(1, 2);
        assertPackagesIgnoreSyncItemInfoAndClear(createServerRebootMessage(1, 2, 0));
        connectionService.sendServerRebootMessage(3, 4);
        connectionService.sendServerRebootMessage(5, 6);
        assertPackagesIgnoreSyncItemInfoAndClear(createServerRebootMessage(3, 4, 1), createServerRebootMessage(5, 6, 2));
        assertPackagesIgnoreSyncItemInfoAndClear();
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, null, null);
        Assert.assertEquals(3, messageIdPackets.size());
        TestMessageIdPacketQueue.assertRebootPacket(5, 6, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertRebootPacket(3, 4, messageIdPackets.get(1));
        TestMessageIdPacketQueue.assertRebootPacket(1, 2, messageIdPackets.get(2));
    }

    private ServerRebootMessagePacket createServerRebootMessage(int reboot, int downTime, int id) {
        ServerRebootMessagePacket serverRebootMessagePacket = new ServerRebootMessagePacket();
        serverRebootMessagePacket.setRebootInSeconds(reboot);
        serverRebootMessagePacket.setDownTimeInMinutes(downTime);
        serverRebootMessagePacket.setMessageId(id);
        return serverRebootMessagePacket;
    }
}
