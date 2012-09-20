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

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.TerrainImageService;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 27.04.2010
 * Time: 17:12:33
 */
public class TerrainImageSurfaceTypeEditor extends MgmtWebPage {
    public static final int LINE_WIDTH = 2;
    @SpringBean
    private TerrainImageService terrainImageService;
    @SpringBean
    private RuServiceHelper<DbTerrainImage> serviceHelper;

    public TerrainImageSurfaceTypeEditor(DbTerrainImage dbTerrainImage) {
        // TODO make per planet  tile width and height global
        throw new UnsupportedOperationException();
        /*
        int cellWidth = terrainImageService.getTerrainSettings().getTileWidth() - LINE_WIDTH;
        int cellHeight = terrainImageService.getTerrainSettings().getTileHeight() - LINE_WIDTH;

        final Form<DbTerrainImage> form = new Form<DbTerrainImage>("form", new CompoundPropertyModel<DbTerrainImage>(new RuModel<DbTerrainImage>(dbTerrainImage, DbTerrainImage.class) {
            @Override
            protected RuServiceHelper<DbTerrainImage> getRuServiceHelper() {
                return serviceHelper;
            }
        }));
        add(form);

        String bgImageUrl = ImageHandler.getTerrainImageUrl(dbTerrainImage.getBaseId());
        WebMarkupContainer table = new WebMarkupContainer("table");
        table.add(new SimpleAttributeModifier("style", "border:solid black " + LINE_WIDTH / 2 + "px;background-image: url(\"" + bgImageUrl + "\")"));
        form.add(table);
        RepeatingView row = new RepeatingView("rows");
        table.add(row);
        for (int y = 0; y < dbTerrainImage.getTileHeight(); y++) {
            final int finalY = y;
            WebMarkupContainer rowContainer = new WebMarkupContainer(row.newChildId());
            row.add(rowContainer);
            RepeatingView cell = new RepeatingView("cell");
            rowContainer.add(cell);
            for (int x = 0; x < dbTerrainImage.getTileWidth(); x++) {
                final int finalX = x;
                WebMarkupContainer cellContainer = new WebMarkupContainer(cell.newChildId());
                cellContainer.add(new SimpleAttributeModifier("style", "border:solid black " + LINE_WIDTH / 2 + "px;width:" + cellWidth + "px;height:" + cellHeight + "px"));
                cell.add(cellContainer);
                DropDownChoice dropDownChoice = new DropDownChoice<SurfaceType>("surfaceType", new IModel<SurfaceType>() {
                    @Override
                    public SurfaceType getObject() {
                        return form.getModelObject().getSurfaceType(finalX, finalY);
                    }

                    @Override
                    public void setObject(SurfaceType surfaceType) {
                        form.getModelObject().setSurfaceType(finalX, finalY, surfaceType);
                    }

                    @Override
                    public void detach() {
                        // Ignore
                    }
                }, Arrays.asList(SurfaceType.values()));
                cellContainer.add(dropDownChoice);
                dropDownChoice.add(new SimpleAttributeModifier("style", "width:" + cellWidth + "px"));
            }
        }

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                serviceHelper.updateDbEntity(form.getModelObject());
            }
        });

        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(TerrainTileEditor.class);
            }
        }); */
    }

}
