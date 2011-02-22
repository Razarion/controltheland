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
package com.btxtech.game.services.from;

import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.forum.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: beat
 * Date: 22.03.2010
 * Time: 19:13:42
 */
public class TestForum extends BaseTestService {
    @Autowired
    private ForumService forumService;

    @Test
    public void testCreateSubForum() {
        SubForum subForum = (SubForum) forumService.createForumEntry(SubForum.class);
        subForum.setTitle("Test");
        subForum.setContent("Content");
        //forumService.insertForumEntry(0, subForum);
    }

    @Test
    public void testCreateCategory() {
        Category category = (Category) forumService.createForumEntry(Category.class);
        category.setTitle("Test");
        category.setContent("Content");
        //int id = forumService.getSubForums().get(0).getId();
        //forumService.insertForumEntry(id, category);
    }

    @Test
    public void testCreateThread() {
        ForumThread forumThread = (ForumThread) forumService.createForumEntry(ForumThread.class);
        forumThread.setTitle("Test");
        forumThread.setContent("Content");
        //SubForum subForum = forumService.getSubForums().get(0);
        //int categoryId = forumService.getCategories(subForum).get(0).getId();
        //forumService.insertForumEntry(categoryId, forumThread);
    }

    @Test
    public void testCreatePost() {
        Post post = (Post) forumService.createForumEntry(Post.class);
        post.setTitle("POst");
        post.setContent("POst POst POstPOstPOstPOst");
        //SubForum subForum = forumService.getSubForums().get(0);
        //Category category = forumService.getCategories(subForum).get(0);
        //int forumThreadId = forumService.getForumThreads(category).get(0).getId();
        //forumService.insertForumEntry(forumThreadId, post);
    }

    @Test
    public void testDisplayForum() {
        List<SubForum> subForums = forumService.getSubForums();
        for (SubForum subForum : subForums) {
            subForum.getId();
            subForum.getTitle();
            subForum.getContent();
            for (Category category : forumService.getCategories(subForum)) {
                category.getId();
                category.getTitle();
                category.getDate();
                category.getThreadCount();
                for (ForumThread forumThread : forumService.getForumThreads(category)) {
                    forumThread.getTitle();
                    forumThread.getDate();
                    forumThread.getPostCount();
                    forumThread.getViewCount();
                    for (Post post : forumThread.getPosts()) {
                        post.getContent();
                        post.getDate();
                        post.getContent();
                    }
                }
            }

        }
    }

    @Test
    public void testDelete() {
        List<SubForum> subForums = forumService.getSubForums();
        //forumService.delete(subForums.get(0));
    }

}
