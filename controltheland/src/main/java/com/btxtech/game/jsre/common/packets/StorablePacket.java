package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: 28.06.13
 * Time: 13:25
 */
public class StorablePacket extends Packet {
    public enum Type {
        GUILD_LOST
    }
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
