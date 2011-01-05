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
    //@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //@JoinTable(name = "GUIDANCE_LEVEL_SKIP_IF_BOUGHT",
    //        joinColumns = @JoinColumn(name = "dbLevelId"),
    //        inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    //)
    //private Set<DbBaseItemType> skipIfItemsBought;
    private boolean realGame;
    @Transient
    private Level level;
    @Column(length = 50000)
    private String html;
    @OneToOne(fetch = FetchType.EAGER)
    private DbConditionConfig dbConditionConfig;
    @ManyToOne
    private DbTutorialConfig dbTutorialConfig;
    @OneToOne
    private DbScope dbScope;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isRealGame() {
        return realGame;
    }

    public void setRealGame(boolean realGame) {
        this.realGame = realGame;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public DbConditionConfig getDbConditionConfig() {
        return dbConditionConfig;
    }

    public void setDbConditionConfig(DbConditionConfig dbConditionConfig) {
        this.dbConditionConfig = dbConditionConfig;
    }

    public DbTutorialConfig getDbTutorialConfig() {
        return dbTutorialConfig;
    }

    public void setDbTutorialConfig(DbTutorialConfig dbTutorialConfig) {
        this.dbTutorialConfig = dbTutorialConfig;
    }

    public DbScope getDbScope() {
        return dbScope;
    }

    public void setDbScope(DbScope dbScope) {
        this.dbScope = dbScope;
    }

    @Override
    public void init() {
        // Ignore
    }

    @Override
    public void setParent(Object o) {
        // Ignore
    }

    public Integer getId() {
        return id;
    }


    public Level getLevel() {
        if (level == null) {
            level = new Level(name, html, realGame, 0);
        }
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

    @Override
    public String toString() {
        return "DbLevel: " + name + " real game: " + isRealGame();
    }
}
