package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.user.UserState;

import java.util.List;

/**
 * User: beat
 * Date: 03.07.13
 * Time: 11:34
 */
public interface MessageIdPacketQueue {
    void initAndPutMessage(MessageIdPacket messageIdPacket);

    void setFilterAndPutMessage(ChatMessage chatMessage, ChatMessageFilter chatMessageFilter);

    List<MessageIdPacket> peekMessages(Integer lastMessageId, ChatMessageFilter chatMessageFilter);

    Packet convertPacketIfNecessary(Packet packet, ChatMessageFilter chatMessageFilter, UserState userState);
}
