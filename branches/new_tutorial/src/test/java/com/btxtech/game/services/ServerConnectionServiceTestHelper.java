package com.btxtech.game.services;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.connection.OnlineUserDTO;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 26.04.12
 * Time: 12:30
 */
public class ServerConnectionServiceTestHelper implements ServerConnectionService {
    private List<PacketEntry> packetEntries = new ArrayList<>();
    private List<MessageEntry> messageEntries = new ArrayList<>();
    private static Log log = LogFactory.getLog(ServerConnectionServiceTestHelper.class);

    @Override
    public boolean hasConnection(UserState userState) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createConnection(UserState userState, String startUuid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendPacket(SimpleBase base, Packet packet) {
        packetEntries.add(new PacketEntry(base, packet));
        log.error("SimpleBase: " + base + " Packet: " + packet);
    }

    @Override
    public void sendPacket(Packet packet) {
        packetEntries.add(new PacketEntry(null, packet));
        log.error("Packet to all: " + packet);
    }

    @Override
    public void sendMessage(SimpleBase simpleBase, String key, Object[] args, boolean showRegisterDialog) {
        messageEntries.add(new MessageEntry(simpleBase, key, args, showRegisterDialog));
        log.error("Packet to simpleBase: " + simpleBase + " key: " + key);
    }

    @Override
    public void setChatMessageFilter(UserState userState, ChatMessageFilter chatMessageFilter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendSyncInfos(Collection<SyncBaseItem> syncItem) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<OnlineUserDTO> getOnlineConnections() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendSyncInfo(SyncItem syncItem) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GameEngineMode getGameEngineMode() {
        return GameEngineMode.MASTER;
    }

    @Override
    public void activate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deactivate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLogout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(UserState userState, String key, Object[] args, boolean showRegisterDialog) {
        Assert.fail();
    }

    @Override
    public boolean sendPacket(UserState userState, Packet packet) {
        Assert.fail();
        return false;
    }

    public void clearReceivedPackets() {
        packetEntries.clear();
    }

    public List<PacketEntry> getPacketEntries() {
        return packetEntries;
    }

    public void clearMessageEntries() {
        messageEntries.clear();
    }

    public List<MessageEntry> getMessageEntries() {
        return messageEntries;
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

    public List<MessageEntry> getMessageEntries(SimpleBase simpleBase) {
        List<MessageEntry> result = new ArrayList<>();
        for (MessageEntry messageEntry : messageEntries) {
            if(messageEntry.getSimpleBase().equals(simpleBase)) {
                result.add(messageEntry);
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

    public class MessageEntry {
        private SimpleBase simpleBase;
        private String key;
        private Object[] args;
        private boolean showRegisterDialog;


        public MessageEntry(SimpleBase simpleBase, String key, Object[] args, boolean showRegisterDialog) {
            this.simpleBase = simpleBase;
            this.key = key;
            this.args = args;
            this.showRegisterDialog = showRegisterDialog;
        }

        public SimpleBase getSimpleBase() {
            return simpleBase;
        }

        public String getKey() {
            return key;
        }

        public Object[] getArgs() {
            return args;
        }

        public boolean isShowRegisterDialog() {
            return showRegisterDialog;
        }
    }
}