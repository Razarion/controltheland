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

import com.btxtech.game.jsre.common.tutorial.ResourceHintConfig;
import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.tutorial.condition.DbAbstractConditionConfig;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
public class DbStepConfig implements Serializable, CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @ManyToOne(optional = false)
    @JoinColumn(name = "dbTaskConfig", insertable = false, updatable = false, nullable = false)
    private DbTaskConfig dbTaskConfig;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DbAbstractConditionConfig abstractConditionConfig;
    @Column(length = 50000)
    private String description;
    @Embedded
    private DbResourceHintConfig dbResourceHintConfig;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init() {
        dbResourceHintConfig = new DbResourceHintConfig();
    }

    @Override
    public void setParent(CrudParent crudParent) {
        dbTaskConfig = (DbTaskConfig) crudParent;
    }

    public DbAbstractConditionConfig getAbstractConditionConfig() {
        return abstractConditionConfig;
    }

    public void setAbstractConditionConfig(DbAbstractConditionConfig abstractConditionConfig) {
        this.abstractConditionConfig = abstractConditionConfig;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DbResourceHintConfig getDbResourceHintConfig() {
        return dbResourceHintConfig;
    }

    public void setDbResourceHintConfig(DbResourceHintConfig dbResourceHintConfig) {
        this.dbResourceHintConfig = dbResourceHintConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbStepConfig)) return false;

        DbStepConfig that = (DbStepConfig) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public StepConfig createStepConfig() {
        ResourceHintConfig resourceHintConfig = null;
        if (dbResourceHintConfig.getData() != null) {
            resourceHintConfig = dbResourceHintConfig.createResourceHintConfig();
        }
        return new StepConfig(abstractConditionConfig.createConditionConfig(), resourceHintConfig, description);
    }
}
