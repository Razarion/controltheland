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

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.tutorial.DbItemTypeAndPosition;
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.DbTaskAllowedItem;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.IndexPanel;
import com.btxtech.game.wicket.uiservices.ItemTypePanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.ServiceHelper;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.pages.BrowserInfoPage;
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
public class TaskEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbTaskConfig> ruTaskServiceHelper;
    @SpringBean
    private ItemService itemService;
    @SpringBean
    private ServiceHelper serviceHelper;

    public TaskEditor(DbTaskConfig dbTaskConfig) {
        add(new FeedbackPanel("msgs"));

        final Form<DbTaskConfig> form = new Form<DbTaskConfig>("taskForm", new CompoundPropertyModel<DbTaskConfig>(new RuModel<DbTaskConfig>(dbTaskConfig, DbTaskConfig.class) {
            @Override
            protected RuServiceHelper<DbTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }
        }));
        add(form);

        form.add(new CheckBox("clearGame"));
        form.add(new TextField<String>("taskText"));
        form.add(new CheckBox("isScrollingAllowed"));
        form.add(new CheckBox("isOptionAllowed"));
        form.add(new CheckBox("isSellingAllowed"));
        form.add(new IndexPanel("scroll"));
        form.add(new TextField("accountBalance"));
        form.add(new TextField("houseCount"));
        form.add(new FileUploadField("upload", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                if (fileUpload == null) {
                    // I don't know why this is null
                    return;
                }
                ((DbTaskConfig) form.getDefaultModelObject()).setFinishImageData(fileUpload.getBytes());
                ((DbTaskConfig) form.getDefaultModelObject()).setFinishedImageContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
            }
        }));
        form.add(new TextField("finishImageDuration"));

        new CrudChildTableHelper<DbTaskConfig, DbTaskAllowedItem>("allowedItemTable", null, "createAllowedItem", false, form, false) {
            @Override
            protected RuServiceHelper<DbTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbTaskConfig getParent() {
                return (DbTaskConfig) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbTaskAllowedItem> getCrudChildServiceHelperImpl() {
                return ((DbTaskConfig) form.getDefaultModelObject()).getAllowedItemHelper();
            }

            @Override
            protected void extendedPopulateItem(Item<DbTaskAllowedItem> dbTaskAllowedItemItem) {
                dbTaskAllowedItemItem.add(new BaseItemTypePanel("dbBaseItemType"));
                dbTaskAllowedItemItem.add(new TextField("count"));
            }
        };


        new CrudChildTableHelper<DbTaskConfig, DbItemTypeAndPosition>("itemTable", null, "createItem", false, form, false) {
            @Override
            protected RuServiceHelper<DbTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbTaskConfig getParent() {
                return (DbTaskConfig) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbItemTypeAndPosition> getCrudChildServiceHelperImpl() {
                return ((DbTaskConfig) form.getDefaultModelObject()).getItemCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbItemTypeAndPosition> dbTaskConfigItem) {
                dbTaskConfigItem.add(new ItemTypePanel("itemType"));
                dbTaskConfigItem.add(new TextField("syncItemId"));
                dbTaskConfigItem.add(new IndexPanel("position"));
                dbTaskConfigItem.add(new TextField("angel"));
                dbTaskConfigItem.add(new TextField<Integer>("baseId"));

            }
        };
        new CrudChildTableHelper<DbTaskConfig, DbStepConfig>("stepTable", null, "createStep", true, form, true) {
            @Override
            protected void onEditSubmit(DbStepConfig dbStepConfig) {
                setResponsePage(new StepEditor(dbStepConfig));
            }

            @Override
            protected RuServiceHelper<DbTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbTaskConfig getParent() {
                return (DbTaskConfig) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbStepConfig> getCrudChildServiceHelperImpl() {
                return ((DbTaskConfig) form.getDefaultModelObject()).getStepConfigCrudServiceHelper();
            }
        };


        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruTaskServiceHelper.updateDbEntity((DbTaskConfig) form.getDefaultModelObject());
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