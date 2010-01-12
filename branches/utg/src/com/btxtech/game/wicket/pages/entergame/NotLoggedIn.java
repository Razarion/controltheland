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

import com.btxtech.game.wicket.pages.BorderPanel;
import com.btxtech.game.wicket.pages.user.NewUser;
import org.apache.wicket.markup.html.link.Link;

/**
 * User: beat
 * Date: Oct 14, 2009
 * Time: 8:24:34 PM
 */
public class NotLoggedIn extends BorderPanel {
    public NotLoggedIn(String id) {
        super(id);
        add(new Link("link") {

            @Override
            public void onClick() {
                setResponsePage(new NewUser());
            }
        });
    }
}
