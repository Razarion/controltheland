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
import com.btxtech.game.jsre.common.tutorial.AutomatedBattleTaskConfig;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Locale;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 14:11:15
 */
@Entity
@DiscriminatorValue("AUTOMATED_BATTLE_TASK_CONFIG")
@TypeDefs({@TypeDef(name = "index", typeClass = IndexUserType.class)})
public class DbAutomatedBattleTaskConfig extends DbAbstractTaskConfig {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType attackBotItemType;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private Index attackBotItemPosition;
    private String attackBotName;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType targetItemType;
    private double attackBotHealthFactor;

    public double getAttackBotHealthFactor() {
        return attackBotHealthFactor;
    }

    public void setAttackBotHealthFactor(double attackBotHealthFactor) {
        this.attackBotHealthFactor = attackBotHealthFactor;
    }

    public Index getAttackBotItemPosition() {
        return attackBotItemPosition;
    }

    public void setAttackBotItemPosition(Index attackBotItemPosition) {
        this.attackBotItemPosition = attackBotItemPosition;
    }

    public String getAttackBotName() {
        return attackBotName;
    }

    public void setAttackBotName(String attackBotName) {
        this.attackBotName = attackBotName;
    }

    public DbBaseItemType getAttackBotItemType() {
        return attackBotItemType;
    }

    public void setAttackBotItemType(DbBaseItemType attackBotItemType) {
        this.attackBotItemType = attackBotItemType;
    }

    public DbBaseItemType getTargetItemType() {
        return targetItemType;
    }

    public void setTargetItemType(DbBaseItemType targetItemType) {
        this.targetItemType = targetItemType;
    }

    @Override
    protected AutomatedBattleTaskConfig createTaskConfig(ServerItemTypeService serverItemTypeService, Locale locale) {
        AutomatedBattleTaskConfig automatedBattleTaskConfig = new AutomatedBattleTaskConfig();
        automatedBattleTaskConfig.setAttackerHealthFactor(attackBotHealthFactor);
        automatedBattleTaskConfig.setBotName(attackBotName);
        if (attackBotItemType != null && attackBotItemPosition != null) {
            automatedBattleTaskConfig.setBotAttacker(new ItemTypeAndPosition(attackBotItemType.getId(), attackBotItemPosition, 0));
        }
        if (targetItemType != null) {
            automatedBattleTaskConfig.setTargetItemType(targetItemType.getId());
        }
        return automatedBattleTaskConfig;
    }
}
