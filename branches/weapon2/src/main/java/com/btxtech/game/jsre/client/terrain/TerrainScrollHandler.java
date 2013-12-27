package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 16.09.12
 * Time: 12:58
 */
public class TerrainScrollHandler {
    public enum ScrollDirection {
        NORTH,
        SOUTH,
        WEST,
        EAST,
        STOP
    }

    public interface ScrollExecutor {
        void moveDelta(int scrollX, int scrollY);
    }

    private static final int SCROLL_AUTO_MOUSE_DETECTION_WIDTH = 40;
    private static final int SCROLL_TIMER_DELAY = 40;
    private static final int SCROLL_AUTO_DISTANCE = 60;
    private ScrollDirection scrollDirectionXKey = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionYKey = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionXMouse = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionYMouse = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionX = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionY = ScrollDirection.STOP;
    private ScrollExecutor scrollExecutor;
    private boolean scrollDisabled;
    private Timer timer = new TimerPerfmon(PerfmonEnum.SCROLL) {
        @Override
        public void runPerfmon() {
            autoScroll();
        }
    };

    public void setScrollExecutor(ScrollExecutor scrollExecutor) {
        this.scrollExecutor = scrollExecutor;
    }

    public void setScrollDisabled(boolean scrollDisabled) {
        this.scrollDisabled = scrollDisabled;
        if (scrollDisabled) {
            try {
                timer.cancel();
            } catch (Exception e) {
                ClientExceptionHandler.handleException("TerrainScrollHandler.setScrollDisabled()", e);
            }
        }
    }

    public void executeAutoScrollKey(ScrollDirection tmpScrollDirectionX, ScrollDirection tmpScrollDirectionY) {
        if (scrollDisabled) {
            return;
        }

        if (tmpScrollDirectionX != scrollDirectionXKey || tmpScrollDirectionY != scrollDirectionYKey) {
            if (tmpScrollDirectionX != null) {
                scrollDirectionXKey = tmpScrollDirectionX;
            }
            if (tmpScrollDirectionY != null) {
                scrollDirectionYKey = tmpScrollDirectionY;
            }
            executeAutoScroll();
        }
    }

    public void executeAutoScrollMouse(ScrollDirection tmpScrollDirectionX, ScrollDirection tmpScrollDirectionY) {
        if (scrollDisabled) {
            return;
        }

        if (tmpScrollDirectionX != scrollDirectionXMouse || tmpScrollDirectionY != scrollDirectionYMouse) {
            scrollDirectionXMouse = tmpScrollDirectionX;
            scrollDirectionYMouse = tmpScrollDirectionY;
            executeAutoScroll();
        }
    }

    public void handleMouseMoveScroll(int x, int y, int width, int height) {
        if (scrollDisabled) {
            return;
        }

        ScrollDirection tmpScrollDirectionX = ScrollDirection.STOP;
        ScrollDirection tmpScrollDirectionY = ScrollDirection.STOP;
        if (x < SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
            tmpScrollDirectionX = ScrollDirection.WEST;
        } else if (x > width - SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
            tmpScrollDirectionX = ScrollDirection.EAST;
        }

        if (y < SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
            tmpScrollDirectionY = ScrollDirection.NORTH;
        } else if (y > height - SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
            tmpScrollDirectionY = ScrollDirection.SOUTH;
        }
        executeAutoScrollMouse(tmpScrollDirectionX, tmpScrollDirectionY);
    }

