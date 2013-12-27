package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.Utils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.debug.PageView;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 27.07.2011
 * Time: 14:54:17
 */
public class DisplayPageViewLink extends Panel {

    public DisplayPageViewLink(String id, final Page page) {
        super(id);
        add(new Link<Void>("displayPageViewLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                DisplayPageViewLink.this.replace(new PageView("componentTree", page));
                setVisible(false);
            }
        });

        add(new Label("componentTree", ""));
    }

    @Override
    public boolean isVisible() {
        return Utils.isTestModeStatic();
    }
}
