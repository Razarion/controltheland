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
package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.value.ValueMap;

/**
 * User: beat Date: 01.06.2011 Time: 10:49:56
 */
public class InventoryImageResource extends WebResource {
    public static final String SHARED_IMAGE_RESOURCES = "inventoryImage";

    @SpringBean
    private GlobalInventoryService globalInventoryService;

    public static Image createArtifactImage(String id, DbInventoryArtifact dbInventoryArtifact) {
        ValueMap valueMap = new ValueMap();
        valueMap.put(Constants.INVENTORY_TYPE, Constants.INVENTORY_TYPE_ARTIFACT);
        valueMap.put(Constants.INVENTORY_ID, dbInventoryArtifact.getId());
        return new Image(id, new ResourceReference(SHARED_IMAGE_RESOURCES), valueMap);
    }

    public static Image createItemImage(String id, DbInventoryItem dbInventoryItem) {
        ValueMap valueMap = new ValueMap();
        valueMap.put(Constants.INVENTORY_TYPE, Constants.INVENTORY_TYPE_ITEM);
        valueMap.put(Constants.INVENTORY_ID, dbInventoryItem.getId());
        return new Image(id, new ResourceReference(SHARED_IMAGE_RESOURCES), valueMap);
    }

    public InventoryImageResource() {
        // Inject CmsService
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    public IResourceStream getResourceStream() {
        String contentType = null;
        byte[] contentData = null;

        int id = Utils.parseIntSave(getParameters().getString(Constants.INVENTORY_ID));
        String type = Utils.parseStringSave(getParameters().getString(Constants.INVENTORY_TYPE));
        switch (type) {
            case Constants.INVENTORY_TYPE_ARTIFACT: {
                DbInventoryArtifact dbInventoryArtifact = globalInventoryService.getArtifactCrud().readDbChild(id);
                if (dbInventoryArtifact.getImageData() != null) {
                    contentType = dbInventoryArtifact.getImageContentType();
                    contentData = dbInventoryArtifact.getImageData();
                }
                break;
            }
            case Constants.INVENTORY_TYPE_ITEM: {
                DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().readDbChild(id);
                if (dbInventoryItem.getImageData() != null) {
                    contentType = dbInventoryItem.getImageContentType();
                    contentData = dbInventoryItem.getImageData();
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Type:" + type + " id: " + id);
        }
        if (contentType != null && contentData != null) {
            return new ByteArrayResource(contentType, contentData).getResourceStream();
        } else {
            return new ByteArrayResource("", new byte[]{}).getResourceStream();
        }
    }
}
