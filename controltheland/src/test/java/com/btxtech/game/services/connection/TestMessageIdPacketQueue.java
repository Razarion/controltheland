package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.ServerRebootMessagePacket;
import com.btxtech.game.services.connection.impl.MessageIdPacketQueue;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * User: beat
 * Date: 02.04.2012
 * Time: 23:44:18
 */
public class TestMessageIdPacketQueue {

    @Test
    public void empty() {
        MessageIdPacketQueue messageIdPacketQueue = new MessageIdPacketQueue();
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(null).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(0).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(1).size());
        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(100).size());
    }

    @Test
    public void multipleMessages() {
        MessageIdPacketQueue messageIdPacketQueue = new MessageIdPacketQueue();

        addChatMessage("m1", "u1", messageIdPacketQueue);
        addRebootPacket(1, 2, messageIdPacketQueue);
        addChatMessage("m2", "u2", messageIdPacketQueue);
        addChatMessage("m3", "u3", messageIdPacketQueue);
        addRebootPacket(3, 4, messageIdPacketQueue);
        addChatMessage("m4", "u4", messageIdPacketQueue);

        List<MessageIdPacket> messageIdPackets = messageIdPacketQueue.peekMessages(null);
        Assert.assertEquals(6, messageIdPackets.size());
        assertChatMessage("m4", "u4", messageIdPackets.get(0));
        assertRebootPacket(3, 4, messageIdPackets.get(1));
        assertChatMessage("m3", "u3", messageIdPackets.get(2));
        assertChatMessage("m2", "u2", messageIdPackets.get(3));
        assertRebootPacket(1, 2, messageIdPackets.get(4));
        assertChatMessage("m1", "u1", messageIdPackets.get(5));
        ChatMessage m1 = (ChatMessage) messageIdPackets.get(2);

        messageIdPackets = messageIdPacketQueue.peekMessages(messageIdPackets.get(2).getMessageId());
        Assert.assertEquals(2, messageIdPackets.size());
        assertChatMessage("m4", "u4", messageIdPackets.get(0));
        assertRebootPacket(3, 4, messageIdPackets.get(1));

        Assert.assertEquals(0, messageIdPacketQueue.peekMessages(messageIdPackets.get(0).getMessageId()).size());

        addChatMessage("m5", "u5", messageIdPacketQueue);
        addRebootPacket(5, 6, messageIdPacketQueue);
        addChatMessage("m6", "u6", messageIdPacketQueue);

        messageIdPackets = messageIdPacketQueue.peekMessages(messageIdPackets.get(0).getMessageId());
        Assert.assertEquals(3, messageIdPackets.size());
        assertChatMessage("m6", "u6", messageIdPackets.get(0));
        assertRebootPacket(5, 6, messageIdPackets.get(1));
        assertChatMessage("m5", "u5", messageIdPackets.get(2));

        List<MessageIdPacket> receivedMessageIdPacketAll = messageIdPacketQueue.peekMessages(null);
        Assert.assertEquals(9, receivedMessageIdPacketAll.size());
        assertChatMessage("m6", "u6", receivedMessageIdPacketAll.get(0));
        assertRebootPacket(5, 6, receivedMessageIdPacketAll.get(1));
        assertChatMessage("m5", "u5", receivedMessageIdPacketAll.get(2));
        assertChatMessage("m4", "u4", receivedMessageIdPacketAll.get(3));
        assertRebootPacket(3, 4, receivedMessageIdPacketAll.get(4));
        assertChatMessage("m3", "u3", receivedMessageIdPacketAll.get(5));
        assertChatMessage("m2", "u2", receivedMessageIdPacketAll.get(6));
        assertRebootPacket(1, 2, receivedMessageIdPacketAll.get(7));
        assertChatMessage("m1", "u1", receivedMessageIdPacketAll.get(8));

        addChatMessage("m7", "u7", messageIdPacketQueue);
        addChatMessage("m8", "u8", messageIdPacketQueue);
        addRebootPacket(7, 8, messageIdPacketQueue);
        addChatMessage("m9", "u9", messageIdPacketQueue);
        addChatMessage("m10", "u10", messageIdPacketQueue);

        receivedMessageIdPacketAll = messageIdPacketQueue.peekMessages(null);
        Assert.assertEquals(10, receivedMessageIdPacketAll.size());
        assertChatMessage("m10", "u10", receivedMessageIdPacketAll.get(0));
        assertChatMessage("m9", "u9", receivedMessageIdPacketAll.get(1));
        assertRebootPacket(7, 8, receivedMessageIdPacketAll.get(2));
        assertChatMessage("m8", "u8", receivedMessageIdPacketAll.get(3));
        assertChatMessage("m7", "u7", receivedMessageIdPacketAll.get(4));
        assertChatMessage("m6", "u6", receivedMessageIdPacketAll.get(5));
        assertRebootPacket(5, 6, receivedMessageIdPacketAll.get(6));
        assertChatMessage("m5", "u5", receivedMessageIdPacketAll.get(7));
        assertChatMessage("m4", "u4", receivedMessageIdPacketAll.get(8));
        assertRebootPacket(3, 4, receivedMessageIdPacketAll.get(9));

        messageIdPackets = messageIdPacketQueue.peekMessages(messageIdPackets.get(0).getMessageId());
        Assert.assertEquals(5, messageIdPackets.size());
        assertChatMessage("m10", "u10", receivedMessageIdPacketAll.get(0));
        assertChatMessage("m9", "u9", receivedMessageIdPacketAll.get(1));
        assertRebootPacket(7, 8, receivedMessageIdPacketAll.get(2));
        assertChatMessage("m8", "u8", receivedMessageIdPacketAll.get(3));
        assertChatMessage("m7", "u7", receivedMessageIdPacketAll.get(4));

        addChatMessage("m11", "u11", messageIdPacketQueue);

        messageIdPackets = messageIdPacketQueue.peekMessages(messageIdPackets.get(0).getMessageId());
        Assert.assertEquals(1, messageIdPackets.size());
        assertChatMessage("m11", "u11", messageIdPackets.get(0));

        receivedMessageIdPacketAll = messageIdPacketQueue.peekMessages(null);
        Assert.assertEquals(10, receivedMessageIdPacketAll.size());
        assertChatMessage("m11", "u11", receivedMessageIdPacketAll.get(0));
        assertChatMessage("m10", "u10", receivedMessageIdPacketAll.get(1));
        assertChatMessage("m9", "u9", receivedMessageIdPacketAll.get(2));
        assertRebootPacket(7, 8, receivedMessageIdPacketAll.get(3));
        assertChatMessage("m8", "u8", receivedMessageIdPacketAll.get(4));
        assertChatMessage("m7", "u7", receivedMessageIdPacketAll.get(5));
        assertChatMessage("m6", "u6", receivedMessageIdPacketAll.get(6));
        assertRebootPacket(5, 6, receivedMessageIdPacketAll.get(7));
        assertChatMessage("m5", "u5", receivedMessageIdPacketAll.get(8));
        assertChatMessage("m4", "u4", receivedMessageIdPacketAll.get(9));

        receivedMessageIdPacketAll = messageIdPacketQueue.peekMessages(m1.getMessageId());
        Assert.assertEquals(10, receivedMessageIdPacketAll.size());
        assertChatMessage("m11", "u11", receivedMessageIdPacketAll.get(0));
        assertChatMessage("m10", "u10", receivedMessageIdPacketAll.get(1));
        assertChatMessage("m9", "u9", receivedMessageIdPacketAll.get(2));
        assertRebootPacket(7, 8, receivedMessageIdPacketAll.get(3));
        assertChatMessage("m8", "u8", receivedMessageIdPacketAll.get(4));
        assertChatMessage("m7", "u7", receivedMessageIdPacketAll.get(5));
        assertChatMessage("m6", "u6", receivedMessageIdPacketAll.get(6));
        assertRebootPacket(5, 6, receivedMessageIdPacketAll.get(7));
        assertChatMessage("m5", "u5", receivedMessageIdPacketAll.get(8));
        assertChatMessage("m4", "u4", receivedMessageIdPacketAll.get(9));

        addChatMessage("m12", "u12", messageIdPacketQueue);
        addChatMessage("m13", "u13", messageIdPacketQueue);
        addChatMessage("m14", "u14", messageIdPacketQueue);

        receivedMessageIdPacketAll = messageIdPacketQueue.peekMessages(null);
        Assert.assertEquals(10, receivedMessageIdPacketAll.size());
        assertChatMessage("m14", "u14", receivedMessageIdPacketAll.get(0));
        assertChatMessage("m13", "u13", receivedMessageIdPacketAll.get(1));
        assertChatMessage("m12", "u12", receivedMessageIdPacketAll.get(2));
        assertChatMessage("m11", "u11", receivedMessageIdPacketAll.get(3));
        assertChatMessage("m10", "u10", receivedMessageIdPacketAll.get(4));
        assertChatMessage("m9", "u9", receivedMessageIdPacketAll.get(5));
        assertRebootPacket(7, 8, receivedMessageIdPacketAll.get(6));
        assertChatMessage("m8", "u8", receivedMessageIdPacketAll.get(7));
        assertChatMessage("m7", "u7", receivedMessageIdPacketAll.get(8));
        assertChatMessage("m6", "u6", receivedMessageIdPacketAll.get(9));
    }

    private void addChatMessage(String message, String name, MessageIdPacketQueue messageIdPacketQueue) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setName(name);
        messageIdPacketQueue.initAndPutMessage(chatMessage);
    }

    private void addRebootPacket(int reboot, int downTime, MessageIdPacketQueue messageIdPacketQueue) {
        ServerRebootMessagePacket serverRebootMessagePacket = new ServerRebootMessagePacket();
        serverRebootMessagePacket.setRebootInSeconds(reboot);
        serverRebootMessagePacket.setDownTimeInMinutes(downTime);
        messageIdPacketQueue.initAndPutMessage(serverRebootMessagePacket);
    }

    public static void assertChatMessage(String message, String name, MessageIdPacket messageIdPacket) {
        ChatMessage chatMessage = (ChatMessage) messageIdPacket;
        Assert.assertEquals(message, chatMessage.getMessage());
        Assert.assertEquals(name, chatMessage.getName());
    }

    public static void assertRebootPacket(int reboot, int downTime, MessageIdPacket messageIdPacket) {
        ServerRebootMessagePacket serverRebootMessagePacket = (ServerRebootMessagePacket) messageIdPacket;
        Assert.assertEquals(reboot, serverRebootMessagePacket.getRebootInSeconds());
        Assert.assertEquals(downTime, serverRebootMessagePacket.getDownTimeInMinutes());
    }

}
