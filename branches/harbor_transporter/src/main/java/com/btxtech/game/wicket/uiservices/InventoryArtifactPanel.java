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

import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.GlobalInventoryService;
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
public class InventoryArtifactPanel extends Panel {
    @SpringBean
    private GlobalInventoryService globalInventoryService;

    public InventoryArtifactPanel(String id) {
        super(id);
        add(new TextField<>("inventoryArtifact", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbInventoryArtifact dbInventoryArtifact = (DbInventoryArtifact) getDefaultModelObject();
                if (dbInventoryArtifact != null) {
                    return dbInventoryArtifact.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                if (integer != null) {
                    DbInventoryArtifact dbInventoryArtifact = globalInventoryService.getArtifactCrud().readDbChild(integer);
                    if (dbInventoryArtifact == null) {
                        error("Inventory artifact does not exist: " + integer);
                        return;
                    }
                    setDefaultModelObject(dbInventoryArtifact);
                } else {
                    setDefaultModelObject(null);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new Label("name", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                DbInventoryArtifact dbInventoryArtifact = (DbInventoryArtifact) getDefaultModelObject();
                if (dbInventoryArtifact != null) {
                    return dbInventoryArtifact.getName();
                } else {
                    return null;
                }
            }
        }));
        add(new Label("rareness", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                DbInventoryArtifact dbInventoryArtifact = (DbInventoryArtifact) getDefaultModelObject();
                if (dbInventoryArtifact != null) {
                    if (dbInventoryArtifact.getRareness() != null) {
                        return "[" + dbInventoryArtifact.getRareness().name() + "]";
                    } else {
                        return "[]";
                    }
                } else {
                    return null;
                }

            }
        }));

    }
}
