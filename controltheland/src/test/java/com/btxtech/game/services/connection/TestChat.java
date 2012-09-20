package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.user.UserService;
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
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testRealGame() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Setup connection
        Thread.sleep(1000); // Wait for the account balance package
        getPackagesIgnoreSyncItemInfoAndClear(false);
        sendMessage("m1");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m1", "Base 1", 0));
        sendMessage("m2");
        sendMessage("m3");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m2", "Base 1", 1), createMessage("m3", "Base 1", 2));
        assertPackagesIgnoreSyncItemInfoAndClear();
        List<ChatMessage> chatMessages = getMovableService().pollChatMessages(null);
        Assert.assertEquals(3, chatMessages.size());
        TestChatMessageQueue.assertMessage("m3", "Base 1", chatMessages.get(0));
        TestChatMessageQueue.assertMessage("m2", "Base 1", chatMessages.get(1));
        TestChatMessageQueue.assertMessage("m1", "Base 1", chatMessages.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbChatMessage> dbChatMessages = HibernateUtil.loadAll(sessionFactory, DbChatMessage.class);
        Assert.assertEquals(3, dbChatMessages.size());
        assertDbChatMessage("m1", "Base 1", dbChatMessages.get(0));
        assertDbChatMessage("m2", "Base 1", dbChatMessages.get(1));
        assertDbChatMessage("m3", "Base 1", dbChatMessages.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRealGameRegUser() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("User 1", "xxx", "xxx", "");
        userService.login("User 1", "xxx");
        getMyBase(); // Setup connection
        Thread.sleep(1000); // Wait for the account balance package
        getPackagesIgnoreSyncItemInfoAndClear(false);
        sendMessage("m1");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m1", "User 1", 0));
        sendMessage("m2");
        sendMessage("m3");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m2", "User 1", 1), createMessage("m3", "User 1", 2));
        assertPackagesIgnoreSyncItemInfoAndClear();
        List<ChatMessage> chatMessages = getMovableService().pollChatMessages(null);
        Assert.assertEquals(3, chatMessages.size());
        TestChatMessageQueue.assertMessage("m3", "User 1", chatMessages.get(0));
        TestChatMessageQueue.assertMessage("m2", "User 1", chatMessages.get(1));
        TestChatMessageQueue.assertMessage("m1", "User 1", chatMessages.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbChatMessage> dbChatMessages = HibernateUtil.loadAll(sessionFactory, DbChatMessage.class);
        Assert.assertEquals(3, dbChatMessages.size());
        assertDbChatMessage("m1", "User 1", dbChatMessages.get(0));
        assertDbChatMessage("m2", "User 1", dbChatMessages.get(1));
        assertDbChatMessage("m3", "User 1", dbChatMessages.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUnregUserTutorial() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendMessage("m1");
        sendMessage("m2");
        sendMessage("m3");
        List<ChatMessage> chatMessages = getMovableService().pollChatMessages(null);
        Assert.assertEquals("Guest", chatMessages.get(0).getName());
        Assert.assertEquals("m3", chatMessages.get(0).getMessage());
        Assert.assertEquals("Guest", chatMessages.get(1).getName());
        Assert.assertEquals("m2", chatMessages.get(1).getMessage());
        Assert.assertEquals("Guest", chatMessages.get(2).getName());
        Assert.assertEquals("m1", chatMessages.get(2).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbChatMessage> dbChatMessages = HibernateUtil.loadAll(sessionFactory, DbChatMessage.class);
        Assert.assertEquals(3, dbChatMessages.size());
        assertDbChatMessage("m1", "Guest", dbChatMessages.get(0));
        assertDbChatMessage("m2", "Guest", dbChatMessages.get(1));
        assertDbChatMessage("m3", "Guest", dbChatMessages.get(2));
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

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        getMovableService().sendChatMessage(chatMessage);
    }
}
