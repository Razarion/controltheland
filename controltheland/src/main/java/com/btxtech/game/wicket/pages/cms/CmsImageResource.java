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

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.common.Utils;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat Date: 01.06.2011 Time: 10:49:56
 */
public class CmsImageResource extends DynamicImageResource {
    public static final String CMS_SHARED_IMAGE_RESOURCES = "cmsimg";
    public static final String PATH = "/cmsimg";
    public static final String ID = "id";

    @SpringBean
    private CmsService cmsService;

    public static Image createImage(String id, DbCmsImage imageId) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(ID, imageId.getId());
        return new Image(id, new PackageResourceReference(CMS_SHARED_IMAGE_RESOURCES), pageParameters);
    }

    public CmsImageResource() {
        // Inject CmsService
        Injector.get().inject(this);
    }

    @Override
    protected byte[] getImageData(Attributes attributes) {
        int imgId = Utils.parseIntSave(attributes.getParameters().get(ID).toString());
        DbCmsImage dbCmsImage = cmsService.getDbCmsImage(imgId);
        setFormat(dbCmsImage.getContentType());
        return dbCmsImage.getData();
    }
}
