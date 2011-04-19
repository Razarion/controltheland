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
import java.io.Serializable;

/**
 * User: beat
 * Date: 07.07.2010
 * Time: 20:48:15
 */
@Entity(name = "CMS_HOME_LAYOUT")
public class DbCmsHomeLayout implements Serializable, CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private boolean isActive;
    @Column(length = 500000)
    private byte[] bgImage;
    private String bgImageContentType;
    @Column(length = 500000)
    private byte[] startImage;
    private String startImageContentType;
    @Column(length = 500000)
    private byte[] infoImage;
    private String infoImageContentType;
    @Column(length = 500000)
    private byte[] registerImage;
    private String registerImageContentType;
    @Column(length = 500000)
    private byte[] borderImage;
    private String borderImageContentType;
    private String internalName;
    @Column(length = 500000)
    private String cssString;

    @Override
    public Serializable getId() {
        return id;
    }

    public byte[] getBgImage() {
        return bgImage;
    }

    public void setBgImage(byte[] bgImage) {
        this.bgImage = bgImage;
    }

    public void setBgImageContentType(String bgImageContentType) {
        this.bgImageContentType = bgImageContentType;
    }

    public String getBgImageContentType() {
        return bgImageContentType;
    }

    public byte[] getStartImage() {
        return startImage;
    }

    public void setStartImage(byte[] startImage) {
        this.startImage = startImage;
    }

    public String getStartImageContentType() {
        return startImageContentType;
    }

    public void setStartImageContentType(String startImageContentType) {
        this.startImageContentType = startImageContentType;
    }

    public byte[] getInfoImage() {
        return infoImage;
    }

    public void setInfoImage(byte[] infoImage) {
        this.infoImage = infoImage;
    }

    public String getInfoImageContentType() {
        return infoImageContentType;
    }

    public void setInfoImageContentType(String infoImageContentType) {
        this.infoImageContentType = infoImageContentType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getName() {
        return internalName;
    }

    public void setName(String internalName) {
        this.internalName = internalName;
    }

    public byte[] getRegisterImage() {
        return registerImage;
    }

    public void setRegisterImage(byte[] registerImage) {
        this.registerImage = registerImage;
    }

    public String getRegisterImageContentType() {
        return registerImageContentType;
    }

    public void setRegisterImageContentType(String registerImageContentType) {
        this.registerImageContentType = registerImageContentType;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getCssString() {
        return cssString;
    }

    public void setCssString(String cssString) {
        this.cssString = cssString;
    }

    public byte[] getBorderImage() {
        return borderImage;
    }

    public void setBorderImage(byte[] borderImage) {
        this.borderImage = borderImage;
    }

    public String getBorderImageContentType() {
        return borderImageContentType;
    }

    public void setBorderImageContentType(String borderImageContentType) {
        this.borderImageContentType = borderImageContentType;
    }

    @Override
    public void init() {
    }

    @Override
    public void setParent(Object o) {
        //No parent
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbCmsHomeLayout)) return false;

        DbCmsHomeLayout that = (DbCmsHomeLayout) o;

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
