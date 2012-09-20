package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.ChatListener;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.user.client.Timer;

import java.util.List;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 03.04.2012
 * Time: 12:45:48
 */
public class ClientChatHandler {
    public static final int START_DELAY = 3000;
    public static final int POLL_DELAY = 5000;
    private static final ClientChatHandler INSTANCE = new ClientChatHandler();
    private Timer startTimer;
    private Timer pollTimer;
    private ChatMessage lastMessage;
    private ConnectionI connection;
    private ChatListener chatListener;
    private Logger log = Logger.getLogger(ClientChatHandler.class.getName());

    /**
     * Singleton
     */
    private ClientChatHandler() {

    }

    public static ClientChatHandler getInstance() {
        return INSTANCE;
    }

    public void onMessageReceived(List<ChatMessage> chatMessages) {
        if (chatMessages != null) {
            for (int i = chatMessages.size() - 1; i >= 0; i--) {
                ChatMessage chatMessage = chatMessages.get(i);
                onMessageReceived(chatMessage);
            }
        }
    }

    public void onMessageReceived(ChatMessage chatMessage) {
        if (startTimer != null) {
            return;
        }
        lastMessage = chatMessage;
        if (chatListener != null) {
            chatListener.addMessage(chatMessage);
        }
    }

    public void stop() {
        if (startTimer != null) {
            startTimer.cancel();
            startTimer = null;
        }
        if (pollTimer != null) {
            pollTimer.cancel();
            pollTimer = null;
        }
        lastMessage = null;
    }

    public void runRealGame(ConnectionI connection, ChatListener chatListener, int startDelay) {
        this.chatListener = chatListener;
        chatListener.clearMessages();
        this.connection = connection;
        startTimer = new TimerPerfmon(PerfmonEnum.CHAT_START) {
            @Override
            public void runPerfmon() {
                startTimer = null;
                pollMessages();
            }
        };
        startTimer.schedule(startDelay);
    }

    public void runSimulatedGame(ConnectionI connection, ChatListener chatListener, int startDelay, final int pollDelay) {
        this.chatListener = chatListener;
        chatListener.clearMessages();
        this.connection = connection;
        startTimer = new TimerPerfmon(PerfmonEnum.CHAT_START) {
            @Override
            public void runPerfmon() {
                startTimer = null;
                pollMessages();
                pollTimer = new TimerPerfmon(PerfmonEnum.CHAT_POLL) {
                    @Override
                    public void runPerfmon() {
                        pollMessages();
                    }
                };
                pollTimer.scheduleRepeating(pollDelay);
            }
        };
        startTimer.schedule(startDelay);
    }


    private void pollMessages() {
        if (connection == null) {
            log.severe("ClientChatHandler.poll() connection == null");
            return;
        }
        Integer lastMessageId = lastMessage != null ? lastMessage.getMessageId() : null;
        connection.pollChatMessages(lastMessageId);
    }

    public void pollMessagesIfInPollMode() {
        if (startTimer != null) {
            return;
        }
        if (pollTimer == null) {
            return;
        }
        pollMessages();
    }

    public void sendMessage(String text) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(text);
        connection.sendChatMessage(chatMessage);
    }
}
