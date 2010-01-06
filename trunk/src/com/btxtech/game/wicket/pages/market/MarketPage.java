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

import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.itemTypeAccess.ItemTypeAccessEntry;
import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import com.btxtech.game.services.itemTypeAccess.impl.UserItemTypeAccess;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import java.util.Iterator;
import java.util.Set;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
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
 * Date: Sep 13, 2009
 * Time: 7:27:03 PM
 */
public class MarketPage extends BasePage {
    @SpringBean
    private ServerItemTypeAccessService serverItemTypeAccessService;

    public MarketPage() {
        add(new Label("xp", Integer.toString(serverItemTypeAccessService.getXp())));

        Form form = new Form("marketForm");
        add(form);

        final DataView<ItemTypeAccessEntry> entries = new DataView<ItemTypeAccessEntry>("itemTypeAccessEntries", new ItemTypeAccessEntryProvider()) {
            protected void populateItem(final Item<ItemTypeAccessEntry> item) {
                DbItemType itemType = item.getModelObject().getItemType();
                // Name
                item.add(new Label("name", itemType.getName()));
                // image
                Image image = new Image("image", new ByteArrayResource("", getImage(itemType)));
                item.add(image);
                // Description
                item.add(new Label("description", itemType.getDescription()));
                // XP
                item.add(new Label("price", Integer.toString(item.getModelObject().getPrice())));
                // Buy
                Button button = new Button("buy") {

                    @Override
                    public void onSubmit() {
                        serverItemTypeAccessService.buy(item.getModelObject());
                    }
                };
                UserItemTypeAccess userItemTypeAccess = serverItemTypeAccessService.getUserItemTypeAccess();
                boolean alreadyBought = userItemTypeAccess.contains(item.getModelObject());
                if(alreadyBought) {
                    button.setVisible(false);
                    item.add(new Label("buyInfo", "Already bought"));
                } else {
                   if(item.getModelObject().getPrice() > userItemTypeAccess.getXp()) {
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

    class ItemTypeAccessEntryProvider implements IDataProvider<ItemTypeAccessEntry> {
        @Override
        public Iterator<ItemTypeAccessEntry> iterator(int first, int count) {
            if (first != 0 && count != serverItemTypeAccessService.getItemTypeAccessEntries().size()) {
                throw new IllegalArgumentException("first: " + first + " count: " + count + " | " + serverItemTypeAccessService.getItemTypeAccessEntries().size());
            }
            return serverItemTypeAccessService.getItemTypeAccessEntries().iterator();
        }

        @Override
        public int size() {
            return serverItemTypeAccessService.getItemTypeAccessEntries().size();
        }

        @Override
        public IModel<ItemTypeAccessEntry> model(ItemTypeAccessEntry itemTypeAccessEntry) {
            return new Model<ItemTypeAccessEntry>(itemTypeAccessEntry);
        }

        @Override
        public void detach() {
        }
    }
}
