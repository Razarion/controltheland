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

import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.mgmt.Html5ImagesUploadConverter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 14.12.2009
 * Time: 22:47:20
 */
@Entity(name = "ITEM_TYPE_IMAGE")
public class DbItemTypeImage implements Serializable, CrudChild<DbItemType> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbItemType itemType;
    private String contentType;
    @Column(length = 500000)
    private byte[] data;
    private int angelIndex;
    private int frame;
    private int step;
    @Enumerated(EnumType.STRING)
    private ItemTypeSpriteMap.SyncObjectState type;


    @Override
    public void setParent(DbItemType dbItemType) {
        itemType = dbItemType;
    }

    @Override
    public DbItemType getParent() {
        return itemType;
    }

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

    public int getAngelIndex() {
        return angelIndex;
    }

    public void setAngelIndex(int angel) {
        this.angelIndex = angel;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public ItemTypeSpriteMap.SyncObjectState getType() {
        return type;
    }

    public void setType(ItemTypeSpriteMap.SyncObjectState type) {
        this.type = type;
    }

    public void setItemTypeImageInfo(ItemTypeImageInfo itemTypeImageInfo, ItemTypeSpriteMap.SyncObjectState type) {
        Html5ImagesUploadConverter.Package aPackage = Html5ImagesUploadConverter.convertInlineImage(itemTypeImageInfo.getBase64ImageData());
        contentType = aPackage.getMime();
        data = aPackage.convertBase64ToBytes();
        angelIndex = itemTypeImageInfo.getAngelIndex();
        frame = itemTypeImageInfo.getFrame();
        step = itemTypeImageInfo.getStep();
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbItemTypeImage that = (DbItemTypeImage) o;
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

