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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelperCollectionImpl;
import com.btxtech.game.services.common.db.SimpleBaseUserType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 11:33:59
 */
@Entity(name = "TUTORIAL_CONFIG")
@TypeDef(name = "base", typeClass = SimpleBaseUserType.class)
public class DbTutorialConfig implements Serializable, CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    @Type(type = "base")
    @Columns(columns = {@Column(name = "baseName"), @Column(name = "baseHtmlColor"), @Column(name = "baseBot")})
    private SimpleBase ownBase;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", nullable = false, base = 0)
    @JoinColumn(name = "dbTutorialConfig", nullable = false)
    private List<DbTaskConfig> dbTaskConfigs;
    @Transient
    private CrudServiceHelper<DbTaskConfig> crudServiceHelper;
    private int width;
    private int height;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public SimpleBase getOwnBase() {
        return ownBase;
    }

    public void setOwnBase(SimpleBase ownBase) {
        this.ownBase = ownBase;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public TutorialConfig createTutorialConfig(ResourceHintManager resourceHintManager) {
        ArrayList<TaskConfig> taskConfigs = new ArrayList<TaskConfig>();
        for (DbTaskConfig dbTaskConfig : dbTaskConfigs) {
            taskConfigs.add(dbTaskConfig.createTaskConfig(resourceHintManager));
        }
        return new TutorialConfig(taskConfigs, ownBase, width, height);
    }

    public void init() {
        ownBase = new SimpleBase("Tutorial", "#FFFFFF", false);
        dbTaskConfigs = new ArrayList<DbTaskConfig>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTutorialConfig)) return false;

        DbTutorialConfig that = (DbTutorialConfig) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public void setParent(CrudParent crudParent) {
        throw new NotImplementedException();
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public CrudServiceHelper<DbTaskConfig> getCrudServiceHelper() {
        if (crudServiceHelper == null) {
            crudServiceHelper = new CrudServiceHelperCollectionImpl<DbTaskConfig>(dbTaskConfigs, DbTaskConfig.class, this);
        }
        return crudServiceHelper;
    }

    public void moveTaskUp(DbTaskConfig task) {
        int i = dbTaskConfigs.indexOf(task);
        if (i > 0) {
            DbTaskConfig old = dbTaskConfigs.set(i - 1, task);
            dbTaskConfigs.set(i, old);
        }
    }

    public void moveTaskDown(DbTaskConfig task) {
        int i = dbTaskConfigs.indexOf(task);
        if (i + 1 < dbTaskConfigs.size()) {
            DbTaskConfig old = dbTaskConfigs.set(i + 1, task);
            dbTaskConfigs.set(i, old);
        }
    }
}
