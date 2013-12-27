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

package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.common.packets.ChatMessage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 13.03.2010
 * Time: 13:17:24
 */
@Entity(name = "TRACKER_CHAT_MESSAGE")
public class DbChatMessage implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private Date timeStamp;
    private String sessionId;
    private String name;
    private Integer userId;
    @Column(length = 1000)
    private String message;
    private Integer guildId;

    /**
     * Used by Hibernate
     */
    public DbChatMessage() {
    }

    public DbChatMessage(String sessionId, ChatMessage chatMessage) {
        this.sessionId = sessionId;
        timeStamp = new Date();
        name = chatMessage.getName();
        message = chatMessage.getMessage();
        userId = chatMessage.getUserId();
        guildId = chatMessage.getGuildId();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

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

    public Integer getGuildId() {
        return guildId;
    }

    public Integer getUserId() {
        return userId;
    }

    public ChatMessage createMessageIdPacket() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setName(name);
        chatMessage.setUserId(userId);
        chatMessage.setGuildId(guildId);
        return chatMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbChatMessage that = (DbChatMessage) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}