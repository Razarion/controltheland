package com.btxtech.game.jsre.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 15.02.2012
 * Time: 12:03:31
 */
public class ProgressBar extends Widget {
    private Element barElement;

    public ProgressBar() {
        // Create the outer shell
        setElement(DOM.createDiv());

        // Create the bar element
        barElement = DOM.createDiv();
        DOM.appendChild(getElement(), barElement);
        DOM.setStyleAttribute(barElement, "height", "100%");
    }


    public void setProgress(double curProgress, double maxProgress) {
        // Display
        double percent = curProgress / maxProgress;
        if (percent > 1.0) {
            percent = 1.0;
        }
        if (percent < 0) {
            percent = 0;
        }
        int intPercent = (int) (100 * percent);
        DOM.setStyleAttribute(barElement, "width", intPercent + "%");
    }

    public void setColors(String progress, String remaining) {
        barElement.getStyle().setBackgroundColor(progress);
        getElement().getStyle().setBackgroundColor(remaining);
    }
}
