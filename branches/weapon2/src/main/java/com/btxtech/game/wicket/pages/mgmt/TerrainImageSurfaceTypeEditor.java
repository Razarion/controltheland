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

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

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
        int cellWidth = Constants.TERRAIN_TILE_WIDTH - LINE_WIDTH;
        int cellHeight = Constants.TERRAIN_TILE_HEIGHT - LINE_WIDTH;

        final Form<DbTerrainImage> form = new Form<>("form", new CompoundPropertyModel<DbTerrainImage>(new RuModel<DbTerrainImage>(dbTerrainImage, DbTerrainImage.class) {
            @Override
            protected RuServiceHelper<DbTerrainImage> getRuServiceHelper() {
                return serviceHelper;
            }
        }));
        add(form);

        String bgImageUrl = ImageHandler.getTerrainImageUrl(dbTerrainImage.getId());
        WebMarkupContainer table = new WebMarkupContainer("table");
        table.add(new AttributeModifier("style", "border:solid black " + LINE_WIDTH / 2 + "px;background-image: url(\"" + bgImageUrl + "\")"));
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
                cellContainer.add(new AttributeModifier("style", "border:solid black " + LINE_WIDTH / 2 + "px;width:" + cellWidth + "px;height:" + cellHeight + "px"));
                cell.add(cellContainer);
                DropDownChoice dropDownChoice = new DropDownChoice<>("surfaceType", new IModel<SurfaceType>() {
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
                dropDownChoice.add(new AttributeModifier("style", "width:" + cellWidth + "px"));
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
        });
    }

}
