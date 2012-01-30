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

package com.btxtech.game.services.forum;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:07:38
 */
@Entity(name = "FORUM_POST")
public class DbPost implements CrudChild<DbForumThread> {
    @Id
    @GeneratedValue
    private Integer id;
    private Date postDate;
    private String name;
    @Column(length = 5000)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "forumThreadId", nullable = false)
    private DbForumThread dbForumThread;

    @Override
    public Serializable getId() {
        return id;
    }

    public Date getDate() {
        return postDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void init(UserService userService) {
        postDate = new Date();
    }

    @Override
    public void setParent(DbForumThread dbForumThread) {
        this.dbForumThread = dbForumThread;
    }

    @Override
    public DbForumThread getParent() {
        return dbForumThread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbPost that = (DbPost) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
