package com.btxtech.game.jsre.client.utg.tip;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * User: beat
 * Date: 03.09.13
 * Time: 22:30
 */
public class TipSplashPopup extends PopupPanel {
    public TipSplashPopup(TipSplashPopupInfo tipSplashPopupInfo) {
        StringBuilder htmlString = new StringBuilder();
        // Title
        htmlString.append("<p style='font-size:25px;'>");
        htmlString.append(tipSplashPopupInfo.getTitle());
        htmlString.append("</p>");
        // Main Text
        if (tipSplashPopupInfo.getMainText() != null) {
            htmlString.append("<p style='font-size:12px;'>");
            htmlString.append(tipSplashPopupInfo.getMainText());
            htmlString.append("</p>");

        }
        // Table
        htmlString.append("<table><tr><td><img width='40' height='40' src='");
        htmlString.append(getImage(tipSplashPopupInfo.getImageType()));
        htmlString.append("'></td><td style='font-size:12px;'>");
        htmlString.append(tipSplashPopupInfo.getTaskText());
        htmlString.append("</td></tr></table>");

        HTML htmlWidget = new HTML(htmlString.toString());
        htmlWidget.setWidth("35em");
        setWidget(htmlWidget);
    }

    public String getImage(TipSplashPopupInfo.ImageType imageType) {
        switch (imageType) {

            case QUEST:
                return "/images/tips/tipQuest.png";
            case TICK:
                return "/images/tips/tick.png";
            default:
                return "";
        }
    }
}
