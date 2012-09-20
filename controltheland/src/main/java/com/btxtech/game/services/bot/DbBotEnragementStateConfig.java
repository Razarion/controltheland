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

import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
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
import java.util.HashSet;

/**
 * User: beat
 * Date: 15.03.2010
 * Time: 22:07:46
 */
@Entity(name = "BOT_ENRAGEMENT_CONFIG")
public class DbBotEnragementStateConfig implements CrudChild<DbBotConfig>, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "dbBotEnragementStateConfig_id")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbBotItemConfig> botItems;
    private String name;
    private Integer enrageUpKills;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBotConfig dbBotConfig;

    @Transient
    private CrudChildServiceHelper<DbBotItemConfig> botItemCrud;

    @Override
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
        botItems = new HashSet<>();
    }

    @Override
    public void setParent(DbBotConfig dbBotConfig) {
        this.dbBotConfig = dbBotConfig;
    }

    public Integer getEnrageUpKills() {
        return enrageUpKills;
    }

    public void setEnrageUpKills(Integer maxKillsPerBase) {
        this.enrageUpKills = maxKillsPerBase;
    }

    @Override
    public DbBotConfig getParent() {
        return dbBotConfig;
    }

    public CrudChildServiceHelper<DbBotItemConfig> getBotItemCrud() {
        if (botItemCrud == null) {
            botItemCrud = new CrudChildServiceHelper<>(botItems, DbBotItemConfig.class, this);
        }
        return botItemCrud;
    }

    public BotEnragementStateConfig createBotEnragementStateConfigg(ServerItemTypeService serverItemTypeService) {
        Collection<BotItemConfig> botItems = new ArrayList<>();
        if (this.botItems != null) {
            for (DbBotItemConfig botItem : this.botItems) {
                botItems.add(botItem.createBotItemConfig(serverItemTypeService));
            }
        }
        return new BotEnragementStateConfig(name, botItems, enrageUpKills);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBotEnragementStateConfig)) return false;

        DbBotEnragementStateConfig dbBotConfig = (DbBotEnragementStateConfig) o;

        return id != null && id.equals(dbBotConfig.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
