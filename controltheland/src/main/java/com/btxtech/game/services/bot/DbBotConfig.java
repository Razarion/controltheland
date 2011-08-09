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

package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.db.RectangleUserType;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 15.03.2010
 * Time: 22:07:46
 */
@Entity(name = "BOT_CONFIG")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbBotConfig implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private int actionDelay;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbBotItemConfig> botItems;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "realmRectX"), @Column(name = "realmRectY"), @Column(name = "realmRectWidth"), @Column(name = "realmRectHeight")})
    private Rectangle realm;
    private String name;
    @Transient
    private CrudChildServiceHelper<DbBotItemConfig> botItemCrud;

    public Integer getId() {
        return id;
    }

    public int getActionDelay() {
        return actionDelay;
    }

    public void setActionDelay(int actionDelay) {
        this.actionDelay = actionDelay;
    }

    public Rectangle getRealm() {
        return realm;
    }

    public void setRealm(Rectangle realm) {
        this.realm = realm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBotConfig)) return false;

        DbBotConfig dbBotConfig = (DbBotConfig) o;

        return !(id != null ? !id.equals(dbBotConfig.id) : dbBotConfig.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
        actionDelay = 3000;
        botItems = new HashSet<DbBotItemConfig>();
    }

    @Override
    public void setParent(Object o) {
        // Ignore
    }

    public CrudChildServiceHelper<DbBotItemConfig> getBotItemCrud() {
        if (botItemCrud == null) {
            botItemCrud = new CrudChildServiceHelper<DbBotItemConfig>(botItems, DbBotItemConfig.class, this);
        }
        return botItemCrud;
    }
}
