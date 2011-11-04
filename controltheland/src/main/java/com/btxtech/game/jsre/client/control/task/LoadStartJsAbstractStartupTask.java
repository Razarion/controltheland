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

import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.google.gwt.core.client.JavaScriptObject;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 12:32:48
 */
public class LoadStartJsAbstractStartupTask extends AbstractStartupTask {
    private Logger log = Logger.getLogger(LoadStartJsAbstractStartupTask.class.getName());

    public LoadStartJsAbstractStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
    }

    @Override
    protected long calculateStartTime() {
        Object object = getNativeCtlStartTime();
        if (object == null) {
            log.severe("LoadStartJsAbstractStartupTask getNativeCtlStartTime() returned 0");
            return 0;
        } else if (object instanceof Number) {
            return (Long) object;
        } else {
            log.severe("LoadStartJsAbstractStartupTask getNativeCtlStartTime() returned: " + object);
            return 0;
        }
    }

    private native JavaScriptObject getNativeCtlStartTime() /*-{
      return $wnd.ctlStartTime;
    }-*/;
}
