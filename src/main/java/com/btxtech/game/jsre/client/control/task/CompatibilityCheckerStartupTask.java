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

import com.btxtech.game.jsre.client.CompatibilityChecker;
import com.btxtech.game.jsre.client.CompatibilityCheckerAsync;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 12:59:49
 */
public class CompatibilityCheckerStartupTask extends AbstractStartupTask {

    public CompatibilityCheckerStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(final DeferredStartup deferredStartup) {
        deferredStartup.setDeferred();
        CompatibilityCheckerAsync compatibilityChecker = GWT.create(CompatibilityChecker.class);
        compatibilityChecker.getServerVersion(new AsyncCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
                deferredStartup.failed(caught);
            }

            @Override
            public void onSuccess(Integer version) {
                if(Constants.INTERFACE_VERSION == version) {
                    deferredStartup.finished();
                } else {
                    deferredStartup.failed(new IncompatibleRemoteServiceException("Server version: " + version + " Client version: " + Constants.INTERFACE_VERSION));
                }
            }
        });
    }
}
