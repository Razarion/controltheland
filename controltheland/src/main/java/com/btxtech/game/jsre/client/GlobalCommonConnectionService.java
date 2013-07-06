package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.packets.ChatMessage;

/**
 * User: beat
 * Date: 03.04.2012
 * Time: 17:18:44
 */
public interface GlobalCommonConnectionService {
    void sendChatMessage(ChatMessage chatMessage, ChatMessageFilter chatMessageFilter);

    void pollChatMessages(Integer lastMessageId, ChatMessageFilter chatMessageFilter);
}
