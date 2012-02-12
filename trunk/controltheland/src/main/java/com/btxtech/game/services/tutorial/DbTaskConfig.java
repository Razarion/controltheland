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
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 14:11:15
 */
@Entity(name = "TUTORIAL_TASK_CONFIG")
@TypeDefs({@TypeDef(name = "index", typeClass = IndexUserType.class)})
public class DbTaskConfig implements CrudParent, CrudChild<DbTutorialConfig> {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @OneToMany(mappedBy = "dbTaskConfig", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<DbItemTypeAndPosition> items;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xScroll"), @Column(name = "yScroll")})
    private Index scroll;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", nullable = false, base = 0)
    @JoinColumn(name = "dbTaskConfig", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<DbStepConfig> stepConfigs;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "dbTaskConfig", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbTaskAllowedItem> dbTaskAllowedItems;
    @Column(name = "accountBalance")
    private int money;
    private int maxMoney;
    double itemSellFactor;
    private int houseCount;
    private RadarMode radarMode;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbTutorialConfig", insertable = false, updatable = false, nullable = false)
    private DbTutorialConfig dbTutorialConfig;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "dbTaskConfig", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbTaskBot> dbTaskBots;

    @Transient
    private CrudChildServiceHelper<DbItemTypeAndPosition> itemTypeAndPositionCrudHelper;
    @Transient
    private CrudChildServiceHelper<DbStepConfig> stepConfigCrudHelper;
    @Transient
    private CrudChildServiceHelper<DbTaskAllowedItem> allowedItemHelper;
    @Transient
    private CrudChildServiceHelper<DbTaskBot> botCrudHelper;

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
        items = new HashSet<DbItemTypeAndPosition>();
        scroll = new Index(0, 0);
        stepConfigs = new ArrayList<DbStepConfig>();
        dbTaskAllowedItems = new HashSet<DbTaskAllowedItem>();
        dbTaskBots = new HashSet<DbTaskBot>();
        radarMode = RadarMode.NONE;
        itemSellFactor = 0.5;
    }

    @Override
    public void setParent(DbTutorialConfig crudParent) {
        dbTutorialConfig = crudParent;
    }

    @Override
    public DbTutorialConfig getParent() {
        return dbTutorialConfig;
    }

    public Index getScroll() {
        return scroll;
    }

    public void setScroll(Index scroll) {
        this.scroll = scroll;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(int maxMoney) {
        this.maxMoney = maxMoney;
    }

    public double getItemSellFactor() {
        return itemSellFactor;
    }

    public void setItemSellFactor(double itemSellFactor) {
        this.itemSellFactor = itemSellFactor;
    }

    public int getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(int houseCount) {
        this.houseCount = houseCount;
    }

    public RadarMode getRadarMode() {
        return radarMode;
    }

    public void setRadarMode(RadarMode radarMode) {
        this.radarMode = radarMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTaskConfig)) return false;

        DbTaskConfig that = (DbTaskConfig) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    public TaskConfig createTaskConfig(ItemService itemService) {
        ArrayList<StepConfig> stepConfigs = new ArrayList<StepConfig>();
        for (DbStepConfig dBstepConfig : this.stepConfigs) {
            stepConfigs.add(dBstepConfig.createStepConfig(itemService));
        }

        ArrayList<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<ItemTypeAndPosition>();
        for (DbItemTypeAndPosition dbItemTypeAndPosition : getItemCrudServiceHelper().readDbChildren()) {
            ItemTypeAndPosition itemTypeAndPosition = dbItemTypeAndPosition.createItemTypeAndPosition();
            if (itemTypeAndPosition != null) {
                itemTypeAndPositions.add(itemTypeAndPosition);
            }
        }

        Map<Integer, Integer> itemTypeLimitation = new HashMap<Integer, Integer>();
        for (DbTaskAllowedItem dbTaskAllowedItem : getAllowedItemHelper().readDbChildren()) {
            Integer count = itemTypeLimitation.get(dbTaskAllowedItem.getDbBaseItemType().getId());
            if (count == null) {
                count = 0;
            }
            Integer newCount = count + dbTaskAllowedItem.getCount();
            itemTypeLimitation.put(dbTaskAllowedItem.getDbBaseItemType().getId(), newCount);
        }

        return new TaskConfig(itemTypeAndPositions,
                scroll,
                stepConfigs,
                houseCount,
                money,
                maxMoney,
                itemSellFactor,
                name,
                convertTaskBots(itemService),
                itemTypeLimitation,
                radarMode);
    }

    public CrudChildServiceHelper<DbItemTypeAndPosition> getItemCrudServiceHelper() {
        if (itemTypeAndPositionCrudHelper == null) {
            itemTypeAndPositionCrudHelper = new CrudChildServiceHelper<DbItemTypeAndPosition>(items, DbItemTypeAndPosition.class, this);
        }
        return itemTypeAndPositionCrudHelper;
    }

    public CrudChildServiceHelper<DbStepConfig> getStepConfigCrudServiceHelper() {
        if (stepConfigCrudHelper == null) {
            stepConfigCrudHelper = new CrudChildServiceHelper<DbStepConfig>(stepConfigs, DbStepConfig.class, this);
        }
        return stepConfigCrudHelper;
    }

    private Collection<BotConfig> convertTaskBots(ItemService itemService) {
        if (dbTaskBots == null || dbTaskBots.isEmpty()) {
            return null;
        }
        List<BotConfig> result = new ArrayList<BotConfig>();
        for (DbTaskBot dbTaskBot : dbTaskBots) {
            if (dbTaskBot.getDbBotConfig() != null) {
                result.add(dbTaskBot.getDbBotConfig().createBotConfig(itemService));
            }
        }
        return result;
    }

    public CrudChildServiceHelper<DbTaskAllowedItem> getAllowedItemHelper() {
        if (allowedItemHelper == null) {
            allowedItemHelper = new CrudChildServiceHelper<DbTaskAllowedItem>(dbTaskAllowedItems, DbTaskAllowedItem.class, this);
        }
        return allowedItemHelper;
    }

    public CrudChildServiceHelper<DbTaskBot> getBotCrudHelper() {
        if (botCrudHelper == null) {
            botCrudHelper = new CrudChildServiceHelper<DbTaskBot>(dbTaskBots, DbTaskBot.class, this);
        }
        return botCrudHelper;
    }
}
