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

import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 06.07.2010
 * Time: 21:39:03
 */
public interface CmsService {
    @Deprecated
    void activateHome();

    @Deprecated
    DbCmsHomeText getDbCmsHomeText();

    @Deprecated
    DbCmsHomeLayout getDbCmsHomeLayout();

    @Deprecated
    CrudRootServiceHelper<DbCmsHomeText> getCmsHomeTextCrudRootServiceHelper();

    @Deprecated
    CrudRootServiceHelper<DbCmsHomeLayout> getCmsHomeLayoutCrudRootServiceHelper();

    DbPage getPage(int pageId);

    DbContent getDbContent(int contentId);

    DbCmsImage getDbCmsImage(int imgId);

    void activateCms();

    CrudRootServiceHelper<DbCmsImage> getImageCrudRootServiceHelper();

    CrudRootServiceHelper<DbPage> getPageCrudRootServiceHelper();

    CrudRootServiceHelper<DbMenu> getMenuCrudRootServiceHelper();

    CrudRootServiceHelper<DbPageStyle> getPageStyleCrudRootServiceHelper();

    DbPage getPredefinedDbPage(DbPage.PredefinedType predefinedType);
}
