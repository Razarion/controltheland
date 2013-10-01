package com.btxtech.game.jsre.client.simulation;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import org.junit.Ignore;

/**
 * User: beat
 * Date: 16.03.2012
 * Time: 11:15:44
 */
@Ignore
public class TestMouseDownEvent extends MouseDownEvent {
    private int x;
    private int y;
    private int nativeButton;

    public TestMouseDownEvent() {
        setNativeEvent(Document.get().createMouseDownEvent(0, 100, 100, 100, 100, false, false, false, false, NativeEvent.BUTTON_LEFT));
    }

    public TestMouseDownEvent(int x, int y, int nativeButton) {
        this.x = x;
        this.y = y;
        this.nativeButton = nativeButton;
    }

    private native String eventGetType(NativeEvent evt) /*-{
      return evt.type;
    }-*/;


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
    public int getNativeButton() {
        return nativeButton;
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
