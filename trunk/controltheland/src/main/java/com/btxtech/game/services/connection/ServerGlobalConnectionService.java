package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 23.09.12
 * Time: 12:09
 */
public interface ServerGlobalConnectionService {
    Session getSession();

    void createConnectionStatisticsNoSession(String baseName, String sessionId, double ticksPerSecond);

    List<MessageIdPacket> pollMessageIdPackets(Integer lastMessageId);

    void sendChatMessage(ChatMessage chatMessage);

    void sendServerRebootMessage(int rebootInSeconds, int downTimeInMinutes);

    Collection<SimpleBase> getOnlineBases();

    void saveClientDebug(Date date, String category, String message);
}
