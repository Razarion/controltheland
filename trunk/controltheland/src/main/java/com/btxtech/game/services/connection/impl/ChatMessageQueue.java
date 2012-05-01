package com.btxtech.game.services.connection.impl;

import com.btxtech.game.jsre.client.common.ChatMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: beat
 * Date: 02.04.2012
 * Time: 20:27:46
 */
public class ChatMessageQueue {
    private static final int OFFLINE_CHAT_QUEUE_SIZE = 10;
    private int lastMessageId;
    private final LinkedList<ChatMessage> queue = new LinkedList<ChatMessage>();

    public List<ChatMessage> peekMessages(Integer lastMessageId) {
        synchronized (queue) {
            if (lastMessageId == null) {
                return new ArrayList<ChatMessage>(queue);
            } else {
                List<ChatMessage> result = new ArrayList<ChatMessage>();
                for (ChatMessage chatMessage : queue) {
                    if (chatMessage.getMessageId() > lastMessageId) {
                        result.add(chatMessage);
                    } else {
                        break;
                    }
                }
                return result;
            }
        }
    }

    public void initAndPutMessage(String name, ChatMessage chatMessage) {
        synchronized (queue) {
            while (queue.size() >= OFFLINE_CHAT_QUEUE_SIZE) {
                queue.removeLast();
            }
            chatMessage.setName(name);
            chatMessage.setMessageId(lastMessageId);
            queue.addFirst(chatMessage);
            lastMessageId++;
        }
    }
}
