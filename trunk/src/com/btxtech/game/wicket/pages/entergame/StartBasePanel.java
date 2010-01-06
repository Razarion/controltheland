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

package com.btxtech.game.wicket.pages.entergame;

import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.services.base.AlreadyUsedException;
import com.btxtech.game.services.base.BaseColor;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.wicket.pages.BorderPanel;
import com.btxtech.game.wicket.pages.Game;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: beat
 * Date: Sep 12, 2009
 * Time: 1:14:52 PM
 */
public class StartBasePanel extends BorderPanel {
    @SpringBean
    private BaseService baseService;
    private String playerName;
    private BaseColor baseColor;
    private Log log = LogFactory.getLog(StartBasePanel.class);

    public StartBasePanel(String id) {
        super(id);
        FeedbackPanel feedbackPanel = new FeedbackPanel("msgs");
        add(feedbackPanel);

        Form<StartBasePanel> form = new Form<StartBasePanel>("startForm", new CompoundPropertyModel<StartBasePanel>(this)) {
            @Override
            protected void onSubmit() {
                try {
                    if(baseService.getFreeColorsMultiColums(15, 3).isEmpty()) {
                        info("Sorry, but the game is full");
                        return;
                    }

                    baseService.createNewBase(playerName, baseColor);
                    setResponsePage(Game.class);
                } catch (AlreadyUsedException e) {
                    info(e.getMessage());
                } catch (NoSuchItemTypeException e) {
                    info(e.getMessage());
                    log.error("", e);
                }
            }
        };
        add(form);
        TextField<String> textField = new TextField<String>("playerName");
        textField.setRequired(true);
        form.add(textField);

        RadioGroup<BaseColor> numbersGroup = new RadioGroup<BaseColor>("baseColor");
        form.add(numbersGroup);

        List<List<BaseColor>> baseColors = baseService.getFreeColorsMultiColums(15, 3);
        if(baseColors.isEmpty()) {
            baseColor = null;
        } else {
            baseColor = baseColors.get(0).get(0);
        }
        playerName = baseService.getFreePlayerName();
        ListView<List<BaseColor>> colorList = new ListView<List<BaseColor>>("numbers", baseColors) {
            protected void populateItem(ListItem<List<BaseColor>> item) {
                addColorCell(0, item);
                addColorCell(1, item);
                addColorCell(2, item);
            }
        };
        colorList.setReuseItems(true);
        numbersGroup.add(colorList);
    }

    private void addColorCell(int column, ListItem<List<BaseColor>> item) {
        List<BaseColor> row = item.getModelObject();
        if (row.size() > column) {
            BaseColor baseColor = row.get(column);
            item.add(new Radio<BaseColor>("radio" + column, new Model<BaseColor>(baseColor)));
            item.add(ColorField.create("number" + column, baseColor.getHtmlColor()));
        } else {
            Label dummy1 = new Label("number" + column);
            dummy1.setVisible(false);
            item.add(dummy1);
            Label dummy2 = new Label("radio" + column);
            dummy2.setVisible(false);
            item.add(dummy2);
        }

    }
}
