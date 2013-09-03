package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.common.packets.ChatMessage;

/**
 * User: beat
 * Date: 03.04.2012
 * Time: 16:59:07
 */
public interface ChatListener {
    void clearMessages();

    void addMessage(ChatMessage chatMessage);

    ChatMessageFilter getChatMessageFilter();
}
