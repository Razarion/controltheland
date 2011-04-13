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
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.ItemService;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * User: beat
 * Date: 13.05.2010
 * Time: 12:20:32
 */
@Entity(name = "GUIDANCE_LEVEL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class DbAbstractLevel implements CrudChild, Serializable {
    protected static final String COPY = "Copy ";
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @OrderBy
    private int orderIndex;
    @Column(length = 50000)
    private String html;
    @Column(length = 1000)
    private String internalDescription;
    @Transient
    private ConditionConfig conditionConfig;
    @Transient
    private Level level;

    /**
     * Used by hibernate
     */
    protected DbAbstractLevel() {
    }

    public DbAbstractLevel(DbAbstractLevel copyFrom) {
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

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
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

    public abstract String getDisplayType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbAbstractLevel)) return false;

        DbAbstractLevel dbAbstractLevel = (DbAbstractLevel) o;

        return !(id != null ? !id.equals(dbAbstractLevel.id) : dbAbstractLevel.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getName();
    }

    public Level getLevel() {
        if (level == null) {
            throw new IllegalStateException("Level was not built. Call createLevel() first: " + name);
        }
        return level;
    }

    public ConditionConfig getConditionConfig() {
        if (conditionConfig == null) {
            throw new IllegalStateException("Condition config was not created");
        }
        return conditionConfig;
    }

    public void activate(ItemService itemService) throws LevelActivationException {
        try {
            conditionConfig = createConditionConfig(itemService);
            level = createLevel();
        } catch (LevelActivationException e) {
            e.addParent(getName());
            throw e;
        }
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    protected abstract Level createLevel() throws LevelActivationException;

    protected abstract ConditionConfig createConditionConfig(ItemService itemService);
}
