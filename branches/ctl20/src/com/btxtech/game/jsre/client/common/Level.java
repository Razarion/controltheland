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

package com.btxtech.game.jsre.client.common;

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.05.2010
 * Time: 18:48:18
 */
public class Level implements Serializable {
    private String name;
    private boolean runTutorial = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRunTutorial() {
        return runTutorial;
    }

    public void setRunTutorial(boolean runTutorial) {
        this.runTutorial = runTutorial;
    }
}
