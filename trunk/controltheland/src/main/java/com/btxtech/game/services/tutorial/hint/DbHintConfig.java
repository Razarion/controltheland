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

package com.btxtech.game.services.tutorial.hint;

import com.btxtech.game.jsre.common.tutorial.HintConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.tutorial.DbStepConfig;
import java.io.Serializable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 19:19:28
 */
@Entity(name = "TUTORIAL_HINT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
abstract public class DbHintConfig implements CrudChild<DbStepConfig> {
    public static Class[] ALL_HINTS = {
            DbItemSpeechBubbleHintConfig.class,
            DbResourceHintConfig.class,
            DbTerrainPositionSpeechBubbleHintConfig.class,
            DbCockpitSpeechBubbleHintConfig.class

    };
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @ManyToOne(optional = false)
    @JoinColumn(name = "dbStepConfig", insertable = false, updatable = false, nullable = false)
    private DbStepConfig parent;
    private boolean closeOnTaskEnd;

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbHintConfig)) return false;

        DbHintConfig dbHintConfig = (DbHintConfig) o;
        if (id != null) {
            return id.equals(dbHintConfig.id);
        } else {
            return this == o;
        }

    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return System.identityHashCode(this);
        }
    }

    abstract public HintConfig createHintConfig(ResourceHintManager resourceHintManager);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setParent(DbStepConfig parent) {
        this.parent = parent;
    }

    public boolean isCloseOnTaskEnd() {
        return closeOnTaskEnd;
    }

    public void setCloseOnTaskEnd(boolean closeOnTaskEnd) {
        this.closeOnTaskEnd = closeOnTaskEnd;
    }
}
