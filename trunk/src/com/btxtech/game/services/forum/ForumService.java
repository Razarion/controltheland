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

import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:04:50
 */
public interface ForumService {
    List<SubForum> getSubForums();

    Category getCategory(int categoryId);

    List<Category> getCategories(SubForum subForumId);

    ForumThread getForumThread(int forumThreadId);

    List<ForumThread> getForumThreads(Category category);

    List<Post> getPosts(final ForumThread forumThread);

    AbstractForumEntry createForumEntry(Class<? extends AbstractForumEntry> aClass);

    void insertForumEntry(int parentId, AbstractForumEntry abstractForumEntry);

    Date getLatestPost(Category category);

    int getPostCount(Category category);
}
