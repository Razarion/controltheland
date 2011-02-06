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

package com.btxtech.game.jsre.client.utg;

import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: 14.11.2010
 * Time: 13:17:44
 */
public interface ImageSizeCallback {
    /**
     * Can be called multiple time
     *
     * @param image Image
     * @param width  of the image
     * @param height of the image
     */
    void onImageSize(Image image, int width, int height);
}
