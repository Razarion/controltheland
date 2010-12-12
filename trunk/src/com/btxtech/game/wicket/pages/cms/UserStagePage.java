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
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.user.LoggedinBox;
import com.btxtech.game.wicket.pages.user.LoginBox;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.10.2010
 * Time: 10:34:23
 */
public class UserStagePage extends WebPage {
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private CmsService cmsService;

    public UserStagePage() {
        add(new Label("style", new PropertyModel(cmsService.getStagingContentStyleDTO(), "style")).setEscapeModelStrings(false));
        add(new Label("text", new PropertyModel(cmsService.getStagingContentStyleDTO(), "text")).setEscapeModelStrings(false));
        BookmarkablePageLink<WebPage> startLink = new BookmarkablePageLink<WebPage>("startLink", Game.class);
        add(startLink);
        startLink.add(new Image("startImage", new IModel<ByteArrayResource>() {

            @Override
            public ByteArrayResource getObject() {
                return new ByteArrayResource(cmsService.getHomeContentStyleDTO().getDbCmsHomeLayout().getStartImageContentType(),
                        cmsService.getHomeContentStyleDTO().getDbCmsHomeLayout().getStartImage());
            }

            @Override
            public void setObject(ByteArrayResource object) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));

    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        userTrackingService.pageAccess(getClass());

        // TODO this is ugly
        Component component = get("signinBox");
        if (component != null) {
            remove(component);
        }
        if (userService.isLoggedin()) {
            add(new LoggedinBox());
        } else {
            add(new LoginBox());
        }
    }
}
