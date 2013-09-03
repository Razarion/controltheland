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

package com.btxtech.game.wicket.uiservices.facebook;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 18.01.2009
 * Time: 12:37:58
 */
public class FacebookController extends Panel {
    public enum Type {
        REGISTER,
        AUTO_LOGON,
        LOGGED_IN,
        GAME
    }
    @SpringBean
    private UserService userService;
    @SpringBean
    private CmsUiService cmsUiService;

    public FacebookController(String id, Type type) {
        super(id);

        PackageTextTemplate jsTemplate;
        switch (type) {
            case REGISTER:
                jsTemplate = new PackageTextTemplate(FacebookController.class, "FacebookRegister.js");
                break;
            case AUTO_LOGON:
                jsTemplate = new PackageTextTemplate(FacebookController.class, "FacebookAutologin.js");
                break;
            case LOGGED_IN:
                jsTemplate = new PackageTextTemplate(FacebookController.class, "FacebookOnLoggedIn.js");
                break;
            case GAME:
                jsTemplate = new PackageTextTemplate(FacebookController.class, "FacebookInit.js");
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FACEBOOK_APP_ID", cmsUiService.getFacebookAppId());
        parameters.put("CHANNEL_URL", "//www.razarion.com/FacebookChannelFile.html");
        parameters.put("FACEBOOK_AUTO_LOGIN", "/" + CmsUtil.MOUNT_GAME_FACEBOOK_AUTO_LOGIN);
        add(new Label("facebookJsSkd", new Model<>(jsTemplate.asString(parameters))).setEscapeModelStrings(false));

    }

}