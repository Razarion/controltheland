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

import com.btxtech.game.jsre.common.tutorial.HintConfig;
import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelperCollectionImpl;
import com.btxtech.game.services.tutorial.hint.DbHintConfig;
import com.btxtech.game.services.tutorial.hint.ResourceHintManager;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.hibernate.annotations.Cascade;

/**
 * User: beat
 * Date: 26.07.2010
 * Time: 19:12:10
 */
@Entity(name = "TUTORIAL_STEP_CONFIG")
public class DbStepConfig implements Serializable, CrudParent, CrudChild<DbTaskConfig> {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @ManyToOne(optional = false)
    @JoinColumn(name = "dbTaskConfig", insertable = false, updatable = false, nullable = false)
    private DbTaskConfig dbTaskConfig;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DbConditionConfig conditionConfig;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "dbStepConfig", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<DbHintConfig> dbHintConfigs;
    @Transient
    private CrudServiceHelper<DbHintConfig> hintConfigCrudHelper;

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
        dbHintConfigs = new HashSet<DbHintConfig>();
    }

    @Override
    public void setParent(DbTaskConfig crudParent) {
        dbTaskConfig = crudParent;
    }

    public DbConditionConfig getConditionConfig() {
        return conditionConfig;
    }

    public void setConditionConfig(DbConditionConfig conditionConfig) {
        this.conditionConfig = conditionConfig;
    }

    public CrudServiceHelper<DbHintConfig> getHintConfigCrudServiceHelper() {
        if (hintConfigCrudHelper == null) {
            hintConfigCrudHelper = new CrudServiceHelperCollectionImpl<DbHintConfig>(dbHintConfigs, DbHintConfig.class, this);
        }
        return hintConfigCrudHelper;
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

    @Override
    public String toString() {
        return "DbStepConfig: " + name + " id: " + id;
    }

    public StepConfig createStepConfig(ResourceHintManager resourceHintManager) {
        if (conditionConfig == null) {
            throw new IllegalStateException("No condition set in step: " + this);
        }
        ArrayList<HintConfig> hintConfigs = new ArrayList<HintConfig>();
        for (DbHintConfig dbHintConfig : dbHintConfigs) {
            HintConfig hintConfig = dbHintConfig.createHintConfig(resourceHintManager);
            if (hintConfig != null) {
                hintConfigs.add(hintConfig);
            }
        }
        return new StepConfig(conditionConfig.createConditionConfig(), hintConfigs, name);
    }
}
