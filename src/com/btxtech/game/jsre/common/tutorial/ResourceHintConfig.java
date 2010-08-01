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

package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import java.io.Serializable;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 22:28:34
 */
public class ResourceHintConfig implements Serializable {
    private Index position;
    private int imageId;

    /**
     * Used by GWT
     */
    public ResourceHintConfig() {
    }

    public ResourceHintConfig(Index position, int imageId) {
        this.position = position;
        this.imageId = imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public Index getPosition() {
        return position;
    }

    public int getImageId() {
        return imageId;
    }
}
