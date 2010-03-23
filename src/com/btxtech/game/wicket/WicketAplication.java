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

package com.btxtech.game.wicket;

import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.wicket.pages.home.Home;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 9:51:34 PM
 */
public class WicketAplication extends WebApplication {
    @Autowired
    private MgmtService mgmtService;
    private String configurationType;

    @Override
    protected void init() {
        addComponentInstantiationListener(new SpringComponentInjector(this));
    }

    public Class<Home> getHomePage() {
        return Home.class;
    }

    @Override
    public String getConfigurationType() {
        if (configurationType == null) {
            if (mgmtService.isTestMode()) {
                configurationType = Application.DEVELOPMENT;
            } else {
                configurationType = Application.DEPLOYMENT;
            }
        }
        return configurationType;
    }
}