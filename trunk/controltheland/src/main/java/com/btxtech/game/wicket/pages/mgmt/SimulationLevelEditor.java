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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.utg.DbSimulationLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.01.2011
 * Time: 22:30:43
 */
public class SimulationLevelEditor extends Panel {
    @SpringBean
    private TutorialService tutorialService;
    private Log log = LogFactory.getLog(DbLevelTable.class);

    public SimulationLevelEditor(String id, final DbSimulationLevel dbSimulationLevel) {
        super(id);
        add(new TextField<Integer>("dbTutorialConfig", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                if (dbSimulationLevel.getDbTutorialConfig() != null) {
                    return dbSimulationLevel.getDbTutorialConfig().getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer id) {
                try {
                    DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().readDbChild(id);
                    dbSimulationLevel.setDbTutorialConfig(dbTutorialConfig);
                } catch (Throwable t) {
                    log.error("", t);
                    error(t.getMessage());
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }
}
