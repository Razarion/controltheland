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

package com.btxtech.game.wicket.pages.cms.content.plugin.emailverification;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.user.*;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 18.01.2009
 * Time: 12:37:58
 */
public class EmailVerificationPanel extends Panel {
    @SpringBean
    private RegisterService registerService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private MgmtService mgmtService;
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public EmailVerificationPanel(String id, ContentContext contentContext) {
        super(id);
        try {
            String verificationId = contentContext.getPageParameters().get(CmsUtil.EMAIL_VERIFICATION_KEY).toString();
            User user = registerService.onVerificationPageCalled(verificationId);
            userService.loginIfNotLoggedIn(user);
            add(new ConversionTrackingPanel("conversionTrackingPanel", user));
            add(new Form("enterGame") {
                @Override
                protected void onSubmit() {
                    PageParameters parameters = new PageParameters();
                    if (!userGuidanceService.isStartRealGame()) {
                        parameters.set(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, userGuidanceService.getDefaultLevelTaskId());
                    }
                    setResponsePage(Game.class, parameters);
                }
            });
        } catch (EmailIsAlreadyVerifiedException e) {
            mgmtService.saveServerDebug(MgmtService.SERVER_DEBUG_EMAIL_VERIFICATION_ALREADY, e);
            cmsUiService.setMessageResponsePage(this, "registerEmailVerified", null);
        } catch (UserDoesNotExitException e) {
            ExceptionHandler.handleException(e);
            cmsUiService.setMessageResponsePage(this, "registerConfirmationInvalid", null);
        }
    }

}