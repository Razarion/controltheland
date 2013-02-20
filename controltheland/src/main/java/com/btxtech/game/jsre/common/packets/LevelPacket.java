package com.btxtech.game.jsre.common.packets;

import com.btxtech.game.jsre.client.common.LevelScope;

/**
 * User: beat
 * Date: 29.06.12
 * Time: 14:17
 */
public class LevelPacket extends Packet {
    private LevelScope levelScope;

    public LevelScope getLevel() {
        return levelScope;
    }

    public void setLevel(LevelScope levelScope) {
        this.levelScope = levelScope;
    }

    @Override
    public String toString() {
        return "LevelPacket{levelScope=" + levelScope + '}';
    }
}
