package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: 27.05.13
 * Time: 14:28
 */
public class UserAttentionPacket extends Packet {
    public enum Type {
        RAISE,
        CLEAR
    }

    private Type news;

    public Type getNews() {
        return news;
    }

    public void setNews(Type news) {
        this.news = news;
    }

    public boolean isNews() {
        return news != null;
    }
}
