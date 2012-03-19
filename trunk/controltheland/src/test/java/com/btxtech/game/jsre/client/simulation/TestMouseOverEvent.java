package com.btxtech.game.jsre.client.simulation;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.UIObject;
import org.junit.Ignore;

/**
 * User: beat
 * Date: 16.03.2012
 * Time: 11:15:44
 */
@Ignore
public class TestMouseOverEvent extends MouseOverEvent {
    private int x;
    private int y;

    public TestMouseOverEvent(UIObject relatedTarget) {
        setNativeEvent(Document.get().createMouseOverEvent(0, 100, 100, 100, 100, false, false, false, false, NativeEvent.BUTTON_LEFT, relatedTarget.getElement()));
    }

    public TestMouseOverEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getRelativeX(Element target) {
        return x;
    }

    @Override
    public int getRelativeY(Element target) {
        return y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void stopPropagation() {
        // Do nothing
    }

    @Override
    public void preventDefault() {
        // Do nothing
    }
}
