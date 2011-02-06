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

import java.util.List;

/**
 * User: beat
 * Date: 06.07.2010
 * Time: 21:39:03
 */
public interface CmsService {
    void activateHome();

    CmsContentStyleDTO getHomeContentStyleDTO();

    List<DbCmsHomeText> getDbCmsHomeTexts();

    void removeDbCmsHomeText(DbCmsHomeText dbCmsHomeText);

    void saveDbCmsHomeText(DbCmsHomeText dbCmsHomeText);

    void saveDbCmsHomeTexts(List<DbCmsHomeText> dbCmsHomeTexts);

    void createDbCmsHomeText();

    List<DbCmsHomeLayout> getDbCmsHomeLayouts();

    void removeDbCmsHomeLayout(DbCmsHomeLayout dbCmsHomeLayout);

    void saveDbCmsHomeLayout(DbCmsHomeLayout dbCmsHomeLayout);

    void saveDbCmsHomeLayouts(List<DbCmsHomeLayout> dbCmsHomeLayouts);

    void createDbCmsHomeLayout();
}
