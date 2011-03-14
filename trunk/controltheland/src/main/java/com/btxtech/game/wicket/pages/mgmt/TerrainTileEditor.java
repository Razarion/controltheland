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
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.swing.*;
import java.util.Arrays;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class TerrainTileEditor extends MgmtWebPage {
    @SpringBean
    private TerrainService terrainService;

    public TerrainTileEditor() {
        Form form = new Form("tileForm");
        add(form);

        new CrudRootTableHelper<DbSurfaceImage>("surfaceImages", "updateSurfaceImages", "createSurfaceImage", false, form, false) {

            @Override
            protected CrudRootServiceHelper<DbSurfaceImage> _getCrudRootServiceHelperImpl() {
                return terrainService.getDbSurfaceImageCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbSurfaceImage> item) {
                // image
                if (item.getModelObject().getImageData() != null && item.getModelObject().getImageData().length > 0) {
                    item.add(new Image("image", new ByteArrayResource("", item.getModelObject().getImageData())));
                } else {
                    item.add(new Image("image").setVisible(false));
                }
                // upload
                FileUploadField upload = new FileUploadField("upload", new IModel<FileUpload>() {
                    @Override
                    public FileUpload getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(FileUpload fileUpload) {
                        item.getModelObject().setImageData(fileUpload.getBytes());
                        item.getModelObject().setContentType(fileUpload.getContentType());
                    }

                    @Override
                    public void detach() {
                        //Ignored
                    }
                });
                item.add(upload);
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
                item.add(new DropDownChoice<SurfaceType>("surfaceType", surfaceTypeIModel, Arrays.asList(SurfaceType.values())));
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }
        };

        form.add(new Label("totalSize", new IModel<String>() {

            @Override
            public String getObject() {
                return Double.toString(terrainService.getDbTerrainImagesBitSize() / 1000.0);
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

        new CrudRootTableHelper<DbTerrainImage>("terrainImages", "updateTerrainImages", "createTerrainImage", true, form, false) {
            @Override
            protected CrudRootServiceHelper<DbTerrainImage> _getCrudRootServiceHelperImpl() {
                return terrainService.getDbTerrainImageCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbTerrainImage> item) {
                // image
                if (item.getModelObject().getImageData() != null && item.getModelObject().getImageData().length > 0) {
                    item.add(new Image("image", new ByteArrayResource("", item.getModelObject().getImageData())));
                } else {
                    item.add(new Image("image").setVisible(false));
                }
                // upload
                FileUploadField upload = new FileUploadField("upload", new IModel<FileUpload>() {
                    @Override
                    public FileUpload getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(FileUpload fileUpload) {
                        ImageIcon image = new ImageIcon(fileUpload.getBytes());
                        item.getModelObject().setImageData(fileUpload.getBytes());
                        item.getModelObject().setContentType(fileUpload.getContentType());
                        item.getModelObject().setTiles((int) Math.ceil(image.getIconWidth() / terrainService.getTerrainSettings().getTileWidth()),
                                (int) Math.ceil(image.getIconHeight() / terrainService.getTerrainSettings().getTileHeight()));
                    }

                    @Override
                    public void detach() {
                        //Ignored
                    }
                });
                item.add(upload);
                // Size
                double size = item.getModelObject().getImageData() != null ? item.getModelObject().getImageData().length / 1000.0 : 0;
                item.add(new Label("size", Double.toString(size)));
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }

            @Override
            protected void onEditSubmit(DbTerrainImage dbTerrainImage) {
                setResponsePage(new TerrainImageSurfaceTypeEditor(dbTerrainImage));
            }
        };

        form.add(new Button("activateTerrain") {
            @Override
            public void onSubmit() {
                terrainService.activateTerrain();
            }
        });
    }

}
