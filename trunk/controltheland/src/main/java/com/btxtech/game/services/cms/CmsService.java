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

import com.btxtech.game.services.cms.generated.cms.DbCmsImage;
import com.btxtech.game.services.cms.generated.cms.DbPage;
import com.btxtech.game.services.cms.generated.cms.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 06.07.2010
 * Time: 21:39:03
 */
public interface CmsService {
    void activateHome();

    DbCmsHomeText getDbCmsHomeText();

    DbCmsHomeLayout getDbCmsHomeLayout();

    CrudRootServiceHelper<DbCmsHomeText> getCmsHomeTextCrudRootServiceHelper();

    CrudRootServiceHelper<DbCmsHomeLayout> getCmsHomeLayoutCrudRootServiceHelper();
    
    DbPage getPage(int pageId);

    DbPage getDefaultPage();

	DbPageStyle getStyle(int styleId);

	DbCmsImage getDbCmsImage(int imgId);
}
