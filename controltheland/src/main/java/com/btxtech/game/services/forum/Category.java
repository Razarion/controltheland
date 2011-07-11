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
import com.btxtech.game.services.user.UserService;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:14:52
 */
@Entity(name = "FORUM_CATEGORY")
public class Category extends AbstractForumEntry implements CrudChild<SubForum>, CrudParent {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category", fetch = FetchType.LAZY)
    private List<ForumThread> forumThreads;
    @ManyToOne
    @JoinColumn(name = "subForumId", nullable = false)
    private SubForum subForum;
    @Transient
    private CrudListChildServiceHelper<ForumThread> forumThreadCrud;


    public void addForumThread(ForumThread forumThread) {
        if (forumThreads == null) {
            forumThreads = new ArrayList<ForumThread>();
        }
        forumThread.setDate();
        forumThread.setCategory(this);
        forumThreads.add(forumThread);
    }

    public List<ForumThread> getForumThreads() {
        return forumThreads;
    }

    public int getThreadCount() {
        return forumThreads.size();
    }

    public SubForum getSubForum() {
        return subForum;
    }

    public void setSubForum(SubForum subForum) {
        this.subForum = subForum;
    }

    @Override
    public void init() {
        forumThreads = new ArrayList<ForumThread>();
        setDate();
    }

    @Override
    public void setParent(SubForum subForum) {
        setSubForum(subForum);
    }

    public CrudListChildServiceHelper<ForumThread> getForumThreadCrud(UserService userService) {
        if(forumThreadCrud == null) {
            forumThreadCrud = new CrudListChildServiceHelper<ForumThread>(forumThreads, ForumThread.class, this, userService, "user");
        }
        return forumThreadCrud;
    }
}
