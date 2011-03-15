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

package com.btxtech.game.services.territory.impl;

import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.territory.impl.AbstractTerritoryServiceImpl;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.DbTerritory;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.user.SecurityRoles;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 23.05.2010
 * Time: 15:00:03
 */
@Component(value = "territoryService")
public class TerritoryServiceImpl extends AbstractTerritoryServiceImpl implements TerritoryService {
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private CrudRootServiceHelper<DbTerritory> dbTerritoryCrudServiceHelper;
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(TerritoryServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void setup() {
        try {
            dbTerritoryCrudServiceHelper.init(DbTerritory.class);
            updateTerritories();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public CrudRootServiceHelper<DbTerritory> getDbTerritoryCrudServiceHelper() {
        return dbTerritoryCrudServiceHelper;
    }

    @SuppressWarnings("unchecked")
    private DbTerritory getDbTerritory(final String name) {
        List<DbTerritory> dbTerritories = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbTerritory.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("name", name));
                return criteria.list();
            }
        });
        if (dbTerritories == null || dbTerritories.isEmpty()) {
            throw new IllegalArgumentException("Territory not found: " + name);
        }
        if (dbTerritories.size() > 1) {
            throw new IllegalArgumentException("More than one territory found: " + name);
        }
        return dbTerritories.get(0);
    }

    private void updateTerritories() {
        ArrayList<Territory> territories = new ArrayList<Territory>();
        Collection<DbTerritory> dbTerritories = dbTerritoryCrudServiceHelper.readDbChildren();
        for (DbTerritory dbTerritory : dbTerritories) {
            if (dbTerritory.getName() == null || dbTerritory.getName().trim().isEmpty()) {
                continue;
            }
            territories.add(dbTerritory.createTerritory());
        }
        setTerritories(territories);
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveTerritory(Territory territory) {
        DbTerritory dbTerritory = getDbTerritory(territory.getName());
        dbTerritory.addDbTerritoryRegion(territory.getTerritoryTileRegions());
        hibernateTemplate.update(dbTerritory);
        updateTerritories();
    }

    @Override
    protected AbstractTerrainService getTerrainService() {
        return terrainService;
    }

    @Override
    public void activate() {
        updateTerritories();
    }
}
