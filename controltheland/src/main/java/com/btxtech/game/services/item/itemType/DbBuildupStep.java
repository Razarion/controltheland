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

package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.BuildupStep;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.mgmt.Html5ImagesUploadConverter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: beat                                                                                       <
 * Date: 14.12.2009
 * Time: 22:47:20
 */
@Entity(name = "ITEM_TYPE_BUILDUP_STEP")
public class DbBuildupStep implements Serializable, CrudChild<DbItemType> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbItemType itemType;
    private String contentType;
    @Column(nullable = false, length = 500000)
    private byte[] data;
    @Column(name = "fromStep")
    private double from;
    @Column(name = "toExclusiveStep")
    private double toExclusive;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setParent(DbItemType dbItemType) {
        itemType = dbItemType;
    }

    @Override
    public DbItemType getParent() {
        return itemType;
    }

    public void setBuildupStep(BuildupStep buildupStep) {
        from = buildupStep.getFrom();
        toExclusive = buildupStep.getToExclusive();
        Html5ImagesUploadConverter.Package aPackage = Html5ImagesUploadConverter.convertInlineImage(buildupStep.getBase64ImageData());
        contentType = aPackage.getMime();
        data = aPackage.convertBase64ToBytes();
    }

    public BuildupStep createBuildupStep() {
        return new BuildupStep(id, from, toExclusive);
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

    public double getFrom() {
        return from;
    }

    public void setFrom(double from) {
        this.from = from;
    }

    public double getToExclusive() {
        return toExclusive;
    }

    public void setToExclusive(double toExclusive) {
        this.toExclusive = toExclusive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbBuildupStep that = (DbBuildupStep) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
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
}
