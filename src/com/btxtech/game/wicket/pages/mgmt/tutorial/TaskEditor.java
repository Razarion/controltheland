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

package com.btxtech.game.wicket.pages.mgmt.tutorial;

import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.tutorial.DbItemTypeAndPosition;
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.wicket.pages.mgmt.ItemsUtil;
import com.btxtech.game.wicket.pages.mgmt.tutorial.condition.ConditionWrapperPanel;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class TaskEditor extends WebPage {
    @SpringBean
    private TutorialService tutorialService;
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(TaskEditor.class);
    private Collection<DbBaseItemType> itemTypes;

    public TaskEditor(final DbTutorialConfig dbTutorialConfig, final DbTaskConfig dbTaskConfig) {
        itemTypes = itemService.getDbBaseItemTypes();

        add(new FeedbackPanel("msgs"));

        Form<DbTaskConfig> form = new Form<DbTaskConfig>("taskForm", new CompoundPropertyModel<DbTaskConfig>(dbTaskConfig));
        add(form);

        form.add(new CheckBox("clearGame"));
        form.add(new TextField<String>("taskText"));
        form.add(new CheckBox("isScrollingAllowed"));
        form.add(new CheckBox("isInfoBoxVisible"));
        form.add(new CheckBox("isOptionAllowed"));
        form.add(new CheckBox("isSellingAllowed"));
        form.add(new TextField("scroll.x"));
        form.add(new TextField("scroll.y"));
        form.add(new ConditionWrapperPanel("completionConditionConfig", dbTaskConfig));
        form.add(new TextField<String>("allowedItemTypeIds", new IModel<String>() {

            @Override
            public String getObject() {
                return ItemsUtil.itemTypesToString(dbTaskConfig.getAllowedItems());
            }

            @Override
            public void setObject(String s) {
                dbTaskConfig.setAllowedItems(ItemsUtil.stringToItemTypes(s, itemTypes));
            }

            @Override
            public void detach() {
                // Ignored
            }
        }));
        form.add(new TextField("accountBalance"));
        form.add(new TextField("itemLimit"));
        form.add(new TextField("houseCount"));
        form.add(new FileUploadField("upload", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                dbTaskConfig.setFinishImageData(fileUpload.getBytes());
                dbTaskConfig.setFinishedImageContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
            }
        }));
        form.add(new TextField("finishImageDuration"));


        new CrudTableHelper<DbItemTypeAndPosition>("itemTable", null, "createItem", false, form) {

            @Override
            protected CrudServiceHelper<DbItemTypeAndPosition> getCrudServiceHelper() {
                return dbTaskConfig.getItemCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbItemTypeAndPosition> dbTaskConfigItem) {
                dbTaskConfigItem.add(new TextField<Integer>("itemTypeId", new IModel<Integer>() {

                    @Override
                    public Integer getObject() {
                        DbItemType itemType = dbTaskConfigItem.getModelObject().getItemType();
                        if (itemType != null) {
                            return itemType.getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        if (id != null) {
                            try {
                                dbTaskConfigItem.getModelObject().setItemType(itemService.getDbItemType(id));
                            } catch (Throwable t) {
                                log.error("", t);
                                error(t.getMessage());
                            }
                        } else {
                            dbTaskConfigItem.getModelObject().setItemType(null);
                        }
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                }, Integer.class));
                dbTaskConfigItem.add(new TextField("syncItemId"));
                dbTaskConfigItem.add(new TextField("position.x"));
                dbTaskConfigItem.add(new TextField("position.y"));
                dbTaskConfigItem.add(new TextField("angel"));
                dbTaskConfigItem.add(new TextField<Integer>("baseId"));

            }
        };
        new CrudTableHelper<DbStepConfig>("stepTable", null, "createStep", true, form) {
            @Override
            protected CrudServiceHelper<DbStepConfig> getCrudServiceHelper() {
                return dbTaskConfig.getStepConfigCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbStepConfig> dbStepConfigItem) {
                super.extendedPopulateItem(dbStepConfigItem);
                dbStepConfigItem.add(new Button("up") {
                    @Override
                    public void onSubmit() {
                        dbTaskConfig.moveTaskUp(dbStepConfigItem.getModelObject());
                    }
                });
                dbStepConfigItem.add(new Button("down") {
                    @Override
                    public void onSubmit() {
                        dbTaskConfig.moveTaskDown(dbStepConfigItem.getModelObject());
                    }
                });
            }

            @Override
            protected void onEditSubmit(DbStepConfig dbStepConfig) {
                setResponsePage(new StepEditor(dbTutorialConfig, dbStepConfig));
            }
        };


        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                tutorialService.getDbTutorialCrudServiceHelper().updateDbChild(dbTutorialConfig);
            }
        });
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(TutorialTable.class);
            }
        });


    }

}