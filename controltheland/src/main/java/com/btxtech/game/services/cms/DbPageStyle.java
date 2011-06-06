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

package com.btxtech.game.services.cms;

import com.btxtech.game.services.common.CrudChild;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 04.06.2011
 * Time: 01:14:41
 */
@Entity(name = "CMS_PAGE_STYLE")
public class DbPageStyle implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 500000)
    private String css;
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init() {
    }

    @Override
    public void setParent(Object o) {
        // Ignore
    }

    public Integer getId() {
        return id;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getCss() {
        return css;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbPageStyle)) return false;

        DbPageStyle that = (DbPageStyle) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
