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
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.tutorial.hint.DbHintConfig;
import com.btxtech.game.services.tutorial.hint.ResourceHintManager;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbStepConfig", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<DbHintConfig> dbHintConfigs;
    @Transient
    private CrudChildServiceHelper<DbHintConfig> hintConfigCrudHelper;

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

    public CrudChildServiceHelper<DbHintConfig> getHintConfigCrudServiceHelper() {
        if (hintConfigCrudHelper == null) {
            hintConfigCrudHelper = new CrudChildServiceHelper<DbHintConfig>(dbHintConfigs, DbHintConfig.class, this);
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
        return "DbStepConfig: '" + name + "' id: " + id;
    }

    public StepConfig createStepConfig(ResourceHintManager resourceHintManager, ItemService itemService) {
        if (conditionConfig == null) {
            throw new IllegalStateException("No condition set in step: " + this);
        }
        ArrayList<HintConfig> hintConfigs = new ArrayList<HintConfig>();
        for (DbHintConfig dbHintConfig : dbHintConfigs) {
            HintConfig hintConfig = dbHintConfig.createHintConfig(resourceHintManager, itemService);
            if (hintConfig != null) {
                hintConfigs.add(hintConfig);
            }
        }
        return new StepConfig(conditionConfig.createConditionConfig(itemService), hintConfigs, name);
    }
}
