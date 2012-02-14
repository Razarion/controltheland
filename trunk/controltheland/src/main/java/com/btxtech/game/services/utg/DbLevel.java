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

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 13.05.2010
 * Time: 12:20:32
 */
@Entity(name = "GUIDANCE_LEVEL")
public class DbLevel implements CrudChild<DbQuestHub>, CrudParent {
    protected static final String COPY = "Copy ";
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Column(length = 50000)
    private String html;
    @Column(length = 1000)
    private String internalDescription;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "dbLevel", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbLevelTask> dbLevelTasks;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbQuestHub", insertable = false, updatable = false, nullable = false)
    private DbQuestHub dbQuestHub;
    // ----- Scope -----
    private int maxMoney;
    private double itemSellFactor;
    private int houseSpace;
    private RadarMode radarMode;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dbLevel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbItemTypeLimitation> itemTypeLimitation;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private DbConditionConfig dbConditionConfig;

    @Transient
    private CrudChildServiceHelper<DbItemTypeLimitation> itemTypeLimitationCrud;
    @Transient
    private CrudChildServiceHelper<DbLevelTask> levelTaskCrud;

    /**
     * Used by CRUD
     */
    public DbLevel() {
    }

    public DbLevel(DbLevel copyFrom) {
        name = COPY + copyFrom.name;
        html = copyFrom.html;
        internalDescription = copyFrom.internalDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getInternalDescription() {
        return internalDescription;
    }

    public void setInternalDescription(String internalDescription) {
        this.internalDescription = internalDescription;
    }

    @Override
    public void init(UserService userService) {
        itemTypeLimitation = new HashSet<DbItemTypeLimitation>();
        dbLevelTasks = new ArrayList<DbLevelTask>();
        radarMode = RadarMode.NONE;
    }

    @Override
    public void setParent(DbQuestHub parent) {
        dbQuestHub = parent;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public DbQuestHub getParent() {
        return dbQuestHub;
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(int maxMoney) {
        this.maxMoney = maxMoney;
    }

    public double getItemSellFactor() {
        return itemSellFactor;
    }

    public void setItemSellFactor(double itemSellFactor) {
        this.itemSellFactor = itemSellFactor;
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    public RadarMode getRadarMode() {
        return radarMode;
    }

    public void setRadarMode(RadarMode radarMode) {
        this.radarMode = radarMode;
    }

    protected int getSaveId() {
        if (id != null) {
            return id;
        } else {
            // Only used in dummy level
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbLevel)) return false;

        DbLevel dbLevel = (DbLevel) o;

        return id != null && id.equals(dbLevel.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getName();
    }

    public CrudChildServiceHelper<DbLevelTask> getLevelTaskCrud() {
        if (levelTaskCrud == null) {
            levelTaskCrud = new CrudChildServiceHelper<DbLevelTask>(dbLevelTasks, DbLevelTask.class, this);
        }
        return levelTaskCrud;
    }

    public CrudChildServiceHelper<DbItemTypeLimitation> getItemTypeLimitationCrud() {
        if (itemTypeLimitationCrud == null) {
            itemTypeLimitationCrud = new CrudChildServiceHelper<DbItemTypeLimitation>(itemTypeLimitation, DbItemTypeLimitation.class, this);
        }
        return itemTypeLimitationCrud;
    }

    public DbTutorialConfig getDbTutorialConfigFromTask(int levelTaskId) {
        return getLevelTaskCrud().readDbChild(levelTaskId).getDbTutorialConfig();
    }

    public LevelScope createLevelScope() {
        Map<Integer, Integer> itemTypeLimitation = new HashMap<Integer, Integer>();
        for (DbItemTypeLimitation dbItemTypeLimitation : this.itemTypeLimitation) {
            itemTypeLimitation.put(dbItemTypeLimitation.getDbBaseItemType().getId(), dbItemTypeLimitation.getCount());
        }
        return new LevelScope(name, maxMoney, itemTypeLimitation, houseSpace, itemSellFactor, radarMode);
    }

    public DbConditionConfig getDbConditionConfig() {
        return dbConditionConfig;
    }

    public void setDbConditionConfig(DbConditionConfig dbConditionConfig) {
        this.dbConditionConfig = dbConditionConfig;
    }

    public DbLevelTask getFirstTutorialLevelTask() {
        for (DbLevelTask dbLevelTask : getLevelTaskCrud().readDbChildren()) {
            if (dbLevelTask.getDbTutorialConfig() != null) {
                return dbLevelTask;
            }
        }
        throw new IllegalStateException("No Tutorial Level Task configured for: " + this);
    }
}
