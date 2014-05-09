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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BuilderType;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.common.ReadonlyCollectionContentProvider;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.media.DbClip;
import com.btxtech.game.services.media.DbClipUtil;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 17:45:47
 */
@Entity(name = "ITEM_BUILDER_TYPE")
public class DbBuilderType implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "theRange")
    private int range;
    private double progress;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "ITEM_BUILDER_TYPE_ABLE_TO_BUILD",
            joinColumns = @JoinColumn(name = "builderId"),
            inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    )
    private Set<DbBaseItemType> ableToBuild;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbClip buildupClip;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ITEM_BUILDER_TYPE_BUILDUP_CLIP_POSITION",
            joinColumns = @JoinColumn(name = "builderTypeId"))
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    @OrderColumn(name = "imageIndex")
    private List<Index> buildupClipPositions;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Collection<DbBaseItemType> getAbleToBuild() {
        return ableToBuild;
    }

    public void setAbleToBuild(Set<DbBaseItemType> ableToBuild) {
        this.ableToBuild = ableToBuild;
    }

    public ContentProvider<DbBaseItemType> getAbleToBuildCrud() {
        return new ReadonlyCollectionContentProvider<>(ableToBuild);
    }

    public void setBuildupClip(DbClip buildupClip) {
        this.buildupClip = buildupClip;
    }

    public void setBuildupClipPositions(Index[] positions) {
        if (buildupClipPositions == null) {
            buildupClipPositions = new ArrayList<>();
        }
        buildupClipPositions.clear();
        if (positions != null) {
            Collections.addAll(buildupClipPositions, positions);
        }
    }

    public BuilderType createBuilderType() {
        return new BuilderType(range, progress, Utils.dbBaseItemTypesToInts(ableToBuild), DbClipUtil.createItemClipPosition(buildupClip, buildupClipPositions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbBuilderType that = (DbBuilderType) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
