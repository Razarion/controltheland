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

package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cascade;

/**
 * User: beat
 * Date: 13.05.2010
 * Time: 12:20:32
 */
@Entity(name = "GUIDANCE_LEVEL")
public class DbLevel implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private int rank;
    @Column(length = 50000)
    private String missionTarget;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "dbLevel")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<DbItemCount> dbItemCounts;
    private Boolean tutorialTermination;
    private Integer minXp;
    private Integer minMoney;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "GUIDANCE_LEVEL_SKIP_IF_BOUGHT",
            joinColumns = @JoinColumn(name = "dbLevelId"),
            inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    )
    private Set<DbBaseItemType> skipIfItemsBought;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getMissionTarget() {
        return missionTarget;
    }

    public void setMissionTarget(String missionTarget) {
        this.missionTarget = missionTarget;
    }

    public Collection<DbItemCount> getDbItemCounts() {
        return dbItemCounts;
    }

    public void createDbItemCount() {
        if (dbItemCounts == null) {
            dbItemCounts = new HashSet<DbItemCount>();
        }
        DbItemCount dbItemCount = new DbItemCount();
        dbItemCount.setDbLevel(this);
        dbItemCounts.add(dbItemCount);
    }

    public void removeDbItemCount(DbItemCount dbItemCount) {
        dbItemCounts.remove(dbItemCount);
    }

    public Boolean isTutorialTermination() {
        return tutorialTermination;
    }

    public void setTutorialTermination(Boolean tutorialTermination) {
        this.tutorialTermination = tutorialTermination;
    }

    public Integer getMinXp() {
        return minXp;
    }

    public void setMinXp(Integer minXp) {
        this.minXp = minXp;
    }

    public Integer getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(Integer minMoney) {
        this.minMoney = minMoney;
    }

    public Collection<DbBaseItemType> getSkipIfItemsBought() {
        return skipIfItemsBought;
    }

    public void setSkipIfItemsBought(Set<DbBaseItemType> skipIfItemsBought) {
        this.skipIfItemsBought = skipIfItemsBought;
    }

    public Level createLevel() {
        Level level = new Level();
        level.setName(name);
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbLevel)) return false;

        DbLevel dbLevel = (DbLevel) o;

        return !(id != null ? !id.equals(dbLevel.id) : dbLevel.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
