package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientChatHandler;
import com.btxtech.game.jsre.client.ConnectionI;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.ChatListener;
import com.btxtech.game.jsre.client.common.AbstractGwtTest;
import com.btxtech.game.jsre.client.common.ChatMessage;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: beat
 * Date: 03.04.2012
 * Time: 17:06:52
 */
@Ignore
public class TestClientChatHandler extends AbstractGwtTest implements ConnectionI, ChatListener {
    private Integer lastMessageId;
    private int pollChatMessagesCalled;
    private List<ChatMessage> displayMessages = new ArrayList<ChatMessage>();
    private List<ChatMessage> sentMessages = new ArrayList<ChatMessage>();
    private GameEngineMode gameEngineMode;

    public void testSimulatedToReal() throws Exception {
        lastMessageId = null;
        ClientLevelHandler.getInstance().setLevelScope(new LevelScope(1, 0, null, 0, 0, RadarMode.NONE));
        gameEngineMode = GameEngineMode.MASTER;
        assertEquals(0, sentMessages.size());
        assertNull(lastMessageId);
        assertEquals(0, displayMessages.size());
        ClientChatHandler.getInstance().runSimulatedGame(this, this, 100, 100);
        sleep(120, new GwtTestRunnable() {
            @Override
            public void run() throws Throwable {
                assertEquals(1, pollChatMessagesCalled);
                ClientChatHandler.getInstance().onMessageReceived(Arrays.asList(createChatMessage("m3", "u3", 1), createChatMessage("m2", "u2", 2)));
                assertNull(lastMessageId);
                assertEquals(2, displayMessages.size());
                assertDisplayChatMessage("m2", "u2", 0);
                assertDisplayChatMessage("m3", "u3", 1);
                ClientChatHandler.getInstance().sendMessage("m4");
                assertEquals(1, sentMessages.size());
                assertSentChatMessage("m4", "lvl1", 0);
                ClientChatHandler.getInstance().sendMessage("m5");
                ClientChatHandler.getInstance().sendMessage("m6");
                assertEquals(3, sentMessages.size());
                assertSentChatMessage("m4", "lvl1", 0);
                assertSentChatMessage("m5", "lvl1", 1);
                assertSentChatMessage("m6", "lvl1", 2);
                ClientChatHandler.getInstance().onMessageReceived(Arrays.asList(createChatMessage("m12", "u12", 13), createChatMessage("m11", "u11", 12)));
                sleep(100, new GwtTestRunnable() {
                    @Override
                    public void run() throws Throwable {
                        assertEquals(2, pollChatMessagesCalled);
                        assertEquals(13, (int) lastMessageId);
                        ClientChatHandler.getInstance().stop();
                        sentMessages.clear();
                        displayMessages.clear();
                        sleep(200, new GwtTestRunnable() {
                            @Override
                            public void run() throws Throwable {
                                assertEquals(2, pollChatMessagesCalled);
                                // Change to real game mode
                                pollChatMessagesCalled = 0;
                                gameEngineMode = GameEngineMode.SLAVE;
                                SimpleBase simpleBase = new SimpleBase(1);
                                ClientBase.getInstance().setAllBaseAttributes(Arrays.asList(new BaseAttributes(simpleBase, "test", false)));
                                ClientBase.getInstance().setBase(simpleBase);
                                lastMessageId = null;
                                assertEquals(0, sentMessages.size());
                                assertNull(lastMessageId);
                                ClientChatHandler.getInstance().runRealGame(TestClientChatHandler.this, TestClientChatHandler.this, 100);
                                ClientChatHandler.getInstance().onMessageReceived(createChatMessage("m1", "u1", null));
                                assertEquals(0, displayMessages.size());
                                sleep(150, new GwtTestRunnable() {
                                    @Override
                                    public void run() throws Throwable {
                                        assertEquals(1, pollChatMessagesCalled);
                                        ClientChatHandler.getInstance().onMessageReceived(Arrays.asList(createChatMessage("m3", "u3", null), createChatMessage("m2", "u2", null)));
                                        assertEquals(2, displayMessages.size());
                                        assertDisplayChatMessage("m2", "u2", 0);
                                        assertDisplayChatMessage("m3", "u3", 1);
                                        ClientChatHandler.getInstance().sendMessage("m4");
                                        assertEquals(1, sentMessages.size());
                                        assertSentChatMessage("m4", "test", 0);
                                        ClientChatHandler.getInstance().sendMessage("m5");
                                        ClientChatHandler.getInstance().sendMessage("m6");
                                        assertEquals(3, sentMessages.size());
                                        assertSentChatMessage("m4", "test", 0);
                                        assertSentChatMessage("m5", "test", 1);
                                        assertSentChatMessage("m6", "test", 2);
                                        ClientChatHandler.getInstance().onMessageReceived(createChatMessage("m1", "u1", null));
                                        assertEquals(3, displayMessages.size());
                                        assertDisplayChatMessage("m2", "u2", 0);
                                        assertDisplayChatMessage("m3", "u3", 1);
                                        assertDisplayChatMessage("m1", "u1", 2);
                                        // Stop and Stop
                                        pollChatMessagesCalled = 0;
                                        ClientChatHandler.getInstance().stop();
                                        ClientChatHandler.getInstance().runRealGame(TestClientChatHandler.this, TestClientChatHandler.this, 100);
                                        ClientChatHandler.getInstance().stop();
                                        assertEquals(0, pollChatMessagesCalled);
                                        finishTest();
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });
        delayTestFinish(20000);
    }

    private ChatMessage createChatMessage(String message, String name, Integer messageId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setName(name);
        if (messageId != null) {
            chatMessage.setMessageId(messageId);
        }
        return chatMessage;
    }

    private void assertDisplayChatMessage(String message, String name, int index) {
        ChatMessage chatMessage = displayMessages.get(index);
        assertEquals(message, chatMessage.getMessage());
        assertEquals(name, chatMessage.getName());
    }

    private void assertSentChatMessage(String message, String name, int index) {
        ChatMessage chatMessage = sentMessages.get(index);
        assertEquals(message, chatMessage.getMessage());
        assertEquals(name, chatMessage.getName());
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        sentMessages.add(chatMessage);
    }

    @Override
    public void pollChatMessages(Integer lastMessageId) {
        this.lastMessageId = lastMessageId;
        pollChatMessagesCalled++;
    }

    @Override
    public GameEngineMode getGameEngineMode() {
        return gameEngineMode;
    }

    @Override
    public void clearMessages() {
        displayMessages.clear();
    }

    @Override
    public void addMessage(ChatMessage chatMessage) {
        displayMessages.add(chatMessage);
    }
}
