package com.btxtech.game.jsre.client;

/**
 * User: beat
 * Date: 23.01.13
 * Time: 15:14
 */
public class AdCellHelper {
    public static final String BID_URL_KEY = "bid";
    public static final String AD_CELL_JS_LIB_URL = "http://www.adcell.de/js/jsadlib.js";

    public static String buildAdCellImageUrl(int userId, String bid) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://www.adcell.de/event.php?pid=3111&eventid=3820&referenz=");
        builder.append(userId);
        builder.append("&bid=");
        builder.append(bid);
        return builder.toString();
    }

}
