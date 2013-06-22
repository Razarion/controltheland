package com.btxtech.game.jsre.client.dialogs.history;

import java.io.Serializable;

/**
 * User: beat
 * Date: 21.06.13
 * Time: 15:07
 */
public class HistoryFilter implements Serializable {
    public enum Type {
        USER,
        GUILD
    }

    private Type type;
    private Integer guildId;
    private Integer userId;
    private int start;
    private int length;

    /**
     * Used by GWT
     */
    HistoryFilter() {
    }

    public static HistoryFilter createUserFilter() {
        HistoryFilter historyFilter = new HistoryFilter();
        historyFilter.type = Type.USER;
        return historyFilter;
    }

    public static HistoryFilter createGuildFilter(int guildId) {
        HistoryFilter historyFilter = new HistoryFilter();
        historyFilter.type = Type.GUILD;
        historyFilter.guildId = guildId;
        return historyFilter;
    }

    public Type getType() {
        return type;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
