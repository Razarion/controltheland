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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
 * Time: 17:16:58
 */
@Entity(name = "FORUM_SUB_FORUM")
public class DbSubForum implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private Date date;
    private String name;
    @Column(length = 5000)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dbSubForum", fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<DbCategory> categories;
    @Transient
    private CrudListChildServiceHelper<DbCategory> categoryCrud;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public void init(UserService userService) {
        date = new Date();
        categories = new ArrayList<DbCategory>();
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public CrudListChildServiceHelper<DbCategory> getCategoryCrud() {
        if (categoryCrud == null) {
            categoryCrud = new CrudListChildServiceHelper<DbCategory>(categories, DbCategory.class, this, "user", new ForumComparator<DbCategory>() {
                @Override
                protected Date getDate(DbCategory dbCategory) {
                    return dbCategory.getLastPost();
                }
            });
        }
        return categoryCrud;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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

    public Date getLastPost() {
        if (getCategoryCrud().readDbChildren().isEmpty()) {
            return null;
        } else {
            return getCategoryCrud().readDbChildren().get(0).getLastPost();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSubForum that = (DbSubForum) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

}
