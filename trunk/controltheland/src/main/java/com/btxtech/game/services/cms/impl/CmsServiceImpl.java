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

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsHomeLayout;
import com.btxtech.game.services.cms.DbCmsHomeText;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentInvoker;
import com.btxtech.game.services.cms.DbContentInvokerButton;
import com.btxtech.game.services.cms.DbContentLink;
import com.btxtech.game.services.cms.DbContentPageLink;
import com.btxtech.game.services.cms.DbMenu;
import com.btxtech.game.services.cms.DbMenuItem;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.cms.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat Date: 06.07.2010 Time: 21:41:45
 */
@Component("cmsService")
public class CmsServiceImpl implements CmsService {
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(CmsServiceImpl.class);
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private CrudRootServiceHelper<DbCmsHomeText> dbCmsHomeTextCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbCmsHomeLayout> dbCmsHomeLayoutCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbCmsImage> imageCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbPage> pageCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbMenu> menuCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbPageStyle> pageStyleCrudRootServiceHelper;

    private DbCmsHomeText dbCmsHomeText;
    private DbCmsHomeLayout dbCmsHomeLayout;
    private Map<Integer, DbCmsImage> imageCache = new HashMap<Integer, DbCmsImage>();
    private Map<Integer, DbPage> pageCache = new HashMap<Integer, DbPage>();
    private Map<Integer, DbContent> contentCache = new HashMap<Integer, DbContent>();
    private Map<DbPage.PredefinedType, DbPage> predefinedDbPages = new HashMap<DbPage.PredefinedType, DbPage>();

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        dbCmsHomeTextCrudRootServiceHelper.init(DbCmsHomeText.class);
        dbCmsHomeLayoutCrudRootServiceHelper.init(DbCmsHomeLayout.class);
        imageCrudRootServiceHelper.init(DbCmsImage.class);
        pageCrudRootServiceHelper.init(DbPage.class);
        menuCrudRootServiceHelper.init(DbMenu.class);
        pageStyleCrudRootServiceHelper.init(DbPageStyle.class);
        SessionFactoryUtils.initDeferredClose(hibernateTemplate.getSessionFactory());
        try {
            activateHome();
            activateCms();
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            SessionFactoryUtils.processDeferredClose(hibernateTemplate.getSessionFactory());
        }
    }

    @Override
    public void activateCms() {
        //checkPredefinedDbPages();
        imageCache.clear();
        for (DbCmsImage dbCmsImage : imageCrudRootServiceHelper.readDbChildren()) {
            imageCache.put(dbCmsImage.getId(), dbCmsImage);
        }
        predefinedDbPages.clear();
        pageCache.clear();
        contentCache.clear();
        for (DbPage dbPage : pageCrudRootServiceHelper.readDbChildren()) {
            initializeLazyDependencies(dbPage);
            pageCache.put(dbPage.getId(), dbPage);
            handlePredefinedDbPages(dbPage);
            DbContent dbContent = dbPage.getContent();
            if (dbContent != null) {
                dbContent = HibernateUtil.deproxy(dbContent, DbContent.class);
                dbPage.setContent(dbContent);
                initializeLazyDependenciesAndFillContentCache(dbContent);
            }
        }
    }

    private void checkPredefinedDbPages() {
        List<DbPage.PredefinedType> predefinedTypes = Arrays.asList(DbPage.PredefinedType.values());
        for (DbPage dbPage : pageCrudRootServiceHelper.readDbChildren()) {
            if (dbPage.getPredefinedType() != null) {
                predefinedTypes.remove(dbPage.getPredefinedType());
            }
        }
        if (predefinedTypes.isEmpty()) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Not all predefined pages have been configured: ");
        for (DbPage.PredefinedType predefinedType : predefinedTypes) {
            builder.append(predefinedType.name());
            builder.append(" ");
        }
        throw new IllegalStateException(builder.toString());
    }

    private void handlePredefinedDbPages(DbPage dbPage) {
        if (dbPage.getPredefinedType() == null) {
            return;
        }
        if (predefinedDbPages.containsKey(dbPage.getPredefinedType())) {
            throw new IllegalStateException("Predefined DbPage already exits: " + dbPage);
        }
        predefinedDbPages.put(dbPage.getPredefinedType(), dbPage);
    }

    private void initializeLazyDependenciesAndFillContentCache(DbContent dbContent) {
        Hibernate.initialize(dbContent);
        if (dbContent instanceof DbContentPageLink) {
            Hibernate.initialize(((DbContentPageLink) dbContent).getDbCmsImage());
        }
        if (dbContent instanceof DbContentLink) {
            Hibernate.initialize(((DbContentLink) dbContent).getDbCmsImage());
        }
        contentCache.put(dbContent.getId(), dbContent);
        Collection<? extends DbContent> children = dbContent.getChildren();
        if (children != null) {
            for (DbContent child : children) {
                initializeLazyDependenciesAndFillContentCache(child);
            }
        }
    }

    private void initializeLazyDependencies(DbPage dbPage) {
        Hibernate.initialize(dbPage);
        Hibernate.initialize(dbPage.getStyle());
        initializeLazyMenu(dbPage.getMenu());
    }

    private void initializeLazyMenu(DbMenu dbMenu) {
        Hibernate.initialize(dbMenu);
        if (dbMenu != null) {
            for (DbMenuItem dbMenuItem : dbMenu.getMenuItemCrudChildServiceHelper().readDbChildren()) {
                Hibernate.initialize(dbMenuItem);
                Hibernate.initialize(dbMenuItem.getPage());
                if (dbMenuItem.getSubMenu() != null) {
                    initializeLazyMenu(dbMenuItem.getSubMenu());
                }
            }
        }
    }

    @Override
    @Deprecated
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
        dbCmsHomeText = dbCmsHomeTexts.get(0);

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
        dbCmsHomeLayout = dbCmsHomeLayouts.get(0);
    }

    @Override
    @Deprecated
    public CrudRootServiceHelper<DbCmsHomeText> getCmsHomeTextCrudRootServiceHelper() {
        return dbCmsHomeTextCrudRootServiceHelper;
    }

    @Override
    @Deprecated
    public CrudRootServiceHelper<DbCmsHomeLayout> getCmsHomeLayoutCrudRootServiceHelper() {
        return dbCmsHomeLayoutCrudRootServiceHelper;
    }

    @Override
    @Deprecated
    public DbCmsHomeText getDbCmsHomeText() {
        return dbCmsHomeText;
    }

    @Override
    @Deprecated
    public DbCmsHomeLayout getDbCmsHomeLayout() {
        return dbCmsHomeLayout;
    }

    @Override
    public CrudRootServiceHelper<DbCmsImage> getImageCrudRootServiceHelper() {
        return imageCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbPage> getPageCrudRootServiceHelper() {
        return pageCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbMenu> getMenuCrudRootServiceHelper() {
        return menuCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbPageStyle> getPageStyleCrudRootServiceHelper() {
        return pageStyleCrudRootServiceHelper;
    }

    @Override
    public DbPage getPredefinedDbPage(DbPage.PredefinedType predefinedType) {
        DbPage dbPage = predefinedDbPages.get(predefinedType);
        if (dbPage == null) {
            throw new IllegalStateException("Predefined DbPage does not exist: " + predefinedType);
        }
        return dbPage;
    }

    @Override
    public DbPage getPage(int pageId) {
        DbPage dbPage = pageCache.get(pageId);
        if (dbPage == null) {
            throw new IllegalArgumentException("No DbPage for id: " + pageId);
        }
        return dbPage;
    }

    @Override
    public DbContent getDbContent(int contentId) {
        DbContent dbContent = contentCache.get(contentId);
        if (dbContent == null) {
            throw new IllegalArgumentException("No content for id: " + contentId);
        }
        return dbContent;
    }

    @Override
    public DbCmsImage getDbCmsImage(int imgId) {
        DbCmsImage dbCmsImage = imageCache.get(imgId);
        if (dbCmsImage == null) {
            throw new IllegalArgumentException("No CmsImage for id: " + imgId);
        }
        return dbCmsImage;
    }
}
