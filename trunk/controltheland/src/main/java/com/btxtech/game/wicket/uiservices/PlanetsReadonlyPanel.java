/*
 * Copyright (c) 2011.
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

package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collection;
import java.util.Iterator;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class PlanetsReadonlyPanel extends Panel {
    @SpringBean
    private PlanetSystemService planetSystemService;

    public PlanetsReadonlyPanel(String id) {
        super(id);
        add(new Label("planetNames", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                Collection<DbPlanet> dbPlanets = (Collection<DbPlanet>) getDefaultModelObject();
                if (dbPlanets != null) {
                    StringBuilder builder = new StringBuilder();
                    for (Iterator<DbPlanet> iterator = dbPlanets.iterator(); iterator.hasNext(); ) {
                        DbPlanet dbPlanet = iterator.next();
                        builder.append(dbPlanet.getName());
                        builder.append(" (");
                        builder.append(dbPlanet.getId());
                        builder.append(")");
                        if (iterator.hasNext()) {
                            builder.append("<br>");
                        }
                    }
                    return builder.toString();
                } else {
                    return null;
                }
            }
        }).setEscapeModelStrings(false));

    }
}
