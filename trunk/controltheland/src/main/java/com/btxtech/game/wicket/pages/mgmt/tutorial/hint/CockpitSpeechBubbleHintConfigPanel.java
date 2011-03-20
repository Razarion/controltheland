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

package com.btxtech.game.wicket.pages.mgmt.tutorial.hint;

import com.btxtech.game.jsre.common.utg.config.CockpitWidgetEnum;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.tutorial.hint.DbCockpitSpeechBubbleHintConfig;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 07.11.2010
 * Time: 15:28:19
 */
public class CockpitSpeechBubbleHintConfigPanel extends Panel {
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(CockpitSpeechBubbleHintConfigPanel.class);

    public CockpitSpeechBubbleHintConfigPanel(String id) {
        super(id);
        add(new CheckBox("closeOnTaskEnd"));
        add(new DropDownChoice<CockpitWidgetEnum>("cockpitWidgetEnum", Arrays.asList(CockpitWidgetEnum.values())));
        add(new BaseItemTypePanel("baseItemType") {
            @Override
            public boolean isVisible() {
                return getCockpitSpeechBubbleHintConfig().getCockpitWidgetEnum().isItemTypeNeeded();
            }
        });

        add(new TextArea("html"));
        add(new TextField<Integer>("blinkDelay"));
        add(new TextField<Integer>("blinkInterval"));
    }

    private DbCockpitSpeechBubbleHintConfig getCockpitSpeechBubbleHintConfig() {
        AbstractPropertyModel propertyModel = (AbstractPropertyModel) CockpitSpeechBubbleHintConfigPanel.this.getDefaultModel();
        return (DbCockpitSpeechBubbleHintConfig) propertyModel.getTarget();
    }
}
