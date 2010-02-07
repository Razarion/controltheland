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

import com.btxtech.game.services.terrain.DbTerrainImage;
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
import javax.swing.ImageIcon;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class TerrainTileEditor extends WebPage {
    @SpringBean
    private TerrainService terrainService;
    private byte[] bgImage;
    private String bgImageType;

    public TerrainTileEditor() {
        bgImage = terrainService.getTerrainSetting().getBgImageData();
        bgImageType = terrainService.getTerrainSetting().getBgContentType();
        Form form = new Form("tileForm");
        add(form);

        // Background image
        Image wicketBgImage = new Image("bgImage") {
            protected Resource getImageResource() {
                if (bgImage != null && bgImageType != null) {
                    return new ByteArrayResource(bgImageType, bgImage);
                } else {
                    return null;
                }
            }
        };
        form.add(wicketBgImage);
        form.add(new FileUploadField("bgUpload", new IModel<FileUpload>() {
            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                bgImage = fileUpload.getBytes();
                bgImageType = fileUpload.getContentType();
            }

            @Override
            public void detach() {
                //Ignored
            }
        }));
        form.add(new Label("bgSize", new IModel<Double>() {
            @Override
            public Double getObject() {
                if (bgImage != null) {
                    return bgImage.length / 1000.0;
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
        final DataView<DbTerrainImage> tileList = new DataView<DbTerrainImage>("tiles", new TileProvider()) {
            protected void populateItem(final Item<DbTerrainImage> item) {
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
                        ImageIcon image = new ImageIcon(fileUpload.getBytes());
                        item.getModelObject().setImageData(fileUpload.getBytes());
                        item.getModelObject().setContentType(fileUpload.getContentType());
                        item.getModelObject().setTileWidth((int) Math.ceil(image.getIconWidth() / terrainService.getTerrainSetting().getTileWidth()));
                        item.getModelObject().setTileHeight((int) Math.ceil(image.getIconHeight() / terrainService.getTerrainSetting().getTileHeight()));
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
                terrainService.saveAndActivateTerrainImages(tileProvider.getTerrainImages(), bgImage, bgImageType);
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

    class TileProvider implements IDataProvider<DbTerrainImage> {
        private List<DbTerrainImage> dbTerrainImages;

        TileProvider() {
            dbTerrainImages = terrainService.getDbTerrainImagesCopy();
        }

        @Override
        public Iterator<DbTerrainImage> iterator(int first, int count) {
            if (first != 0 && count != dbTerrainImages.size()) {
                throw new IllegalArgumentException("first: " + first + " count: " + count + " | " + dbTerrainImages.size());
            }
            return dbTerrainImages.iterator();
        }

        @Override
        public int size() {
            return dbTerrainImages.size();
        }

        @Override
        public IModel<DbTerrainImage> model(DbTerrainImage tile) {
            return new Model<DbTerrainImage>(tile);
        }

        @Override
        public void detach() {
        }

        public void createTile() {
            dbTerrainImages.add(new DbTerrainImage());
        }

        public void removeTile(DbTerrainImage dbTerrainImage) {
            dbTerrainImages.remove(dbTerrainImage);
        }

        public List<DbTerrainImage> getTerrainImages() {
            return dbTerrainImages;
        }
    }

}
