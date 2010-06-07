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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.territory.DbTerritory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

/**
 * User: beat
 * Date: 22.05.2010
 * Time: 15:22:28
 */
public class TerritoryDesigner extends WebPage {
    public TerritoryDesigner(DbTerritory dbTerritory) {

    }

    public TerritoryDesigner(PageParameters parameters) {
        super(parameters);
    }
}
