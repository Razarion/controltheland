/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: 12.03.2010
 * Time: 23:42:44
 */
public class ChatMessage extends MessageIdPacket {
    public enum Type {
        OWN,
        GUILD,
        ENEMY,
        // Admin is never set
        ADMIN,
    }

    private String name;
    private Integer userId;
    private String message;
    private Type type;
    private Integer guildId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public boolean hasGuild() {
        return guildId != null;
    }

    public boolean isSameGuild(int guildId) {
        return this.guildId != null && this.guildId == guildId;
    }

    public void setGuildId(Integer guildId) {
        this.guildId = guildId;
    }

    public Integer getUserId() {
        return userId;
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isSameUser(Integer userId) {
        return this.userId != null && userId != null && this.userId.equals(userId);
    }

    public ChatMessage getCopy() {
        ChatMessage copy = new ChatMessage();
        copy(copy);
        copy.name = name;
        copy.userId = userId;
        copy.message = message;
        copy.type = type;
        copy.guildId = guildId;
        return copy;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageId=" + getMessageId() +
                ", guildId=" + guildId +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", type=" + type +
                '}';
    }
}
