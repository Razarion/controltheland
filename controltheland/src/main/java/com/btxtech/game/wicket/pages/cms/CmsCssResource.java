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
import com.btxtech.game.services.cms.page.DbPage;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.value.ValueMap;

/**
 * User: beat
 * Date: 30.05.2011
 * Time: 10:49:56
 */
public class CmsCssResource extends WebResource {
    public static final String CMS_SHARED_CSS_RESOURCES = "cssResource";
    private static final String ID = "id";

    @SpringBean
    private CmsService cmsService;

    public static ResourceLink<Object> createCss(String id, DbPage dbPage) {
        ResourceLink<Object> link = new ResourceLink<Object>(id, new ResourceReference(CMS_SHARED_CSS_RESOURCES), new ValueMap(ID + "=" + dbPage.getId()));
        link.add(new SimpleAttributeModifier("rel", "stylesheet"));
        link.add(new SimpleAttributeModifier("type", "text/css"));
        return link;
    }

    public CmsCssResource() {
        // Inject CmsService
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    public IResourceStream getResourceStream() {
        int pageId = Integer.parseInt(getParameters().getString(ID));
        return new StringResourceStream(cmsService.getPage(pageId).getStyle().getCss(), "text/css");
    }
}
