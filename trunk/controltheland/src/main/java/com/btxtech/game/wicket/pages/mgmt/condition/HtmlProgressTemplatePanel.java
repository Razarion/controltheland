package com.btxtech.game.wicket.pages.mgmt.condition;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 13.04.12
 * Time: 22:08
 */
public class HtmlProgressTemplatePanel extends Panel {
    public HtmlProgressTemplatePanel(String id) {
        super(id);
        add(new TextArea("htmlProgressTemplate"));
    }
}
