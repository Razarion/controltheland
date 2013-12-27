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
import com.btxtech.game.services.user.UserService;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 04.06.2011
 * Time: 01:14:51
 */
@Entity(name = "CMS_IMAGE")
public class DbCmsImage implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private String contentType;
    @Column(length = 500000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    @Override
    public Integer getId() {
        return id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbCmsImage)) return false;

        DbCmsImage that = (DbCmsImage) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
