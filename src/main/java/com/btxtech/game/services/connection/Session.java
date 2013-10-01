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

package com.btxtech.game.services.connection;

import com.btxtech.game.services.cms.EditMode;
import com.btxtech.game.services.user.DbInvitationInfo;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbFacebookSource;

import javax.servlet.http.HttpServletRequest;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:45:30 PM
 */
public interface Session {
    Connection getConnection();

    void setConnection(Connection connection);

    String getSessionId();

    String getTrackingCookieId();

    String getUserAgent();

    void onJavaScriptDetected(Boolean html5Support);

    boolean isJavaScriptDetected();

    boolean isHtml5Support();

    UserState getUserState();

    void setUserState(UserState userState);

    @Deprecated
    /**
     * Use RequestHelper instead
     */
    HttpServletRequest getRequest();

    EditMode getEditMode();

    void setEditMode(EditMode editMode);

    DbFacebookSource getDbFacebookSource();

    DbInvitationInfo getDbInvitationInfo();
}
