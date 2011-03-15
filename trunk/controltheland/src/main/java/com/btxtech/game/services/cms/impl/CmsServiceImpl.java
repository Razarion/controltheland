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

package com.btxtech.game.services.cms.impl;

import com.btxtech.game.services.cms.CmsContentStyleDTO;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsHomeLayout;
import com.btxtech.game.services.cms.DbCmsHomeText;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;

/**
 * User: beat
 * Date: 06.07.2010
 * Time: 21:41:45
 */
@Component("cmsServiceImpl")
public class CmsServiceImpl implements CmsService {
    private CmsContentStyleDTO homeContentStyleDTO = new CmsContentStyleDTO();
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(CmsServiceImpl.class);
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private CrudRootServiceHelper<DbCmsHomeText> dbCmsHomeTextCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbCmsHomeLayout> dbCmsHomeLayoutCrudRootServiceHelper;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        dbCmsHomeTextCrudRootServiceHelper.init(DbCmsHomeText.class);
        dbCmsHomeLayoutCrudRootServiceHelper.init(DbCmsHomeLayout.class);
        try {
            activateHome();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void activateHome() {
        @SuppressWarnings("unchecked")
        List<DbCmsHomeText> dbCmsHomeTexts = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbCmsHomeText.class);
                criteria.add(Restrictions.eq("isActive", true));
                return criteria.list();
            }
        });
        if (dbCmsHomeTexts.isEmpty()) {
            log.error("No active DbCmsHomeText found");
            return;
        }
        if (dbCmsHomeTexts.size() > 1) {
            log.info("More the one active DbCmsHomeText found. Take first one.");
        }
        DbCmsHomeText dbCmsHomeText = dbCmsHomeTexts.get(0);

        @SuppressWarnings("unchecked")
        List<DbCmsHomeLayout> dbCmsHomeLayouts = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbCmsHomeLayout.class);
                criteria.add(Restrictions.eq("isActive", true));
                return criteria.list();
            }
        });
        if (dbCmsHomeLayouts.isEmpty()) {
            log.error("No active DbCmsHomeLayout found");
            return;
        }
        if (dbCmsHomeLayouts.size() > 1) {
            log.info("More the one active DbCmsHomeLayout found. Take first one.");
        }
        DbCmsHomeLayout dbCmsHomeLayout = dbCmsHomeLayouts.get(0);

        homeContentStyleDTO.update(dbCmsHomeText, dbCmsHomeLayout);
    }

    @Override
    public CmsContentStyleDTO getHomeContentStyleDTO() {
        return homeContentStyleDTO;
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbCmsHomeText(DbCmsHomeText dbCmsHomeText) {
        hibernateTemplate.saveOrUpdate(dbCmsHomeText);
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbCmsHomeLayout(DbCmsHomeLayout dbCmsHomeLayout) {
        hibernateTemplate.saveOrUpdate(dbCmsHomeLayout);
    }

    @Override
    public CrudRootServiceHelper<DbCmsHomeText> getCmsHomeTextCrudRootServiceHelper() {
        return dbCmsHomeTextCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbCmsHomeLayout> getCmsHomeLayoutCrudRootServiceHelper() {
        return dbCmsHomeLayoutCrudRootServiceHelper;
    }
}
