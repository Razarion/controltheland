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

import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.StartupSeq;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.Arrays;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Jun 1, 2009
 * Time: 12:10:57 AM
 */
public class Game extends WebPage {
    private static final String WORKING = "working.gif";
    private static final String FINISHED = "finished.png";
    private static final String FAILED = "failed.png";

    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public Game() {
        StartupSeq startupSeq = userGuidanceService.getColdStartupSeq();

        setupStartupSeq(startupSeq);

        // Startup visualisation table
        ListView<StartupTaskEnum> taskTable = new ListView<StartupTaskEnum>("tasks", Arrays.asList(startupSeq.getAbstractStartupTaskEnum())) {
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

    private void setupStartupSeq(StartupSeq startupSeq) {
        Label startupSeqLabel = new Label("startupSeq", "");
        startupSeqLabel.add(new SimpleAttributeModifier("id", com.btxtech.game.jsre.client.Game.STARTUP_SEQ_ID));
        startupSeqLabel.add(new SimpleAttributeModifier(com.btxtech.game.jsre.client.Game.STARTUP_SEQ_ID, startupSeq.name()));
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
}
