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
public class CmsImageResource extends WebResource {
    public static final String CMS_SHARED_IMAGE_RESOURCES = "cmsimg";
    public static final String PATH = "/cmsimg";
    public static final String ID = "id";
    private static CmsImageResource INSTANCE;

    @SpringBean
    private CmsService cmsService;

    public static Image createImage(String id, DbCmsImage imageId) {
        return new Image(id, new ResourceReference(CMS_SHARED_IMAGE_RESOURCES), new ValueMap(ID + "=" + imageId.getId()));
    }

    public CmsImageResource() {
        INSTANCE = this;
        // Inject CmsService
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    public IResourceStream getResourceStream() {
        int imgId = Integer.parseInt(getParameters().getString(ID));
        DbCmsImage dbCmsImage = cmsService.getDbCmsImage(imgId);
        return new ByteArrayResource(dbCmsImage.getContentType(), dbCmsImage.getData()).getResourceStream();
    }
}
