package com.btxtech.game.jsre.client;

import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * User: beat
 * Date: 23.02.2011
 * Time: 13:44:29
 */
public class ExtendedProgressBar extends ProgressBar {
    public ExtendedProgressBar(double minProgress, double maxProgress) {
        super(minProgress, maxProgress);
    }

    public void setColor(String progress, String remaining) {
        getElement().getStyle().setBackgroundColor(remaining);
        getBarElement().getStyle().setBackgroundColor(progress);
    }
}
