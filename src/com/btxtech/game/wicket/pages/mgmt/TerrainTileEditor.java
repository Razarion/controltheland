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
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
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
    private TerrainImageProvider terrainImageProvider;
    private SurfaceImageProvider surfaceImageProvider;

    public TerrainTileEditor() {
        Form form = new Form("tileForm");
        add(form);

        surfaceImagesTable(form);
        terrainImagesTable(form);
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                terrainService.saveAndActivateTerrainImages(terrainImageProvider.getImages(), surfaceImageProvider.getImages());
            }
        });

    }

    private void surfaceImagesTable(Form form) {
        surfaceImageProvider = new SurfaceImageProvider();
        form.add(new DataView<DbSurfaceImage>("surfaceImages", surfaceImageProvider) {
            protected void populateItem(final Item<DbSurfaceImage> item) {
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
                // Delete
                Button delete = new Button("delete") {
                    @Override
                    public void onSubmit() {
                        surfaceImageProvider.removeImage(item.getModelObject());
                    }
                };
                item.add(delete);
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }
        });
        form.add(new Button("addSurfaceImage") {
            @Override
            public void onSubmit() {
                surfaceImageProvider.createImage();
            }
        });
    }

    private void terrainImagesTable(Form form) {
        terrainImageProvider = new TerrainImageProvider();
        final DataView<DbTerrainImage> tileList = new DataView<DbTerrainImage>("terrainImages", terrainImageProvider) {
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
                        item.getModelObject().setTileWidth((int) Math.ceil(image.getIconWidth() / terrainService.getDbTerrainSettings().getTileWidth()));
                        item.getModelObject().setTileHeight((int) Math.ceil(image.getIconHeight() / terrainService.getDbTerrainSettings().getTileHeight()));
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
                // Delete
                Button delete = new Button("delete") {
                    @Override
                    public void onSubmit() {
                        terrainImageProvider.removeImage(item.getModelObject());
                    }
                };
                item.add(delete);
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }
        };
        form.add(tileList);
        form.add(new Button("addTerrainImage") {
            @Override
            public void onSubmit() {
                terrainImageProvider.createImage();
            }
        });
    }

    class TerrainImageProvider implements IDataProvider<DbTerrainImage> {
        private List<DbTerrainImage> dbTerrainImages;

        TerrainImageProvider() {
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

        public void createImage() {
            dbTerrainImages.add(new DbTerrainImage());
        }

        public void removeImage(DbTerrainImage dbTerrainImage) {
            dbTerrainImages.remove(dbTerrainImage);
        }

        public List<DbTerrainImage> getImages() {
            return dbTerrainImages;
        }
    }

    class SurfaceImageProvider implements IDataProvider<DbSurfaceImage> {
        private List<DbSurfaceImage> dbSurfaceImages;

        SurfaceImageProvider() {
            dbSurfaceImages = terrainService.getDbSurfaceImagesCopy();
        }

        @Override
        public Iterator<DbSurfaceImage> iterator(int first, int count) {
            if (first != 0 && count != dbSurfaceImages.size()) {
                throw new IllegalArgumentException("first: " + first + " count: " + count + " | " + dbSurfaceImages.size());
            }
            return dbSurfaceImages.iterator();
        }

        @Override
        public int size() {
            return dbSurfaceImages.size();
        }

        @Override
        public IModel<DbSurfaceImage> model(DbSurfaceImage dbSurfaceImage) {
            return new Model<DbSurfaceImage>(dbSurfaceImage);
        }

        @Override
        public void detach() {
        }

        public void createImage() {
            dbSurfaceImages.add(new DbSurfaceImage());
        }

        public void removeImage(DbSurfaceImage dbSurfaceImage) {
            dbSurfaceImages.remove(dbSurfaceImage);
        }

        public List<DbSurfaceImage> getImages() {
            return dbSurfaceImages;
        }
    }
}
