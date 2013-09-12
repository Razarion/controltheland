package com.btxtech.game.jsre.client.utg.tip;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * User: beat
 * Date: 03.09.13
 * Time: 22:30
 */
public class StorySplashPopup extends PopupPanel {
    public StorySplashPopup(StorySplashPopupInfo storySplashPopupInfo) {
        StringBuilder htmlString = new StringBuilder();
        // Title
        htmlString.append("<p style='font-size:25px;'>");
        htmlString.append(storySplashPopupInfo.getTitle());
        htmlString.append("</p>");
        // Main Text
        if (storySplashPopupInfo.getMainText() != null) {
            htmlString.append("<p style='font-size:12px;'>");
            htmlString.append(storySplashPopupInfo.getMainText());
            htmlString.append("</p>");

        }
        // Table
        htmlString.append("<table><tr><td><img width='40' height='40' src='");
        htmlString.append(getImage(storySplashPopupInfo.getImageType()));
        htmlString.append("'></td><td style='font-size:12px;'>");
        htmlString.append(storySplashPopupInfo.getTaskText());
        htmlString.append("</td></tr></table>");

        HTML htmlWidget = new HTML(htmlString.toString());
        htmlWidget.setWidth("35em");
        setWidget(htmlWidget);
        getElement().getStyle().setZIndex(99); // TODO zindex
        center();
    }

    public String getImage(StorySplashPopupInfo.ImageType imageType) {
        switch (imageType) {

            case QUEST:
                return "/images/tips/tipQuest.png";
            case TICK:
                return "/images/tips/tick.png";
            default:
                return "";
        }
    }

    public void fadeOut() {
        removeStyleName("raz-fade-in");
        addStyleName("raz-fade-out");
    }

    public void fadeIn() {
        removeStyleName("raz-fade-out");
        addStyleName("raz-fade-in");
    }
}
