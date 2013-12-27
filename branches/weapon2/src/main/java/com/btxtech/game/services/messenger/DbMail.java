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

package com.btxtech.game.services.messenger;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 31.03.2010
 * Time: 22:25:12
 */
@Entity(name = "MESSENGER_MAIL")
public class DbMail implements Serializable, CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false, length = 5000)
    private String toUsers;
    @Column(nullable = false)
    private String fromUser;
    @Column(nullable = false, length = 1000)
    private String subject;
    @Column(nullable = false, length = 10000)
    private String body;
    @Column(nullable = false)
    private Date sent;
    @Column(name = "read_flag")    
    private boolean read = false;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User user;


    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public String getToUsers() {
        return toUsers;
    }

    public void setToUsers(String toUsers) {
        this.toUsers = toUsers;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getSent() {
        return sent;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbMail dbMail = (DbMail) o;

        return id != null && id.equals(dbMail.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
