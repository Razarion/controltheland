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

package com.btxtech.game.services.utg.condition;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.common.db.DbI18nString;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Locale;

/**
 * User: beat
 * Date: 29.12.2010
 * Time: 18:21:51
 */
@Entity(name = "GUIDANCE_CONDITION_CONFIG")
@TypeDef(name = "index", typeClass = IndexUserType.class)
public class DbConditionConfig implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private DbAbstractComparisonConfig dbAbstractComparisonConfig; // TODO DELETE_ORPHAN does not work
    private ConditionTrigger conditionTrigger;
    @org.hibernate.annotations.Type(type = "index")
    @Columns(columns = {@Column(name = "xRadarPositionHin"), @Column(name = "yRadarPositionHin")})
    private Index radarPositionHint;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbI18nString i18nAdditionalDescription = new DbI18nString();
    private boolean hideQuestProgress;
    @Transient
    private ConditionConfig conditionConfig;

    public DbConditionConfig() {
    }

    public DbConditionConfig(DbConditionConfig dbConditionConfig) {
        dbAbstractComparisonConfig = dbConditionConfig.dbAbstractComparisonConfig.copy();
        conditionTrigger = dbConditionConfig.conditionTrigger;
    }

    public Integer getId() {
        return id;
    }

    public DbAbstractComparisonConfig getDbAbstractComparisonConfig() {
        return dbAbstractComparisonConfig;
    }

    public void setDbAbstractComparisonConfig(DbAbstractComparisonConfig dbAbstractComparisonConfig) {
        this.dbAbstractComparisonConfig = dbAbstractComparisonConfig;
    }

    public ConditionTrigger getConditionTrigger() {
        return conditionTrigger;
    }

    public void setConditionTrigger(ConditionTrigger conditionTrigger) {
        this.conditionTrigger = conditionTrigger;
    }

    public Index getRadarPositionHint() {
        return radarPositionHint;
    }

    public void setRadarPositionHint(Index radarPositionHint) {
        this.radarPositionHint = radarPositionHint;
    }

    public DbI18nString getI18nAdditionalDescription() {
        return i18nAdditionalDescription;
    }

    public boolean isHideQuestProgress() {
        return hideQuestProgress;
    }

    public void setHideQuestProgress(boolean hideQuestProgress) {
        this.hideQuestProgress = hideQuestProgress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbConditionConfig)) return false;

        DbConditionConfig that = (DbConditionConfig) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    public ConditionConfig createConditionConfig(ServerItemTypeService serverItemTypeService, Locale locale) {
        if (conditionTrigger == null) {
            throw new IllegalStateException("conditionTrigger is null");
        }
        if (conditionTrigger.isComparisonNeeded() && dbAbstractComparisonConfig == null) {
            throw new IllegalStateException("dbAbstractComparisonConfig is null");
        }
        AbstractComparisonConfig abstractComparisonConfig = null;
        if (conditionTrigger.isComparisonNeeded()) {
            abstractComparisonConfig = dbAbstractComparisonConfig.createComparisonConfig(serverItemTypeService);
        }
        conditionConfig = new ConditionConfig(conditionTrigger, abstractComparisonConfig, radarPositionHint, i18nAdditionalDescription.getString(locale), hideQuestProgress);
        return conditionConfig;
    }
}
