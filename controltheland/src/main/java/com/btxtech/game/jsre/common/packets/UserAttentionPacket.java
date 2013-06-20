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
    private Type guildMembershipRequest;
    private Type guildInvitation;

    public Type getNews() {
        return news;
    }

    public void setNews(Type news) {
        this.news = news;
    }

    public boolean isNews() {
        return news != null;
    }

    public Type getGuildMembershipRequest() {
        return guildMembershipRequest;
    }

    public void setGuildMembershipRequest(Type guildMembershipRequest) {
        this.guildMembershipRequest = guildMembershipRequest;
    }

    public Type getGuildInvitation() {
        return guildInvitation;
    }

    public void setGuildInvitation(Type guildInvitation) {
        this.guildInvitation = guildInvitation;
    }

    @Override
    public String toString() {
        return "UserAttentionPacket{" +
                "news=" + news +
                ", guildMembershipRequest=" + guildMembershipRequest +
                ", guildInvitation=" + guildInvitation +
                '}';
    }
}
