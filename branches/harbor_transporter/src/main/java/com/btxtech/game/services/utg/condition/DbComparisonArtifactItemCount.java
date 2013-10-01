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

package com.btxtech.game.services.utg.condition;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;


/**
 * User: beat
 * Date: 10.09.2013
 * Time: 11:40:42
 */
@Entity(name = "GUIDANCE_COMPARISON_ARTIFACT_ITEM_COUNT")
public class DbComparisonArtifactItemCount implements CrudChild<DbArtifactItemIdComparisonConfig>, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbArtifactItemIdComparisonConfig dbArtifactItemIdComparisonConfig;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbInventoryArtifact dbInventoryArtifact;
    @Column(name = "theCount")
    private int count;

    /**
     * Used by hibernate
     */
    public DbComparisonArtifactItemCount() {
    }

    public DbComparisonArtifactItemCount(DbComparisonArtifactItemCount original) {
        dbInventoryArtifact = original.dbInventoryArtifact;
        count = original.count;
    }

    public Integer getId() {
        return id;
    }

    public DbInventoryArtifact getDbInventoryArtifact() {
        return dbInventoryArtifact;
    }

    public void setDbInventoryArtifact(DbInventoryArtifact dbInventoryArtifact) {
        this.dbInventoryArtifact = dbInventoryArtifact;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbComparisonArtifactItemCount)) {
            return false;
        }

        DbComparisonArtifactItemCount that = (DbComparisonArtifactItemCount) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
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
        // Ignore
    }

    @Override
    public void setParent(DbArtifactItemIdComparisonConfig dbArtifactItemIdComparisonConfig) {
        this.dbArtifactItemIdComparisonConfig = dbArtifactItemIdComparisonConfig;
    }

    @Override
    public DbArtifactItemIdComparisonConfig getParent() {
        return dbArtifactItemIdComparisonConfig;
    }
}
