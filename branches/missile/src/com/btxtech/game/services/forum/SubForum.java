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

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:16:58
 */
@Entity(name = "FORUM_SUB_FORUM")
public class SubForum extends AbstractForumEntry {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subForum", fetch = FetchType.LAZY)
    private List<Category> categories;

    public void addCategory(Category category) {
        if (categories == null) {
            categories = new ArrayList<Category>();
        }
        category.setDate();
        category.setSubForum(this);
        categories.add(category);
    }

    public List<Category> getCategories() {
        return categories;
    }
}
