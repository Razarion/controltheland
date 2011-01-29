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

package com.btxtech.game.services.cms;

import com.btxtech.game.controllers.CmsImageController;
import java.io.Serializable;

/**
 * User: beat
 * Date: 07.07.2010
 * Time: 16:19:07
 */
public class CmsContentStyleDTO implements Serializable {
    private String text;
    private String style;
    private DbCmsHomeLayout dbCmsHomeLayout;

    public String getText() {
        return text;
    }

    public DbCmsHomeLayout getDbCmsHomeLayout() {
        return dbCmsHomeLayout;
    }

    public String getStyle() {
        return style;
    }

    private String createStyle(DbCmsHomeLayout dbCmsHomeLayout) {
        StringBuilder builder = new StringBuilder();
        // body
        builder.append("body {margin: 0;padding: 0;text-align: center;");
        builder.append("background-color:");
        builder.append(dbCmsHomeLayout.getBodyBackgroundColor());
        builder.append(";}");
        // main
        builder.append("#main {background: url(");
        builder.append(CmsImageController.CONTROLLER);
        builder.append("?");
        builder.append(CmsImageController.IMG_PARAMETER);
        builder.append("=");
        builder.append(CmsImageController.IMG_START);
        builder.append(") center no-repeat;margin: 0 auto;padding: 0;text-align: left;width: ");
        builder.append(dbCmsHomeLayout.getBgImageWidth());
        builder.append("px; height: ");
        builder.append(dbCmsHomeLayout.getBgImageHeight());
        builder.append("px;}");
        // absoluteContainer
        builder.append("#absoluteContainer {position: absolute; width:");
        builder.append(dbCmsHomeLayout.getBgImageWidth());
        builder.append("px; height: ");
        builder.append(dbCmsHomeLayout.getBgImageHeight());
        builder.append("px;}");
        // text
        builder.append("#text {position: absolute;top: ");
        builder.append(dbCmsHomeLayout.getTextTop());
        builder.append("px; left: ");
        builder.append(dbCmsHomeLayout.getTextLeft());
        builder.append("px; right: ");
        builder.append(dbCmsHomeLayout.getTextRight());
        builder.append("px; color: ");
        builder.append(dbCmsHomeLayout.getTextColor());
        builder.append("}");
        // startBtn
        builder.append("#startLink {position: absolute;top: ");
        builder.append(dbCmsHomeLayout.getStartLinkTop());
        builder.append("px; left: ");
        builder.append(dbCmsHomeLayout.getStartLinkLeft());
        builder.append("px;}");
        // infoBtn
        builder.append("#infoLink {position: absolute;top: ");
        builder.append(dbCmsHomeLayout.getInfoLinkTop());
        builder.append("px; left: ");
        builder.append(dbCmsHomeLayout.getInfoLinkLeft());
        builder.append("px;}");

        return builder.toString();
    }

    public void update(DbCmsHomeText dbCmsHomeText, DbCmsHomeLayout dbCmsHomeLayout) {
        this.dbCmsHomeLayout = dbCmsHomeLayout;
        style = createStyle(dbCmsHomeLayout);
        text = dbCmsHomeText.getText();
    }
}
