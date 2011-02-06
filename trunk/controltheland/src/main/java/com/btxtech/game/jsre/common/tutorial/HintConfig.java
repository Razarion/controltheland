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

import java.io.Serializable;

/**
 * User: beat
 * Date: 05.11.2010
 * Time: 18:44:42
 */
public class HintConfig implements Serializable {
    private boolean closeOnTaskEnd;

    /**
     * Used by GWT
     */
    public HintConfig() {
    }

    public HintConfig(boolean closeOnTaskEnd) {
        this.closeOnTaskEnd = closeOnTaskEnd;
    }

    public boolean isCloseOnTaskEnd() {
        return closeOnTaskEnd;
    }
}
