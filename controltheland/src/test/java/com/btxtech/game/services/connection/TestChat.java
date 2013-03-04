package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.utg.DbChatMessage;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Locale;

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
        configureSimplePlanetNoResources();

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
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, null);
        Assert.assertEquals(3, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m3", "Base 1", messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m2", "Base 1", messageIdPackets.get(1));
        TestMessageIdPacketQueue.assertChatMessage("m1", "Base 1", messageIdPackets.get(2));
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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("User 1");
        getMyBase(); // Setup connection
        Thread.sleep(1000); // Wait for the account balance package
        getPackagesIgnoreSyncItemInfoAndClear(false);
        sendMessage("m1");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m1", "User 1", 0));
        sendMessage("m2");
        sendMessage("m3");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m2", "User 1", 1), createMessage("m3", "User 1", 2));
        assertPackagesIgnoreSyncItemInfoAndClear();
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, null);
        Assert.assertEquals(3, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m3", "User 1", messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m2", "User 1", messageIdPackets.get(1));
        TestMessageIdPacketQueue.assertChatMessage("m1", "User 1", messageIdPackets.get(2));
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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        sendMessage("m1");
        sendMessage("m2");
        sendMessage("m3");
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, null);
        Assert.assertEquals("Guest", ((ChatMessage) messageIdPackets.get(0)).getName());
        Assert.assertEquals("m3", ((ChatMessage) messageIdPackets.get(0)).getMessage());
        Assert.assertEquals("Guest", ((ChatMessage) messageIdPackets.get(1)).getName());
        Assert.assertEquals("m2", ((ChatMessage) messageIdPackets.get(1)).getMessage());
        Assert.assertEquals("Guest", ((ChatMessage) messageIdPackets.get(2)).getName());
        Assert.assertEquals("m1", ((ChatMessage) messageIdPackets.get(2)).getMessage());
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

    @Test
    @DirtiesContext
    public void testUnregUserTutorialDe() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        sendMessage("m1");
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, null);
        Assert.assertEquals("Gast", ((ChatMessage) messageIdPackets.get(0)).getName());
        Assert.assertEquals("m1", ((ChatMessage) messageIdPackets.get(0)).getMessage());
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
