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

package com.btxtech.game.jsre.client.control.task;

import com.btxtech.game.jsre.client.GwtCommon;

/**
 * User: beat
 * Date: 07.12.2010
 * Time: 21:45:05
 */
public class SimpleDeferredStartup implements DeferredStartup {
    @Override
    public void setDeferred() {
        // Ignore
    }

    @Override
    public void finished() {
        // Ignore
    }

    @Override
    public void failed(Throwable throwable) {
        GwtCommon.handleException(throwable);
    }

    @Override
    public void failed(String error) {
        GwtCommon.sendLogToServer(error);
    }

    @Override
    public void setParallel() {
        // Ignore
    }
}
