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

import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.InventoryService;
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
    public static final String PATH = "/inventoryImg";
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String TYPE_ARTIFACT = "art";
    private static final String TYPE_ITEM = "item";

    @SpringBean
    private InventoryService inventoryService;

    public static Image createArtifactImage(String id, DbInventoryArtifact dbInventoryArtifact) {
        ValueMap valueMap = new ValueMap();
        valueMap.put(TYPE, TYPE_ARTIFACT);
        valueMap.put(ID, dbInventoryArtifact.getId());
        return new Image(id, new ResourceReference(SHARED_IMAGE_RESOURCES), valueMap);
    }

    public static Image createItemImage(String id, DbInventoryItem dbInventoryItem) {
        ValueMap valueMap = new ValueMap();
        valueMap.put(TYPE, TYPE_ITEM);
        valueMap.put(ID, dbInventoryItem.getId());
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

        int id = Utils.parseIntSave(getParameters().getString(ID));
        String type = Utils.parseStringSave(getParameters().getString(TYPE));
        switch (type) {
            case TYPE_ARTIFACT: {
                DbInventoryArtifact dbInventoryArtifact = inventoryService.getArtifactCrud().readDbChild(id);
                if (dbInventoryArtifact.getImageData() != null) {
                    contentType = dbInventoryArtifact.getImageContentType();
                    contentData = dbInventoryArtifact.getImageData();
                }
                break;
            }
            case TYPE_ITEM: {
                DbInventoryItem dbInventoryItem = inventoryService.getItemCrud().readDbChild(id);
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
