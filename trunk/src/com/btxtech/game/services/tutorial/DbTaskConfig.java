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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.ResourceHintConfig;
import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelperCollectionImpl;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.tutorial.condition.DbAbstractConditionConfig;
import com.btxtech.game.wicket.pages.mgmt.ItemsUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 14:11:15
 */
@Entity(name = "TUTORIAL_TASK_CONFIG")
@TypeDefs({@TypeDef(name = "index", typeClass = IndexUserType.class)})
public class DbTaskConfig implements Serializable, CrudParent, CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private boolean clearGame;
    @OneToMany(mappedBy = "dbTaskConfig", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<DbItemTypeAndPosition> items;
    private boolean isScrollingAllowed;
    private boolean isOnlineBoxVisible;
    private boolean isInfoBoxVisible;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xScroll"), @Column(name = "yScroll")})
    private Index scroll;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", nullable = false, base = 0)
    @JoinColumn(name = "dbTaskConfig", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<DbStepConfig> stepConfigs;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DbAbstractConditionConfig completionConditionConfig; // TODO orphans are not removed from the condition table
    @Embedded
    private DbResourceHintConfig dbResourceHintConfig;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "TUTORIAL_TASK_CONFIG_ALLOWED_ITEMS",
            joinColumns = @JoinColumn(name = "factoryId"),
            inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    )
    private Set<DbBaseItemType> allowedItems;
    private int accountBalance;
    @Column(length = 50000)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dbTutorialConfig", insertable = false, updatable = false, nullable = false)
    private DbTutorialConfig dbTutorialConfig;
    @Transient
    private CrudServiceHelper<DbItemTypeAndPosition> itemTypeAndPositionCrudHelper;
    @Transient
    private CrudServiceHelper<DbStepConfig> stepConfigCrudHelper;

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
        items = new HashSet<DbItemTypeAndPosition>();
        scroll = new Index(0, 0);
        stepConfigs = new ArrayList<DbStepConfig>();
        dbResourceHintConfig = new DbResourceHintConfig();
        allowedItems = new HashSet<DbBaseItemType>();
    }

    @Override
    public void setParent(CrudParent crudParent) {
        dbTutorialConfig = (DbTutorialConfig) crudParent;
    }

    public void setClearGame(boolean clearGame) {
        this.clearGame = clearGame;
    }

    public boolean isScrollingAllowed() {
        return isScrollingAllowed;
    }

    public void setScrollingAllowed(boolean scrollingAllowed) {
        isScrollingAllowed = scrollingAllowed;
    }

    public boolean isOnlineBoxVisible() {
        return isOnlineBoxVisible;
    }

    public void setOnlineBoxVisible(boolean onlineBoxVisible) {
        isOnlineBoxVisible = onlineBoxVisible;
    }

    public boolean isInfoBoxVisible() {
        return isInfoBoxVisible;
    }

    public void setInfoBoxVisible(boolean infoBoxVisible) {
        isInfoBoxVisible = infoBoxVisible;
    }

    public Index getScroll() {
        return scroll;
    }

    public void setScroll(Index scroll) {
        this.scroll = scroll;
    }

    public Set<DbBaseItemType> getAllowedItems() {
        return allowedItems;
    }

    public void setAllowedItems(Set<DbBaseItemType> allowedItems) {
        this.allowedItems = allowedItems;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
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
        if (!(o instanceof DbTaskConfig)) return false;

        DbTaskConfig that = (DbTaskConfig) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    public DbAbstractConditionConfig getCompletionConditionConfig() {
        return completionConditionConfig;
    }

    public void setCompletionConditionConfig(DbAbstractConditionConfig completionConditionConfig) {
        this.completionConditionConfig = completionConditionConfig;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public TaskConfig createTaskConfig() {
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        for (DbStepConfig dBstepConfig : this.stepConfigs) {
            stepConfigs.add(dBstepConfig.createStepConfig());
        }

        ArrayList<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        for (DbItemTypeAndPosition dbItemTypeAndPosition : items) {
            itemTypeAndPositions.add(dbItemTypeAndPosition.createItemTypeAndPosition());
        }
        ResourceHintConfig resourceHintConfig = null;
        if (dbResourceHintConfig.getData() != null) {
            resourceHintConfig = dbResourceHintConfig.createResourceHintConfig();
        }
        return new TaskConfig(clearGame,
                itemTypeAndPositions,
                isScrollingAllowed,
                isOnlineBoxVisible,
                isInfoBoxVisible,
                scroll,
                stepConfigs,
                completionConditionConfig.createConditionConfig(),
                resourceHintConfig,
                ItemsUtil.itemTypesToCollection(allowedItems),
                accountBalance,
                description);
    }

    public CrudServiceHelper<DbItemTypeAndPosition> getItemCrudServiceHelper() {
        if (itemTypeAndPositionCrudHelper == null) {
            itemTypeAndPositionCrudHelper = new CrudServiceHelperCollectionImpl<DbItemTypeAndPosition>(items, DbItemTypeAndPosition.class, this);
        }
        return itemTypeAndPositionCrudHelper;
    }

    public CrudServiceHelper<DbStepConfig> getStepConfigCrudServiceHelper() {
        if (stepConfigCrudHelper == null) {
            stepConfigCrudHelper = new CrudServiceHelperCollectionImpl<DbStepConfig>(stepConfigs, DbStepConfig.class, this);
        }
        return stepConfigCrudHelper;
    }

    public void moveTaskUp(DbStepConfig stepConfig) {
        int i = stepConfigs.indexOf(stepConfig);
        if (i > 0) {
            DbStepConfig old = stepConfigs.set(i - 1, stepConfig);
            stepConfigs.set(i, old);
        }
    }

    public void moveTaskDown(DbStepConfig stepConfig) {
        int i = stepConfigs.indexOf(stepConfig);
        if (i + 2 < stepConfigs.size()) {
            DbStepConfig old = stepConfigs.set(i + 1, stepConfig);
            stepConfigs.set(i, old);
        }
    }
}
