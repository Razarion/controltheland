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

package com.btxtech.game.services.forum.impl;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.forum.DbSubForum;
import com.btxtech.game.services.forum.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:05:26
 */
@Component("forumService")
public class ForumServiceImpl implements ForumService {
    @Autowired
    private CrudRootServiceHelper<DbSubForum> subForumCrud;

    @PostConstruct
    public void init() {
        subForumCrud.init(DbSubForum.class, null, false, false, "user");
    }

    @Override
    public CrudRootServiceHelper<DbSubForum> getSubForumCrud() {
        return subForumCrud;
    }
}
