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

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.ImageIcon;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class ResourceItemTypeEditor extends WebPage {
    @SpringBean
    private ItemService itemService;


    public ResourceItemTypeEditor(final DbResourceItemType dbResourceItemType) {
        FeedbackPanel feedbackPanel = new FeedbackPanel("msgs");
        add(feedbackPanel);

        Form<DbResourceItemType> form = new Form<DbResourceItemType>("itemTypeForm", new CompoundPropertyModel<DbResourceItemType>(dbResourceItemType));

        form.add(new TextField<String>("name"));
        form.add(new TextField<String>("description"));
        form.add(new TextField<String>("amount"));
        form.add(new DropDownChoice<TerrainType>("terrainType", Arrays.asList(TerrainType.values())));        
        form.add(new FileUploadField("upload", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                ImageIcon image = new ImageIcon(fileUpload.getBytes());
                dbResourceItemType.setHeight(image.getIconHeight());
                dbResourceItemType.setWidth(image.getIconWidth());
                DbItemTypeImage itemTypeImage = new DbItemTypeImage();
                itemTypeImage.setItemType(dbResourceItemType);
                itemTypeImage.setContentType(fileUpload.getContentType());
                itemTypeImage.setNumber(1);
                itemTypeImage.setData(fileUpload.getBytes());
                HashSet<DbItemTypeImage> dbItemTypeImages = new HashSet<DbItemTypeImage>();
                dbItemTypeImages.add(itemTypeImage);
                dbResourceItemType.setItemTypeImages(dbItemTypeImages);
            }

            @Override
            public void detach() {
            }
        }));


        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                itemService.saveDbItemType(dbResourceItemType);
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