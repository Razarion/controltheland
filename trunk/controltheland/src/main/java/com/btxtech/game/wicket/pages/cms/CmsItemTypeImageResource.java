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
package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
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
public class CmsItemTypeImageResource extends AbstractResource {
    private static final String ID = "id";
    @SpringBean
    private ServerItemTypeService serverItemTypeService;
    @SpringBean
    private MgmtService mgmtService;

    public static Image createImage(String id, DbItemType dbItemType) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(ID, dbItemType.getId());
        return new Image(id, new PackageResourceReference(CmsUtil.MOUNT_SINGLE_ITEM_TYPE_IMAGES), pageParameters);
    }

    public CmsItemTypeImageResource() {
        // Inject CmsService
        Injector.get().inject(this);
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        try {
            int itmTypeId = Utils.parseIntSave(attributes.getParameters().get(ID).toString());
            final DbItemTypeImage dbItemTypeImage = serverItemTypeService.getCmsDbItemTypeImage(itmTypeId);
            ResourceResponse response = new ResourceResponse();
            response.setContentType(dbItemTypeImage.getContentType());
            response.setWriteCallback(new WriteCallback() {
                @Override
                public void writeData(final Attributes attributes) {
                    try {
                        attributes.getResponse().write(dbItemTypeImage.getData());
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e, "CmsItemTypeImageResource writeData");
                    }
                }
            });
            return response;
        } catch (Exception e) {
            mgmtService.saveServerDebug(MgmtService.SERVER_DEBUG_CMS, ((ServletWebRequest) attributes.getRequest()).getContainerRequest(), null, e);
            ResourceResponse response = new ResourceResponse();
            response.setError(HttpServletResponse.SC_NOT_FOUND);
            return response;
        }
    }
}
