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
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
    @JoinColumn(name = "dbTaskConfig", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbTaskAllowedItem> dbTaskAllowedItems;
    @Column(name = "accountBalance")
    private int money;
    private int maxMoney;
    private int houseCount;
    private RadarMode radarMode;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbTutorialConfig", insertable = false, updatable = false, nullable = false)
    private DbTutorialConfig dbTutorialConfig;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbBotConfig> dbBotConfigs;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbConditionConfig conditionConfig;
    @Enumerated(EnumType.STRING)
    private GameTipConfig.Tip tip;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType tipActor;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType tipToBeBuilt;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbResourceItemType tipResource;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "tipXTerrainPositionHint"), @Column(name = "tipYTerrainPositionHint")})
    private Index tipTerrainPositionHint;
    private boolean tipShowWatchQuestVisualisationCockpit;
    private boolean clearGame;

    @Transient
    private CrudChildServiceHelper<DbItemTypeAndPosition> itemTypeAndPositionCrudHelper;
    @Transient
    private CrudChildServiceHelper<DbTaskAllowedItem> allowedItemHelper;
    @Transient
    private CrudChildServiceHelper<DbBotConfig> botCrud;

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
        items = new HashSet<>();
        scroll = new Index(0, 0);
        dbTaskAllowedItems = new HashSet<>();
        dbBotConfigs = new HashSet<>();
        radarMode = RadarMode.NONE;
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

    public GameTipConfig.Tip getTip() {
        return tip;
    }

    public void setTip(GameTipConfig.Tip tip) {
        this.tip = tip;
    }

    public DbBaseItemType getTipActor() {
        return tipActor;
    }

    public void setTipActor(DbBaseItemType tipActor) {
        this.tipActor = tipActor;
    }

    public DbBaseItemType getTipToBeBuilt() {
        return tipToBeBuilt;
    }

    public void setTipToBeBuilt(DbBaseItemType tipToBeBuilt) {
        this.tipToBeBuilt = tipToBeBuilt;
    }

    public DbResourceItemType getTipResource() {
        return tipResource;
    }

    public void setTipResource(DbResourceItemType tipResource) {
        this.tipResource = tipResource;
    }

    public Index getTipTerrainPositionHint() {
        return tipTerrainPositionHint;
    }

    public void setTipTerrainPositionHint(Index tipTerrainPositionHint) {
        this.tipTerrainPositionHint = tipTerrainPositionHint;
    }

    public boolean isTipShowWatchQuestVisualisationCockpit() {
        return tipShowWatchQuestVisualisationCockpit;
    }

    public void setTipShowWatchQuestVisualisationCockpit(boolean tipShowWatchQuestVisualisationCockpit) {
        this.tipShowWatchQuestVisualisationCockpit = tipShowWatchQuestVisualisationCockpit;
    }

    public boolean isClearGame() {
        return clearGame;
    }

    public void setClearGame(boolean clearGame) {
        this.clearGame = clearGame;
    }

    public TaskConfig createTaskConfig(ServerItemTypeService serverItemTypeService) {
        ArrayList<ItemTypeAndPosition> itemTypeAndPositions = new ArrayList<>();
        for (DbItemTypeAndPosition dbItemTypeAndPosition : getItemCrudServiceHelper().readDbChildren()) {
            ItemTypeAndPosition itemTypeAndPosition = dbItemTypeAndPosition.createItemTypeAndPosition();
            if (itemTypeAndPosition != null) {
                itemTypeAndPositions.add(itemTypeAndPosition);
            }
        }

        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
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
                conditionConfig != null ? conditionConfig.createConditionConfig(serverItemTypeService) : null,
                houseCount,
                money,
                maxMoney,
                name,
                convertTaskBots(serverItemTypeService),
                itemTypeLimitation,
                radarMode,
                createGameTipConfig(),
                clearGame);
    }

    private GameTipConfig createGameTipConfig() {
        if (tip == null) {
            return null;
        }
        GameTipConfig gameTipConfig = new GameTipConfig();
        gameTipConfig.setTip(tip);
        gameTipConfig.setActor(tipActor != null ? tipActor.getId() : 0);
        gameTipConfig.setToBeBuiltId(tipToBeBuilt != null ? tipToBeBuilt.getId() : 0);
        gameTipConfig.setTerrainPositionHint(tipTerrainPositionHint);
        gameTipConfig.setResourceId(tipResource != null ? tipResource.getId() : 0);
        gameTipConfig.setHighlightQuestVisualisationCockpit(tipShowWatchQuestVisualisationCockpit);
        return gameTipConfig;
    }

    public CrudChildServiceHelper<DbItemTypeAndPosition> getItemCrudServiceHelper() {
        if (itemTypeAndPositionCrudHelper == null) {
            itemTypeAndPositionCrudHelper = new CrudChildServiceHelper<>(items, DbItemTypeAndPosition.class, this);
        }
        return itemTypeAndPositionCrudHelper;
    }

    private Collection<BotConfig> convertTaskBots(ServerItemTypeService serverItemTypeService) {
        if (dbBotConfigs == null || dbBotConfigs.isEmpty()) {
            return null;
        }
        List<BotConfig> result = new ArrayList<>();
        for (DbBotConfig dbBotConfig : dbBotConfigs) {
            result.add(dbBotConfig.createBotConfig(serverItemTypeService));
        }
        return result;
    }

    public CrudChildServiceHelper<DbTaskAllowedItem> getAllowedItemHelper() {
        if (allowedItemHelper == null) {
            allowedItemHelper = new CrudChildServiceHelper<>(dbTaskAllowedItems, DbTaskAllowedItem.class, this);
        }
        return allowedItemHelper;
    }

    public CrudChildServiceHelper<DbBotConfig> getBotCrud() {
        if (botCrud == null) {
            botCrud = new CrudChildServiceHelper<>(dbBotConfigs, DbBotConfig.class, this);
        }
        return botCrud;
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
        if (!(o instanceof DbTaskConfig)) return false;

        DbTaskConfig that = (DbTaskConfig) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
