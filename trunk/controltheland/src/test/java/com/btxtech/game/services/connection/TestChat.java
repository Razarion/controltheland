package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.ChatMessage;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.utg.DbChatMessage;
import org.hibernate.SessionFactory;
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
public class TestChat extends AbstractServiceTest {
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @DirtiesContext
    public void testRealGame() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Setup connection
        Thread.sleep(1000); // Wait for the account balance package
        getPackagesIgnoreSyncItemInfoAndClear();
        sendMessage("m1", "u1");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m1", "u1", 0));
        sendMessage("m2", "u2");
        sendMessage("m3", "u3");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m2", "u2", 1), createMessage("m3", "u3", 2));
        assertPackagesIgnoreSyncItemInfoAndClear();
        List<ChatMessage> chatMessages = getMovableService().pollChatMessages(null);
        Assert.assertEquals(3, chatMessages.size());
        TestChatMessageQueue.assertMessage("m3", "u3", chatMessages.get(0));
        TestChatMessageQueue.assertMessage("m2", "u2", chatMessages.get(1));
        TestChatMessageQueue.assertMessage("m1", "u1", chatMessages.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbChatMessage> dbChatMessages = HibernateUtil.loadAll(sessionFactory, DbChatMessage.class);
        Assert.assertEquals(3, dbChatMessages.size());
        assertDbChatMessage("m1", "u1", dbChatMessages.get(0));
        assertDbChatMessage("m2", "u2", dbChatMessages.get(1));
        assertDbChatMessage("m3", "u3", dbChatMessages.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertDbChatMessage(String message, String name, DbChatMessage dbChatMessage) {
        Assert.assertEquals(message, dbChatMessage.getMessage());
        Assert.assertEquals(name, dbChatMessage.getName());
    }

    private ChatMessage createMessage(String message, String name, int id) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setName(name);
        chatMessage.setMessageId(id);
        return chatMessage;
    }

    private void sendMessage(String message, String name) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setName(name);
        getMovableService().sendChatMessage(chatMessage);
    }
}
