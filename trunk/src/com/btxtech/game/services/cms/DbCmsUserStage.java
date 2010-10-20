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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 14.12.2009
 * Time: 22:47:20
 */
@Entity(name = "CMS_USER_STAGE")
public class DbCmsUserStage implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private String bgContentType;
    @Column(nullable = false, length = 500000)
    private byte[] bgData;
    @Column(nullable = false, length = 500000)    
    private String layout;
    private boolean isActive;

    public String getBgContentType() {
        return bgContentType;
    }

    public void setBgContentType(String bgContentType) {
        this.bgContentType = bgContentType;
    }

    public byte[] getBgData() {
        return bgData;
    }

    public void setBgData(byte[] bgData) {
        this.bgData = bgData;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbCmsUserStage that = (DbCmsUserStage) o;
        return id != null ? id.equals(that.id) : super.equals(that);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }
}