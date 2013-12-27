package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: 03.03.13
 * Time: 19:12
 */
public class MessageIdPacket extends Packet{
    private int messageId;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    protected void copy(MessageIdPacket copy) {
        copy.messageId = messageId;
    }
}
