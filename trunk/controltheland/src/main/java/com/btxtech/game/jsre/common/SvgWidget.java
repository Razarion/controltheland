package com.btxtech.game.jsre.common;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HandlesAllFocusEvents;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 27.10.2011
 * Time: 10:47:15
 */
public class SvgWidget extends FocusWidget {
    /**
     * Loads the SVG from the server
     *
     * @param url SVG url
     */
    public SvgWidget(String url) {
        ObjectElement objectElement = Document.get().createObjectElement();
        objectElement.setData(url);
        objectElement.setType("image/svg+xml");
        setElement(objectElement);
    }

}
