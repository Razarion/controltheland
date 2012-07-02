package com.btxtech.game.services;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.ConnectionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 26.04.12
 * Time: 12:30
 */
public class ConnectionServiceTestHelper implements ConnectionService {
    private List<PacketEntry> packetEntries = new ArrayList<>();

    @Override
    public void clientLog(String message, Date date) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasConnection() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasConnection(SimpleBase simpleBase) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Connection getConnection() throws NoConnectionException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createConnection(Base base) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeConnection() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeConnection(SimpleBase simpleBase) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendPacket(SimpleBase base, Packet packet) {
        packetEntries.add(new PacketEntry(base, packet));
        System.out.println("SimpleBase: " + base + " Packet: " + packet);
    }

    @Override
    public void sendPacket(Packet packet) {
        packetEntries.add(new PacketEntry(null, packet));
        System.out.println("Packet: " + packet);
    }

    @Override
    public void sendSyncInfos(Collection<SyncBaseItem> syncItem) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ChatMessage> pollChatMessages(Integer lastMessageId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<SimpleBase> getOnlineBases() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendSyncInfo(SyncItem syncItem) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GameEngineMode getGameEngineMode() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void clearReceivedPackets() {
        packetEntries.clear();
    }

    public List<PacketEntry> getPacketEntries() {
        return packetEntries;
    }

    public List<PacketEntry> getPacketEntries(SimpleBase simpleBase, Class<? extends Packet> filter) {
        List<PacketEntry> result = new ArrayList<>();
        for (PacketEntry packetEntry : packetEntries) {
            if (filter.isAssignableFrom(packetEntry.getPacket().getClass()) && packetEntry.getSimpleBase() != null && packetEntry.getSimpleBase().equals(simpleBase)) {
                result.add(packetEntry);
            }
        }
        return result;
    }

    public List<PacketEntry> getPacketEntriesToAllBases(Class<? extends Packet> filter) {
        List<PacketEntry> result = new ArrayList<>();
        for (PacketEntry packetEntry : packetEntries) {
            if (filter.isAssignableFrom(packetEntry.getPacket().getClass()) && packetEntry.getSimpleBase() == null) {
                result.add(packetEntry);
            }
        }
        return result;
    }

    public class PacketEntry {
        private SimpleBase simpleBase;
        private Packet packet;

        public PacketEntry(SimpleBase simpleBase, Packet packet) {
            this.simpleBase = simpleBase;
            this.packet = packet;
        }

        public SimpleBase getSimpleBase() {
            return simpleBase;
        }

        public Packet getPacket() {
            return packet;
        }
    }
}
