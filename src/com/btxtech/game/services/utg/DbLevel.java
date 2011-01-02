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
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.hibernate.annotations.Cascade;

/**
 * User: beat
 * Date: 13.05.2010
 * Time: 12:20:32
 */
@Entity(name = "GUIDANCE_LEVEL")
public class DbLevel implements CrudChild, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private int orderIndex;
    @Column(length = 50000)
    private String missionTarget;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "dbLevel")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<DbItemCount> dbItemCounts;
    private Integer minXp;
    private Integer minMoney;
    private Integer deltaMoney;
    private Integer deltaKills;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "GUIDANCE_LEVEL_SKIP_IF_BOUGHT",
            joinColumns = @JoinColumn(name = "dbLevelId"),
            inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    )
    private Set<DbBaseItemType> skipIfItemsBought;
    private int itemLimit;
    private int houseSpace;
    private boolean realGame;
    @Transient
    private Level level;
    private String html;
    @OneToOne(fetch = FetchType.EAGER)
    private DbConditionConfig dbConditionConfig;
    @ManyToOne
    private DbTutorialConfig dbTutorialConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init() {
        // Ignore
    }

    @Override
    public void setParent(Object o) {
        // Ignore
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

    public Integer getDeltaMoney() {
        return deltaMoney;
    }

    public void setDeltaMoney(Integer deltaMoney) {
        this.deltaMoney = deltaMoney;
    }

    public Integer getDeltaKills() {
        return deltaKills;
    }

    public void setDeltaKills(Integer deltaKills) {
        this.deltaKills = deltaKills;
    }

    public Collection<DbBaseItemType> getSkipIfItemsBought() {
        return skipIfItemsBought;
    }

    public void setSkipIfItemsBought(Set<DbBaseItemType> skipIfItemsBought) {
        this.skipIfItemsBought = skipIfItemsBought;
    }

    public int getItemLimit() {
        return itemLimit;
    }

    public void setItemLimit(int itemLimit) {
        this.itemLimit = itemLimit;
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    public DbTutorialConfig getDbTutorialConfig() {
        return dbTutorialConfig;
    }

    public void setDbTutorialConfig(DbTutorialConfig dbTutorialConfig) {
        this.dbTutorialConfig = dbTutorialConfig;
    }

    public Integer getId() {
        return id;
    }

    public Level createLevel() {
        Level level = new Level();
        level.setName(name);
        level.setItemLimit(itemLimit);
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

    public boolean isRealGame() {
        return realGame;
    }

    public void setRealGame(boolean realGame) {
        this.realGame = realGame;
    }

    public Level getLevel() {
        return level;
    }

    public DbScope getDbScope() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public String getHtml() {
        return html;
    }

    public DbConditionConfig getDbConditionConfig() {
        return dbConditionConfig;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public String toString() {
        return "DbLevel: " + name + " real game: " + isRealGame();
    }
}
