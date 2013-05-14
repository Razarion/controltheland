package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserState;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 03.05.12
 * Time: 11:22
 */
public class TestConnection extends AbstractServiceTest {
    @Test
    @DirtiesContext
    public void noPendingPackets() {
        Connection connection = new Connection(new UserState(), null, "1234", null);
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
    }

    @Test
    @DirtiesContext
    public void pendingPackets() throws Exception {
        configureSimplePlanetNoResources();

        Connection connection = new Connection(new UserState(), null, "1234", null);
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

        Connection connection = new Connection(new UserState(), null, "1234", null);
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
