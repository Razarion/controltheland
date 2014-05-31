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
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.mgmt.MgmtService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;

/**
 * User: beat Date: 01.06.2011 Time: 10:49:56
 */
public class InventoryImageResource extends AbstractResource {
    @SpringBean
    private GlobalInventoryService globalInventoryService;
    @SpringBean
    private MgmtService mgmtService;

    public static Image createArtifactImage(String id, DbInventoryArtifact dbInventoryArtifact) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(Constants.INVENTORY_TYPE, Constants.INVENTORY_TYPE_ARTIFACT);
        pageParameters.set(Constants.INVENTORY_ID, dbInventoryArtifact.getId());
        return new Image(id, new PackageResourceReference(CmsUtil.MOUNT_INVENTORY_IMAGES), pageParameters);
    }

    public static Image createItemImage(String id, DbInventoryItem dbInventoryItem) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(Constants.INVENTORY_TYPE, Constants.INVENTORY_TYPE_ITEM);
        pageParameters.set(Constants.INVENTORY_ID, dbInventoryItem.getId());
        return new Image(id, new PackageResourceReference(CmsUtil.MOUNT_INVENTORY_IMAGES), pageParameters);
    }

    public InventoryImageResource() {
        // Inject CmsService
        Injector.get().inject(this);
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        try {
            int id = Utils.parseIntSave(attributes.getParameters().get(Constants.INVENTORY_ID).toString());
            String type = Utils.parseStringSave(attributes.getParameters().get(Constants.INVENTORY_TYPE).toString());
            switch (type) {
                case Constants.INVENTORY_TYPE_ARTIFACT: {
                    final DbInventoryArtifact dbInventoryArtifact = globalInventoryService.getArtifactCrud().readDbChild(id);
                    if (dbInventoryArtifact.getImageData() != null) {
                        ResourceResponse response = new ResourceResponse();
                        response.setContentType(dbInventoryArtifact.getImageContentType());
                        response.setWriteCallback(new WriteCallback() {
                            @Override
                            public void writeData(final Attributes attributes) {
                                try {
                                    attributes.getResponse().write(dbInventoryArtifact.getImageData());
                                } catch (Exception e) {
                                    ExceptionHandler.handleException(e, "InventoryImageResource writeData INVENTORY_TYPE_ARTIFACT");
                                }
                            }
                        });
                        return response;
                    }
                    break;
                }
                case Constants.INVENTORY_TYPE_ITEM: {
                    final DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().readDbChild(id);
                    if (dbInventoryItem.getImageData() != null) {
                        ResourceResponse response = new ResourceResponse();
                        response.setContentType(dbInventoryItem.getImageContentType());
                        response.setWriteCallback(new WriteCallback() {
                            @Override
                            public void writeData(final Attributes attributes) {
                                try {
                                    attributes.getResponse().write(dbInventoryItem.getImageData());
                                } catch (Exception e) {
                                    ExceptionHandler.handleException(e, "InventoryImageResource writeData INVENTORY_TYPE_ITEM");
                                }
                            }
                        });
                        return response;
                    }
                    break;
                }
            }
            throw new Exception("Can not deliver inventory image resource: " + attributes.getParameters().toString());
        } catch (Exception e) {
            mgmtService.saveServerDebug(MgmtService.SERVER_DEBUG_CMS, ((ServletWebRequest) attributes.getRequest()).getContainerRequest(), null, e);
            ResourceResponse response = new ResourceResponse();
            response.setError(HttpServletResponse.SC_NOT_FOUND);
            return response;
        }
    }


}
