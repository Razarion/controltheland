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

package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 26.07.2010
 * Time: 19:12:10
 */
@Entity(name = "TUTORIAL_STEP_CONFIG")
public class DbStepConfig implements CrudParent, CrudChild<DbTaskConfig> {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbTaskConfig", insertable = false, updatable = false, nullable = false)
    private DbTaskConfig dbTaskConfig;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbConditionConfig conditionConfig;

    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(DbTaskConfig crudParent) {
        dbTaskConfig = crudParent;
    }

    @Override
    public DbTaskConfig getParent() {
        return dbTaskConfig;
    }

    public DbConditionConfig getConditionConfig() {
        return conditionConfig;
    }

    public void setConditionConfig(DbConditionConfig conditionConfig) {
        this.conditionConfig = conditionConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbStepConfig)) return false;

        DbStepConfig that = (DbStepConfig) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbStepConfig: '" + name + "' id: " + id;
    }

    public StepConfig createStepConfig(ItemService itemService) {
        if (conditionConfig == null) {
            throw new IllegalStateException("No condition set in step: " + this);
        }
        return new StepConfig(conditionConfig.createConditionConfig(itemService), name);
    }
}
