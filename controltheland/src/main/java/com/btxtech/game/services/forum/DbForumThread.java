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
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
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
    private List<DbPost> posts;
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private DbCategory dbCategory;
    @Transient
    private CrudListChildServiceHelper<DbPost> postCrud;
    @Transient
    private CrudListChildServiceHelper<DbPost> postCrudLastDate;

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
        posts = new ArrayList<>();
        // There is never an empty thread
        getPostCrud().createDbChild(userService);
    }

    @Override
    public void setParent(DbCategory category) {
        this.dbCategory = category;
    }

    @Override
    public DbCategory getParent() {
        return dbCategory;
    }

    public CrudListChildServiceHelper<DbPost> getPostCrud() {
        if (postCrud == null) {
            postCrud = new CrudListChildServiceHelper<>(posts, DbPost.class, this, "user", new Comparator<DbPost>() {
                @Override
                public int compare(DbPost o1, DbPost o2) {
                    Date d1 = o1.getDate();
                    Date d2 = o2.getDate();
                    if (d1 == null && d2 == null) {
                        return 0;
                    } else if (d1 == null) {
                        return -1;
                    } else if (d2 == null) {
                        return 1;
                    } else {
                        return d1.compareTo(d2);
                    }
                }
            });
        }
        return postCrud;
    }

    private CrudListChildServiceHelper<DbPost> getPostCrudLastDate() {
        if (postCrudLastDate == null) {
            postCrudLastDate = new CrudListChildServiceHelper<>(posts, DbPost.class, this, "user", new ForumComparator<DbPost>() {
                @Override
                protected Date getDate(DbPost dbPost) {
                    return dbPost.getDate();
                }
            });
        }
        return postCrudLastDate;
    }

    public Date getLastPost() {
        if (getPostCrudLastDate().readDbChildren().isEmpty()) {
            return null;
        } else {
            return getPostCrudLastDate().readDbChildren().get(0).getDate();
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
