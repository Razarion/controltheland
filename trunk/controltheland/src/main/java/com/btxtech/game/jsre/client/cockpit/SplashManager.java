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
    private List<String> imageNameQueue = new ArrayList<String>();
    private Image image;

    public static SplashManager getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SplashManager() {
    }

    public void onLevelUp() {
        imageNameQueue.add(LEVEL_UP_IMAGE);
        processQueue();
    }


    public void onLevelTaskCone() {
        imageNameQueue.add(LEVEL_TASK_DONE_IMAGE);
        processQueue();
    }

    private void processQueue() {
        if (imageNameQueue.isEmpty() || image != null) {
            return;
        }
        String imageUrl = ImageHandler.getSplashImageUrl(imageNameQueue.remove(0));
        setupImage(imageUrl);
        Timer timer = new TimerPerfmon(PerfmonEnum.SPLASH_MANAGER) {
            @Override
            public void runPerfmon() {
                MapWindow.getAbsolutePanel().remove(image);
                image = null;
                processQueue();
            }
        };
        timer.schedule(DISPLAY_TIME);
    }

    private void setupImage(String imageUrl) {
        image = new Image();
        image.getElement().getStyle().setZIndex(Constants.Z_INDEX_HIDDEN);
        image.setUrl(imageUrl);
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                Image image = (Image) event.getSource();
                int xPos = (TerrainView.getInstance().getViewWidth() - image.getOffsetWidth()) / 2;
                int yPos = (TerrainView.getInstance().getViewHeight() - image.getOffsetHeight()) / 2;
                image.getElement().getStyle().setZIndex(Constants.Z_INDEX_LEVEL_SPLASH);
                MapWindow.getAbsolutePanel().setWidgetPosition(image, xPos, yPos);
            }
        });
        MapWindow.getAbsolutePanel().add(image);
    }

}
