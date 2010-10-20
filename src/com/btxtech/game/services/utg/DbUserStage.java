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

package com.btxtech.game.services.utg;

import com.btxtech.game.services.tutorial.DbTutorialConfig;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 17.10.2010
 * Time: 12:39:45
 */
@Entity(name = "GUIDANCE_USER_STAGE")
public class DbUserStage implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private boolean isRealGame;
    @Column(nullable = false, length = 500000)
    private String html;
    private int orderIndex;
    @OneToOne(fetch = FetchType.EAGER)
    private DbTutorialConfig dbTutorialConfig;

    public boolean isRealGame() {
        return isRealGame;
    }

    public void setRealGame(boolean realGame) {
        isRealGame = realGame;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbUserStage)) return false;

        DbUserStage dbUserStage = (DbUserStage) o;

        return !(id != null ? !id.equals(dbUserStage.id) : dbUserStage.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + name + " id: " + id;
    }

    public DbTutorialConfig getDbTutorialConfig() {
        return dbTutorialConfig;
    }

    public void setDbTutorialConfig(DbTutorialConfig dbTutorialConfig) {
        this.dbTutorialConfig = dbTutorialConfig;
    }
}
