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

package com.btxtech.game.wicket.pages.info;

import com.btxtech.game.wicket.pages.BorderPanel;
import com.btxtech.game.wicket.uiservices.GameControlService;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Oct 14, 2009
 * Time: 2:04:48 PM
 */
public class InfoMainContent extends BorderPanel {
    @SpringBean
    private GameControlService gameControlService;

    public InfoMainContent(String id) {
        super(id);
        Form form = new Form("form") {
            @Override
            protected void onSubmit() {
                setResponsePage(gameControlService.getEnterGamePage(false));
            }
        };
        add(form);

    }

}
