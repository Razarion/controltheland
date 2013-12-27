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
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.user.UserService;
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
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: beat Date: 13.05.2010 Time: 12:20:32
 */
@Entity(name = "GUIDANCE_LEVEL")
public class DbLevel implements CrudChild, CrudParent {
    protected static final String COPY = "Copy ";
    @Id
    @GeneratedValue
    private Integer id;
    @OrderBy
    @SuppressWarnings({"UnusedDeclaration"})
    private int orderIndex;
    private int number;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "dbLevel", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", base = 0)
    private List<DbLevelTask> dbLevelTasks;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbPlanet dbPlanet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dbLevel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbLevelItemTypeLimitation> levelItemTypeLimitation;
    private int xp;
    private int friendInvitationBonus;

    @Transient
    private CrudChildServiceHelper<DbLevelItemTypeLimitation> itemTypeLimitationCrud;
    @Transient
    private CrudListChildServiceHelper<DbLevelTask> levelTaskCrud;

    /**
     * Used by CRUD
     */
    public DbLevel() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return Integer.toString(number);
    }

    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(UserService userService) {
        levelItemTypeLimitation = new HashSet<>();
        dbLevelTasks = new ArrayList<>();
    }

    @Override
    public void setParent(Object parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Object getParent() {
        return null;
    }

    public boolean hasDbPlanet() {
        return dbPlanet != null;
    }

    public DbPlanet getDbPlanet() {
        return dbPlanet;
    }

    public void setDbPlanet(DbPlanet dbPlanet) {
        this.dbPlanet = dbPlanet;
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
        if (this == o)
            return true;
        if (!(o instanceof DbLevel))
            return false;

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

    public CrudListChildServiceHelper<DbLevelTask> getLevelTaskCrud() {
        if (levelTaskCrud == null) {
            levelTaskCrud = new CrudListChildServiceHelper<>(dbLevelTasks, DbLevelTask.class, this);
        }
        return levelTaskCrud;
    }

    public CrudChildServiceHelper<DbLevelItemTypeLimitation> getItemTypeLimitationCrud() {
        if (itemTypeLimitationCrud == null) {
            itemTypeLimitationCrud = new CrudChildServiceHelper<>(levelItemTypeLimitation, DbLevelItemTypeLimitation.class, this);
        }
        return itemTypeLimitationCrud;
    }

    public DbTutorialConfig getDbTutorialConfigFromTask(int levelTaskId) {
        return getLevelTaskCrud().readDbChild(levelTaskId).getDbTutorialConfig();
    }

    public LevelScope createLevelScope() {
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        for (DbLevelItemTypeLimitation dbLevelItemTypeLimitation : this.levelItemTypeLimitation) {
            itemTypeLimitation.put(dbLevelItemTypeLimitation.getDbBaseItemType().getId(), dbLevelItemTypeLimitation.getCount());
        }
        return new LevelScope(dbPlanet != null ? dbPlanet.createPlanetInfo().getPlanetLiteInfo() : null, id, number, itemTypeLimitation, xp);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getFriendInvitationBonus() {
        return friendInvitationBonus;
    }

    public void setFriendInvitationBonus(int friendInvitationBonus) {
        this.friendInvitationBonus = friendInvitationBonus;
    }

    public DbLevelTask getFirstTutorialLevelTask(Collection<Integer> levelTaskDone) {
        for (DbLevelTask dbLevelTask : getLevelTaskCrud().readDbChildren()) {
            if (!dbLevelTask.isDbTutorialConfig()) {
                continue;
            }
            if (levelTaskDone != null && levelTaskDone.contains(dbLevelTask.getId())) {
                continue;
            }
            return dbLevelTask;
        }
        throw new IllegalStateException("No Tutorial Level Task configured for: " + this);
    }
}
