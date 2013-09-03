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

package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.facebook.FacebookController;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Jun 1, 2009
 * Time: 12:10:57 AM
 */
public class Game extends RazarionPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private CmsUiService cmsUiService;
    private Integer levelTaskId;

    public Game(PageParameters parameters) {
        super(parameters);
        if (!userTrackingService.isHtml5Support()) {
            cmsUiService.setPredefinedResponsePage(this, CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
            return;
        }

        add(new WebMarkupContainer("metaGwtLocale").add(new AttributeModifier("content", "locale=" + getSession().getLocale().toString())));

        add(new FacebookController("facebook", FacebookController.Type.GAME));

        GameStartupSeq gameStartupSeq;

        if (!parameters.get(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID).isNull() && !userGuidanceService.isStartRealGame()) {
            // userGuidanceService.isStartRealGame() prevent error if user finished first mission and press reload in real game (url: .../taskId/1)
            levelTaskId = parameters.get(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID).toInt();
            gameStartupSeq = GameStartupSeq.COLD_SIMULATED;
        } else {
            gameStartupSeq = GameStartupSeq.COLD_REAL;
        }

        add(new Label("startupTaskText", new ResourceModel(gameStartupSeq.getAbstractStartupTaskEnum()[0].name(), gameStartupSeq.getAbstractStartupTaskEnum()[0].name())));

        setupStartupSeq(gameStartupSeq, levelTaskId);
    }

    private void setupStartupSeq(GameStartupSeq gameStartupSeq, Integer levelTaskId) {
        Component startupSeqLabel = new Label("startupSeq", "");
        startupSeqLabel.add(new AttributeModifier("id", com.btxtech.game.jsre.client.Game.STARTUP_SEQ_ID));
        startupSeqLabel.add(new AttributeModifier(com.btxtech.game.jsre.client.Game.STARTUP_SEQ_ID, gameStartupSeq.name()));
        if (levelTaskId != null) {
            startupSeqLabel.add(new AttributeModifier(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, levelTaskId.toString()));
        }
        add(startupSeqLabel);
    }

    @Override
    protected void onBeforeRender() {
        try {
            if (levelTaskId != null) {
                userTrackingService.pageAccess(getClass().getName(), "LevelTaskId=" + levelTaskId);
            } else {
                userTrackingService.pageAccess(getClass().getName(), null);
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        super.onBeforeRender();
    }

}
