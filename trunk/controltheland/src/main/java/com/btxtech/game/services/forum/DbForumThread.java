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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:08:04
 */
@Entity(name = "FORUM_THREAD")
public class DbForumThread implements CrudChild<DbCategory>, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private Date date;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dbForumThread", fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @OrderBy("postDate ASC")
    private List<DbPost> posts;
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private DbCategory dbCategory;
    @Transient
    private CrudListChildServiceHelper<DbPost> postCrud;

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
        // Fist post        
        posts.get(0).setName(name);
    }

    public void setContent(String content) {
        posts.get(0).setContent(content);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public void init(UserService userService) {
        date = new Date();
        posts = new ArrayList<DbPost>();
        // There is never an empty thread
        getPostCrud().createDbChild(userService);
    }

    @Override
    public void setParent(DbCategory category) {
        this.dbCategory = category;
    }

    public CrudListChildServiceHelper<DbPost> getPostCrud() {
        if (postCrud == null) {
            postCrud = new CrudListChildServiceHelper<DbPost>(posts, DbPost.class, this, "user", null);
        }
        return postCrud;
    }

    public Date getLastPost() {
        if (getPostCrud().readDbChildren().isEmpty()) {
            return null;
        } else {
            return getPostCrud().readDbChildren().get(0).getDate();
        }
    }

    public int getPostCount() {
        return posts.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbForumThread that = (DbForumThread) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
