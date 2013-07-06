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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 26.04.12
 * Time: 12:30
 */
public class ServerConnectionServiceTestHelperNew implements ServerConnectionService {
    private List<PacketEntry> packetEntries = new ArrayList<>();
    private List<MessageEntry> messageEntries = new ArrayList<>();
    private static Log log = LogFactory.getLog(ServerConnectionServiceTestHelperNew.class);

    @Override
    public boolean hasConnection(UserState userState) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createConnection(UserState userState, String startUuid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendPacket(SimpleBase simpleBase, Packet packet) {
        log.error("Packet to SimpleBase: " + simpleBase + " " + packet);
    }

    @Override
    public void sendPacket(Packet packet) {
        packetEntries.add(new PacketEntry(null, packet));
        log.error("Packet to all: " + packet);
    }

    @Override
    public void sendMessage(SimpleBase simpleBase, String key, Object[] args, boolean showRegisterDialog) {
        log.error("Message to SimpleBase: " + simpleBase + " " + key + " " + args);
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
        messageEntries.add(new MessageEntry(userState, key, args, showRegisterDialog));
        log.error("Packet to UserState: " + userState + " key: " + key);
    }

    @Override
    public boolean sendPacket(UserState userState, Packet packet) {
        packetEntries.add(new PacketEntry(userState, packet));
        log.error("UserState: " + userState + " Packet: " + packet);
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

    public List<PacketEntry> getPacketEntries(UserState userState, Class<? extends Packet> filter) {
        List<PacketEntry> result = new ArrayList<>();
        for (PacketEntry packetEntry : packetEntries) {
            if (filter.isAssignableFrom(packetEntry.getPacket().getClass()) && packetEntry.getUserState() != null && packetEntry.getUserState().equals(userState)) {
                result.add(packetEntry);
            }
        }
        return result;
    }

    public List<PacketEntry> getPacketEntriesToAllBases(Class<? extends Packet> filter) {
        List<PacketEntry> result = new ArrayList<>();
        for (PacketEntry packetEntry : packetEntries) {
            if (filter.isAssignableFrom(packetEntry.getPacket().getClass()) && packetEntry.getUserState() == null) {
                result.add(packetEntry);
            }
        }
        return result;
    }

    public List<MessageEntry> getMessageEntries(UserState userState) {
        List<MessageEntry> result = new ArrayList<>();
        for (MessageEntry messageEntry : messageEntries) {
            if (messageEntry.getUserState().equals(userState)) {
                result.add(messageEntry);
            }
        }
        return result;
    }


    public class PacketEntry {
        private UserState userState;
        private Packet packet;

        public PacketEntry(UserState userState, Packet packet) {
            this.userState = userState;
            this.packet = packet;
        }

        public UserState getUserState() {
            return userState;
        }

        public Packet getPacket() {
            return packet;
        }
    }

    public class MessageEntry {
        private UserState userState;
        private String key;
        private Object[] args;
        private boolean showRegisterDialog;


        public MessageEntry(UserState userState, String key, Object[] args, boolean showRegisterDialog) {
            this.userState = userState;
            this.key = key;
            this.args = args;
            this.showRegisterDialog = showRegisterDialog;
        }

        public UserState getUserState() {
            return userState;
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