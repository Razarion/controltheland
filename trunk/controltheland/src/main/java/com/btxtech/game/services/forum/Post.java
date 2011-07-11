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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:07:38
 */
@Entity(name = "FORUM_POST")
public class Post extends AbstractForumEntry implements CrudChild<ForumThread> {
    @ManyToOne
    @JoinColumn(name = "forumThreadId", nullable = false)
    private ForumThread forumThread;

    public ForumThread getForumThread() {
        return forumThread;
    }

    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
    }

    @Override
    public void init() {
    }

    @Override
    public void setParent(ForumThread forumThread) {
        setForumThread(forumThread);
    }
}
