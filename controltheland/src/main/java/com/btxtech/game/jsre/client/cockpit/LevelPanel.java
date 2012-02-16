package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * User: beat
 * Date: 07.02.2012
 * Time: 01:14:40
 */
public class LevelPanel extends AbsolutePanel {
    private static final int WEIGHT = 30;
    private static final int HEIGHT = 30;
    private static final int BLINK_DELAY = 500;
    private static final String BACKGROUND_COLOR = "#FFFF00";
    private Label levelName;
    private boolean isBlinking = false;

    public LevelPanel() {
        setPixelSize(WEIGHT, HEIGHT);
        setTitle(ToolTips.TOOL_TIP_LEVEL);
        Image image = ImageHandler.getIcon16("medal");
        add(image, 2, 2);
        levelName = new Label("?");
        add(levelName, 20, 2);
        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                isBlinking = false;
                getElement().getStyle().clearBackgroundColor();
                Window.open(Connection.getInstance().getGameInfo().getPredefinedUrls().get(CmsUtil.CmsPredefinedPage.USER_PAGE), CmsUtil.TARGET_BLANK, "");
            }
        }, ClickEvent.getType());
        getElement().getStyle().setCursor(Style.Cursor.POINTER);
    }

    public void onLevelUp(LevelScope levelScope) {
        levelName.setText(levelScope.getLevelName());
        startBlink();
    }

    public void onLevelTaskDone() {
        startBlink();
    }

    private void startBlink() {
        isBlinking = true;
        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if (isBlinking && (getElement().getStyle().getBackgroundColor() == null || getElement().getStyle().getBackgroundColor().trim().isEmpty())) {
                    getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
                } else {
                    getElement().getStyle().clearBackgroundColor();
                }
                return isBlinking;
            }
        }, BLINK_DELAY);
    }
}
