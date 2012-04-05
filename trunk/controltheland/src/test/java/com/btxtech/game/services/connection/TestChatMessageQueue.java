package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.ChatMessage;
import com.btxtech.game.services.connection.impl.ChatMessageQueue;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * User: beat
 * Date: 02.04.2012
 * Time: 23:44:18
 */
public class TestChatMessageQueue {

    @Test
    public void empty() {
        ChatMessageQueue chatMessageQueue = new ChatMessageQueue();
        Assert.assertEquals(0, chatMessageQueue.peekMessages(null).size());
        Assert.assertEquals(0, chatMessageQueue.peekMessages(0).size());
        Assert.assertEquals(0, chatMessageQueue.peekMessages(1).size());
        Assert.assertEquals(0, chatMessageQueue.peekMessages(100).size());
    }

    @Test
    public void multipleMessages() {
        ChatMessageQueue chatMessageQueue = new ChatMessageQueue();

        addMessage("m1", "u1", chatMessageQueue);
        addMessage("m2", "u2", chatMessageQueue);
        addMessage("m3", "u3", chatMessageQueue);
        addMessage("m4", "u4", chatMessageQueue);

        List<ChatMessage> receivedMessages = chatMessageQueue.peekMessages(null);
        Assert.assertEquals(4, receivedMessages.size());
        assertMessage("m4", "u4", receivedMessages.get(0));
        assertMessage("m3", "u3", receivedMessages.get(1));
        assertMessage("m2", "u2", receivedMessages.get(2));
        assertMessage("m1", "u1", receivedMessages.get(3));
        ChatMessage m1 = receivedMessages.get(3);

        receivedMessages = chatMessageQueue.peekMessages(receivedMessages.get(2).getMessageId());
        Assert.assertEquals(2, receivedMessages.size());
        assertMessage("m4", "u4", receivedMessages.get(0));
        assertMessage("m3", "u3", receivedMessages.get(1));

        Assert.assertEquals(0, chatMessageQueue.peekMessages(receivedMessages.get(0).getMessageId()).size());

        addMessage("m5", "u5", chatMessageQueue);
        addMessage("m6", "u6", chatMessageQueue);

        receivedMessages = chatMessageQueue.peekMessages(receivedMessages.get(0).getMessageId());
        Assert.assertEquals(2, receivedMessages.size());
        assertMessage("m6", "u6", receivedMessages.get(0));
        assertMessage("m5", "u5", receivedMessages.get(1));

        List<ChatMessage> receivedMessagesAll = chatMessageQueue.peekMessages(null);
        Assert.assertEquals(6, receivedMessagesAll.size());
        assertMessage("m6", "u6", receivedMessagesAll.get(0));
        assertMessage("m5", "u5", receivedMessagesAll.get(1));
        assertMessage("m4", "u4", receivedMessagesAll.get(2));
        assertMessage("m3", "u3", receivedMessagesAll.get(3));
        assertMessage("m2", "u2", receivedMessagesAll.get(4));
        assertMessage("m1", "u1", receivedMessagesAll.get(5));

        addMessage("m7", "u7", chatMessageQueue);
        addMessage("m8", "u8", chatMessageQueue);
        addMessage("m9", "u9", chatMessageQueue);
        addMessage("m10", "u10", chatMessageQueue);

        receivedMessagesAll = chatMessageQueue.peekMessages(null);
        Assert.assertEquals(10, receivedMessagesAll.size());
        assertMessage("m10", "u10", receivedMessagesAll.get(0));
        assertMessage("m9", "u9", receivedMessagesAll.get(1));
        assertMessage("m8", "u8", receivedMessagesAll.get(2));
        assertMessage("m7", "u7", receivedMessagesAll.get(3));
        assertMessage("m6", "u6", receivedMessagesAll.get(4));
        assertMessage("m5", "u5", receivedMessagesAll.get(5));
        assertMessage("m4", "u4", receivedMessagesAll.get(6));
        assertMessage("m3", "u3", receivedMessagesAll.get(7));
        assertMessage("m2", "u2", receivedMessagesAll.get(8));
        assertMessage("m1", "u1", receivedMessagesAll.get(9));

        receivedMessages = chatMessageQueue.peekMessages(receivedMessages.get(0).getMessageId());
        Assert.assertEquals(4, receivedMessages.size());
        assertMessage("m10", "u10", receivedMessagesAll.get(0));
        assertMessage("m9", "u9", receivedMessagesAll.get(1));
        assertMessage("m8", "u8", receivedMessagesAll.get(2));
        assertMessage("m7", "u7", receivedMessagesAll.get(3));

        addMessage("m11", "u11", chatMessageQueue);

        receivedMessages = chatMessageQueue.peekMessages(receivedMessages.get(0).getMessageId());
        Assert.assertEquals(1, receivedMessages.size());
        assertMessage("m11", "u11", receivedMessages.get(0));

        receivedMessagesAll = chatMessageQueue.peekMessages(null);
        Assert.assertEquals(10, receivedMessagesAll.size());
        assertMessage("m11", "u11", receivedMessagesAll.get(0));
        assertMessage("m10", "u10", receivedMessagesAll.get(1));
        assertMessage("m9", "u9", receivedMessagesAll.get(2));
        assertMessage("m8", "u8", receivedMessagesAll.get(3));
        assertMessage("m7", "u7", receivedMessagesAll.get(4));
        assertMessage("m6", "u6", receivedMessagesAll.get(5));
        assertMessage("m5", "u5", receivedMessagesAll.get(6));
        assertMessage("m4", "u4", receivedMessagesAll.get(7));
        assertMessage("m3", "u3", receivedMessagesAll.get(8));
        assertMessage("m2", "u2", receivedMessagesAll.get(9));

        receivedMessagesAll = chatMessageQueue.peekMessages(m1.getMessageId());
        Assert.assertEquals(10, receivedMessagesAll.size());
        assertMessage("m11", "u11", receivedMessagesAll.get(0));
        assertMessage("m10", "u10", receivedMessagesAll.get(1));
        assertMessage("m9", "u9", receivedMessagesAll.get(2));
        assertMessage("m8", "u8", receivedMessagesAll.get(3));
        assertMessage("m7", "u7", receivedMessagesAll.get(4));
        assertMessage("m6", "u6", receivedMessagesAll.get(5));
        assertMessage("m5", "u5", receivedMessagesAll.get(6));
        assertMessage("m4", "u4", receivedMessagesAll.get(7));
        assertMessage("m3", "u3", receivedMessagesAll.get(8));
        assertMessage("m2", "u2", receivedMessagesAll.get(9));

        addMessage("m12", "u12", chatMessageQueue);
        addMessage("m13", "u13", chatMessageQueue);
        addMessage("m14", "u14", chatMessageQueue);

        receivedMessagesAll = chatMessageQueue.peekMessages(null);
        Assert.assertEquals(10, receivedMessagesAll.size());
        assertMessage("m14", "u14", receivedMessagesAll.get(0));
        assertMessage("m13", "u13", receivedMessagesAll.get(1));
        assertMessage("m12", "u12", receivedMessagesAll.get(2));
        assertMessage("m11", "u11", receivedMessagesAll.get(3));
        assertMessage("m10", "u10", receivedMessagesAll.get(4));
        assertMessage("m9", "u9", receivedMessagesAll.get(5));
        assertMessage("m8", "u8", receivedMessagesAll.get(6));
        assertMessage("m7", "u7", receivedMessagesAll.get(7));
        assertMessage("m6", "u6", receivedMessagesAll.get(8));
        assertMessage("m5", "u5", receivedMessagesAll.get(9));
    }

    private void addMessage(String message, String name, ChatMessageQueue chatMessageQueue) {
        ChatMessage m1 = new ChatMessage();
        m1.setMessage(message);
        m1.setName(name);
        chatMessageQueue.putMessagesAndSetId(m1);
    }

    public static void assertMessage(String message, String name, ChatMessage chatMessage) {
        Assert.assertEquals(message, chatMessage.getMessage());
        Assert.assertEquals(name, chatMessage.getName());
    }

}
