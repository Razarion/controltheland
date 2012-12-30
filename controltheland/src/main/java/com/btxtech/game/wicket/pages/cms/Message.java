package com.btxtech.game.wicket.pages.cms;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 31.07.2011
 * Time: 14:52:00
 */
public class Message extends Panel {
    public Message(String id, String message) {
        super(id);
        add(new Label("message", message).setEscapeModelStrings(false));
    }
}
