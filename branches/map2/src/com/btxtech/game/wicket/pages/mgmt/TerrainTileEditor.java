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

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.terrain.Tile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
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
    private HashMap<Tile, Model<TerrainType>> allowedItems = new HashMap<Tile, Model<TerrainType>>();
    private HashMap<Tile, FileUploadField> uploads = new HashMap<Tile, FileUploadField>();

    public TerrainTileEditor() {
        Form form = new Form("tileForm");
        add(form);

        final List<TerrainType> allItemTypes = Arrays.asList(TerrainType.values());


        DataView<Tile> tileList = new DataView<Tile>("tiles", new TileProvider()) {
            protected void populateItem(final Item<Tile> item) {
                // image
                if (item.getModelObject().getImageData() != null && item.getModelObject().getImageData().length > 0) {
                    Image image = new Image("image", new ByteArrayResource("", item.getModelObject().getImageData()));
                    item.add(image);
                    image.add(new AttributeModifier("width", true, new Model<Integer>(Constants.TILE_WIDTH)));
                    image.add(new AttributeModifier("height", true, new Model<Integer>(Constants.TILE_HEIGHT)));
                } else {
                    Image noImage = new Image("image");
                    noImage.setVisible(false);
                    item.add(noImage);
                }
                // upload
                FileUploadField upload = new FileUploadField("upload");
                uploads.put(item.getModelObject(), upload);
                item.add(upload);
                // Size
                double size = item.getModelObject().getImageSize() / 1000.0;
                item.add(new Label("size", Double.toString(size)));
                // Allowed Item
                Model<TerrainType> allowedItemTypeModel = new Model<TerrainType>(item.getModelObject().getTerrainType());
                allowedItems.put(item.getModelObject(), allowedItemTypeModel);
                item.add(new DropDownChoice<TerrainType>("allowedItem", allowedItemTypeModel, allItemTypes));
                // Delete
                Button delete = new Button("delete") {
                    @Override
                    public void onSubmit() {
                        super.onSubmit();
                        terrainService.deleteTile(item.getModelObject());
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
                super.onSubmit();
                for (Tile tile : terrainService.getTiles()) {
                    Model<TerrainType> itemType = allowedItems.get(tile);
                    if (itemType != null) {
                        tile.setTerrainType(itemType.getObject());
                    }
                    FileUploadField upload = uploads.get(tile);
                    if (upload != null && upload.getFileUpload() != null && upload.getFileUpload().getBytes() != null && upload.getFileUpload().getBytes().length > 0) {
                        tile.setImageData(upload.getFileUpload().getBytes());
                    }

                    terrainService.saveTile(tile);
                }
            }
        });

        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                super.onSubmit();
                terrainService.createTile();
            }
        });


    }

    class TileProvider implements IDataProvider<Tile> {
        @Override
        public Iterator<Tile> iterator(int first, int count) {
            if (first != 0 && count != terrainService.getTiles().size()) {
                throw new IllegalArgumentException("first: " + first + " count: " + count + " | " + terrainService.getTiles().size());
            }
            return terrainService.getTiles().iterator();
        }

        @Override
        public int size() {
            return terrainService.getTiles().size();
        }

        @Override
        public IModel<Tile> model(Tile tile) {
            return new Model<Tile>(tile);
        }

        @Override
        public void detach() {
        }
    }

}
