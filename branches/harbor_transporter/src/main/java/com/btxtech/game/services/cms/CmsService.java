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

package com.btxtech.game.services.cms;

import com.btxtech.game.jsre.common.CmsPredefinedPageDoesNotExistException;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.page.DbAds;
import com.btxtech.game.services.cms.page.DbMenu;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.cms.page.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 06.07.2010
 * Time: 21:39:03
 */
public interface CmsService {
    DbPage getPage(int pageId);

    DbContent getDbContent(int contentId);

    DbCmsImage getDbCmsImage(int imgId);

    void activateCms();

    CrudRootServiceHelper<DbCmsImage> getImageCrudRootServiceHelper();

    CrudRootServiceHelper<DbPage> getPageCrudRootServiceHelper();

    CrudRootServiceHelper<DbMenu> getMenuCrudRootServiceHelper();

    CrudRootServiceHelper<DbPageStyle> getPageStyleCrudRootServiceHelper();

    CrudRootServiceHelper<DbContent> getContentCrud();

    boolean hasPredefinedDbPage(CmsUtil.CmsPredefinedPage predefinedType);

    DbPage getPredefinedDbPage(CmsUtil.CmsPredefinedPage predefinedType) throws CmsPredefinedPageDoesNotExistException;

    CmsSectionInfo getCmsSectionInfo(String sectionName);

    CmsSectionInfo getCmsSectionInfo4Class(Class clazz);

    String getAdsCode();

    CrudRootServiceHelper<DbAds> getAdsCrud();
}
