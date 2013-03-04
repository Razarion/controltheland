package com.btxtech.game.services.connection.impl;

import com.btxtech.game.jsre.common.packets.MessageIdPacket;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: beat
 * Date: 02.04.2012
 * Time: 20:27:46
 */
public class MessageIdPacketQueue {
    private static final int OFFLINE_CHAT_QUEUE_SIZE = 10;
    private int lastMessageId;
    private final LinkedList<MessageIdPacket> queue = new LinkedList<>();

    public List<MessageIdPacket> peekMessages(Integer lastMessageId) {
        synchronized (queue) {
            if (lastMessageId == null) {
                return new ArrayList<>(queue);
            } else {
                List<MessageIdPacket> result = new ArrayList<>();
                for (MessageIdPacket messageIdPacket : queue) {
                    if (messageIdPacket.getMessageId() > lastMessageId) {
                        result.add(messageIdPacket);
                    } else {
                        break;
                    }
                }
                return result;
            }
        }
    }

    public void initAndPutMessage(MessageIdPacket messageIdPacket) {
        synchronized (queue) {
            while (queue.size() >= OFFLINE_CHAT_QUEUE_SIZE) {
                queue.removeLast();
            }
            messageIdPacket.setMessageId(lastMessageId);
            queue.addFirst(messageIdPacket);
            lastMessageId++;
        }
    }
}
