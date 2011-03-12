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

import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.btxtech.game.jsre.mapview.territory.TerritoryEditModel;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.DbTerritory;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import com.btxtech.game.wicket.uiservices.CrudTableListProvider;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 23.05.2010
 * Time: 16:28:44
 */
public class TerritoryEditor extends MgmtWebPage {
    @SpringBean
    private TerritoryService territoryService;
    @SpringBean
    private ItemService itemService;
    @SpringBean
    private TerrainService terrainService;

    public TerritoryEditor() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("territoryForm");
        add(form);

        new CrudTableHelper<DbTerritory>("territories", "save", "create", true, form, false) {

            @Override
            protected CrudServiceHelper<DbTerritory> getCrudServiceHelper() {
                return territoryService.getDbTerritoryCrudServiceHelper();
            }

            @Override
            protected void onEditSubmit(DbTerritory dbTerritory) {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(TerrainEditorAsync.TERRAIN_SETTING_ID, terrainService.getDbTerrainSetting4RealGame().getId().toString());
                pageParameters.put(TerritoryEditModel.TERRITORY_TO_EDIT, dbTerritory.getName());
                setResponsePage(TerritoryDesigner.class, pageParameters);
            }
        };

        setupMatrix(form);

        form.add(new Button("activate") {
            @Override
            public void onSubmit() {
                territoryService.activate();
            }
        });

    }

    private void setupMatrix(Form form) {
        ArrayList<IColumn<DbTerritory>> columnList = new ArrayList<IColumn<DbTerritory>>();
        columnList.add(new AbstractColumn<DbTerritory>(new Model<String>("")) {

            @Override
            public void populateItem(Item<ICellPopulator<DbTerritory>> cellItem, String componentId, IModel<DbTerritory> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getName()));
            }
        });

        for (DbBaseItemType baseItemType : itemService.getDbBaseItemTypes()) {
            final int baseItemTypeId = baseItemType.getId();
            columnList.add(new AbstractColumn<DbTerritory>(new Model<String>(baseItemType.getName())) {

                @Override
                public void populateItem(Item<ICellPopulator<DbTerritory>> cellItem, String componentId, IModel<DbTerritory> rowModel) {
                    cellItem.add(new TerritoryAssignment(componentId, baseItemTypeId, rowModel));
                }
            });
        }

        @SuppressWarnings("unchecked")
        IColumn<DbTerritory>[] columnArray = (IColumn<DbTerritory>[]) columnList.toArray(new IColumn[columnList.size()]);
        final CrudTableListProvider<DbTerritory> territoryProvider = new CrudTableListProvider<DbTerritory>() {
            @Override
            protected List<DbTerritory> createList() {
                return (List<DbTerritory>) territoryService.getDbTerritoryCrudServiceHelper().readDbChildren();
            }
        };

        DataTable<DbTerritory> dataTable = new DataTable<DbTerritory>("dataTable", columnArray, territoryProvider, Integer.MAX_VALUE);
        dataTable.addTopToolbar(new HeadersToolbar(dataTable, null));
        dataTable.addBottomToolbar(new NoRecordsToolbar(dataTable, new Model<String>("No Territories")));
        form.add(dataTable);

        form.add(new Button("saveMatrix") {
            @Override
            public void onSubmit() {
                territoryService.getDbTerritoryCrudServiceHelper().updateDbChildren(territoryProvider.getLastModifiedList());
            }
        });

    }
}
