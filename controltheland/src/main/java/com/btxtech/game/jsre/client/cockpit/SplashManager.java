package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 06.02.2012
 * Time: 23:59:54
 */
public class SplashManager {
    private static final int DISPLAY_TIME = 3000;
    private static final String LEVEL_UP_IMAGE = "LevelUp.png";
    private static final String LEVEL_TASK_DONE_IMAGE = "LevelTaskDone.png";
    private static SplashManager INSTANCE = new SplashManager();
    private List<SplashImage> splashImages = new ArrayList<SplashImage>();
    private SplashImage currentSplashImage;

    public static SplashManager getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SplashManager() {
    }

    public void onLevelUp() {
        splashImages.add(new SplashImage(LEVEL_UP_IMAGE));
    }


    public void onLevelTaskCone() {
        splashImages.add(new SplashImage(LEVEL_TASK_DONE_IMAGE));
    }

    public SplashImage getCurrentSplash(long timeStamp) {
        if (currentSplashImage == null && splashImages.isEmpty()) {
            return null;
        }
        if (currentSplashImage == null) {
            return showNextSplash(timeStamp);
        } else {
            if (currentSplashImage.isInTime(timeStamp)) {
                return currentSplashImage;
            } else {
                return showNextSplash(timeStamp);
            }
        }
    }

    private SplashImage showNextSplash(long timeStamp) {
        if (splashImages.isEmpty()) {
            currentSplashImage = null;
        } else {
            currentSplashImage = splashImages.remove(0);
            currentSplashImage.setEndShowTime(timeStamp + DISPLAY_TIME);
        }
        return currentSplashImage;
    }
}
