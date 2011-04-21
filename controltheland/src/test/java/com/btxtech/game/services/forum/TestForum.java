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

    @Test
    public void testCreateSubForum() throws Exception {
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
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        Assert.assertEquals(1, forumService.getSubForums().size());
        Assert.assertEquals("Test", forumService.getSubForums().get(0).getTitle());
        Assert.assertEquals("Content", forumService.getSubForums().get(0).getContent());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    public void testCreateCategory() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Category category = (Category) forumService.createForumEntry(Category.class);
        category.setTitle("TestCategory");
        category.setContent("ContentCategory");
        int id = forumService.getSubForums().get(0).getId();
        forumService.insertCategoryEntry(id, category);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SubForum> subForums = forumService.getSubForums();
        Assert.assertEquals(1, subForums.size());
        List<Category> categories = subForums.get(0).getCategories();
        Assert.assertEquals(1, categories.size());
        Assert.assertEquals("Test", subForums.get(0).getTitle());
        Assert.assertEquals("TestCategory", categories.get(0).getTitle());
        Assert.assertEquals("ContentCategory", categories.get(0).getContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    public void testCreateThread() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Category category = forumService.getSubForums().get(0).getCategories().get(0);
        ForumThread forumThread = (ForumThread) forumService.createForumEntry(ForumThread.class);
        forumThread.setTitle("ForumThreadTest");
        forumThread.setContent("ForumThreadContent");
        forumService.insertForumThreadEntry(category.getId(), forumThread);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SubForum> subForums = forumService.getSubForums();
        Assert.assertEquals(1, subForums.size());
        List<Category> categories = subForums.get(0).getCategories();
        Assert.assertEquals(1, categories.size());
        List<ForumThread> forumThreads = categories.get(0).getForumThreads();
        Assert.assertEquals(1, forumThreads.size());
        Assert.assertEquals("ForumThreadTest", forumThreads.get(0).getTitle());
        Assert.assertEquals("", forumThreads.get(0).getContent());
        Assert.assertEquals(1, forumThreads.get(0).getPosts().size());
        Assert.assertEquals("ForumThreadTest", forumThreads.get(0).getPosts().get(0).getTitle());
        Assert.assertEquals("ForumThreadContent", forumThreads.get(0).getPosts().get(0).getContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    public void testCreatePost() throws InterruptedException {
        Thread.sleep(1000); // To make date newer -> MySql does not have ms
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Post post = (Post) forumService.createForumEntry(Post.class);
        post.setTitle("POst");
        post.setContent("POst POst POstPOstPOstPOst");
        ForumThread forumThread = forumService.getSubForums().get(0).getCategories().get(0).getForumThreads().get(0);
        forumService.insertPostEntry(forumThread.getId(), post);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        forumThread = forumService.getSubForums().get(0).getCategories().get(0).getForumThreads().get(0);
        Assert.assertEquals(2, forumThread.getPosts().size());
        List<Post> posts = forumService.getPosts(forumThread);
        Post previous = null;
        for (Post post1 : posts) {
            if (previous != null) {
                Assert.assertTrue(post1.getDate().getTime() >= previous.getDate().getTime());
            }
            previous = post1;
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    public void testCreateMultiplePost() throws InterruptedException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        ForumThread forumThread = forumService.getSubForums().get(0).getCategories().get(0).getForumThreads().get(0);
        Post lastPost = null;
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000); // To make date newer -> MySql does not have ms
            Post post = (Post) forumService.createForumEntry(Post.class);
            post.setTitle("post" + i);
            post.setContent("content" + i);
            lastPost = post;
            forumService.insertPostEntry(forumThread.getId(), post);
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify order
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        forumThread = forumService.getSubForums().get(0).getCategories().get(0).getForumThreads().get(0);
        Assert.assertEquals(7, forumThread.getPosts().size());
        List<Post> posts = forumService.getPosts(forumThread);
        Post previous = null;
        for (Post post1 : posts) {
            if (previous != null) {
                // Test newest post most bottom
                Assert.assertTrue(post1.getDate().getTime() >= previous.getDate().getTime());
            }
            previous = post1;
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify last post
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Category category = forumService.getCategories(forumService.getSubForums().get(0)).get(0);
        Assert.assertNotNull(lastPost);
        Assert.assertEquals(lastPost.getDate(), category.getLastPost());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    public void testMultipleCategoriesAndPost() throws InterruptedException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Category category1 = (Category) forumService.createForumEntry(Category.class);
        category1.setTitle("TestCategory1");
        category1.setContent("ContentCategory1");
        forumService.insertCategoryEntry(forumService.getSubForums().get(0).getId(), category1);

        Category category2 = (Category) forumService.createForumEntry(Category.class);
        category2.setTitle("TestCategory2");
        category2.setContent("ContentCategory2");
        forumService.insertCategoryEntry(forumService.getSubForums().get(0).getId(), category2);

        Thread.sleep(1000);
        ForumThread forumThread1 = (ForumThread) forumService.createForumEntry(ForumThread.class);
        forumThread1.setTitle("ForumThreadTest1");
        forumThread1.setContent("ForumThreadContent1");
        forumService.insertForumThreadEntry(category1.getId(), forumThread1);

        Thread.sleep(1000);
        ForumThread forumThread2 = (ForumThread) forumService.createForumEntry(ForumThread.class);
        forumThread2.setTitle("ForumThreadTest2");
        forumThread2.setContent("ForumThreadContent2");
        forumService.insertForumThreadEntry(category2.getId(), forumThread2);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify last post
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<Category> categories = forumService.getCategories(forumService.getSubForums().get(0));
        Assert.assertEquals(3, categories.size());
        Assert.assertEquals("TestCategory2", categories.get(0).getTitle());
        Assert.assertEquals("TestCategory1", categories.get(1).getTitle());
        Assert.assertEquals("TestCategory", categories.get(2).getTitle());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    public void testAddThread() throws InterruptedException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Category oldestCategory = null;
        for (Category category : forumService.getCategories(forumService.getSubForums().get(0))) {
            if(category.getTitle().equals("TestCategory")) {
                oldestCategory = category;
                break;
            }
        }
        Assert.assertNotNull(oldestCategory);
        Thread.sleep(1000);
        ForumThread forumThread2 = (ForumThread) forumService.createForumEntry(ForumThread.class);
        forumThread2.setTitle("ForumThreadTest3");
        forumThread2.setContent("ForumThreadContent3");
        forumService.insertForumThreadEntry(oldestCategory.getId(), forumThread2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify last post
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<Category> categories = forumService.getCategories(forumService.getSubForums().get(0));
        Assert.assertEquals(3, categories.size());
        Assert.assertEquals("TestCategory", categories.get(0).getTitle());
        Assert.assertEquals("TestCategory2", categories.get(1).getTitle());
        Assert.assertEquals("TestCategory1", categories.get(2).getTitle());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    public void testAddPost() throws InterruptedException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Category myCategory = null;
        for (Category category : forumService.getCategories(forumService.getSubForums().get(0))) {
            if(category.getTitle().equals("TestCategory")) {
                myCategory = category;
                break;
            }
        }
        Assert.assertNotNull(myCategory);
        List<ForumThread> forumThreads = forumService.getForumThreads(myCategory);
        Assert.assertEquals(2, forumThreads.size());
        Assert.assertEquals(myCategory.getLastPost(), forumThreads.get(0).getLastPost());
        List<Post> posts = forumService.getPosts(forumThreads.get(0));
        Assert.assertEquals(forumThreads.get(0).getLastPost(), posts.get(0).getDate());

        Thread.sleep(1000);
        Post post = (Post) forumService.createForumEntry(Post.class);
        post.setTitle("ForumThreadTest6");
        post.setContent("ForumThreadContent6");
        forumService.insertPostEntry(myCategory.getForumThreads().get(1).getId(), post);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        
        forumThreads = forumService.getForumThreads(myCategory);
        posts = forumService.getPosts(forumThreads.get(0));
        Assert.assertEquals(forumThreads.get(0).getLastPost(), posts.get(posts.size() -1).getDate());    

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    public void testDelete() {
        // TODO
    }

}
