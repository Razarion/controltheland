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
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.Arrays;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 27.04.2010
 * Time: 17:12:33
 */
public class TerrainImageSurfaceTypeEditor extends WebPage {
    public static final int LINE_WIDTH = 2;
    private DbTerrainImage dbTerrainImage;
    @SpringBean
    private TerrainService terrainService;

    public TerrainImageSurfaceTypeEditor(final TerrainTileEditor terrainImage, final DbTerrainImage dbTerrainImage) {
        this.dbTerrainImage = dbTerrainImage;
        int cellWidth = terrainService.getTerrainSettings().getTileWidth() - LINE_WIDTH;
        int cellHeight = terrainService.getTerrainSettings().getTileHeight() - LINE_WIDTH;

        Form form = new Form("form");
        add(form);

        String bgImageUrl = ImageHandler.getTerrainImageUrl(dbTerrainImage.getId());
        WebMarkupContainer table = new WebMarkupContainer("table");
        table.add(new SimpleAttributeModifier("style", "border:solid black " + LINE_WIDTH / 2 + "px;background-image: url(\"" + bgImageUrl + "\")"));
        form.add(table);
        RepeatingView row = new RepeatingView("rows");
        table.add(row);
        for (int y = 0; y < dbTerrainImage.getTileHeight(); y++) {
            WebMarkupContainer rowContainer = new WebMarkupContainer(row.newChildId());
            row.add(rowContainer);
            RepeatingView cell = new RepeatingView("cell");
            rowContainer.add(cell);
            for (int x = 0; x < dbTerrainImage.getTileWidth(); x++) {
                WebMarkupContainer cellContainer = new WebMarkupContainer(cell.newChildId());
                cellContainer.add(new SimpleAttributeModifier("style", "border:solid black " + LINE_WIDTH / 2 + "px;width:" + cellWidth + "px;height:" + cellHeight + "px"));
                cell.add(cellContainer);
                DropDownChoice dropDownChoice = new DropDownChoice<SurfaceType>("surfaceType", new ComboSurfaceModel(x, y), Arrays.asList(SurfaceType.values()));
                cellContainer.add(dropDownChoice);
                dropDownChoice.add(new SimpleAttributeModifier("style", "width:" + cellWidth + "px"));
            }
        }
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(terrainImage);
            }
        });
    }

    class ComboSurfaceModel implements IModel<SurfaceType> {
        private int x;
        private int y;

        ComboSurfaceModel(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public SurfaceType getObject() {
            return dbTerrainImage.getSurfaceType(x, y);
        }

        @Override
        public void setObject(SurfaceType surfaceType) {
            dbTerrainImage.setSurfaceType(x, y, surfaceType);
        }

        @Override
        public void detach() {
            // Ignore
        }
    }
}
