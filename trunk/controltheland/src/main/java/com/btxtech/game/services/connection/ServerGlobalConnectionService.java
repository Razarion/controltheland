package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.user.UserState;
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

    void createConnectionStatisticsNoSession(String sessionId, double ticksPerSecond, int planetId);

    List<MessageIdPacket> pollMessageIdPackets(Integer lastMessageId, GameEngineMode gameEngineMode);

    void sendChatMessage(ChatMessage chatMessage);

    void sendServerRebootMessage(int rebootInSeconds, int downTimeInMinutes);

    void saveClientDebug(Date date, String category, String message);

    List<UserState> getAllOnlineMissionUserState();
}
