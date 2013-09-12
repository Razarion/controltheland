package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainScrollListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.tutorial.GuiEventTaskConfig;
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 11.09.13
 * Time: 16:25
 */
public class GuiEventTask extends AbstractTask implements TerrainScrollListener {
    private Rectangle scrollTargetRectangle;
    private Timer prompterTimer;
    private long lastConversionTimeStamp;

    public GuiEventTask(GuiEventTaskConfig guiEventTaskConfig) {
        super(guiEventTaskConfig);
        scrollTargetRectangle = guiEventTaskConfig.getScrollTargetRectangle();
    }

    @Override
    public void internStart() {
        TerrainView.getInstance().addTerrainScrollListener(this);
        prompterTimer = new Timer() {
            @Override
            public void run() {
               if(lastConversionTimeStamp > 0 && lastConversionTimeStamp + 1000 < System.currentTimeMillis()) {
                   lastConversionTimeStamp = 0;
                   onTaskPoorConversion();
               }
            }
        };
        prompterTimer.scheduleRepeating(1000);
    }

    @Override
    public void internCleanup() {
        TerrainView.getInstance().removeTerrainScrollListener(this);
        prompterTimer.cancel();
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        onTaskConversion();
        lastConversionTimeStamp = System.currentTimeMillis();
        if (new Rectangle(left, top, width, height).adjoins(scrollTargetRectangle)) {
            onTaskSucceeded();
        }
    }
}
