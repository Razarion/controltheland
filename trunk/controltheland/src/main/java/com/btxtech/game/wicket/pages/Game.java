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
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Jun 1, 2009
 * Time: 12:10:57 AM
 */
public class Game extends WebPage {
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private CmsUiService cmsUiService;

    public Game(PageParameters parameters) {
        super(parameters);
        if (!userTrackingService.isHtml5Support()) {
            cmsUiService.setPredefinedResponsePage(this, CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
            return;
        }

        GameStartupSeq gameStartupSeq;

        Integer levelTaskId = null;
        if (parameters.containsKey(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID)) {
            levelTaskId = parameters.getInt(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID);
            gameStartupSeq = GameStartupSeq.COLD_SIMULATED;
        } else {
            gameStartupSeq = GameStartupSeq.COLD_REAL;
        }

        add(new Label("startupTaskText", gameStartupSeq.getAbstractStartupTaskEnum()[0].getStartupTaskEnumHtmlHelper().getNiceText()));

        setupStartupSeq(gameStartupSeq, levelTaskId);
    }

    private void setupStartupSeq(GameStartupSeq gameStartupSeq, Integer levelTaskId) {
        Component startupSeqLabel = new Label("startupSeq", "");
        startupSeqLabel.add(new SimpleAttributeModifier("id", com.btxtech.game.jsre.client.Game.STARTUP_SEQ_ID));
        startupSeqLabel.add(new SimpleAttributeModifier(com.btxtech.game.jsre.client.Game.STARTUP_SEQ_ID, gameStartupSeq.name()));
        if (levelTaskId != null) {
            startupSeqLabel.add(new SimpleAttributeModifier(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, levelTaskId.toString()));
        }
        add(startupSeqLabel);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        userTrackingService.pageAccess(getClass());
    }
}
