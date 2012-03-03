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

import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.terrain.DbTerrainSetting;
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
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;


/**
 * User: beat
 * Date: 24.07.2010
 * Time: 11:33:59
 */
@Entity(name = "TUTORIAL_CONFIG")
public class DbTutorialConfig implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", nullable = false, base = 0)
    @JoinColumn(name = "dbTutorialConfig", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<DbTaskConfig> dbTaskConfigs;
    private int width;
    private int height;
    private String ownBaseName;
    private boolean tracking;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbTerrainSetting dbTerrainSetting;
    @Column(length = 50000)
    private String inGameHtml;
    private boolean showTip;

    @Transient
    private CrudChildServiceHelper<DbTaskConfig> dbTaskConfigCrudChildServiceHelper;
    @Transient
    private TutorialConfig tutorialConfig;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getOwnBaseName() {
        return ownBaseName;
    }

    public void setOwnBaseName(String ownBaseName) {
        this.ownBaseName = ownBaseName;
    }

    public Integer getId() {
        return id;
    }

    public boolean isTracking() {
        return tracking;
    }

    public void setTracking(boolean tracking) {
        this.tracking = tracking;
    }

    public List<DbTaskConfig> getDbTaskConfigs() {
        return dbTaskConfigs;
    }

    public void setDbTaskConfigs(List<DbTaskConfig> dbTaskConfigs) {
        this.dbTaskConfigs = dbTaskConfigs;
    }

    public DbTerrainSetting getDbTerrainSetting() {
        return dbTerrainSetting;
    }

    public void setDbTerrainSetting(DbTerrainSetting dbTerrainSetting) {
        this.dbTerrainSetting = dbTerrainSetting;
    }

    public void init(UserService userService) {
        ownBaseName = "My Base";
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
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public CrudChildServiceHelper<DbTaskConfig> getDbTaskConfigCrudChildServiceHelper() {
        if (dbTaskConfigCrudChildServiceHelper == null) {
            dbTaskConfigCrudChildServiceHelper = new CrudChildServiceHelper<DbTaskConfig>(dbTaskConfigs, DbTaskConfig.class, this);
        }
        return dbTaskConfigCrudChildServiceHelper;
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

    public String getInGameHtml() {
        return inGameHtml;
    }

    public void setInGameHtml(String inGameHtml) {
        this.inGameHtml = inGameHtml;
    }

    public boolean isShowTip() {
        return showTip;
    }

    public void setShowTip(boolean showTip) {
        this.showTip = showTip;
    }

    @Override
    public String toString() {
        return "DbTutorialSetting: " + name;
    }

    public TutorialConfig getTutorialConfig(ItemService itemService) {
        if (tutorialConfig == null) {
            tutorialConfig = createTutorialConfig(itemService);
        }
        return tutorialConfig;
    }

    private TutorialConfig createTutorialConfig(ItemService itemService) {
        ArrayList<TaskConfig> taskConfigs = new ArrayList<TaskConfig>();
        for (DbTaskConfig dbTaskConfig : dbTaskConfigs) {
            taskConfigs.add(dbTaskConfig.createTaskConfig(itemService));
        }

        return new TutorialConfig(taskConfigs, ownBaseName, width, height, tracking, inGameHtml, showTip);
    }

}