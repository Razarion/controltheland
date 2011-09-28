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
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: Jun 1, 2009
 * Time: 12:10:57 AM
 */
public class Game extends WebPage implements IHeaderContributor {
    private static final String WORKING = "working.gif";
    private static final String FINISHED = "finished.png";
    private static final String FAILED = "failed.png";
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public Game() {
        GameStartupSeq gameStartupSeq = userGuidanceService.getColdStartupSeq();

        add(new Label("startupTaskText", gameStartupSeq.getAbstractStartupTaskEnum()[0].getStartupTaskEnumHtmlHelper().getNiceText()));

        setupStartupSeq(gameStartupSeq);

        // Startup visualisation table
        ListView<StartupTaskEnum> taskTable = new ListView<StartupTaskEnum>("tasks", Arrays.asList(gameStartupSeq.getAbstractStartupTaskEnum())) {
            protected void populateItem(ListItem<StartupTaskEnum> linkItem) {
                addImage(linkItem, "taskImageLoad", WORKING);
                addImage(linkItem, "taskImageDone", FINISHED);
                addImage(linkItem, "taskImageFailed", FAILED);

                Label taskName = new Label("taskName", linkItem.getModelObject().getStartupTaskEnumHtmlHelper().getNiceText());
                taskName.add(new SimpleAttributeModifier("id", linkItem.getModelObject().getStartupTaskEnumHtmlHelper().getNameId()));
                if (linkItem.getModelObject().isFirstTask()) {
                    taskName.add(new SimpleAttributeModifier("style", "font-weight:bold;"));
                }
                linkItem.add(taskName);

                Component time = new Label("taskTime", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").setEscapeModelStrings(false);
                time.add(new SimpleAttributeModifier("id", linkItem.getModelObject().getStartupTaskEnumHtmlHelper().getTimeId()));
                linkItem.add(time);
            }
        };
        add(taskTable);
    }

    private void setupStartupSeq(GameStartupSeq gameStartupSeq) {
        Component startupSeqLabel = new Label("startupSeq", userGuidanceService.getDbLevelHtml()).setEscapeModelStrings(false);
        startupSeqLabel.add(new SimpleAttributeModifier("id", com.btxtech.game.jsre.client.Game.STARTUP_SEQ_ID));
        startupSeqLabel.add(new SimpleAttributeModifier(com.btxtech.game.jsre.client.Game.STARTUP_SEQ_ID, gameStartupSeq.name()));
        add(startupSeqLabel);
    }

    private void addImage(ListItem<StartupTaskEnum> linkItem, String id, String imageName) {
        Image working = new Image(id, imageName);
        if (imageName.equals(WORKING)) {
            if (!linkItem.getModelObject().isFirstTask()) {
                working.add(new SimpleAttributeModifier("width", "0px"));
                working.add(new SimpleAttributeModifier("height", "0px"));
            }
            working.add(new SimpleAttributeModifier("id", linkItem.getModelObject().getStartupTaskEnumHtmlHelper().getImgIdWorking()));
        } else if (imageName.equals(FINISHED)) {
            working.add(new SimpleAttributeModifier("width", "0px"));
            working.add(new SimpleAttributeModifier("height", "0px"));
            working.add(new SimpleAttributeModifier("id", linkItem.getModelObject().getStartupTaskEnumHtmlHelper().getImgIdFinished()));
        } else if (imageName.equals(FAILED)) {
            working.add(new SimpleAttributeModifier("width", "0px"));
            working.add(new SimpleAttributeModifier("height", "0px"));
            working.add(new SimpleAttributeModifier("id", linkItem.getModelObject().getStartupTaskEnumHtmlHelper().getImgIdFailed()));
        } else {
            throw new IllegalArgumentException("Unknown image: " + imageName);
        }

        linkItem.add(working);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        userTrackingService.pageAccess(getClass());
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        if (!userTrackingService.isJavaScriptDetected()) {
            iHeaderResponse.renderJavascript(CmsPage.JAVA_SCRIPT_DETECTION, null);
        }
    }
}
