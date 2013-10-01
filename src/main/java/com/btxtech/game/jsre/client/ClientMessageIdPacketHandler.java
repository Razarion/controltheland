package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.ChatListener;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.ServerRestartDialog;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.ServerRebootMessagePacket;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.user.client.Timer;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 03.04.2012
 * Time: 12:45:48
 */
public class ClientMessageIdPacketHandler {
    public static final int START_DELAY = 3000;
    public static final int POLL_DELAY = 5000;
    private static final ClientMessageIdPacketHandler INSTANCE = new ClientMessageIdPacketHandler();
    private Timer startTimer;
    private Timer pollTimer;
    private GlobalCommonConnectionService globalCommonConnectionService;
    private ChatListener chatListener;
    private Logger log = Logger.getLogger(ClientMessageIdPacketHandler.class.getName());
    private MessageIdPacket lastMessageIdPacket;

    /**
     * Singleton
     */
    private ClientMessageIdPacketHandler() {

    }

    public static ClientMessageIdPacketHandler getInstance() {
        return INSTANCE;
    }

    public void onMessageReceived(List<MessageIdPacket> messageIdPackets) {
        if (messageIdPackets != null) {
            // Handle in reverse order
            Collections.reverse(messageIdPackets);
            for (MessageIdPacket messageIdPacket : messageIdPackets) {
                onMessageReceived(messageIdPacket);
            }
        }
    }

    public void onMessageReceived(MessageIdPacket messageIdPacket) {
        if (startTimer != null) {
            return;
        }
        lastMessageIdPacket = messageIdPacket;
        if (messageIdPacket instanceof ChatMessage) {
            if (chatListener != null) {
                chatListener.addMessage((ChatMessage) messageIdPacket);
            }
        } else if (messageIdPacket instanceof ServerRebootMessagePacket) {
            DialogManager.showDialog(new ServerRestartDialog((ServerRebootMessagePacket) messageIdPacket), DialogManager.Type.PROMPTLY);
        } else {
            log.warning("ClientMessageIdPacketHandler.onMessageReceived() can not handle packet: " + messageIdPacket);
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
        lastMessageIdPacket = null;
    }

    public void runRealGame(GlobalCommonConnectionService globalCommonConnectionService, final ChatListener chatListener, int startDelay) {
        this.chatListener = chatListener;
        chatListener.clearMessages();
        this.globalCommonConnectionService = globalCommonConnectionService;
        startTimer = new TimerPerfmon(PerfmonEnum.CHAT_START) {
            @Override
            public void runPerfmon() {
                startTimer = null;
                pollMessages(chatListener.getChatMessageFilter());
            }
        };
        startTimer.schedule(startDelay);
    }

    public void runSimulatedGame(GlobalCommonConnectionService globalCommonConnectionService, final ChatListener chatListener, int startDelay, final int pollDelay) {
        this.chatListener = chatListener;
        chatListener.clearMessages();
        this.globalCommonConnectionService = globalCommonConnectionService;
        startTimer = new TimerPerfmon(PerfmonEnum.CHAT_START) {
            @Override
            public void runPerfmon() {
                startTimer = null;
                pollMessages(chatListener.getChatMessageFilter());
                pollTimer = new TimerPerfmon(PerfmonEnum.CHAT_POLL) {
                    @Override
                    public void runPerfmon() {
                        pollMessages(chatListener.getChatMessageFilter());
                    }
                };
                pollTimer.scheduleRepeating(pollDelay);
            }
        };
        startTimer.schedule(startDelay);
    }


    private void pollMessages(ChatMessageFilter chatMessageFilter) {
        if (globalCommonConnectionService == null) {
            log.severe("ClientMessageIdPacketHandler.poll() connection == null");
            return;
        }
        Integer lastMessageId = lastMessageIdPacket != null ? lastMessageIdPacket.getMessageId() : null;
        globalCommonConnectionService.pollChatMessages(lastMessageId, chatMessageFilter);
    }

    public void pollMessagesIfInPollMode(ChatMessageFilter chatMessageFilter) {
        if (startTimer != null) {
            return;
        }
        if (pollTimer == null) {
            return;
        }
        pollMessages(chatMessageFilter);
    }

    public void sendMessage(String text, ChatMessageFilter chatMessageFilter) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(text);
        globalCommonConnectionService.sendChatMessage(chatMessage, chatMessageFilter);
    }

    public void onSetChatMessageFilterChanged(List<MessageIdPacket> messageIdPackets) {
        lastMessageIdPacket = null;
        chatListener.clearMessages();
        Collections.reverse(messageIdPackets);
        for (MessageIdPacket messageIdPacket : messageIdPackets) {
            if(messageIdPacket instanceof ChatMessage) {
                // DO not show the ServerRebootMessagePacket package again
                onMessageReceived(messageIdPacket);
            }
        }
    }
}
