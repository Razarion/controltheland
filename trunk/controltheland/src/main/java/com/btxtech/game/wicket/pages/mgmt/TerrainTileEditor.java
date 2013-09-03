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

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.ImageSpriteMapPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.List;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class TerrainTileEditor extends MgmtWebPage {
    @SpringBean
    private TerrainImageService terrainImageService;

    public TerrainTileEditor() {
        Form form = new Form("tileForm");
        add(form);

        new CrudRootTableHelper<DbSurfaceImage>("surfaceImages", "updateSurfaceImages", "createSurfaceImage", false, form, false) {

            @Override
            protected CrudRootServiceHelper<DbSurfaceImage> getCrudRootServiceHelperImpl() {
                return terrainImageService.getDbSurfaceImageCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbSurfaceImage> item) {
                displayId(item);
                // image
                if (item.getModelObject().getImageData() != null && item.getModelObject().getImageData().length > 0) {
                    item.add(new Image("image", new ByteArrayResource("", item.getModelObject().getImageData())));
                } else {
                    item.add(new Image("image", "").setVisible(false));
                }
                // upload
                FileUploadField upload = new FileUploadField("upload", new IModel<List<FileUpload>>() {
                    @Override
                    public List<FileUpload> getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(List<FileUpload> list) {
                        if(list == null) {
                            // Don't know why
                            return;
                        }
                        item.getModelObject().setImageData(list.get(0).getBytes());
                        item.getModelObject().setContentType(list.get(0).getContentType());
                    }

                    @Override
                    public void detach() {
                        //Ignored
                    }
                });
                item.add(upload);
                item.add(new Button("clear") {

                    @Override
                    public void onSubmit() {
                        item.getModelObject().setImageData(null);
                        item.getModelObject().setContentType(null);
                        updateDbChildren(getList());
                    }
                });
                // Background image sprite map
                item.add(new ImageSpriteMapPanel("imageSpriteMap"));
                // Size
                double size = item.getModelObject().getImageData() != null ? item.getModelObject().getImageData().length / 1000.0 : 0;
                item.add(new Label("size", Double.toString(size)));
                // Surface type
                IModel<SurfaceType> surfaceTypeIModel = new IModel<SurfaceType>() {
                    @Override
                    public SurfaceType getObject() {
                        return item.getModelObject().getSurfaceType();
                    }

                    @Override
                    public void setObject(SurfaceType surfaceType) {
                        item.getModelObject().setSurfaceType(surfaceType);
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                };
                item.add(new DropDownChoice<>("surfaceType", surfaceTypeIModel, Arrays.asList(SurfaceType.values())));
                item.add(new TextField("htmlBackgroundColor"));
                // alternating row color
                item.add(new AttributeModifier("class", new Model<>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }
        };

        form.add(new Label("totalSize", new IModel<String>() {

            @Override
            public String getObject() {
                return Double.toString(terrainImageService.getDbTerrainImagesSizeInBytes() / 1000.0);
            }

            @Override
            public void setObject(String s) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));

        new CrudRootTableHelper<DbTerrainImageGroup>("terrainImageGroups", "updateTerrainImageGroups", "createTerrainImageGroup", true, form, false) {
            @Override
            protected CrudRootServiceHelper<DbTerrainImageGroup> getCrudRootServiceHelperImpl() {
                return terrainImageService.getDbTerrainImageGroupCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(Item<DbTerrainImageGroup> dbTerrainImageGroupItem) {
                displayId(dbTerrainImageGroupItem);
                super.extendedPopulateItem(dbTerrainImageGroupItem);
            }

            @Override
            protected void onEditSubmit(DbTerrainImageGroup dbTerrainImageGroup) {
                setResponsePage(new TerrainImageGroupEditor(dbTerrainImageGroup));
            }
        };

        form.add(new Button("activateTerrain") {
            @Override
            public void onSubmit() {
                terrainImageService.activate();
            }
        });
    }

}
