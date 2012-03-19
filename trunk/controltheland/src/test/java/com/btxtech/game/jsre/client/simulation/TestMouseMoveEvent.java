package com.btxtech.game.jsre.client.simulation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import org.junit.Ignore;

/**
 * User: beat
 * Date: 16.03.2012
 * Time: 12:05:44
 */
@Ignore
public class TestMouseMoveEvent extends MouseMoveEvent {
    private int x;
    private int y;

    public TestMouseMoveEvent() {
    }

    public TestMouseMoveEvent(int x, int y) {
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
}
