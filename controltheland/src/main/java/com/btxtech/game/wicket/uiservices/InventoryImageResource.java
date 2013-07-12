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
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat Date: 01.06.2011 Time: 10:49:56
 */
public class InventoryImageResource extends DynamicImageResource {
    public static final String SHARED_IMAGE_RESOURCES = "inventoryImage";

    @SpringBean
    private GlobalInventoryService globalInventoryService;

    public static Image createArtifactImage(String id, DbInventoryArtifact dbInventoryArtifact) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(Constants.INVENTORY_TYPE, Constants.INVENTORY_TYPE_ITEM);
        pageParameters.set(Constants.INVENTORY_ID, dbInventoryArtifact.getId());
        return new Image(id, new PackageResourceReference(SHARED_IMAGE_RESOURCES), pageParameters);
    }

    public static Image createItemImage(String id, DbInventoryItem dbInventoryItem) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(Constants.INVENTORY_TYPE, Constants.INVENTORY_TYPE_ITEM);
        pageParameters.set(Constants.INVENTORY_ID, dbInventoryItem.getId());
        return new Image(id, new PackageResourceReference(SHARED_IMAGE_RESOURCES), pageParameters);
    }

    public InventoryImageResource() {
        // Inject CmsService
        Injector.get().inject(this);
    }


    @Override
    protected byte[] getImageData(Attributes attributes) {
        String contentType = null;
        byte[] contentData = null;

        int id = Utils.parseIntSave(attributes.getParameters().get(Constants.INVENTORY_ID).toString());
        String type = Utils.parseStringSave(attributes.getParameters().get(Constants.INVENTORY_TYPE).toString());
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
            setFormat(contentType);
            return contentData;
        } else {
            return new byte[]{};
        }
    }
}
