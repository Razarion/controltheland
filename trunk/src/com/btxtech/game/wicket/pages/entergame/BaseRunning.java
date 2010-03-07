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

package com.btxtech.game.wicket.pages.entergame;

import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.wicket.pages.BorderPanel;
import com.btxtech.game.wicket.pages.Game;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Oct 14, 2009
 * Time: 8:24:34 PM
 */
public class BaseRunning extends BorderPanel {
    @SpringBean
    private BaseService baseService;

    public BaseRunning(String id) {
        super(id);
        Form form = new Form("form");
        add(form);
        form.add(new Button("continueExistingBase") {

            @Override
            public void onSubmit() {
                baseService.continueBase();
                setResponsePage(Game.class);
            }
        });
        form.add(new Button("startNewBase") {

            @Override
            public void onSubmit() {
                baseService.surrenderBase(baseService.getBaseForLoggedInUser());
            }
        });
    }
}