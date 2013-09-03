package com.btxtech.game.jsre.client.cockpit;

/**
 * User: beat
 * Date: 11.08.12
 * Time: 12:38
 */
public class SplashImage {
    private long endShowTime;
    private String splashImageName;


    public SplashImage(String splashImageName) {
        this.splashImageName = splashImageName;
    }

    public String getSplashImageName() {
        return splashImageName;
    }

    public void setEndShowTime(long endShowTime) {
        this.endShowTime = endShowTime;
    }

    public boolean isInTime(long timeStamp) {
        return timeStamp < endShowTime;
    }
}
