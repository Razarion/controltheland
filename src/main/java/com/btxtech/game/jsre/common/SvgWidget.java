package com.btxtech.game.jsre.common;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 27.10.2011
 * Time: 10:47:15
 */
public class SvgWidget extends Widget {
    /**
     * Loads the SVG from the server
     *
     * @param url SVG url
     * @param width width
     * @param height height
     */
    public SvgWidget(String url, int width, int height) {
        ObjectElement objectElement = Document.get().createObjectElement();
        objectElement.setData(url);
        objectElement.setType("image/svg+xml");
        setElement(objectElement);
        setPixelSize(width, height);
    }

}
