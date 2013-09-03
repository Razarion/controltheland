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

import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.wicket.pages.mgmt.items.ItemTypeTable;
import com.btxtech.game.wicket.uiservices.ListProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.html.basic.Label;
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
public class AttackMatrix extends MgmtWebPage {
    @SpringBean
    private ServerItemTypeService serverItemTypeService;

    public AttackMatrix() {
        add(new FeedbackPanel("msgs"));

        ArrayList<IColumn<DbBaseItemType, DbBaseItemType>> columnList = new ArrayList<>();
        columnList.add(new AbstractColumn<DbBaseItemType, DbBaseItemType>(new Model<>("Attacker")) {

            @Override
            public void populateItem(Item<ICellPopulator<DbBaseItemType>> cellItem, String componentId, IModel<DbBaseItemType> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getName()));
            }
        });

        for (final DbBaseItemType baseItemType : serverItemTypeService.getDbBaseItemTypes()) {
            columnList.add(new AbstractColumn<DbBaseItemType, DbBaseItemType>(new Model<>(baseItemType.getName())) {

                @Override
                public void populateItem(Item<ICellPopulator<DbBaseItemType>> cellItem, String componentId, IModel<DbBaseItemType> rowModel) {
                    cellItem.add(new AttackMatrixAssignment(componentId, baseItemType, rowModel.getObject().getDbWeaponType()));
                }
            });
        }

        final ListProvider<DbBaseItemType> weaponProvider = new ListProvider<DbBaseItemType>() {
            @Override
            protected List<DbBaseItemType> createList() {
                return new ArrayList<>(serverItemTypeService.getWeaponDbBaseItemTypes());
            }
        };

        Form form = new Form("form") {
            protected void onSubmit() {
                serverItemTypeService.saveAttackMatrix(weaponProvider.getLastModifiedList());
                setResponsePage(ItemTypeTable.class);
            }
        };
        add(form);

        DataTable<DbBaseItemType, DbBaseItemType> dataTable = new DataTable<>("dataTable", columnList, weaponProvider, Integer.MAX_VALUE);
        dataTable.addTopToolbar(new HeadersToolbar<>(dataTable, null));
        dataTable.addBottomToolbar(new NoRecordsToolbar(dataTable, new Model<>("No Weapon items")));
        form.add(dataTable);
    }
}