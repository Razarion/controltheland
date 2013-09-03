package com.btxtech.game.wicket.pages.cms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 03.02.2012
 * Time: 12:56:52
 */
public class BorderWrapper extends Panel {
    public BorderWrapper(String id, Component content, String borderCss) {
        super(id);
        WebMarkupContainer border = new WebMarkupContainer("border");
        border.add(new AttributeModifier("class", borderCss));
        add(border);
        border.add(content);
    }
}
