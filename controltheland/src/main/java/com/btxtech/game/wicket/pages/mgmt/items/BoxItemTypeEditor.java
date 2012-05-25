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

package com.btxtech.game.wicket.pages.mgmt.items;

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.wicket.pages.mgmt.BoundingBoxEditor;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.tutorial.TaskEditor;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.InventoryArtifactPanel;
import com.btxtech.game.wicket.uiservices.InventoryItemPanel;
import com.btxtech.game.wicket.uiservices.MinutePanel;
import com.btxtech.game.wicket.uiservices.PercentPanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.swing.*;
import java.util.Arrays;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class BoxItemTypeEditor extends MgmtWebPage {
    @SpringBean
    private ItemService itemService;
    @SpringBean
    private RuServiceHelper<DbBoxItemType> ruServiceHelper;

    public BoxItemTypeEditor(DbBoxItemType dbBoxItemType) {
        add(new FeedbackPanel("msgs"));

        final Form<DbBoxItemType> form = new Form<>("boxItemTypeForm", new CompoundPropertyModel<DbBoxItemType>(new RuModel<DbBoxItemType>(dbBoxItemType, DbBoxItemType.class) {
            @Override
            protected RuServiceHelper<DbBoxItemType> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new Button("editBoundingBox") {
            @Override
            public void onSubmit() {
                setResponsePage(new BoundingBoxEditor(form.getModelObject().getId()));
            }
        });
        form.add(new TextField<String>("name"));
        form.add(new TextField<String>("description"));
        form.add(new DropDownChoice<>("terrainType", Arrays.asList(TerrainType.values())));
        form.add(new MinutePanel("ttl"));

        new CrudChildTableHelper<DbBoxItemType, DbBoxItemTypePossibility>("possibilities", null, "createPossibility", false, form, false) {

            @Override
            protected RuServiceHelper<DbBoxItemType> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbBoxItemType getParent() {
                return (DbBoxItemType) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbBoxItemTypePossibility> getCrudChildServiceHelperImpl() {
                return ((DbBoxItemType) form.getDefaultModelObject()).getBoxPossibilityCrud();
            }

            @Override
            protected void extendedPopulateItem(Item<DbBoxItemTypePossibility> dbBoxItemTypePossibilityItem) {
                dbBoxItemTypePossibilityItem.add(new PercentPanel("possibility"));
                dbBoxItemTypePossibilityItem.add(new InventoryItemPanel("dbInventoryItem"));
                dbBoxItemTypePossibilityItem.add(new InventoryArtifactPanel("dbInventoryArtifact"));
                dbBoxItemTypePossibilityItem.add(new TextField<Integer>("razarion"));

            }
        };

        form.add(new FileUploadField("upload", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                if(fileUpload == null) {
                    // Don't know why...
                    return;
                }
                DbBoxItemType boxItemType = form.getModelObject();
                ImageIcon image = new ImageIcon(fileUpload.getBytes());
                boxItemType.setImageHeight(image.getIconHeight());
                boxItemType.setImageWidth(image.getIconWidth());
                boxItemType.getItemTypeImageCrud().deleteAllChildren();
                DbItemTypeImage itemTypeImage = boxItemType.getItemTypeImageCrud().createDbChild();
                itemTypeImage.setContentType(fileUpload.getContentType());
                itemTypeImage.setNumber(1);
                itemTypeImage.setData(fileUpload.getBytes());
            }

            @Override
            public void detach() {
            }
        }));


        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
                setResponsePage(ItemTypeTable.class);
            }
        });
        form.add(new Button("cancel") {
            @Override
            public void onSubmit() {
                setResponsePage(ItemTypeTable.class);
            }
        });
        add(form);
    }
}