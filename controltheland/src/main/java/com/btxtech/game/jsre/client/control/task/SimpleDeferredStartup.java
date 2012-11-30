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

import com.btxtech.game.jsre.client.ClientExceptionHandler;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 07.12.2010
 * Time: 21:45:05
 */
public class SimpleDeferredStartup implements DeferredStartup {
    private Logger log = Logger.getLogger(SimpleDeferredStartup.class.getName());

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
        ClientExceptionHandler.handleException(throwable);
    }

    @Override
    public void failed(String error) {
        log.severe("SimpleDeferredStartup.failed(): " + error);
    }

    @Override
    public void setBackground() {
        // Ignore
    }

    @Override
    public boolean isBackground() {
        return false;
    }
}
