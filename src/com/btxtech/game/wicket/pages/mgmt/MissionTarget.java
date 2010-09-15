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

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.utg.DbItemCount;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.uiservices.ListProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * User: beat
 * Date: 14.05.2010
 * Time: 14:53:19
 */
public class MissionTarget extends WebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(MissionTarget.class);

    public MissionTarget(final DbLevel dbLevel) {
        add(new FeedbackPanel("msgs"));

        Form<DbLevel> form = new Form<DbLevel>("form", new CompoundPropertyModel<DbLevel>(dbLevel));

        TextArea<String> contentArea = new TextArea<String>("missionTarget");
        TinyMCESettings tinyMCESettings = new TinyMCESettings();
        contentArea.add(new TinyMceBehavior(tinyMCESettings));
        form.add(contentArea);

        final ListProvider<DbItemCount> itemCounts = new ListProvider<DbItemCount>() {
            @Override
            protected List<DbItemCount> createList() {
                return new ArrayList<DbItemCount>(dbLevel.getDbItemCounts());
            }
        };
        form.add(new DataView<DbItemCount>("itemCounts", itemCounts) {

            @Override
            protected void populateItem(final Item<DbItemCount> dbLevelItem) {
                dbLevelItem.add(new TextField<Integer>("itemType", new IModel<Integer>() {

                    @Override
                    public Integer getObject() {
                        DbBaseItemType dbBaseItemType = dbLevelItem.getModelObject().getBaseItemType();
                        if (dbBaseItemType != null) {
                            return dbBaseItemType.getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        if (id != null) {
                            try {
                                DbBaseItemType baseItemType = (DbBaseItemType) itemService.getDbItemType(id);
                                dbLevelItem.getModelObject().setBaseItemType(baseItemType);
                            } catch (Throwable t) {
                                log.error("", t);
                                throw new RuntimeException(t);
                            }
                        } else {
                            dbLevelItem.getModelObject().setBaseItemType(null);
                        }
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                }, Integer.class));
                dbLevelItem.add(new TextField<Integer>("count"));

                dbLevelItem.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        dbLevel.removeDbItemCount(dbLevelItem.getModelObject());
                    }
                });
            }
        });
        form.add(new Button("addItemCount") {
            @Override
            public void onSubmit() {
                dbLevel.createDbItemCount();
            }
        });
        form.add(new TextField<Integer>("minXp"));
        form.add(new TextField<Integer>("minMoney"));
        form.add(new TextField<Integer>("deltaMoney"));
        form.add(new TextField<Integer>("deltaKills"));
        form.add(new TextField<String>("skipItemsBought", new IModel<String>() {

            @Override
            public String getObject() {
                return ItemsUtil.itemTypesToString(dbLevel.getSkipIfItemsBought());
            }

            @Override
            public void setObject(String itemsString) {
                dbLevel.setSkipIfItemsBought(ItemsUtil.stringToItemTypes(itemsString, itemService.getDbBaseItemTypes()));
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
        form.add(new TextField<Integer>("itemLimit"));
        form.add(new TextField<Integer>("houseSpace"));


        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                userGuidanceService.saveDbLevel(dbLevel);
                setResponsePage(LevelAndMissionTarget.class);
            }
        });
        form.add(new Button("cancel") {
            @Override
            public void onSubmit() {
                setResponsePage(LevelAndMissionTarget.class);
            }
        });
        add(form);

    }
}
