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

import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
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
public class CmsItemTypeImageResource extends WebResource {
    public static final String CMS_SHARED_IMAGE_RESOURCES = "cmsitemtypeimg";
    public static final String PATH = "/cmsitemimg";
    private static final String ID = "id";

    @SpringBean
    private ItemService itemService;

    public static Image createImage(String id, DbItemType dbItemType) {
        return new Image(id, new ResourceReference(CMS_SHARED_IMAGE_RESOURCES), new ValueMap(ID + "=" + dbItemType.getId()));
    }

    public CmsItemTypeImageResource() {
        // Inject CmsService
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    public IResourceStream getResourceStream() {
        int itmTypeId = Utils.parseIntSave(getParameters().getString(ID));
        DbItemTypeImage dbItemTypeImage = itemService.getCmsDbItemTypeImage(itmTypeId);
        return new ByteArrayResource(dbItemTypeImage.getContentType(), dbItemTypeImage.getData()).getResourceStream();
    }
}
