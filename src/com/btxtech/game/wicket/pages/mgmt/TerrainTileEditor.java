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

import com.btxtech.game.services.terrain.TerrainImage;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class TerrainTileEditor extends WebPage {
    @SpringBean
    private TerrainService terrainService;
    private TerrainImage terrainBackgroundImage;

    public TerrainTileEditor() {
        terrainBackgroundImage = terrainService.getTerrainSetting().getTerrainBackground();
        Form form = new Form("tileForm");
        add(form);

        // Background image
        Image bgImage = new Image("bgImage") {
            protected Resource getImageResource() {
                if (terrainBackgroundImage != null) {
                    return new ByteArrayResource(terrainBackgroundImage.getContentType(), terrainBackgroundImage.getImageData());
                } else {
                    return null;
                }
            }
        };
        form.add(bgImage);
        form.add(new FileUploadField("bgUpload", new IModel<FileUpload>() {
            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                terrainBackgroundImage = new TerrainImage();
                terrainBackgroundImage.setImageData(fileUpload.getBytes());
                terrainBackgroundImage.setContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
                //Ignored
            }
        }));
        form.add(new Label("bgSize", new IModel<Double>() {
            @Override
            public Double getObject() {
                TerrainImage terrainImage = terrainService.getTerrainSetting().getTerrainBackground();
                if (terrainImage != null) {
                    return terrainImage.getImageData().length / 1000.0;
                } else {
                    return 0.0;
                }
            }

            @Override
            public void setObject(Double aDouble) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));

        // Image table
        final DataView<TerrainImage> tileList = new DataView<TerrainImage>("tiles", new TileProvider()) {
            protected void populateItem(final Item<TerrainImage> item) {
                // image
                if (item.getModelObject().getImageData() != null && item.getModelObject().getImageData().length > 0) {
                    Image image = new Image("image", new ByteArrayResource("", item.getModelObject().getImageData()));
                    item.add(image);
                } else {
                    Image noImage = new Image("image");
                    noImage.setVisible(false);
                    item.add(noImage);
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
                // Allowed Item
                // Delete
                Button delete = new Button("delete") {
                    @Override
                    public void onSubmit() {
                        TileProvider tileProvider = (TileProvider) getDataProvider();
                        tileProvider.removeTile(item.getModelObject());
                    }
                };
                item.add(delete);
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }
        };
        form.add(tileList);
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                TileProvider tileProvider = (TileProvider) tileList.getDataProvider();
                terrainService.saveAndActivateTerrainImages(tileProvider.getTerrainImages(), terrainBackgroundImage);
            }
        });

        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                TileProvider tileProvider = (TileProvider) tileList.getDataProvider();
                tileProvider.createTile();
            }
        });


    }

    class TileProvider implements IDataProvider<TerrainImage> {
        private List<TerrainImage> terrainImages;

        TileProvider() {
            terrainImages = terrainService.getTerrainImagesCopy();
        }

        @Override
        public Iterator<TerrainImage> iterator(int first, int count) {
            if (first != 0 && count != terrainImages.size()) {
                throw new IllegalArgumentException("first: " + first + " count: " + count + " | " + terrainImages.size());
            }
            return terrainImages.iterator();
        }

        @Override
        public int size() {
            return terrainImages.size();
        }

        @Override
        public IModel<TerrainImage> model(TerrainImage tile) {
            return new Model<TerrainImage>(tile);
        }

        @Override
        public void detach() {
        }

        public void createTile() {
            terrainImages.add(new TerrainImage());
        }

        public void removeTile(TerrainImage terrainImage) {
            terrainImages.remove(terrainImage);
        }

        public List<TerrainImage> getTerrainImages() {
            return terrainImages;
        }
    }

}
