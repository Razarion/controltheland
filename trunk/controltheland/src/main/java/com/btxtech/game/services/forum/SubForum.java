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
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:16:58
 */
@Entity(name = "FORUM_SUB_FORUM")
public class SubForum extends AbstractForumEntry implements CrudChild, CrudParent {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subForum", fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<Category> categories;
    @Transient
    private CrudListChildServiceHelper<Category> categoryCrud;

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

    @Override
    public void init(UserService userService) {
        categories = new ArrayList<Category>();
        setDate();
    }

    @Override
    public void setParent(Object o) {
    }

    public CrudListChildServiceHelper<Category> getCategoryCrud() {
        if (categoryCrud == null) {
            categoryCrud = new CrudListChildServiceHelper<Category>(categories, Category.class, this, "user");
        }
        return categoryCrud;
    }
}
