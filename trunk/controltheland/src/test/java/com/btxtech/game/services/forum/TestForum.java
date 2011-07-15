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

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

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
        CrudRootServiceHelper<SubForum> subForumCrud = forumService.getSubForumCrud();
        SubForum subForum = subForumCrud.createDbChild();
        subForum.setName("SubForumName1");
        subForum.setContent("SubForumContent1");
        CrudListChildServiceHelper<Category> categoryCrud = subForum.getCategoryCrud(userService);
        Category category = categoryCrud.createDbChild();
        category.setName("CategoryName1");
        category.setContent("CategoryContent1");
        CrudListChildServiceHelper<ForumThread> forumThreadCrud = category.getForumThreadCrud(userService);
        ForumThread forumThread = forumThreadCrud.createDbChild();
        forumThread.setName("ForumThreadName1");
        forumThread.setContent("PostContent1");

        subForumCrud.updateDbChild(subForum);
    }

    @Test
    public void testCreateAndFillForum() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        SubForum subForum = (SubForum) forumService.createForumEntry(SubForum.class);
        subForum.setTitle("Test");
        subForum.setContent("Content");
        forumService.insertSubForumEntry(subForum);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        fillForum(forumService, userService);
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }
}