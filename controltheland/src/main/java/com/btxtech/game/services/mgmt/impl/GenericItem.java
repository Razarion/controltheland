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

package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.common.db.IdUserType;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.common.db.PathUserType;
import com.btxtech.game.services.item.itemType.DbItemType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: Sep 20, 2009
 * Time: 8:59:10 PM
 */
@Entity(name = "BACKUP_GENERIC_ITEM")
@TypeDefs({@TypeDef(name = "index", typeClass = IndexUserType.class),
        @TypeDef(name = "id", typeClass = IdUserType.class),
        @TypeDef(name = "path", typeClass = PathUserType.class)})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ITEM_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class GenericItem {
    // TODO some entries should not be nullable
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DbBackupEntry backupEntry;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbItemType itemType;
    @Type(type = "id")
    @Columns(columns = {@Column(name = "ownId"), @Column(name = "parentId")})
    private com.btxtech.game.jsre.common.gameengine.syncObjects.Id itemId;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private Index position;

    /**
     * Used by hibernate
     */
    public GenericItem() {
    }

    public GenericItem(DbBackupEntry backupEntry) {
        this.backupEntry = backupEntry;
    }

    public DbItemType getDbItemTyp() {
        return itemType;
    }

    public void setDbItemType(DbItemType itemType) {
        this.itemType = itemType;
    }

    public com.btxtech.game.jsre.common.gameengine.syncObjects.Id getItemId() {
        return itemId;
    }

    public void setItemId(com.btxtech.game.jsre.common.gameengine.syncObjects.Id itemId) {
        this.itemId = itemId;
    }

    public void setPosition(Index position) {
        this.position = position;
    }

    public Index getPosition() {
        return position;
    }

}
