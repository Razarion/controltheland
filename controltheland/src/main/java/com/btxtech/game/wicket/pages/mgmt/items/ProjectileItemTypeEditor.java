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

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.wicket.pages.mgmt.ItemTypeImageEditor;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.swing.*;

/**
 * User: beat
 * Date: 13.10.2010
 * Time: 12:55:51
 */
public class ProjectileItemTypeEditor extends MgmtWebPage {
    @SpringBean
    private ItemService itemService;
    @SpringBean
    private RuServiceHelper<DbProjectileItemType> ruServiceHelper;

    public ProjectileItemTypeEditor(DbProjectileItemType dbProjectileItemType) {
        add(new FeedbackPanel("msgs"));

        final Form<DbProjectileItemType> form = new Form<DbProjectileItemType>("itemTypeForm", new CompoundPropertyModel<DbProjectileItemType>(new RuModel<DbProjectileItemType>(dbProjectileItemType, DbProjectileItemType.class) {
            @Override
            protected RuServiceHelper<DbProjectileItemType> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new Button("editBoundingBox") {
            @Override
            public void onSubmit() {
                setResponsePage(new ItemTypeImageEditor(form.getModelObject().getId()));
            }
        });

        form.add(new TextField<String>("name"));
        form.add(new TextArea<String>("description"));
        form.add(new TextArea<String>("proDescription"));
        form.add(new TextArea<String>("contraDescription"));
        form.add(new TextField<String>("price"));
        form.add(new TextField<Double>("buildup"));
        form.add(new TextField<Integer>("explosionRadius"));
        form.add(new TextField<Integer>("damage"));
        form.add(new TextField<Integer>("speed"));
        form.add(new TextField<Integer>("range"));
        form.add(new FileUploadField("upload", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                if (fileUpload == null) {
                    // Don't know why...
                    return;
                }
                ImageIcon image = new ImageIcon(fileUpload.getBytes());
                form.getModelObject().setImageHeight(image.getIconHeight());
                form.getModelObject().setImageWidth(image.getIconWidth());
                form.getModelObject().getItemTypeImageCrud().deleteAllChildren();
                DbItemTypeImage itemTypeImage = form.getModelObject().getItemTypeImageCrud().createDbChild();
                itemTypeImage.setContentType(fileUpload.getContentType());
                // TODO itemTypeImage.setNumber(1);
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
