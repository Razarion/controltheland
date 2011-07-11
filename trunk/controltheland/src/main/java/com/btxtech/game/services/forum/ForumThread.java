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
 * Time: 17:08:04
 */
@Entity(name = "FORUM_THREAD")
public class ForumThread extends AbstractForumEntry implements CrudChild<Category>, CrudParent {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "forumThread", fetch = FetchType.LAZY)
    private List<Post> posts;
    private int viewCount = 100;
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;
    @Transient
    private CrudListChildServiceHelper<Post> postCrud;


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

    @Override
    public void init() {
        posts = new ArrayList<Post>();
        setDate();
    }

    @Override
    public void setParent(Category category) {
        setCategory(category);
    }

    public CrudListChildServiceHelper<Post> getPostCrud(UserService userService) {
        if (postCrud == null) {
            postCrud = new CrudListChildServiceHelper<Post>(posts, Post.class, this, userService, "user");
        }
        return postCrud;
    }
}
