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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class PlanetPanel extends Panel {
    @SpringBean
    private PlanetSystemService planetSystemService;

    public PlanetPanel(String id) {
        super(id);
        add(new TextField<>("planet", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbPlanet dbPlanet = (DbPlanet) getDefaultModelObject();
                if (dbPlanet != null) {
                    return dbPlanet.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                if (integer != null) {
                    DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(integer);
                    setDefaultModelObject(dbPlanet);
                } else {
                    setDefaultModelObject(null);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new Label("planetName", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                DbPlanet dbPlanet = (DbPlanet) getDefaultModelObject();
                if (dbPlanet != null) {
                    return dbPlanet.getName();
                } else {
                    return null;
                }
            }
        }));

    }
}
