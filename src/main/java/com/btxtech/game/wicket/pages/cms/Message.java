package com.btxtech.game.wicket.pages.cms;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

/**
 * User: beat
 * Date: 31.07.2011
 * Time: 14:52:00
 */
public class Message extends Panel {
    public Message(String id, String key, String additionalParameter) {
        super(id);
        Object[] args = null;
        if (additionalParameter != null) {
            args = new Object[]{additionalParameter};
        }
        add(new Label("message", new StringResourceModel(key, null, args)).setEscapeModelStrings(false));
    }
}
