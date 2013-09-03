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

import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 22.03.2010
 * Time: 19:13:42
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestForum extends AbstractServiceTest {
    @Autowired
    private ForumService forumService;
    @Autowired
    private UserService userService;

    public static void fillForum(ForumService forumService, UserService userService) {
        CrudRootServiceHelper<DbSubForum> subForumCrud = forumService.getSubForumCrud();
        DbSubForum subForum = subForumCrud.createDbChild();
        subForum.setName("SubForumName1");
        subForum.setContent("SubForumContent1");
        CrudListChildServiceHelper<DbCategory> categoryCrud = subForum.getCategoryCrud();
        DbCategory category = categoryCrud.createDbChild(userService);
        category.setName("CategoryName1");
        CrudListChildServiceHelper<DbForumThread> forumThreadCrud = category.getForumThreadCrud();
        DbForumThread forumThread = forumThreadCrud.createDbChild(userService);
        forumThread.setName("ForumThreadName1");
        forumThread.setContent("PostContent1");

        subForumCrud.updateDbChild(subForum);
    }

    @Test
    @DirtiesContext
    public void testCreateAndFillForum() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();

        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        fillForum(forumService, userService);
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testOrderForum() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();

        // Fill forum
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbSubForum> subForumCrud = forumService.getSubForumCrud();
        DbSubForum subForum1 = subForumCrud.createDbChild();
        subForum1.setName("SubForumName1");
        subForum1.setContent("SubForumContent1");
        CrudListChildServiceHelper<DbCategory> categoryCrud = subForum1.getCategoryCrud();
        DbCategory category1 = categoryCrud.createDbChild(userService);
        category1.setName("CategoryName1");
        CrudListChildServiceHelper<DbForumThread> forumThreadCrud = category1.getForumThreadCrud();
        DbForumThread dbForumThread1 = forumThreadCrud.createDbChild(userService);
        dbForumThread1.setName("ForumThreadName1");
        subForumCrud.updateDbChild(subForum1);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        DbSubForum subForum2 = subForumCrud.createDbChild();
        subForum2.setName("SubForumName2");
        subForum2.setContent("SubForumContent2");
        subForumCrud.updateDbChild(subForum2);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        DbSubForum subForum3 = subForumCrud.createDbChild();
        subForum3.setName("SubForumName3");
        subForum3.setContent("SubForumContent3");
        subForumCrud.updateDbChild(subForum3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Add categories
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        subForumCrud = forumService.getSubForumCrud();
        subForum1 = subForumCrud.readDbChild(subForum1.getId());
        categoryCrud = subForum1.getCategoryCrud();
        DbCategory category2 = categoryCrud.createDbChild(userService);
        category2.setName("CategoryName2");
        subForumCrud.updateDbChild(subForum1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify category
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        subForumCrud = forumService.getSubForumCrud();
        List<DbSubForum> dbSubForums = (List<DbSubForum>) subForumCrud.readDbChildren();
        List<DbCategory> dbCategories = dbSubForums.get(0).getCategoryCrud().readDbChildren();
        Assert.assertEquals("CategoryName1", dbCategories.get(0).getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Add thread and post
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        subForumCrud = forumService.getSubForumCrud();
        subForum1 = subForumCrud.readDbChild(subForum1.getId());
        category1 = subForum1.getCategoryCrud().readDbChild(category1.getId());
        DbForumThread dbForumThread2 = category1.getForumThreadCrud().createDbChild(userService);
        dbForumThread2.setName("ForumThreadName2");
        dbForumThread2.getPostCrud().readDbChildren().get(0).setContent("ForumPostContent2");
        subForumCrud.updateDbChild(subForum1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify ForumThread
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        subForumCrud = forumService.getSubForumCrud();
        subForum1 = subForumCrud.readDbChild(subForum1.getId());
        category1 = subForum1.getCategoryCrud().readDbChild(category1.getId());
        List<DbForumThread> dbForumThreads = category1.getForumThreadCrud().readDbChildren();
        Assert.assertEquals("ForumThreadName2", dbForumThreads.get(0).getName());
        Assert.assertEquals("ForumThreadName1", dbForumThreads.get(1).getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Add thread and post
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        subForumCrud = forumService.getSubForumCrud();
        subForum1 = subForumCrud.readDbChild(subForum1.getId());
        category1 = subForum1.getCategoryCrud().readDbChild(category1.getId());
        DbForumThread dbForumThread3 = category1.getForumThreadCrud().createDbChild(userService);
        dbForumThread3.setName("ForumThreadName3");
        subForumCrud.updateDbChild(subForum1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify ForumThread
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        subForumCrud = forumService.getSubForumCrud();
        subForum1 = subForumCrud.readDbChild(subForum1.getId());
        category1 = subForum1.getCategoryCrud().readDbChild(category1.getId());
        dbForumThreads = category1.getForumThreadCrud().readDbChildren();
        Assert.assertEquals("ForumThreadName3", dbForumThreads.get(0).getName());
        Assert.assertEquals("ForumThreadName2", dbForumThreads.get(1).getName());
        Assert.assertEquals("ForumThreadName1", dbForumThreads.get(2).getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Add post
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        subForumCrud = forumService.getSubForumCrud();
        subForum1 = subForumCrud.readDbChild(subForum1.getId());
        category1 = subForum1.getCategoryCrud().readDbChild(category1.getId());
        dbForumThread1 = category1.getForumThreadCrud().readDbChild(dbForumThread1.getId());
        DbPost dbPost4 = dbForumThread1.getPostCrud().createDbChild(userService);
        dbPost4.setName("PostName4");
        subForumCrud.updateDbChild(subForum1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify post
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        subForumCrud = forumService.getSubForumCrud();
        subForum1 = subForumCrud.readDbChild(subForum1.getId());
        category1 = subForum1.getCategoryCrud().readDbChild(category1.getId());
        dbForumThread1 = category1.getForumThreadCrud().readDbChild(dbForumThread1.getId());
        List<DbPost> dbPosts = dbForumThread1.getPostCrud().readDbChildren();
        Assert.assertEquals("ForumThreadName1", dbPosts.get(0).getName());
        Assert.assertEquals("PostName4", dbPosts.get(1).getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void testLastPost() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();

        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        fillForum(forumService, userService);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbSubForum> subForumCrud = forumService.getSubForumCrud();
        DbSubForum dbSubForum = CommonJava.getFirst(subForumCrud.readDbChildren());
        DbForumThread dbForumThread = dbSubForum.getCategoryCrud().readDbChildren().get(0).getForumThreadCrud().readDbChildren().get(0);
        DbPost oldPost = dbForumThread.getPostCrud().createDbChild(userService);
        Thread.sleep(100); // Dates should be different
        DbPost newPost = dbForumThread.getPostCrud().createDbChild(userService);
        subForumCrud.updateDbChild(dbSubForum);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbSubForum = CommonJava.getFirst(subForumCrud.readDbChildren());
        dbForumThread = dbSubForum.getCategoryCrud().readDbChildren().get(0).getForumThreadCrud().readDbChildren().get(0);
        oldPost = dbForumThread.getPostCrud().readDbChild(oldPost.getId());
        newPost = dbForumThread.getPostCrud().readDbChild(newPost.getId());
        Assert.assertEquals(newPost.getDate(), dbForumThread.getLastPost());
        Assert.assertEquals(newPost.getDate(), dbForumThread.getPostCrud().readDbChildren().get(0).getDate());
        Assert.assertEquals(oldPost.getDate(), dbForumThread.getPostCrud().readDbChildren().get(1).getDate());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

}