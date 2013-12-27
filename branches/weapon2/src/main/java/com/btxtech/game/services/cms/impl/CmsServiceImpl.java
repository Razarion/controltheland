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

import com.btxtech.game.jsre.common.CmsPredefinedPageDoesNotExistException;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.CmsSectionInfo;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.cms.NoDbContentInCacheException;
import com.btxtech.game.services.cms.NoDbPageException;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentBook;
import com.btxtech.game.services.cms.layout.DbContentBooleanExpressionImage;
import com.btxtech.game.services.cms.layout.DbContentGameLink;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.services.cms.layout.DbContentPageLink;
import com.btxtech.game.services.cms.layout.DbContentRow;
import com.btxtech.game.services.cms.layout.DbContentStaticHtml;
import com.btxtech.game.services.cms.page.DbAds;
import com.btxtech.game.services.cms.page.DbMenu;
import com.btxtech.game.services.cms.page.DbMenuItem;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.cms.page.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.db.DbI18nString;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private Log log = LogFactory.getLog(CmsServiceImpl.class);
    @Autowired
    private CrudRootServiceHelper<DbCmsImage> imageCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbPage> pageCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbMenu> menuCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbPageStyle> pageStyleCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbContent> contentCrud;
    @Autowired
    private CrudRootServiceHelper<DbAds> adsCrud;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private SessionFactory sessionFactory;

    private Map<Integer, DbCmsImage> imageCache = new HashMap<>();
    private Map<Integer, DbPage> pageCache = new HashMap<>();
    private Map<Integer, DbContent> contentCache = new HashMap<>();
    private Map<CmsUtil.CmsPredefinedPage, DbPage> predefinedDbPages = new HashMap<>();
    private Map<String, CmsSectionInfo> cmsSectionInfoMap = new HashMap<>();
    private String adsCode;

    @PostConstruct
    public void init() {
        imageCrudRootServiceHelper.init(DbCmsImage.class);
        pageCrudRootServiceHelper.init(DbPage.class);
        menuCrudRootServiceHelper.init(DbMenu.class);
        pageStyleCrudRootServiceHelper.init(DbPageStyle.class);
        contentCrud.init(DbContent.class);
        adsCrud.init(DbAds.class);
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            activateCms();
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
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
            try {
                initializeLazyDependencies(dbPage);
                pageCache.put(dbPage.getId(), dbPage);
                handlePredefinedDbPages(dbPage);
                DbContent dbContent = dbPage.getContent();
                if (dbContent != null) {
                    dbContent = HibernateUtil.deproxy(dbContent, DbContent.class);
                    dbPage.setContent(dbContent);
                    initializeLazyDependenciesAndFillContentCache(dbContent, dbPage);
                }
            } catch (Exception e) {
                log.error("CmsServiceImpl.activateCms() Activating page failed: " + dbPage, e);
            }
        }
        adsCode = null;
        for (DbAds dbAds : adsCrud.readDbChildren()) {
            if (dbAds.isActive()) {
                if (adsCode != null) {
                    log.warn("More than one active ADS configured");
                }
                adsCode = dbAds.getCode();
            }
        }
        if (adsCode == null) {
            log.warn("No active ADS configured");
            adsCode = null;
        }
        cmsUiService.setupPredefinedUrls();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void checkPredefinedDbPages() {
        List<CmsUtil.CmsPredefinedPage> predefinedTypes = Arrays.asList(CmsUtil.CmsPredefinedPage.values());
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
        for (CmsUtil.CmsPredefinedPage predefinedType : predefinedTypes) {
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
            log.warn("Predefined DbPage already exits: " + dbPage);
        }
        predefinedDbPages.put(dbPage.getPredefinedType(), dbPage);
    }


    private void initializeLazyDependenciesAndFillContentCache(DbContent dbContent, DbPage dbPage) {
        Hibernate.initialize(dbContent);
        if (dbContent instanceof DbContentPageLink) {
            Hibernate.initialize(((DbContentPageLink) dbContent).getDbCmsImage());
            initializeI18n(((DbContentPageLink) dbContent).getDbI18nName());
        }
        if (dbContent instanceof DbContentGameLink) {
            Hibernate.initialize(((DbContentGameLink) dbContent).getDbCmsImage());
            initializeI18n(((DbContentGameLink) dbContent).getDbI18nName());
        }
        if (dbContent instanceof DbContentBooleanExpressionImage) {
            Hibernate.initialize(((DbContentBooleanExpressionImage) dbContent).getTrueImage());
            Hibernate.initialize(((DbContentBooleanExpressionImage) dbContent).getFalseImage());
        }
        if (dbContent instanceof DbContentStaticHtml) {
            initializeI18n(((DbContentStaticHtml) dbContent).getDbI18nHtml());
        }
        if (dbContent instanceof DbContentRow) {
            initializeI18n(((DbContentRow) dbContent).getDbI18nName());
        }
        if (dbContent instanceof DbContentBook) {
            DbContentBook dbContentBook = (DbContentBook) dbContent;
            if (dbContentBook.getClassName() != null) {
                try {
                    Class childClass = Class.forName(dbContentBook.getClassName());
                    if (DbItemType.class.isAssignableFrom(childClass)) {
                        cmsSectionInfoMap.put(CmsUtil.UNIT_SECTION, new CmsSectionInfo(DbItemType.class, HibernateUtil.deproxy(dbContentBook.getParent(), DbContentList.class), CmsUtil.UNIT_SECTION, dbPage.getId()));
                    } else if (DbLevel.class.isAssignableFrom(childClass)) {
                        cmsSectionInfoMap.put(CmsUtil.LEVEL_SECTION, new CmsSectionInfo(DbLevel.class, HibernateUtil.deproxy(dbContentBook.getParent(), DbContentList.class), CmsUtil.LEVEL_SECTION, dbPage.getId()));
                    } else if (DbLevelTask.class.isAssignableFrom(childClass)) {
                        cmsSectionInfoMap.put(CmsUtil.LEVEL_TASK_SECTION, new CmsSectionInfo(DbLevelTask.class, HibernateUtil.deproxy(dbContentBook.getParent(), DbContentList.class), CmsUtil.LEVEL_TASK_SECTION, dbPage.getId()));
                    }
                } catch (ClassNotFoundException e) {
                    log.error("", e);
                }
            }
        }
        contentCache.put(dbContent.getId(), dbContent);
        Collection<? extends DbContent> children = dbContent.getChildren();
        if (children != null) {
            for (DbContent child : children) {
                initializeLazyDependenciesAndFillContentCache(child, dbPage);
            }
        }
    }

    private void initializeI18n(DbI18nString dbI18nName) {
        Hibernate.initialize(dbI18nName);
        Hibernate.initialize(dbI18nName.getLocalizedStrings());
    }

    private void initializeLazyDependencies(DbPage dbPage) {
        Hibernate.initialize(dbPage);
        Hibernate.initialize(dbPage.getStyle());
        initializeI18n(dbPage.getDbI18nName());
        initializeLazyMenu(dbPage.getMenu());
    }

    private void initializeLazyMenu(DbMenu dbMenu) {
        Hibernate.initialize(dbMenu);
        if (dbMenu != null) {
            if (dbMenu.getBottom() != null) {
                initializeLazyDependenciesAndFillContentCache(HibernateUtil.deproxy(dbMenu.getBottom(), DbContent.class), null);
                dbMenu.setBottom(HibernateUtil.deproxy(dbMenu.getBottom(), DbContent.class));
                contentCache.put(dbMenu.getBottom().getId(), dbMenu.getBottom());
            }
            for (DbMenuItem dbMenuItem : dbMenu.getMenuItemCrudChildServiceHelper().readDbChildren()) {
                Hibernate.initialize(dbMenuItem);
                initializeI18n(dbMenuItem.getDbI18nName());
                Hibernate.initialize(dbMenuItem.getPage());
                if (dbMenuItem.getSubMenu() != null) {
                    initializeLazyMenu(dbMenuItem.getSubMenu());
                }
            }
        }
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
    public CrudRootServiceHelper<DbContent> getContentCrud() {
        return contentCrud;
    }

    @Override
    public DbPage getPredefinedDbPage(CmsUtil.CmsPredefinedPage predefinedType) throws CmsPredefinedPageDoesNotExistException {
        DbPage dbPage = predefinedDbPages.get(predefinedType);
        if (dbPage == null) {
            throw new CmsPredefinedPageDoesNotExistException(predefinedType);
        }
        return dbPage;
    }

    @Override
    public CmsSectionInfo getCmsSectionInfo(String sectionName) {
        CmsSectionInfo cmsSectionInfo = cmsSectionInfoMap.get(sectionName);
        if (cmsSectionInfo == null) {
            throw new IllegalArgumentException("No CMS section entry for: " + sectionName);
        }
        return cmsSectionInfo;
    }

    @Override
    public CmsSectionInfo getCmsSectionInfo4Class(Class clazz) {
        for (CmsSectionInfo cmsSectionInfo : cmsSectionInfoMap.values()) {
            if (cmsSectionInfo.isAssignableFrom(clazz)) {
                return cmsSectionInfo;
            }
        }
        return null;
    }

    @Override
    public boolean hasPredefinedDbPage(CmsUtil.CmsPredefinedPage predefinedType) {
        return predefinedDbPages.containsKey(predefinedType);
    }

    @Override
    public DbPage getPage(int pageId) {
        DbPage dbPage = pageCache.get(pageId);
        if (dbPage == null) {
            throw new NoDbPageException(pageId);
        }
        return dbPage;
    }

    @Override
    public DbContent getDbContent(int contentId) {
        DbContent dbContent = contentCache.get(contentId);
        if (dbContent == null) {
            throw new NoDbContentInCacheException(contentId);
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

    @Override
    public String getAdsCode() {
        return adsCode;
    }

    @Override
    public CrudRootServiceHelper<DbAds> getAdsCrud() {
        return adsCrud;
    }
}
