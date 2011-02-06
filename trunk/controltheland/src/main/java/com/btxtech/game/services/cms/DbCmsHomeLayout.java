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
 * Date: 07.07.2010
 * Time: 20:48:15
 */
@Entity(name = "CMS_HOME_LAYOUT")
public class DbCmsHomeLayout implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private boolean isActive;
    private String bodyBackgroundColor;
    @Column(length = 500000)
    private byte[] bgImage;
    private String bgImageContentType;
    private int bgImageWidth;
    private int bgImageHeight;
    private String textColor;
    private int textTop;
    private int textLeft;
    private int textRight;
    private int startLinkLeft;
    private int startLinkTop;
    private int infoLinkLeft;
    private int infoLinkTop;
    @Column(length = 500000)
    private byte[] startImage;
    private String startImageContentType;
    @Column(length = 500000)
    private byte[] infoImage;
    private String infoImageContentType;
    private String internalName;

    public String getBodyBackgroundColor() {
        return bodyBackgroundColor;
    }

    public void setBodyBackgroundColor(String bodyBackgroundColor) {
        this.bodyBackgroundColor = bodyBackgroundColor;
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

    public int getBgImageWidth() {
        return bgImageWidth;
    }

    public void setBgImageWidth(int bgImageWidth) {
        this.bgImageWidth = bgImageWidth;
    }

    public int getBgImageHeight() {
        return bgImageHeight;
    }

    public void setBgImageHeight(int bgImageHeight) {
        this.bgImageHeight = bgImageHeight;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public int getTextTop() {
        return textTop;
    }

    public void setTextTop(int textTop) {
        this.textTop = textTop;
    }

    public int getTextLeft() {
        return textLeft;
    }

    public void setTextLeft(int textLeft) {
        this.textLeft = textLeft;
    }

    public int getTextRight() {
        return textRight;
    }

    public void setTextRight(int textRight) {
        this.textRight = textRight;
    }

    public int getStartLinkLeft() {
        return startLinkLeft;
    }

    public void setStartLinkLeft(int startLinkLeft) {
        this.startLinkLeft = startLinkLeft;
    }

    public int getStartLinkTop() {
        return startLinkTop;
    }

    public void setStartLinkTop(int startLinkTop) {
        this.startLinkTop = startLinkTop;
    }

    public int getInfoLinkLeft() {
        return infoLinkLeft;
    }

    public void setInfoLinkLeft(int infoLinkLeft) {
        this.infoLinkLeft = infoLinkLeft;
    }

    public int getInfoLinkTop() {
        return infoLinkTop;
    }

    public void setInfoLinkTop(int infoLinkTop) {
        this.infoLinkTop = infoLinkTop;
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

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbCmsHomeLayout)) return false;

        DbCmsHomeLayout that = (DbCmsHomeLayout) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
