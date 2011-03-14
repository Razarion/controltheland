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

import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.utg.DbItemTypeLimitation;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.01.2011
 * Time: 22:28:24
 */
public class RealGameLevelEditor extends Panel {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public RealGameLevelEditor(String id) {
        super(id);
        // Condition
        add(new ConditionConfigPanel("dbConditionConfig"));

        // Scope
        add(new TextField("houseSpace"));
        add(new TextField("itemSellFactor"));

        // Reward
        add(new TextField("deltaMoney"));
        add(new TextField("deltaXp"));

        // Create Base
        add(new CheckBox("createRealBase"));
        add(new BaseItemTypePanel("startItemType"));
        add(new RectanglePanel("startRectangle"));
        add(new TextField("startItemFreeRange"));

        // Limitations
        add(new TextField("maxMoney"));
        add(new TextField("maxXp"));

        new CrudRootTableHelper<DbItemTypeLimitation>("itemTypeLimitation", null, "createItemTypeLimitation", false, this, false) {

            @Override
            protected CrudServiceHelper<DbItemTypeLimitation> getCrudRootServiceHelperImpl() {
                return ((DbRealGameLevel) RealGameLevelEditor.this.getParent().getDefaultModelObject()).getDbItemTypeLimitationCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(Item<DbItemTypeLimitation> dbItemTypeLimitationItem) {
                dbItemTypeLimitationItem.add(new BaseItemTypePanel("dbBaseItemType"));
                dbItemTypeLimitationItem.add(new TextField("count"));
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new Button(createId) {

                    @Override
                    public void onSubmit() {
                        DbRealGameLevel dbRealGameLevel = (DbRealGameLevel) RealGameLevelEditor.this.getParent().getDefaultModelObject();
                        userGuidanceService.createDbItemTypeLimitation(dbRealGameLevel);
                    }
                });
            }
        };

    }
}
