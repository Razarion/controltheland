package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.ChatMessage;
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
 * Date: 02.04.2012
 * Time: 23:44:18
 */
public class TestMessageIdPacketQueue extends AbstractServiceTest {
    @Autowired
    private MessageIdPacketQueue messageIdPacketQueue;

    @Test
    @DirtiesContext
    public void empty() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(null, ChatMessageFilter.GLOBAL).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(null, ChatMessageFilter.GUILD).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(0, ChatMessageFilter.GLOBAL).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(0, ChatMessageFilter.GUILD).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(1, ChatMessageFilter.GLOBAL).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(1, ChatMessageFilter.GUILD).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(100, ChatMessageFilter.GLOBAL).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(100, ChatMessageFilter.GUILD).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void multipleMessages() throws Exception {
        configureSimplePlanetNoResources();

        int guildId = createGuildAnd2Users();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        loginUser("member1");
        // Preparation
        addChatMessage("m1", "member1", getUserId(), null);
        addRebootPacket(1, 2);
        addChatMessage("m2", "member1", getUserId(), guildId);
        addChatMessage("m3", "member1", getUserId(), null);
        addRebootPacket(3, 4);
        addChatMessage("m4", "member1", getUserId(), guildId);
        // Guild no message id
        List<MessageIdPacket> messageIdPackets = messageIdPacketQueue.peekMessages(null, ChatMessageFilter.GUILD);
        Assert.assertEquals(4, messageIdPackets.size());
        assertChatMessage("m4", "member1", getUserId(), guildId, ChatMessage.Type.OWN, messageIdPackets.get(0));
        assertRebootPacket(3, 4, messageIdPackets.get(1));
        assertChatMessage("m2", "member1", getUserId(), guildId, ChatMessage.Type.OWN, messageIdPackets.get(2));
        assertRebootPacket(1, 2, messageIdPackets.get(3));
        int rebootId = messageIdPackets.get(3).getMessageId();
        // Global no message id
        messageIdPackets = messageIdPacketQueue.peekMessages(null, ChatMessageFilter.GLOBAL);
        Assert.assertEquals(4, messageIdPackets.size());
        assertRebootPacket(3, 4, messageIdPackets.get(0));
        assertChatMessage("m3", "member1", getUserId(), null, ChatMessage.Type.OWN, messageIdPackets.get(1));
        assertRebootPacket(1, 2, messageIdPackets.get(2));
        assertChatMessage("m1", "member1", getUserId(), null, ChatMessage.Type.OWN, messageIdPackets.get(3));
        // Guild and message id
        messageIdPackets = messageIdPacketQueue.peekMessages(rebootId, ChatMessageFilter.GUILD);
        Assert.assertEquals(3, messageIdPackets.size());
        assertChatMessage("m4", "member1", getUserId(), guildId, ChatMessage.Type.OWN, messageIdPackets.get(0));
        assertRebootPacket(3, 4, messageIdPackets.get(1));
        assertChatMessage("m2", "member1", getUserId(), guildId, ChatMessage.Type.OWN, messageIdPackets.get(2));
        // Global and message id
        messageIdPackets = messageIdPacketQueue.peekMessages(rebootId, ChatMessageFilter.GLOBAL);
        Assert.assertEquals(2, messageIdPackets.size());
        assertRebootPacket(3, 4, messageIdPackets.get(0));
        assertChatMessage("m3", "member1", getUserId(), null, ChatMessage.Type.OWN, messageIdPackets.get(1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void queueSize() throws Exception {
        configureSimplePlanetNoResources();

        int guildId = createGuildAnd2Users();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member1");

        for (int i = 0; i < 17; i++) {
            addChatMessage("m" + i, "user", getUserId(), null);
        }

        for (int i = 0; i < 3; i++) {
            addRebootPacket(i, 2);
        }

        for (int i = 0; i < 50; i++) {
            addChatMessage("m" + i, "user", getUserId(), guildId);
        }


        List<MessageIdPacket> messageIdPackets = messageIdPacketQueue.peekMessages(null, ChatMessageFilter.GLOBAL);
        Assert.assertEquals(7, messageIdPackets.size());
        for (int i = 0; i < 3; i++) {
            assertRebootPacket(2 - i, 2, messageIdPackets.get(i));
        }
        for (int i = 3; i < 7; i++) {
            assertChatMessage("m" + (19 - i), "user", getUserId(), null, ChatMessage.Type.OWN, messageIdPackets.get(i));
        }

        messageIdPackets = messageIdPacketQueue.peekMessages(null, ChatMessageFilter.GUILD);
        Assert.assertEquals(14, messageIdPackets.size());
        for (int i = 0; i < 11; i++) {
            assertChatMessage("m" + (49 - i), "user", getUserId(), guildId, ChatMessage.Type.OWN, messageIdPackets.get(i));
        }
        for (int i = 11; i < 14; i++) {
            assertRebootPacket(13 - i, 2, messageIdPackets.get(i));
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void convertPacketIfNecessary() throws Exception {
        configureSimplePlanetNoResources();
        // Other package
        Assert.assertNotNull(messageIdPacketQueue.convertPacketIfNecessary(new AccountBalancePacket(), null, null));
        // Guild
        int guildId = createGuildAnd2Users();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member1");
        int userId = getUserId();
        // Same guild
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setGuildId(guildId);
        Assert.assertNotNull(messageIdPacketQueue.convertPacketIfNecessary(chatMessage, ChatMessageFilter.GUILD, getUserState()));
        // Different guild
        chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setGuildId(guildId + 1);
        Assert.assertNull(messageIdPacketQueue.convertPacketIfNecessary(chatMessage, ChatMessageFilter.GUILD, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void addChatMessage(String message, String name, Integer userId, Integer guildId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setName(name);
        chatMessage.setUserId(userId);
        chatMessage.setGuildId(guildId);
        messageIdPacketQueue.initAndPutMessage(chatMessage);
    }

    private void addRebootPacket(int reboot, int downTime) {
        ServerRebootMessagePacket serverRebootMessagePacket = new ServerRebootMessagePacket();
        serverRebootMessagePacket.setRebootInSeconds(reboot);
        serverRebootMessagePacket.setDownTimeInMinutes(downTime);
        messageIdPacketQueue.initAndPutMessage(serverRebootMessagePacket);
    }

    public static void assertChatMessage(String message, String name, Integer userId, Integer guildId, ChatMessage.Type type, MessageIdPacket messageIdPacket) {
        ChatMessage chatMessage = (ChatMessage) messageIdPacket;
        Assert.assertEquals(message, chatMessage.getMessage());
        Assert.assertEquals(name, chatMessage.getName());
        Assert.assertEquals(userId, chatMessage.getUserId());
        Assert.assertEquals(guildId, chatMessage.getGuildId());
        Assert.assertEquals(type, chatMessage.getType());
    }

    public static void assertRebootPacket(int reboot, int downTime, MessageIdPacket messageIdPacket) {
        ServerRebootMessagePacket serverRebootMessagePacket = (ServerRebootMessagePacket) messageIdPacket;
        Assert.assertEquals(reboot, serverRebootMessagePacket.getRebootInSeconds());
        Assert.assertEquals(downTime, serverRebootMessagePacket.getDownTimeInMinutes());
    }

}
