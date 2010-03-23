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
 * Time: 17:08:04
 */
@Entity(name = "FORUM_THREAD")
public class ForumThread extends AbstractForumEntry {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "forumThread", fetch = FetchType.LAZY)
    private List<Post> posts;
    private int viewCount = 100;
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    public void addPost(Post post) {
        if (posts == null) {
            posts = new ArrayList<Post>();
        }
        post.setDate();
        post.setForumThread(this);
        posts.add(post);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public int getPostCount() {
        return posts.size();
    }

    public int getReplyCount() {
        return posts.size() - 1;
    }

    public int getViewCount() {
        return viewCount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
