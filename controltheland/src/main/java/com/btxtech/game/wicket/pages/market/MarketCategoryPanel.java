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

package com.btxtech.game.wicket.pages.market;

import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.market.MarketCategory;
import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.market.MarketFunction;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.utg.UserGuidanceService;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 04.04.2010
 * Time: 16:03:39
 */
public class MarketCategoryPanel extends Panel {
    @SpringBean
    private ServerMarketService serverMarketService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    private MarketCategory marketCategory;

    public MarketCategoryPanel(String id, MarketCategory marketCategory) {
        super(id);
        this.marketCategory = marketCategory;

        add(new Label("category", marketCategory.getName()));

        Form form = new Form("marketForm");
        add(form);

        final DataView<MarketEntry> entries = new DataView<MarketEntry>("itemTypeAccessEntries", new ItemTypeAccessEntryProvider()) {
            protected void populateItem(final Item<MarketEntry> item) {
                DbBaseItemType itemType = (DbBaseItemType) item.getModelObject().getItemType();
                // Name
                item.add(new Label("name", itemType.getName()));
                // Function
                MarketFunction marketFunction = item.getModelObject().getMarketFunction();
                if (marketFunction != null) {
                    item.add(new Label("function", marketFunction.getName()));
                } else {
                    item.add(new Label("function", ""));
                }
                // image
                item.add(new Image("image", new ByteArrayResource("", getImage(itemType))));
                // Description
                item.add(new Label("description", itemType.getDescription()));
                // Pro
                item.add(new Label("pro", itemType.getProDescription()));
                // contra
                item.add(new Label("contra", itemType.getContraDescription()));
                // XP
                item.add(new Label("price", Integer.toString(item.getModelObject().getPrice())));
                // Buy
                Button button = new Button("buy") {

                    @Override
                    public void onSubmit() {
                        serverMarketService.buy(item.getModelObject());
                    }
                };
                UserItemTypeAccess userItemTypeAccess = serverMarketService.getUserItemTypeAccess();
                boolean alreadyBought = userItemTypeAccess.contains(item.getModelObject());
                if (alreadyBought) {
                    if (!userGuidanceService.isBaseItemTypeAllowedInLevel(itemType)) {
                        button.setVisible(false);
                        item.add(new Label("buyInfo", "Not allowed in your level"));
                    } else {
                        button.setVisible(false);
                        item.add(new Label("buyInfo", "Already bought"));
                    }
                } else {
                    if (!userGuidanceService.isBaseItemTypeAllowedInLevel(itemType)) {
                        button.setVisible(false);
                        item.add(new Label("buyInfo", "Not allowed in your level"));
                    } else if (item.getModelObject().getPrice() > userItemTypeAccess.getXp()) {
                        button.setVisible(false);
                        item.add(new Label("buyInfo", "Not enough XP"));
                    } else {
                        button.setVisible(true);
                        Label label = new Label("buyInfo", "");
                        label.setVisible(false);
                        item.add(label);
                    }
                }
                item.add(button);
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }
        };
        form.add(entries);
    }

    private byte[] getImage(DbItemType itemType) {
        Set<DbItemTypeImage> dbItemTypeImages = itemType.getItemTypeImages();
        if (dbItemTypeImages == null || dbItemTypeImages.isEmpty()) {
            return null;
        }
        return dbItemTypeImages.iterator().next().getData();
    }

    class ItemTypeAccessEntryProvider implements IDataProvider<MarketEntry> {
        private List<MarketEntry> marketEntries;

        private void setupMarketEntries() {
            if (marketEntries == null) {
                marketEntries = serverMarketService.getMarketEntries(marketCategory);
            }
        }

        @Override
        public Iterator<MarketEntry> iterator(int first, int count) {
            setupMarketEntries();
            return marketEntries.subList(first, first + count).iterator();
        }

        @Override
        public int size() {
            setupMarketEntries();
            return marketEntries.size();
        }

        @Override
        public IModel<MarketEntry> model(MarketEntry marketEntry) {
            return new Model<MarketEntry>(marketEntry);
        }

        @Override
        public void detach() {
            marketEntries = null;
        }
    }

}
