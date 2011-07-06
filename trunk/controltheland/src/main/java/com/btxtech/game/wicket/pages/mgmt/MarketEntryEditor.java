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

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.market.DbMarketEntry;
import com.btxtech.game.services.market.DbMarketCategory;
import com.btxtech.game.services.market.DbMarketFunction;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.ItemTypePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Set;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class MarketEntryEditor extends MgmtWebPage {
    @SpringBean
    private ServerMarketService serverMarketService;
    @SpringBean
    private ItemService itemService;

    public MarketEntryEditor() {
        showCategoryList();
        showFunctionList();
        showMarketList();
    }

    private void showCategoryList() {
        Form form = new Form("categoryForm");
        add(form);

        new CrudRootTableHelper<DbMarketCategory>("categories", "save", "add", false, form, false) {
            @Override
            protected CrudRootServiceHelper<DbMarketCategory> getCrudRootServiceHelperImpl() {
                return serverMarketService.getCrudMarketCategoryService();
            }
        };
    }

    private void showFunctionList() {
        Form form = new Form("functionForm");
        add(form);

        new CrudRootTableHelper<DbMarketFunction>("functions", "save", "add", false, form, false) {
            @Override
            protected CrudRootServiceHelper<DbMarketFunction> getCrudRootServiceHelperImpl() {
                return serverMarketService.getCrudMarketFunctionService();
            }
        };
    }

    private void showMarketList() {
        Form form = new Form("itemTypeAccessForm");
        add(form);

        new CrudRootTableHelper<DbMarketEntry>("itemTypeAccessEntries", "save", "add", false, form, false) {
            @Override
            protected CrudRootServiceHelper<DbMarketEntry> getCrudRootServiceHelperImpl() {
                return serverMarketService.getCrudMarketEntryService();
            }

            @Override
            protected void extendedPopulateItem(Item<DbMarketEntry> item) {
                // Id
                item.add(new Label("id"));
                // image
                DbItemType dbItemType = item.getModelObject().getItemType();
                if (dbItemType != null) {
                    Image image = new Image("image", new ByteArrayResource("", getImage(dbItemType)));
                    item.add(image);
                } else {
                    item.add(new Label("image", "No image"));
                }
                item.add(new ItemTypePanel("itemType"));
                item.add(new TextField("price"));
                item.add(new CheckBox("alwaysAllowed"));
                item.add(new DropDownChoice<DbMarketCategory>("marketCategory", serverMarketService.getMarketCategories()));
                item.add(new DropDownChoice<DbMarketFunction>("marketFunction", serverMarketService.getMarketFunctions()));
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }
        };
    }

    private byte[] getImage(DbItemType itemType) {
        Set<DbItemTypeImage> dbItemTypeImages = itemType.getItemTypeImages();
        if (dbItemTypeImages == null || dbItemTypeImages.isEmpty()) {
            return null;
        }
        return dbItemTypeImages.iterator().next().getData();
    }
}