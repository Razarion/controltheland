package com.btxtech.game.jsre.client.utg.tip;

import java.io.Serializable;

/**
 * User: beat
 * Date: 18.09.13
 * Time: 12:54
 */
abstract public class AbstractSplashPopupInfo implements Serializable {
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