    private void executeAutoScroll() {
        ScrollDirection newScrollDirectionX = ScrollDirection.STOP;
        if (scrollDirectionXKey != ScrollDirection.STOP) {
            newScrollDirectionX = scrollDirectionXKey;
        } else if (scrollDirectionXMouse != ScrollDirection.STOP) {
            newScrollDirectionX = scrollDirectionXMouse;
        }

        ScrollDirection newScrollDirectionY = ScrollDirection.STOP;
        if (scrollDirectionYKey != ScrollDirection.STOP) {
            newScrollDirectionY = scrollDirectionYKey;
        } else if (scrollDirectionYMouse != ScrollDirection.STOP) {
            newScrollDirectionY = scrollDirectionYMouse;
        }

        if (newScrollDirectionX != scrollDirectionX || newScrollDirectionY != scrollDirectionY) {
            boolean isTimerRunningOld = scrollDirectionX != ScrollDirection.STOP || scrollDirectionY != ScrollDirection.STOP;
            boolean isTimerRunningNew = newScrollDirectionX != ScrollDirection.STOP || newScrollDirectionY != ScrollDirection.STOP;
            scrollDirectionX = newScrollDirectionX;
            scrollDirectionY = newScrollDirectionY;
            if (isTimerRunningOld != isTimerRunningNew) {
                if (isTimerRunningNew) {
                    autoScroll();
                    timer.scheduleRepeating(SCROLL_TIMER_DELAY);
                } else {
                    timer.cancel();
                }
            }
        }
    }

    private void autoScroll() {
        int scrollX = 0;
        if (scrollDirectionX == ScrollDirection.WEST) {
            scrollX = -SCROLL_AUTO_DISTANCE;
        } else if (scrollDirectionX == ScrollDirection.EAST) {
            scrollX = SCROLL_AUTO_DISTANCE;
        }

        int scrollY = 0;
        if (scrollDirectionY == ScrollDirection.SOUTH) {
            scrollY = SCROLL_AUTO_DISTANCE;
        } else if (scrollDirectionY == ScrollDirection.NORTH) {
            scrollY = -SCROLL_AUTO_DISTANCE;
        }

        scrollExecutor.moveDelta(scrollX, scrollY);
    }

    public static Index calculateSafeDelta(int deltaX, int deltaY, TerrainSettings terrainSettings, Rectangle viewRect) {
        if (terrainSettings == null) {
            return new Index(0, 0);
        }
        int viewOriginLeft = viewRect.getX();
        int viewOriginTop = viewRect.getY();
        int viewWidth = viewRect.getWidth();
        int viewHeight = viewRect.getHeight();

        if (viewWidth == 0 && viewHeight == 0) {
            return new Index(0, 0);
        }

        int orgViewOriginLeft = viewOriginLeft;
        int orgViewOriginTop = viewOriginTop;

        int tmpViewOriginLeft = viewOriginLeft + deltaX;
        int tmpViewOriginTop = viewOriginTop + deltaY;

        if (tmpViewOriginLeft < 0) {
            deltaX = deltaX - tmpViewOriginLeft;
        } else if (tmpViewOriginLeft > terrainSettings.getPlayFieldXSize() - viewWidth - 1) {
            deltaX = deltaX - (tmpViewOriginLeft - (terrainSettings.getPlayFieldXSize() - viewWidth)) - 1;
        }
        if (viewWidth >= terrainSettings.getPlayFieldXSize()) {
            deltaX = 0;
            viewOriginLeft = 0;
        } else {
            viewOriginLeft += deltaX;
        }

        if (tmpViewOriginTop < 0) {
            deltaY = deltaY - tmpViewOriginTop;
        } else if (tmpViewOriginTop > terrainSettings.getPlayFieldYSize() - viewHeight - 1) {
            deltaY = deltaY - (tmpViewOriginTop - (terrainSettings.getPlayFieldYSize() - viewHeight)) - 1;
        }
        if (viewHeight >= terrainSettings.getPlayFieldYSize()) {
            deltaY = 0;
            viewOriginTop = 0;
        } else {
            viewOriginTop += deltaY;
        }

        if (orgViewOriginLeft == viewOriginLeft && orgViewOriginTop == viewOriginTop) {
            // No moveDelta
            return new Index(0, 0);
        }
        return new Index(deltaX, deltaY);
    }

    public void cleanup() {
        scrollDisabled = false;
        try {
            timer.cancel();
        } catch (Exception e) {
            ClientExceptionHandler.handleException("TerrainScrollHandler.cleanup()", e);
        }
    }
}
