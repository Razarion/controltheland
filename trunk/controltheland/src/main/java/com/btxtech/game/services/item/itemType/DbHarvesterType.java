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
import com.btxtech.game.jsre.common.gameengine.itemType.HarvesterType;
import com.btxtech.game.services.media.DbClip;
import com.btxtech.game.services.media.DbClipUtil;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 17:44:40
 */
@Entity(name = "ITEM_HARVESTER_TYPE")
public class DbHarvesterType implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "theRange")
    private int range;
    private double progress;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbClip harvestClip;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ITEM_HARVESTER_TYPE_HARVEST_CLIP_POSITION",
            joinColumns = @JoinColumn(name = "harvesterTypeId"))
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    @OrderColumn(name = "imageIndex")
    private List<Index> harvestClipPositions;

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

    public void setHarvestClip(DbClip harvestClip) {
        this.harvestClip = harvestClip;
    }

    public void setHarvestClipPositions(Index[] positions) {
        if (harvestClipPositions == null) {
            harvestClipPositions = new ArrayList<>();
        }
        harvestClipPositions.clear();
        if (positions != null) {
            Collections.addAll(harvestClipPositions, positions);
        }
    }

    public HarvesterType createHarvesterType() {
        return new HarvesterType(range, progress, DbClipUtil.createItemClipPosition(harvestClip, harvestClipPositions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbHarvesterType that = (DbHarvesterType) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
