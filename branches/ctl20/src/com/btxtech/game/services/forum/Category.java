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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:14:52
 */
@Entity(name = "FORUM_CATEGORY")
public class Category extends AbstractForumEntry {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category", fetch = FetchType.LAZY)
    private List<ForumThread> forumThreads;
    @ManyToOne
    @JoinColumn(name = "subForumId", nullable = false)
    private SubForum subForum;


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
}
