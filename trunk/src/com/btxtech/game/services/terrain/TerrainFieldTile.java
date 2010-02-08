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

package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 12:21:54 PM
 */
@Deprecated
public interface TerrainFieldTile {
    Tile getTile();

    void setTile(Tile tile);

    Index getIndex();

}
