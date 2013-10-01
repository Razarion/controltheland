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
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:14:52
 */
@Entity(name = "FORUM_CATEGORY")
public class DbCategory implements CrudChild<DbSubForum>, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private Date date;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dbCategory", fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<DbForumThread> forumThreads;
    @ManyToOne
    @JoinColumn(name = "subForumId", nullable = false)
    private DbSubForum dbSubForum;
    @Transient
    private CrudListChildServiceHelper<DbForumThread> forumThreadCrud;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init(UserService userService) {
        forumThreads = new ArrayList<DbForumThread>();
        date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void setParent(DbSubForum subForum) {
        this.dbSubForum = subForum;
    }

    @Override
    public DbSubForum getParent() {
        return dbSubForum;
    }

    public CrudListChildServiceHelper<DbForumThread> getForumThreadCrud() {
        if (forumThreadCrud == null) {
            forumThreadCrud = new CrudListChildServiceHelper<DbForumThread>(forumThreads, DbForumThread.class, this, "user", new ForumComparator<DbForumThread>() {
                @Override
                protected Date getDate(DbForumThread dbForumThread) {
                    return dbForumThread.getLastPost();
                }
            });
        }
        return forumThreadCrud;
    }

    public int getPostCount() {
        int postCount = 0;
        for (DbForumThread forumThread : forumThreads) {
            postCount += forumThread.getPostCount();
        }
        return postCount;
    }

    public Date getLastPost() {
        if (getForumThreadCrud().readDbChildren().isEmpty()) {
            return null;
        } else {
            return getForumThreadCrud().readDbChildren().get(0).getLastPost();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbCategory that = (DbCategory) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
