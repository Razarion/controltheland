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

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.wicket.pages.mgmt.items.ItemTypeTable;
import com.btxtech.game.wicket.uiservices.ListProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
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
    private ItemService itemService;

    public AttackMatrix() {
        add(new FeedbackPanel("msgs"));

        ArrayList<IColumn<DbBaseItemType>> columnList = new ArrayList<IColumn<DbBaseItemType>>();
        columnList.add(new AbstractColumn<DbBaseItemType>(new Model<String>("Attacker")) {

            @Override
            public void populateItem(Item<ICellPopulator<DbBaseItemType>> cellItem, String componentId, IModel<DbBaseItemType> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getName()));
            }
        });

        for (final DbBaseItemType baseItemType : itemService.getDbBaseItemTypes()) {
            columnList.add(new AbstractColumn<DbBaseItemType>(new Model<String>(baseItemType.getName())) {

                @Override
                public void populateItem(Item<ICellPopulator<DbBaseItemType>> cellItem, String componentId, IModel<DbBaseItemType> rowModel) {
                    cellItem.add(new AttackMatrixAssignment(componentId, baseItemType, rowModel.getObject().getDbWeaponType()));
                }
            });
        }

        @SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
        IColumn<DbBaseItemType>[] columnArray = (IColumn<DbBaseItemType>[]) columnList.toArray(new IColumn[columnList.size()]);
        final ListProvider<DbBaseItemType> territoryProvider = new ListProvider<DbBaseItemType>() {
            @Override
            protected List<DbBaseItemType> createList() {
                return new ArrayList<DbBaseItemType>(itemService.getWeaponDbBaseItemTypes());
            }
        };

        Form form = new Form("form") {
            protected void onSubmit() {
                itemService.saveAttackMatrix(territoryProvider.getLastModifiedList());
                setResponsePage(ItemTypeTable.class);
            }
        };
        add(form);

        DataTable<DbBaseItemType> dataTable = new DataTable<DbBaseItemType>("dataTable", columnArray, territoryProvider, Integer.MAX_VALUE);
        dataTable.addTopToolbar(new HeadersToolbar(dataTable, null));
        dataTable.addBottomToolbar(new NoRecordsToolbar(dataTable, new Model<String>("No Weapon items")));
        form.add(dataTable);
    }
}