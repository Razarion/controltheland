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
import com.btxtech.game.services.common.db.IndexUserType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 05.12.2009
 * Time: 18:49:56
 */
@Entity
@TypeDefs({@TypeDef(name = "index", typeClass = IndexUserType.class)})
@DiscriminatorValue("PROJECTILE")
public class GenericProjectileItem extends GenericItem {
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbBase base;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xUnloadPos"), @Column(name = "yUnloadPos")})
    private Index targetPosition;

    /**
     * Used by hibernate
     */
    public GenericProjectileItem() {
    }

    public GenericProjectileItem(DbBackupEntry dbBackupEntry) {
        super(dbBackupEntry);
    }

    public DbBase getBase() {
        return base;
    }

    public void setBase(DbBase base) {
        this.base = base;
    }

    public Index getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Index targetPosition) {
        this.targetPosition = targetPosition;
    }
}
