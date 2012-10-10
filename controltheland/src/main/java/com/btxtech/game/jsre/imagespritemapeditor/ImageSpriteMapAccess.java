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

package com.btxtech.game.jsre.imagespritemapeditor;

import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:18:09 PM
 */
@RemoteServiceRelativePath("imageSpriteMapAccess")
public interface ImageSpriteMapAccess extends RemoteService {
    ImageSpriteMapInfo loadImageSpriteMapInfo(int imageSpriteMapId);

    void saveImageSpriteMapInfo(ImageSpriteMapInfo imageSpriteMapInfo, String[] overriddenImages);
}