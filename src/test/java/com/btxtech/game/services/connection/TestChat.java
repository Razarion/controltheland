package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.NotAGuildMemberException;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.connection.impl.MessageIdPacketQueueImpl;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.utg.DbChatMessage;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
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
    @Autowired
    private GuildService guildService;
    @Autowired
    private MessageIdPacketQueue messageIdPacketQueue;

    @Test
    @DirtiesContext
    public void testRealGame() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase(); // Setup connection
        Thread.sleep(200); // Wait for the account balance package
        getPackagesIgnoreSyncItemInfoAndClear(false);
        sendMessageGlobal("m1");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m1", "Base 1", null, null, 0, ChatMessage.Type.ENEMY));
        sendMessageGlobal("m2");
        sendMessageGlobal("m3");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m2", "Base 1", null, null, 1, ChatMessage.Type.ENEMY), createMessage("m3", "Base 1", null, null, 2, ChatMessage.Type.ENEMY));
        assertPackagesIgnoreSyncItemInfoAndClear();
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
        Assert.assertEquals(3, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m3", "Base 1", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m2", "Base 1", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(1));
        TestMessageIdPacketQueue.assertChatMessage("m1", "Base 1", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbChatMessage> dbChatMessages = HibernateUtil.loadAll(sessionFactory, DbChatMessage.class);
        Assert.assertEquals(3, dbChatMessages.size());
        assertDbChatMessage("m1", "Base 1", null, null, dbChatMessages.get(0));
        assertDbChatMessage("m2", "Base 1", null, null, dbChatMessages.get(1));
        assertDbChatMessage("m3", "Base 1", null, null, dbChatMessages.get(2));
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
        getOrCreateBase(); // Setup connection
        Thread.sleep(1000); // Wait for the account balance package
        getPackagesIgnoreSyncItemInfoAndClear(false);
        sendMessageGlobal("m1");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m1", "User 1", getUserId(), null, 0, ChatMessage.Type.OWN));
        sendMessageGlobal("m2");
        sendMessageGlobal("m3");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m2", "User 1", getUserId(), null, 1, ChatMessage.Type.OWN), createMessage("m3", "User 1", getUserId(), null, 2, ChatMessage.Type.OWN));
        assertPackagesIgnoreSyncItemInfoAndClear();
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
        Assert.assertEquals(3, messageIdPackets.size());
        int userId = getUserId();
        TestMessageIdPacketQueue.assertChatMessage("m3", "User 1", userId, null, ChatMessage.Type.OWN, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m2", "User 1", userId, null, ChatMessage.Type.OWN, messageIdPackets.get(1));
        TestMessageIdPacketQueue.assertChatMessage("m1", "User 1", userId, null, ChatMessage.Type.OWN, messageIdPackets.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbChatMessage> dbChatMessages = HibernateUtil.loadAll(sessionFactory, DbChatMessage.class);
        Assert.assertEquals(3, dbChatMessages.size());
        assertDbChatMessage("m1", "User 1", userId, null, dbChatMessages.get(0));
        assertDbChatMessage("m2", "User 1", userId, null, dbChatMessages.get(1));
        assertDbChatMessage("m3", "User 1", userId, null, dbChatMessages.get(2));
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
        sendMessageGlobal("m1");
        sendMessageGlobal("m2");
        sendMessageGlobal("m3");
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
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
        assertDbChatMessage("m1", "Guest", null, null, dbChatMessages.get(0));
        assertDbChatMessage("m2", "Guest", null, null, dbChatMessages.get(1));
        assertDbChatMessage("m3", "Guest", null, null, dbChatMessages.get(2));
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
        sendMessageGlobal("m1");
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
        Assert.assertEquals("Gast", ((ChatMessage) messageIdPackets.get(0)).getName());
        Assert.assertEquals("m1", ((ChatMessage) messageIdPackets.get(0)).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void setChatMessageFilterErrorCases() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            getMovableService().setChatMessageFilter(ChatMessageFilter.GUILD);
            Assert.fail("NotAGuildMemberException expected");
        } catch (NotAGuildMemberException e) {
            // Expected
        }
        try {
            getMovableService().setChatMessageFilter(ChatMessageFilter.GUILD);
            Assert.fail("NotAGuildMemberException expected");
        } catch (NotAGuildMemberException e) {
            // Expected
        }
        getMovableService().setChatMessageFilter(ChatMessageFilter.GLOBAL);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRealGameRegUserGuild() throws Exception {
        configureSimplePlanetNoResources();

        int guildId = createGuildAnd2Users();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member1");
        int memberUserId = getUserId();
        getOrCreateBase(); // Setup connection
        Thread.sleep(1000); // Wait for the account balance package
        getPackagesIgnoreSyncItemInfoAndClear(false);
        sendMessageGlobal("m1");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m1", "member1", getUserId(), null, 0, ChatMessage.Type.OWN));
        // DO not switch -> no packet expected
        sendMessageGuild("m2");
        assertPackagesIgnoreSyncItemInfoAndClear();
        // Do switch to guild chat
        List<MessageIdPacket> messageIdPackets = getMovableService().setChatMessageFilter(ChatMessageFilter.GUILD);
        Assert.assertEquals(1, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m2", "member1", 1, guildId, ChatMessage.Type.OWN, messageIdPackets.get(0));
        sendMessageGuild("m3");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m3", "member1", getUserId(), guildId, 2, ChatMessage.Type.OWN));
        sendMessageGuild("m4");
        sendMessageGuild("m5");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m4", "member1", getUserId(), guildId, 3, ChatMessage.Type.OWN), createMessage("m5", "member1", getUserId(), guildId, 4, ChatMessage.Type.OWN));
        // Switch back
        messageIdPackets = getMovableService().setChatMessageFilter(ChatMessageFilter.GLOBAL);
        Assert.assertEquals(1, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m1", "member1", getUserId(), null, ChatMessage.Type.OWN, messageIdPackets.get(0));
        // Send to guild chat -> no packet expected
        sendMessageGuild("m6");
        assertPackagesIgnoreSyncItemInfoAndClear();
        sendMessageGlobal("m7");
        assertPackagesIgnoreSyncItemInfoAndClear(createMessage("m7", "member1", getUserId(), null, 6, ChatMessage.Type.OWN));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Login other guild member
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("presi");
        messageIdPackets = messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
        Assert.assertEquals(2, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m7", "member1", memberUserId, null, ChatMessage.Type.GUILD, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m1", "member1", memberUserId, null, ChatMessage.Type.GUILD, messageIdPackets.get(1));
        messageIdPackets = messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GUILD, null);
        Assert.assertEquals(5, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m6", "member1", memberUserId, guildId, ChatMessage.Type.GUILD, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m5", "member1", memberUserId, guildId, ChatMessage.Type.GUILD, messageIdPackets.get(1));
        TestMessageIdPacketQueue.assertChatMessage("m4", "member1", memberUserId, guildId, ChatMessage.Type.GUILD, messageIdPackets.get(2));
        TestMessageIdPacketQueue.assertChatMessage("m3", "member1", memberUserId, guildId, ChatMessage.Type.GUILD, messageIdPackets.get(3));
        TestMessageIdPacketQueue.assertChatMessage("m2", "member1", memberUserId, guildId, ChatMessage.Type.GUILD, messageIdPackets.get(4));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Login no member
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        messageIdPackets = messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
        Assert.assertEquals(2, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m7", "member1", memberUserId, null, ChatMessage.Type.ENEMY, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m1", "member1", memberUserId, null, ChatMessage.Type.ENEMY, messageIdPackets.get(1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // unreg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        messageIdPackets = messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
        Assert.assertEquals(2, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m7", "member1", memberUserId, null, ChatMessage.Type.ENEMY, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m1", "member1", memberUserId, null, ChatMessage.Type.ENEMY, messageIdPackets.get(1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // User from a different guild
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("presi2");
        guildService.createGuild("guild2");
        messageIdPackets = messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
        Assert.assertEquals(2, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("m7", "member1", memberUserId, null, ChatMessage.Type.ENEMY, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("m1", "member1", memberUserId, null, ChatMessage.Type.ENEMY, messageIdPackets.get(1));
        messageIdPackets = messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GUILD, null);
        Assert.assertTrue(messageIdPackets.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testReloadOnStartup() throws Exception {
        configureSimplePlanetNoResources();

        int guildId = createGuildAnd2Users();

        saveMessage("U01", "text text 01", 1, null, 101000L);
        saveMessage("U02", "text text 02", null, null, 102000L);
        saveMessage("U03", "text text 03", null, null, 103000L);
        saveMessage("U04", "text text 04", 1, guildId, 104000L);
        saveMessage("U05", "text text 05", null, null, 105000L);
        saveMessage("U06", "text text 06", null, null, 106000L);
        saveMessage("U07", "text text 07", 1, guildId, 107000L);
        saveMessage("U08", "text text 08", null, null, 108000L);
        saveMessage("U09", "text text 09", null, null, 109000L);
        saveMessage("U10", "text text 10", 2, null, 110000L);
        saveMessage("U01", "text text 11", null, null, 121000L);
        saveMessage("U02", "text text 12", null, null, 122000L);
        saveMessage("U03", "text text 13", null, null, 123000L);
        saveMessage("U04", "text text 14", null, null, 124000L);
        saveMessage("U05", "text text 15", 2, 999, 125000L);
        saveMessage("U06", "text text 16", null, null, 126000L);
        saveMessage("U07", "text text 17", null, null, 127000L);
        saveMessage("U08", "text text 18", 3, 999, 128000L);
        saveMessage("U09", "text text 19", null, null, 129000L);
        saveMessage("U10", "text text 20", null, null, 130000L);
        saveMessage("U01", "text text 21", 4, 999, 131000L);
        saveMessage("U02", "text text 22", null, null, 132000L);
        saveMessage("U03", "text text 23", null, null, 133000L);
        saveMessage("U04", "text text 24", null, null, 134000L);
        saveMessage("U05", "text text 25", 2, null, 135000L);
        saveMessage("U06", "text text 26", null, null, 136000L);
        saveMessage("U07", "text text 27", null, null, 137000L);
        saveMessage("U08", "text text 28", 1, null, 138000L);
        saveMessage("U09", "text text 29", null, null, 139000L);
        saveMessage("U10", "text text 30", 2, null, 140000L);

        // Fake server start
        MessageIdPacketQueueImpl messageIdPacketQueueImpl = (MessageIdPacketQueueImpl) deAopProxy(messageIdPacketQueue);
        messageIdPacketQueueImpl.init();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<MessageIdPacket> messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GLOBAL, null);
        Assert.assertEquals(20, messageIdPackets.size());
        TestMessageIdPacketQueue.assertChatMessage("text text 30", "U10", 2, null, ChatMessage.Type.ENEMY, messageIdPackets.get(0));
        TestMessageIdPacketQueue.assertChatMessage("text text 29", "U09", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(1));
        TestMessageIdPacketQueue.assertChatMessage("text text 28", "U08", 1, null, ChatMessage.Type.ENEMY, messageIdPackets.get(2));
        TestMessageIdPacketQueue.assertChatMessage("text text 27", "U07", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(3));
        TestMessageIdPacketQueue.assertChatMessage("text text 26", "U06", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(4));
        TestMessageIdPacketQueue.assertChatMessage("text text 25", "U05", 2, null, ChatMessage.Type.ENEMY, messageIdPackets.get(5));
        TestMessageIdPacketQueue.assertChatMessage("text text 24", "U04", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(6));
        TestMessageIdPacketQueue.assertChatMessage("text text 23", "U03", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(7));
        TestMessageIdPacketQueue.assertChatMessage("text text 22", "U02", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(8));
        TestMessageIdPacketQueue.assertChatMessage("text text 20", "U10", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(9));
        TestMessageIdPacketQueue.assertChatMessage("text text 19", "U09", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(10));
        TestMessageIdPacketQueue.assertChatMessage("text text 17", "U07", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(11));
        TestMessageIdPacketQueue.assertChatMessage("text text 16", "U06", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(12));
        TestMessageIdPacketQueue.assertChatMessage("text text 14", "U04", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(13));
        TestMessageIdPacketQueue.assertChatMessage("text text 13", "U03", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(14));
        TestMessageIdPacketQueue.assertChatMessage("text text 12", "U02", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(15));
        TestMessageIdPacketQueue.assertChatMessage("text text 11", "U01", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(16));
        TestMessageIdPacketQueue.assertChatMessage("text text 10", "U10", 2, null, ChatMessage.Type.ENEMY, messageIdPackets.get(17));
        TestMessageIdPacketQueue.assertChatMessage("text text 09", "U09", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(18));
        TestMessageIdPacketQueue.assertChatMessage("text text 08", "U08", null, null, ChatMessage.Type.ENEMY, messageIdPackets.get(19));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("member1");
        messageIdPackets = getMovableService().pollMessageIdPackets(null, ChatMessageFilter.GUILD, null);
        Assert.assertTrue(messageIdPackets.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    private void saveMessage(String userName, String message, Integer userId, Integer guildId, long milliS) throws Exception {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setName(userName);
        chatMessage.setUserId(userId);
        chatMessage.setGuildId(guildId);
        DbChatMessage dbChatMessage = new DbChatMessage("1234", chatMessage);
        setPrivateField(DbChatMessage.class, dbChatMessage, "timeStamp", new Date(milliS));
        saveOrUpdateInTransaction(dbChatMessage);
    }


    private void assertDbChatMessage(String message, String name, Integer userId, Integer guildId, DbChatMessage dbChatMessage) {
        Assert.assertEquals(message, dbChatMessage.getMessage());
        Assert.assertEquals(name, dbChatMessage.getName());
        Assert.assertEquals(userId, dbChatMessage.getUserId());
        Assert.assertEquals(guildId, dbChatMessage.getGuildId());
    }

    private ChatMessage createMessage(String message, String name, Integer userId, Integer guildId, int id, ChatMessage.Type type) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setName(name);
        chatMessage.setUserId(userId);
        chatMessage.setMessageId(id);
        chatMessage.setGuildId(guildId);
        chatMessage.setType(type);
        return chatMessage;
    }

    private void sendMessageGlobal(String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        getMovableService().sendChatMessage(chatMessage, ChatMessageFilter.GLOBAL);
    }

    private void sendMessageGuild(String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        getMovableService().sendChatMessage(chatMessage, ChatMessageFilter.GUILD);
    }
}
